package org.pretent.mrpc.support.bean;

import org.apache.log4j.Logger;
import org.pretent.mrpc.Provider;

/**
 * date: 16/12/11 10:12
 * author: PRETENT
 **/
public class ExportBean {

    private static Logger LOGGER = Logger.getLogger(ExportBean.class);

    public synchronized void export(Provider provider, Object bean) {
        try {
            System.err.println("0000000000000000000发布服务:" + bean.getClass().getName());
            provider.export(bean);
            if (!provider.started()) {
                try {
                    LOGGER.debug("=================mrpc=================服务启动:");
                    provider.start();
                } catch (Exception e) {
                    LOGGER.error(e.getStackTrace());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
