package cc.mi.gate.net;

import cc.mi.core.coder.Coder;
import cc.mi.gate.system.SystemManager;
import cc.mi.gate.task.SendToCenterTask;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerInboundHandler extends SimpleChannelInboundHandler<Coder> {
	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		System.out.println("server建立连接" + System.currentTimeMillis());
		SystemManager.channelActived(ctx.channel());
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				ctx.close();
			} else if (event.state() == IdleState.WRITER_IDLE) {
				
			} else if (event.state() == IdleState.ALL_IDLE) {
				
			}
		}
	}
	
	@Override
	public void channelRead0(final ChannelHandlerContext ctx, final Coder msg) throws Exception {
		msg.setId(SystemManager.getChannelId(ctx.channel()));
		msg.setInternalDestFD(-1);
		SystemManager.submitTask(new SendToCenterTask(msg));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
	 @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		 System.out.println("客户端断开连接");
		 SystemManager.channelInactived(ctx.channel());
		 ctx.fireChannelInactive();
    }
}
