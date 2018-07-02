package cc.mi.gate.config;

import java.io.IOException;
import java.net.URL;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import cc.mi.core.constance.NetConst;

public class ServerConfig {
	private static final String OUTER = "outer";
	private static final String INNER = "inner";
	private static int inner_port;
	private static int outer_port;
	
	public static void loadConfig() throws NumberFormatException, Exception {
		Config cfg = new Config();
		URL url = ServerConfig.class.getResource("/config.ini");
		Ini ini = new Ini();
        ini.setConfig(cfg);
        try {
        	// 加载配置文件  
        	ini.load(url);

        	Section section1 = ini.get(INNER);
        	inner_port = Integer.parseInt(section1.get(NetConst.PORT));
        	
        	Section section2 = ini.get(OUTER);
        	outer_port = Integer.parseInt(section2.get(NetConst.PORT));
        	
        } catch (IOException e) {
        	e.printStackTrace();
	    }  
	}

	public static int getInnerPort() {
		return inner_port;
	}
	
	public static int getOuterPort() {
		return outer_port;
	}
}
