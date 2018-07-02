package cc.mi.gate.task;

import cc.mi.core.coder.Packet;
import cc.mi.core.task.base.AbstractCoderTask;
import cc.mi.gate.system.GateSystemManager;
import io.netty.channel.Channel;

public class DealInnerDataTask extends AbstractCoderTask {
	private final Channel channel;
	public DealInnerDataTask(Channel channel, Packet coder) {
		super(coder);
		this.channel = channel;
	}
	
	@Override
	protected void doTask() {
		GateSystemManager.INSTANCE.invokeHandler(this.channel, coder);
	}

}
