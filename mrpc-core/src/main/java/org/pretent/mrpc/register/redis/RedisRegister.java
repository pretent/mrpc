package org.pretent.mrpc.register.redis;


import org.pretent.mrpc.provider.Service;
import org.pretent.mrpc.register.ClientFactory;
import org.pretent.mrpc.register.ProtocolType;
import org.pretent.mrpc.register.Register;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * date: 16/12/8 21:22
 * author: PRETENT
 **/
public class RedisRegister implements Register {
    private Jedis jedis;
    public  RedisRegister()throws Exception {
        jedis=(Jedis)new ClientFactory().getClient(ProtocolType.REDIS);
    }

    public void register(Service service) throws Exception {
        List<String> list=null;
        if (jedis.exists(RedisKey.REDIS_MAIN_KEY+"::"+service.getClassName())) {
            list = jedis.lrange(RedisKey.REDIS_MAIN_KEY+"::"+service.getClassName(),0,-1);
        }else {
            list =new ArrayList<String>();

        }
        String serviceInfo=service.getClassName()+"::"+service.getIp()+"::"+service.getPort();
        list.add(serviceInfo);
        jedis.lpush(RedisKey.REDIS_MAIN_KEY+"::"+service.getClassName(),serviceInfo);

    }

    public void register(Set<Service> services) throws Exception {
            Iterator<Service> iter = services.iterator();
            while (iter.hasNext()) {
                Service service = iter.next();
                register(service);
            }
    }
}
