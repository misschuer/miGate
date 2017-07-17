package cc.mi.gate.gateClient;

import cc.mi.core.coder.Coder;
import cc.mi.gate.system.SystemManager;
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
		//TODO: 如果是网关处理的 在这里进行处理
		
		// 否则就发送到客户端
		int id = coder.getId();
		coder.setId(0);
		SystemManager.sendToClient(id, coder);
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
