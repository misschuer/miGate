package cc.mi.gate.task;

import cc.mi.core.generate.msg.DestroyConnection;
import cc.mi.core.task.base.AbstractTask;
import io.netty.channel.Channel;

public class NoticeDestroyTask extends AbstractTask {
	private final Channel channel;
	private final int fd;
	
	public NoticeDestroyTask(Channel channel, int fd) {
		this.channel = channel;
		this.fd		 =      fd;
	}
	
	@Override
	protected void doTask() {
		DestroyConnection dc = new DestroyConnection();
		dc.setFd(fd);
//		dc.setInternalDestFD(MsgConst.MSG_TO_CENTER);
		channel.writeAndFlush(dc);
	}
}
