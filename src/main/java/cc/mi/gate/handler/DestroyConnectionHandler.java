package cc.mi.gate.handler;

import cc.mi.core.coder.Coder;
import cc.mi.core.generate.msg.DestroyConnection;
import cc.mi.core.handler.AbstractHandler;
import cc.mi.core.server.ServerContext;
import cc.mi.gate.system.SystemManager;
import io.netty.channel.Channel;

public class DestroyConnectionHandler extends AbstractHandler {

	@Override
	public void handle(ServerContext player, Channel channel, Coder decoder) {
		DestroyConnection dc = (DestroyConnection)decoder;
		SystemManager.destroyConnection(dc.getFd());
	}
}
