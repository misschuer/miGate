package cc.mi.gate.task;

import cc.mi.core.coder.Coder;
import cc.mi.core.handler.Handler;
import cc.mi.core.task.base.AbstractCoderTask;
import cc.mi.gate.system.GateSystemManager;

public class DealInnerDataTask extends AbstractCoderTask {
	public DealInnerDataTask(Coder coder) {
		super(coder);
	}
	
	@Override
	protected void doTask() {
		Handler handler = GateSystemManager.getHandler(coder.getOpcode());
		handler.handle(null, GateSystemManager.getCenterChannel(), coder);
	}

}
