package cc.mi.gate.task;

import cc.mi.core.coder.Coder;
import cc.mi.core.task.AbstractTask;
import cc.mi.gate.system.SystemManager;

public class SendToCenterTask extends AbstractTask {
	private final Coder coder;
	public SendToCenterTask(Coder coder) {
		this.coder = coder;
	}
	
	@Override
	protected void doTask() {
		SystemManager.getCenterChannel().writeAndFlush(coder);
	}

}
