package org.pretent.mrpc.support.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * date: 16/12/10 17:05
 * author: PRETENT
 **/
@ConfigurationProperties(prefix = "mrpc", locations = {"classpath:application.properties"})
public class ProtocolConfig {

    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
