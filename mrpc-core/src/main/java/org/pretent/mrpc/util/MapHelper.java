package org.pretent.mrpc.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class MapHelper {

	private static final Logger LOGGER = Logger.getLogger(MapHelper.class);

	public static void print(Map map) {
		Set<Map.Entry> sets = map.entrySet();
		Iterator<Map.Entry> iter = sets.iterator();
		while (iter.hasNext()) {
			Map.Entry entry = iter.next();
			LOGGER.info(entry.getKey() + ":" + entry.getValue());
		}
	}

}
