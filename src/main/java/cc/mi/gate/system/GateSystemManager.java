package cc.mi.gate.system;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import cc.mi.core.coder.Coder;
import cc.mi.core.constance.IdentityConst;
import cc.mi.core.generate.Opcodes;
import cc.mi.core.generate.msg.ServerRegIdentity;
import cc.mi.core.handler.Handler;
import cc.mi.core.task.SendToCenterTask;
import cc.mi.core.task.base.Task;
import cc.mi.gate.handler.DestroyConnectionHandler;
import cc.mi.gate.task.NoticeDestroyTask;
import cc.mi.gate.task.SendCreateConnectionTask;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class GateSystemManager {
	// 通道的id属性
	private static final AttributeKey<Integer> CHANNEL_ID;
	
	// 单线程逻辑
	private static final ExecutorService executor;
	
	// 通道列表
	private static final Map<Integer, Channel> channelHash;
	// 通道的整形惟一id
	private static final AtomicInteger idGenerater;
	
	// 句柄
	private static final Handler[] handlers = new Handler[1<<12];
	
	private static Channel centerChannel = null;
		
	static {
		channelHash = new ConcurrentHashMap<>();
		CHANNEL_ID	= AttributeKey.valueOf("channel_id");
		idGenerater = new AtomicInteger(0);
		executor = Executors.newSingleThreadExecutor();
		
		handlers[Opcodes.MSG_DESTROYCONNECTION] = new DestroyConnectionHandler();
	}
	
	public static Handler getHandler(int opcode) {
		return handlers[opcode];
	}
	
	public static Channel getCenterChannel() {
		return centerChannel;
	}
	
	public static void setCenterChannel(Channel channel) {
		if (centerChannel == null || !centerChannel.isActive()) {
			centerChannel = channel;
		}
	}
	
	// 提交客户端过来的任务
	public static void submitTask(Task task) {
		executor.submit(task);
	}
	
	public static void channelActived(Channel channel) {
		int id = idGenerater.incrementAndGet();
		channel.attr(CHANNEL_ID).set(id);
		channelHash.put(id, channel);
		
		// 发送给登陆服信息
		InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
		submitTask(
			new SendCreateConnectionTask(
				centerChannel, 
				id, 
				remoteAddress.getAddress().getHostAddress(), 
				(short) remoteAddress.getPort()
			)
		);
	}
	
	public static int getChannelId(Channel channel) {
		return channel.attr(CHANNEL_ID).get();
	}
		
	public static void sendToClient(int id, Coder coder) {
		Channel channel = channelHash.get(id);
		if (channel == null || !channel.isActive()) {
			return;
		}
		channel.writeAndFlush(coder);
	}
	
	public static void channelInactived(Channel channel) {
		Attribute<Integer> attr = channel.attr(CHANNEL_ID);
		int id = attr.get();
		attr.set(null);
		channelHash.remove(id);
		
		//通知中心服 客户端断网了
		submitTask(new NoticeDestroyTask(GateSystemManager.centerChannel, id));
	}
	
	public static void destroyConnection(int fd) {
		Channel channel = channelHash.get(fd);
		if (channel != null && channel.isActive()) {
			channel.close();
		}
	}
	
	public static void regToCenter() {
		ServerRegIdentity identity = new ServerRegIdentity();
		identity.setInternalDestFD(IdentityConst.IDENDITY_CENTER);
		identity.setIdentity(IdentityConst.IDENDITY_GATE);
		submitTask(new SendToCenterTask(centerChannel, identity));
	}
}
