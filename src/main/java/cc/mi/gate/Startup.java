package cc.mi.gate;

import java.io.IOException;
import java.net.URL;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import cc.mi.gate.gateClient.GateClient;
import cc.mi.gate.net.Server;

public class Startup {
	private static final String GATE_SERVER = "gateServer";
	private static final String GATE_CLIENT = "gateClient";
	private static final String IP = "ip";
	private static final String PORT = "port";
	
	private static void loadConfig() throws NumberFormatException, Exception {
		Config cfg = new Config();
		URL url = Startup.class.getResource("/config.ini");
		Ini ini = new Ini();
        ini.setConfig(cfg);
        try {
        	// 加载配置文件  
        	ini.load(url);

        	Section section1 = ini.get(GATE_SERVER);
        	Section section2 = ini.get(GATE_CLIENT);
        	GateClient.start(section2.get(IP), Integer.parseInt(section2.get(PORT)));
        	Server.run(Integer.parseInt(section1.get(PORT)));
        	
        } catch (IOException e) {
        	e.printStackTrace();
	    }  
	}

	public static void main(String[] args) throws NumberFormatException, Exception {
		loadConfig();
	}

}
