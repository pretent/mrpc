package org.pretent.mrpc.provider;

import org.pretent.mrpc.RegisterConfig;

public interface ServiceFactory {

    /**
     * 获取服务
     *
     * @return
     * @throws Exception
     */
    public Service getService(String serviceName) throws Exception;

    public Service getService(String serviceName, RegisterConfig registerConfig) throws Exception;



}
