package cc.mi.gate.net;

import cc.mi.core.coder.Packet;
import cc.mi.core.handler.ChannelHandlerGenerator;
import cc.mi.core.log.CustomLogger;
import cc.mi.gate.system.GateSystemManager;
import cc.mi.gate.task.DealInnerDataTask;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GateLocalHandler extends SimpleChannelInboundHandler<Packet> implements ChannelHandlerGenerator {
	static final CustomLogger logger = CustomLogger.getLogger(GateLocalHandler.class);
	
	public void channelActive(final ChannelHandlerContext ctx) {
		logger.devLog("an inner server connet to gate");
	}
	
	@Override
	public void channelRead0(final ChannelHandlerContext ctx, final Packet coder) throws Exception {
		int fd = coder.getFD();
		if (fd > 0) {
			// send to client
		} else {
			GateSystemManager.INSTANCE.submitTask(new DealInnerDataTask(ctx.channel(), coder));
		}
	}
	
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.devLog("an inner server is disconneted");
		ctx.fireChannelInactive();
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
		throwable.printStackTrace();
		ctx.close();
	}

	@Override
	public ChannelHandler newChannelHandler() {
		return new GateLocalHandler();
	}
}