package cc.mi.gate;

import cc.mi.core.net.ClientCore;
import cc.mi.core.net.ServerCore;
import cc.mi.gate.config.ServerConfig;
import cc.mi.gate.net.GateClientHandler;
import cc.mi.gate.net.GateServerHandler;

public class Startup {

	private static void start() throws NumberFormatException, Exception {
		ServerConfig.loadConfig();
		ClientCore.start(ServerConfig.getIp(), ServerConfig.getGateClientPort(), new GateClientHandler(), false);
    	ServerCore.run(ServerConfig.getGateServerPort(), new GateServerHandler());
	}

	public static void main(String[] args) throws NumberFormatException, Exception {
		start();
	}
}
