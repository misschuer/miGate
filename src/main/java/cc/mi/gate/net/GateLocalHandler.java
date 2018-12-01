package cc.mi.gate.net;

import cc.mi.core.handler.ChannelHandlerGenerator;
import cc.mi.core.log.CustomLogger;
import cc.mi.core.packet.Packet;
import cc.mi.gate.server.GateServerManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GateLocalHandler extends SimpleChannelInboundHandler<Packet> implements ChannelHandlerGenerator {
	static final CustomLogger logger = CustomLogger.getLogger(GateLocalHandler.class);
	
	public void channelActive(final ChannelHandlerContext ctx) {
		
	}
	
	@Override
	public void channelRead0(final ChannelHandlerContext ctx, final Packet coder) throws Exception {
		int fd = coder.getBaseFd();
		if (fd > 0) {
			GateServerManager.INSTANCE.sendToClient(coder);
		} else {
			GateServerManager.INSTANCE.dealInnerData(ctx.channel(), coder);
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
