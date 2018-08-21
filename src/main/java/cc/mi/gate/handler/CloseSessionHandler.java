package cc.mi.gate.handler;

import cc.mi.core.generate.msg.CloseSession;
import cc.mi.core.handler.HandlerImpl;
import cc.mi.core.packet.Packet;
import cc.mi.core.server.ServerContext;
import cc.mi.gate.server.GateServerManager;
import io.netty.channel.Channel;

public class CloseSessionHandler extends HandlerImpl {

	@Override
	public void handle(ServerContext nil, Channel channel, Packet decoder) {
		CloseSession packet = (CloseSession) decoder;
		int fd = packet.getFd();
		GateServerManager.INSTANCE.closeSession(fd, packet.getReasonType());
	}

}
