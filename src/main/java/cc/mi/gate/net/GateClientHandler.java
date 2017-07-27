package cc.mi.gate.net;

import cc.mi.core.coder.Coder;
import cc.mi.gate.system.SystemManager;
import cc.mi.gate.task.DealInnerDataTask;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GateClientHandler extends SimpleChannelInboundHandler<Coder> {
	
	public void channelActive(final ChannelHandlerContext ctx) {
		System.out.println("connect to center success");
		SystemManager.setCenterChannel(ctx.channel());
		SystemManager.regToCenter();
	}
	
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		
	}
	
	@Override
	public void channelRead0(final ChannelHandlerContext ctx, final Coder coder) throws Exception {
		int id = coder.getId();
		if (id > 0) {
			SystemManager.sendToClient(id, coder);
		} else {
			SystemManager.submitTask(new DealInnerDataTask(coder));
		}
	}
	
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("gate client inactive");
		ctx.fireChannelInactive();
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
		throwable.printStackTrace();
		ctx.close();
	}
}
