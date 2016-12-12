package org.pretent.mrpc;

import java.util.HashMap;
import java.util.Map;

/**
 * date: 16/12/12 21:47
 * author: PRETENT
 **/
public abstract class AbstractProvider implements Provider{

    protected static Map<String, Object> ALL_SERVICE = new HashMap<String, Object>();

    public static Map<String, Object> getAllExportedService() {
        return ALL_SERVICE;
    }
}
