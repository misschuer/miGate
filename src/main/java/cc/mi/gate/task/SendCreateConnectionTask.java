package cc.mi.gate.task;

import cc.mi.core.constance.MsgConst;
import cc.mi.core.generate.msg.CreateConnection;
import cc.mi.core.task.base.AbstractTask;
import io.netty.channel.Channel;

public class SendCreateConnectionTask extends AbstractTask {
	private final Channel channel;
	private final int fd;
	private final String ip;
	private final short port;
	
	public SendCreateConnectionTask(Channel channel, int fd, String ip, short port) {
		this.channel = channel;
		this.fd      =      fd;
		this.ip      =      ip;
		this.port    =    port;
	}
	
	@Override
	protected void doTask() {
		CreateConnection cc = new CreateConnection();
		cc.setFd(this.fd);
		cc.setInternalDestFD(MsgConst.MSG_TO_CENTER);
		cc.setRemoteIp(this.ip);
		cc.setRemotePort(this.port);
		
		channel.writeAndFlush(cc);
	}

}
