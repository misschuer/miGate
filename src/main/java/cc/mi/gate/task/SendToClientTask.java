package cc.mi.gate.task;

import cc.mi.core.packet.Packet;
import cc.mi.core.task.base.AbstractCoderTask;
import io.netty.channel.Channel;

public class SendToClientTask extends AbstractCoderTask {
	private final Channel channel;
	public SendToClientTask(Channel channel, Packet coder) {
		super(coder);
		this.channel = channel;
	}
	
	@Override
	protected void doTask() {
		this.channel.writeAndFlush(coder);
	}
}
