package org.pretent.mrpc.register.zk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.pretent.mrpc.provider.Service;
import org.pretent.mrpc.register.ClientFactory;
import org.pretent.mrpc.register.ProtocolType;
import org.pretent.mrpc.register.Register;

/**
 * 
 * @author pretent
 *
 */
public class ZkRegister implements Register {

	private ZkClient zkclient;

	public ZkRegister() throws Exception {
		zkclient = (ZkClient) new ClientFactory().getClient(ProtocolType.DEFAULT);
	}

	public void register(Service service) throws Exception {
		preRegister();
		if (zkclient.exists(ZkPath.ZK_MAIN_PATH + "/" + service.getClassName())) {
			Object data = zkclient.readData(ZkPath.ZK_MAIN_PATH + "/" + service.getClassName());
			List<Service> list = (List<Service>) data;
			list.add(service);
			zkclient.writeData(ZkPath.ZK_MAIN_PATH + "/" + service.getClassName(), list);
		} else {
			List<Service> list = new ArrayList<Service>();
			list.add(service);
			zkclient.create(ZkPath.ZK_MAIN_PATH + "/" + service.getClassName(), list, CreateMode.EPHEMERAL);
		}
	}

	public void register(Set<Service> services) throws Exception {
		Iterator<Service> iter = services.iterator();
		while (iter.hasNext()) {
			Service service = iter.next();
			register(service);
		}
	}

	private void preRegister() {
		if (!zkclient.exists(ZkPath.ZK_MAIN_PATH)) {
			zkclient.createPersistent(ZkPath.ZK_MAIN_PATH);
		}
	}
}
