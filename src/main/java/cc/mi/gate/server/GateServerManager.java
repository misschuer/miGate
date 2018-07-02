package cc.mi.gate.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import cc.mi.core.constance.IdentityConst;
import cc.mi.core.constance.TaskDirectConst;
import cc.mi.core.generate.Opcodes;
import cc.mi.core.handler.Handler;
import cc.mi.core.log.CustomLogger;
import cc.mi.core.packet.Packet;
import cc.mi.core.task.base.Task;
import cc.mi.gate.handler.CenterIsReadyHandler;
import cc.mi.gate.handler.DestroyConnectionHandler;
import cc.mi.gate.handler.IdentityServerTypeHandler;
import cc.mi.gate.task.CreateConnectionTask;
import cc.mi.gate.task.NoticeDestroyTask;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public enum GateServerManager {
	INSTANCE;
	
	private static final CustomLogger logger = CustomLogger.getLogger(GateServerManager.class);
	// 通道的id属性
	private static final AttributeKey<Integer> CHANNEL_ID = AttributeKey.valueOf("channel_id");
	// 是否是客户端通道
	private static final AttributeKey<Boolean> CLIENT_CHECK = AttributeKey.valueOf("client");
	
	
	// 只有网关服启动完成 和 中心服启动完成 才算完成
	// 判断服务器是否启动完成
	private boolean gateBootstrap = false;
	// 判断中心服自己是否启动好
	private boolean centerBootstrap = false;

	// 逻辑线程组 给它进行负载均衡
	// 还能保证每个客户端的消息一定是有序的
	private final ExecutorService[] clientInGroup;
	private final ExecutorService[] clientOutGroup;
	private static final int GROUP_SIZE = 4;
	private static final int MOD = GROUP_SIZE - 1;
	
	// 内部服务器channel列表
	private final Map<Integer, Channel> innerChannelHash = new ConcurrentHashMap<>();
	// 连接中心服的channel
	protected Channel centerChannel = null;
	
	// 句柄
	private final Handler[] handlers = new Handler[1<<6];
	
	// 客户端channe列表
	private final Map<Integer, Channel> channelHash = new ConcurrentHashMap<>();
	
	
	// 通道的整形惟一id
	private final AtomicInteger idGenerater = new AtomicInteger(0);
	
	private GateServerManager() {
		// 初始化线程组, 数量一定要2的幂, 否则会导致分配线程逻辑错误
		clientInGroup = new ExecutorService[GROUP_SIZE];
		for (int i = 0; i< GROUP_SIZE; ++ i) {
			clientInGroup[ i ] = Executors.newFixedThreadPool(1);
		}
		
		clientOutGroup = new ExecutorService[GROUP_SIZE];
		for (int i = 0; i< GROUP_SIZE; ++ i) {
			clientOutGroup[ i ] = Executors.newFixedThreadPool(1);
		}
		
		handlers[Opcodes.MSG_DESTROYCONNECTION] = new DestroyConnectionHandler();
		handlers[Opcodes.MSG_IDENTITYSERVERMSG] = new IdentityServerTypeHandler();
		handlers[Opcodes.MSG_SERVERSTARTFINISHMSG] = new CenterIsReadyHandler();
	}
	
	public void invokeHandler(Channel channel, Packet decoder) {
		Handler handle = handlers[decoder.getOpcode()];
		if (handle != null) {
			handle.handle(null, channel, decoder);
		}
	}
	
	/**
	 * 客户端连进来了
	 * @param channel
	 */
	public void onClientConnected(Channel channel) {
		int fd = idGenerater.incrementAndGet();
		channel.attr(CHANNEL_ID).set(fd);
		channel.attr(CLIENT_CHECK).set(true);
		channelHash.put(fd, channel);
		// 通知内部服务器, 有客户端连接进来了, (统一发到中心服, 让中心服再发给需要这条消息的服务器)
		this.noticeCreateConnected(fd, channel);
	}
	
	private void noticeCreateConnected(int fd, Channel channel) {
		String remoteAddress = channel.remoteAddress().toString().substring(1);
		int indx = remoteAddress.indexOf(':');
		if (indx < 0) {
			logger.devLog("err remoteaddress for {}", remoteAddress);
			channel.close();
			return;
		}
		String ip = remoteAddress.substring(0, indx);
		int port = Integer.parseInt(remoteAddress.substring(indx+1));
		this.submitTask(fd, new CreateConnectionTask(this.centerChannel, fd, ip, port));
	}
	
	/**
	 * 客户端断开连接了
	 * @param channel
	 */
	public void onClientDisconnected(Channel channel) {
		Attribute<Integer> attr = channel.attr(CHANNEL_ID);
		Integer fd = attr.get();
		if (fd != null) {
			channelHash.remove(fd);
		} else {
			logger.devLog("err for channel host = {}", channel.remoteAddress().toString());
		}
		
		//通知中心服 客户端断网了
		this.submitTask(fd, new NoticeDestroyTask(this.centerChannel, fd));
	}
	
	
	public boolean isBootstrap() {
		return this.gateBootstrap && this.centerBootstrap;
	}
	
	public void setCenterBootstrap(boolean bootstrap) {
		this.centerBootstrap = bootstrap;
		this.logIfAllServersEnabled();
	}
	
	private void logIfAllServersEnabled() {
		if (this.isBootstrap()) {
			logger.devLog("all server found");
		}
	}
	
	/**
	 * 只需要中心服, 登录服, 应用服, 场景服
	 */
	private void checkAllServerFound() {
		boolean vist = true;
		if (this.centerChannel == null) {
			vist = false;
		}
		if (!innerChannelHash.containsKey(IdentityConst.SERVER_TYPE_LOGIN)) {
			vist = false;
		}
		
		if (!innerChannelHash.containsKey(IdentityConst.SERVER_TYPE_APP)) {
			vist = false;
		}
		
		if (innerChannelHash.size() == 2) {
			vist = false;
		}
		if (!this.gateBootstrap && vist) {
			this.logIfAllServersEnabled();
		} else if (this.gateBootstrap && !vist) {
			logger.devLog("some servers is not found");
		}
		this.gateBootstrap = vist;
	}
	
	/**
	 * 内部服务器连进来了
	 * @param channel
	 */
	public void onInnerServerIdentity(Channel channel, int serverType) {
		int fd = serverType;
		if (serverType == IdentityConst.SERVER_TYPE_CENTER) {
			this.centerChannel = channel;
			logger.devLog("identity fd = {} serverType = {}", fd, serverType);
			return;
		}
		
		if (serverType == IdentityConst.SERVER_TYPE_SCENE) {
			boolean vist = false;
			for (int i = fd; i < 100; ++ i) {
				if (!innerChannelHash.containsKey(i)) {
					fd = i;
					vist = true;
					break;
				}
			}
			if (!vist)
				throw new RuntimeException(String.format("serverType = %d, has too many", serverType));
		}
		
		if (innerChannelHash.containsKey(fd)) {
			throw new RuntimeException(String.format("serverType = %d, has duplicate fd %d", serverType, fd));
		}
		
		channel.attr(CHANNEL_ID).set(fd);
		innerChannelHash.put(fd, channel);
		
		logger.devLog("identity fd = {} serverType = {}", fd, serverType);
		this.checkAllServerFound();
	}
	
	/**
	 * 内部服务器断开连接了
	 * @param channel
	 */
	public void onInnerServertDisconnected(Channel channel) {
		int fd = getChannelFd(channel);
		if (fd == IdentityConst.SERVER_TYPE_CENTER) {
			this.centerChannel = null;
			this.centerBootstrap = false;
		} else {
			innerChannelHash.remove(fd);
		}
		this.checkAllServerFound();
	}
	
	/**
	 * 
	 * @param direct 朝向, 是网关服进消息还是出消息,
	 * @param fd
	 * @param task
	 */
	public void submitTask(int direct, int fd, Task task) {
		ExecutorService[] group = TaskDirectConst.TASK_DIRECT_IN == direct ? clientInGroup : clientOutGroup;
		group[fd & MOD].submit(task);
	}
	
	/**
	 * 
	 * @param fd
	 * @param task
	 */
	public void submitTask(int fd, Task task) {
		this.submitTask(TaskDirectConst.TASK_DIRECT_OUT, fd, task);
	}
	
	public int getChannelFd(Channel channel) {
		return channel.attr(CHANNEL_ID).get();
	}
}