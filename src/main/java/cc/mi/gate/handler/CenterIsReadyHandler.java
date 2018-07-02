package cc.mi.gate.handler;

import cc.mi.core.generate.msg.ServerStartFinishMsg;
import cc.mi.core.handler.AbstractHandler;
import cc.mi.core.packet.Packet;
import cc.mi.core.server.ServerContext;
import cc.mi.gate.system.GateSystemManager;
import io.netty.channel.Channel;

public class CenterIsReadyHandler extends AbstractHandler {
	@Override
	public void handle(ServerContext player, Channel channel, Packet decoder) {
		ServerStartFinishMsg msg = (ServerStartFinishMsg)decoder;
		GateSystemManager.INSTANCE.setCenterBootstrap(msg.getBootstrap());
	}
}
