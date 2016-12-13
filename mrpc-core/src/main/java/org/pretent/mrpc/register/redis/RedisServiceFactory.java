package org.pretent.mrpc.register.redis;

import org.apache.log4j.Logger;
import org.pretent.mrpc.RegisterConfig;
import org.pretent.mrpc.provider.Service;
import org.pretent.mrpc.provider.ServiceFactory;
import org.pretent.mrpc.register.ClientFactory;
import org.pretent.mrpc.register.ProtocolType;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Random;

/**
 * @author pretent
 */
public class RedisServiceFactory implements ServiceFactory {

    private static final Logger LOGGER = Logger.getLogger(RedisServiceFactory.class);

    public Service getService(String serviceName) throws Exception {
        Jedis jedis = (Jedis) new ClientFactory().getClient(ProtocolType.REDIS);
        if (!jedis.exists(RedisKey.REDIS_MAIN_KEY + "::" + serviceName)) {
            throw new Exception("service is not available.");
        }
        List<String> list = jedis.lrange(RedisKey.REDIS_MAIN_KEY + "::" + serviceName, 0, -1);
        if (list == null) {
            throw new Exception("service is not available.");
        }
        LOGGER.debug("service is only one return it.");
        String serviceInfo = null;
        if (list.size() == 1) {
            serviceInfo = list.get(0);
        }
        LOGGER.debug("service is more than one,return random.");
        serviceInfo = list.get(new Random().nextInt(list.size()));
        // org.pretent.service.UserService::host::port
        String[] arr = serviceInfo.split("::");
        Service service = new Service(arr[0], arr[1], Integer.parseInt(arr[2]));
        return service;
    }

    public Service getService(String serviceName, RegisterConfig registerConfig) throws Exception {
        Jedis jedis = (Jedis) new ClientFactory(registerConfig.getAddress()).getClient(ProtocolType.REDIS);
        if (!jedis.exists(RedisKey.REDIS_MAIN_KEY + "::" + serviceName)) {
            throw new Exception("service is not available.");
        }
        List<String> list = jedis.lrange(RedisKey.REDIS_MAIN_KEY + "::" + serviceName, 0, -1);
        if (list == null) {
            throw new Exception("service is not available.");
        }
        LOGGER.debug("service is only one return it.");
        String serviceInfo = null;
        if (list.size() == 1) {
            serviceInfo = list.get(0);
        }
        LOGGER.debug("service is more than one,return random.");
        serviceInfo = list.get(new Random().nextInt(list.size()));
        // org.pretent.service.UserService::host::port
        String[] arr = serviceInfo.split("::");
        Service service = new Service(arr[0], arr[1], Integer.parseInt(arr[2]));
        return service;
    }
}
