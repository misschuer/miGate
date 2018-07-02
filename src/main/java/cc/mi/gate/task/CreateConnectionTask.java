package cc.mi.gate.task;

import cc.mi.core.generate.msg.CreateConnection;
import cc.mi.core.task.base.AbstractTask;
import io.netty.channel.Channel;

public class CreateConnectionTask extends AbstractTask {
	private final Channel channel;
	private final int fd;
	private final String ip;
	private final int port;
	
	public CreateConnectionTask(Channel channel, int fd, String ip, int port) {
		this.channel = channel;
		this.fd      =      fd;
		this.ip      =      ip;
		this.port    =    port;
	}
	
	@Override
	protected void doTask() {
		CreateConnection cc = new CreateConnection();
		cc.setFd(this.fd);
		cc.setRemoteIp(this.ip);
		cc.setRemotePort(this.port);
		channel.writeAndFlush(cc);
	}

}
