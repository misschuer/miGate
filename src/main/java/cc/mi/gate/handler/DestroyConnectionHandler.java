package cc.mi.gate.handler;

import cc.mi.core.handler.HandlerImpl;
import cc.mi.core.packet.Packet;
import cc.mi.core.server.ServerContext;
import io.netty.channel.Channel;

public class DestroyConnectionHandler extends HandlerImpl {

	@Override
	public void handle(ServerContext player, Channel channel, Packet decoder) {
//		DestroyConnection dc = (DestroyConnection)decoder;
//		GateSystemManager.destroyConnection(dc.getFd());
	}
}
