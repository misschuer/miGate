package cc.mi.gate.net;

import cc.mi.core.constance.TaskDirectConst;
import cc.mi.core.handler.ChannelHandlerGenerator;
import cc.mi.core.log.CustomLogger;
import cc.mi.core.packet.Packet;
import cc.mi.gate.server.GateServerManager;
import cc.mi.gate.task.DealInnerDataTask;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GateLocalHandler extends SimpleChannelInboundHandler<Packet> implements ChannelHandlerGenerator {
	static final CustomLogger logger = CustomLogger.getLogger(GateLocalHandler.class);
	
	public void channelActive(final ChannelHandlerContext ctx) {
		
	}
	
	@Override
	public void channelRead0(final ChannelHandlerContext ctx, final Packet coder) throws Exception {
		int fd = coder.getFD();
		if (fd > 0) {
			// send to client
		} else {
			GateServerManager.INSTANCE.submitTask(TaskDirectConst.TASK_DIRECT_OUT, 0, new DealInnerDataTask(ctx.channel(), coder));
		}
	}
	
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		GateServerManager.INSTANCE.onInnerServertDisconnected(ctx.channel());
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
