package cc.mi.gate.handler;

import cc.mi.core.coder.Packet;
import cc.mi.core.handler.AbstractHandler;
import cc.mi.core.server.ServerContext;
import io.netty.channel.Channel;

public class DestroyConnectionHandler extends AbstractHandler {

	@Override
	public void handle(ServerContext player, Channel channel, Packet decoder) {
//		DestroyConnection dc = (DestroyConnection)decoder;
//		GateSystemManager.destroyConnection(dc.getFd());
	}
}
