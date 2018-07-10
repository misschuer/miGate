package cc.mi.gate.net;

import cc.mi.core.handler.ChannelHandlerGenerator;
import cc.mi.core.log.CustomLogger;
import cc.mi.core.packet.Packet;
import cc.mi.gate.server.GateServerManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class GateHandler extends SimpleChannelInboundHandler<Packet> implements ChannelHandlerGenerator {
	static final CustomLogger logger = CustomLogger.getLogger(GateHandler.class);
	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		GateServerManager.INSTANCE.onClientConnected(ctx.channel());
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				ctx.close();
				return;
			} else if (event.state() == IdleState.WRITER_IDLE) {
				
			} else if (event.state() == IdleState.ALL_IDLE) {
				
			}
		}
		ctx.fireUserEventTriggered(evt);
	}
	
	@Override
	public void channelRead0(final ChannelHandlerContext ctx, final Packet msg) throws Exception {
		GateServerManager.INSTANCE.sendToCenter(ctx.channel(), msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
	 @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		 logger.devLog("a client disconneted");
		 GateServerManager.INSTANCE.onClientDisconnected(ctx.channel());
		 ctx.fireChannelInactive();
    }

	@Override
	public ChannelHandler newChannelHandler() {
		return new GateHandler();
	}
}
