package cc.mi.gate.handler;

import cc.mi.core.generate.msg.ServerStartFinishMsg;
import cc.mi.core.handler.HandlerImpl;
import cc.mi.core.packet.Packet;
import cc.mi.core.server.ServerContext;
import cc.mi.gate.server.GateServerManager;
import io.netty.channel.Channel;

public class CenterIsReadyHandler extends HandlerImpl {
	@Override
	public void handle(ServerContext nil, Channel channel, Packet decoder) {
		ServerStartFinishMsg msg = (ServerStartFinishMsg)decoder;
		GateServerManager.INSTANCE.setCenterBootstrap(msg.getBootstrap());
	}
}
