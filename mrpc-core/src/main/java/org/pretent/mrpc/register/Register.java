package org.pretent.mrpc.register;

import java.util.Set;

import org.pretent.mrpc.provider.Service;

/**
 * 服务注册
 * 
 * @author pretent
 *
 */
public interface Register {

	void register(Service service) throws Exception;

	void register(Set<Service> services) throws Exception;
}
