package cc.mi.gate.config;

import java.io.IOException;
import java.net.URL;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import cc.mi.core.constance.NetConst;

public class ServerConfig {
	private static final String GATE_SERVER = "gateServer";
	private static final String GATE_CLIENT = "gateClient";
	private static String ip;
	private static int gateServerPort;
	private static int gateClientPort;
	
	public static void loadConfig() throws NumberFormatException, Exception {
		Config cfg = new Config();
		URL url = ServerConfig.class.getResource("/config.ini");
		Ini ini = new Ini();
        ini.setConfig(cfg);
        try {
        	// 加载配置文件  
        	ini.load(url);

        	Section section1 = ini.get(GATE_SERVER);
        	gateServerPort = Integer.parseInt(section1.get(NetConst.PORT));
        	
        	Section section2 = ini.get(GATE_CLIENT);
        	ip = section2.get(NetConst.IP);
        	gateClientPort = Integer.parseInt(section2.get(NetConst.PORT));
        } catch (IOException e) {
        	e.printStackTrace();
	    }  
	}

	public static String getIp() {
		return ip;
	}

	public static int getGateServerPort() {
		return gateServerPort;
	}

	public static int getGateClientPort() {
		return gateClientPort;
	}
}
