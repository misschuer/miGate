package cc.mi.gate;

import cc.mi.core.constance.IdentityConst;
import cc.mi.core.log.CustomLogger;
import cc.mi.core.net.ServerCore;
import cc.mi.gate.config.ServerConfig;
import cc.mi.gate.net.GateHandler;
import cc.mi.gate.net.GateLocalHandler;

public class Startup {
	static final CustomLogger logger = CustomLogger.getLogger(Startup.class);
	
	private static void start() throws NumberFormatException, Exception {
		ServerConfig.loadConfig();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						ServerCore.INSTANCE.run(ServerConfig.getOuterPort(), new GateHandler(), IdentityConst.SERVER_TYPE_GATE);
					} catch (Exception e) {
					} finally {
						logger.devLog("监听客户端端口发生错误,系统将在1秒钟后重新执行");
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, "gate-for-clients").start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						ServerCore.INSTANCE.run(ServerConfig.getInnerPort(), new GateLocalHandler(), IdentityConst.SERVER_TYPE_GATE);
					} catch (Exception e) {
					} finally {
						logger.devLog("监听内部服务器端口发生错误,系统将在1秒钟后重新执行");
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, "gate-for-inner").start();
	}

	public static void main(String[] args) throws NumberFormatException, Exception {
		start();
	}
}
