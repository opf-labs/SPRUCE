package uk.bl.spruce;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	
	private Map<String, String> metadata;
	public final String NA = "Not available";
	
	public Entity() {
		metadata = new HashMap<String, String>();
	}
	
	public void addField(String name, String value) {
		metadata.put(name.toLowerCase(), value);
	}

	public String getValue(String fieldname) {
		String key = fieldname.toLowerCase();
		if ( metadata.containsKey(key) ) {
			String s = metadata.get(key);
			if ( s.length() == 0 ) {
				return NA;
			} else {
				return s;
			}
		} else {
			return NA;
		}
	}	
}
