package cc.mi.gate.task;

import cc.mi.core.packet.Packet;
import cc.mi.core.task.base.AbstractCoderTask;
import cc.mi.gate.server.GateServerManager;
import io.netty.channel.Channel;

public class DealInnerDataTask extends AbstractCoderTask {
	private final Channel channel;
	public DealInnerDataTask(Channel channel, Packet coder) {
		super(coder);
		this.channel = channel;
	}
	
	@Override
	protected void doTask() {
		GateServerManager.INSTANCE.invokeHandler(this.channel, coder);
	}

}
