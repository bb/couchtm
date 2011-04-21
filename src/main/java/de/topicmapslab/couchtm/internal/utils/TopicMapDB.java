package de.topicmapslab.couchtm.internal.utils;

import java.util.Map;
import java.util.Set;
import org.tmapi.core.Locator;
import de.topicmapslab.couchtm.internal.utils.CollectionFactory;
import org.json.*;

/**
 * Class to manage the locator to topic map id map
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 *
 */
public class TopicMapDB {
	private Map<Locator, String> topicMaps; 
	private String rev;
	
	public TopicMapDB() {
		topicMaps = CollectionFactory.createMap();
		rev = null;
	}
	
	public TopicMapDB(Map<Locator, String> map, String rev) {
		topicMaps = map;
		this.rev = rev;
	}
	
	/**
	 * Removes the entry associates with the locator
	 * 
	 * @param loc
	 */
	public void remove(Locator loc) {
		topicMaps.remove(loc);
	}
	
	/**
	 * Puts a new locator-id pair in the map
	 * 
	 * @param loc
	 * @param tm
	 */
	public void put(Locator loc, String tm) {
		topicMaps.put(loc, tm);
	}
	
	/**
	 * Returns the JSON String representation of the class
	 * 
	 * @return
	 */
	public String toJSON() {
		String str = "";
		JSONObject obj = new JSONObject();
		try {
			if(rev != null) obj.put("_rev", rev);
			for(Locator loc : topicMaps.keySet()) {
				obj.put(loc.getReference(), topicMaps.get(loc));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		str = obj.toString();
		return str;
	}
	
	/**
	 * Sets the revision number
	 * 
	 * @param rev
	 */
	public void setRev(String rev) {
		this.rev = rev;
	}
	
	/**
	 * Returns the id for a given locator
	 * 
	 * @param loc
	 * @return
	 */
	public String get(Locator loc) {
		return topicMaps.get(loc);
	}
	
	/**
	 * Returns the set of keys (locators)
	 * 
	 * @return
	 */
	public Set<Locator> keySet() {
		return topicMaps.keySet();
	}
}
