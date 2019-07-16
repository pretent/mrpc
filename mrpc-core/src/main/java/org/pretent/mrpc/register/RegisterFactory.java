package org.pretent.mrpc.register;

import org.pretent.mrpc.register.redis.RedisRegister;
import org.pretent.mrpc.register.zk.ZkRegister;
import org.pretent.mrpc.util.ProtocolUtils;

public class RegisterFactory {

    private ProtocolType protocol;

    public RegisterFactory() {
        protocol = ProtocolUtils.getProtocol();
    }

    public Register getRegister() throws Exception {
        return getRegister(protocol);
    }

    public Register getRegister(ProtocolType protocol) throws Exception {
        switch (protocol) {
            case ZOOKEEPER: {
                return new ZkRegister();
            }
            case REDIS: {
                return new RedisRegister();
            }
            case MULTICAST: {
                throw new Exception("not implements");
            }
            default: {
                return new ZkRegister();
            }
        }
    }

    public Register getRegister(String address, ProtocolType protocol) throws Exception {
        switch (protocol) {
            case ZOOKEEPER: {
                return new ZkRegister(address);
            }
            case REDIS: {
                return new RedisRegister(address);
            }
            case MULTICAST: {
                throw new Exception("not implementats");
            }
            default: {
                return new ZkRegister(address);
            }
        }
    }

    public ProtocolType getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolType protocol) {
        this.protocol = protocol;
    }

}
