package org.pretent.mrpc.register.zk;

import java.util.List;
import java.util.Random;

import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;
import org.pretent.mrpc.provider.Service;
import org.pretent.mrpc.provider.ServiceFactory;
import org.pretent.mrpc.register.ClientFactory;
import org.pretent.mrpc.register.ProtocolType;

/**
 * 
 * @author pretent
 *
 */
public class ZkServiceFactory implements ServiceFactory {

	private static final Logger LOGGER = Logger.getLogger(ZkServiceFactory.class);

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public Service getService(String serviceName) throws Exception {
		ZkClient zkClient = (ZkClient) new ClientFactory().getClient(ProtocolType.ZOOKEEPER);
		if (!zkClient.exists(ZkPath.ZK_MAIN_PATH + "/" + serviceName)) {
			throw new Exception("service is not available.");
		}
		Object data = zkClient.readData(ZkPath.ZK_MAIN_PATH + "/" + serviceName);
		List<Service> list = (List<Service>) data;
		if (list == null) {
			throw new Exception("service is not available.");
		}
		LOGGER.debug("service is only one return it.");
		if (list.size() == 1) {
			return list.get(0);
		}
		LOGGER.debug("service is more than one,return random.");
		return list.get(new Random().nextInt(list.size()));
	}

}
