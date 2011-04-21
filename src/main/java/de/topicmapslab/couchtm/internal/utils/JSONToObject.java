/*
 * Copyright 2009 Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.topicmapslab.couchtm.internal.utils;

import org.json.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.ITopicMapSystem;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.IVariant;
import de.topicmapslab.couchtm.internal.api.IOccurrence;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.IReifiable;
import de.topicmapslab.couchtm.core.ConstructImpl;
import de.topicmapslab.couchtm.core.LocatorImpl;
import de.topicmapslab.couchtm.core.TopicImpl;
import de.topicmapslab.couchtm.core.TopicMapImpl;
import de.topicmapslab.couchtm.core.ReifiableImpl;
import de.topicmapslab.couchtm.core.NameImpl;
import de.topicmapslab.couchtm.core.VariantImpl;
import de.topicmapslab.couchtm.core.OccurrenceImpl;
import de.topicmapslab.couchtm.core.RoleImpl;
import de.topicmapslab.couchtm.core.AssociationImpl;
import de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager;

/**
 * This class preprocesses results of database queries.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class JSONToObject {
	
	/**
	 * Database names are converted from a <tt>JSONArray</tt> to a set.
	 * 
	 * @param result
	 * @returnset database names
	 */
	public static Set<String> toSet(String result) {
		Set<String> set = new HashSet<String>();
		try {
			JSONArray dbs = new JSONArray(new JSONTokener(result));
			for(int i = 0; i < dbs.length(); i++) set.add(dbs.getString(i));
			return set;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * DBup message is returned as <tt>boolean</tt>.
	 * 
	 * @param result
	 * @return boolean
	 */
	public static boolean dbUp(String result) {
		try {
			JSONObject resultObject = getObjectFromResult(result);
			if(resultObject.optString("couchdb").equals("Welcome")) return true;
			else return false;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Returns the revision number from a <tt>JSONObject</tt>.
	 * 
	 * @param result result string
	 * @return rev
	 */
	public static String getRev(String result) {
		String str = "";
		try {
			JSONObject resultObject = getObjectFromResult(result);
			if(!resultObject.optString("rev").equals("")) str = resultObject.getString("rev");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	private static JSONObject getObjectFromResult(String result) {
		try {
			return new JSONObject(new JSONTokener(result));
		} catch(Exception e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}
	
	private static JSONObject getConstructObjectFromResult(String result, int position) {
		try {
			JSONObject firstObject = null;
			JSONArray rowsArray = null;
			JSONObject firstObjectValue = null;
			JSONObject resultObj = new JSONObject(new JSONTokener(result));
			if(resultObj != null) rowsArray = resultObj.optJSONArray("rows");
			if(rowsArray != null) firstObject = rowsArray.optJSONObject(position);
			if(firstObject != null) firstObjectValue = firstObject.optJSONObject("value");
			return firstObjectValue;
		} catch(Exception e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}
	
	/**
	 * Returns a <tt>ITopicMap</tt> build from the result string.
	 * 
	 * @param result result string
	 * @param sys topic map system
	 * @param position position in result object
	 * @return tm topic map
	 */
	public static ITopicMap JSONToTopicMap(String result, ITopicMapSystem sys, int position) {
		ITopicMap topicMap = null;
		String reifierId = null;
		String id = null;
		String rev = null;
		Set<Locator> iids = CollectionFactory.createSet();
		Locator locator = null;
		JSONObject obj = null;
		try {
			obj = new JSONObject(new JSONTokener(result));
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(obj != null) {
			reifierId = (obj.optString("reifier").equals("") ? null : obj.optString("reifier"));
			locator = (obj.optString("locator").equals("") ? null : new LocatorImpl(obj.optString("locator")));
			id = (obj.optString("_id").equals("") ? null : obj.optString("_id"));
			JSONArray _iids;
			if((_iids = obj.optJSONArray("itemidentifiers")) != null) {
				for(int i = 0; i < _iids.length(); i++) {
					try{ 
						iids.add(new LocatorImpl(_iids.getString(i)));		
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			rev = (obj.optString("_rev").equals("") ? null : obj.optString("_rev"));
		}
		if(id != null) {
			DB db = null;
			try {
				db = new DB((String) sys.getProperty("DB"), Integer.valueOf((String) sys.getProperty("PORT")), id, sys);
			} catch(Exception e) {
				e.printStackTrace();
			}
			topicMap = new TopicMapImpl(sys, id, rev, iids, locator, db);
			ITopic reifier = null;
			if(reifierId != null) reifier = new TopicImpl(reifierId, topicMap);
			((IReifiable) topicMap).setReifier((Topic) reifier, true);
		}
		return topicMap;
	}
	
	/**
	 * Returns the <tt>IConstruct</tt> which is at the first position in the result object. 
	 * @param result
	 * @param tm
	 * @param tmom
	 * @return
	 */
	public static IConstruct JSONToConstruct(String result, ITopicMap tm, TopicMapObjectManager tmom) {
		return JSONToConstruct(result, tm, tmom, 0);
	}
	
	/**
	 * Returns the keyset of a result object.
	 * 
	 * @param result result string
	 * @return set keyset
	 */
	public static Set<String> getKeyset(String result) {
		Set<String> keyset = CollectionFactory.createSet();
		try {
			JSONArray rowsArray = null;
			JSONObject resultObj = new JSONObject(new JSONTokener(result));
			if(resultObj != null) rowsArray = resultObj.optJSONArray("rows");
			if(rowsArray != null) {
				for(int i = 0; i < rowsArray.length(); i++) {
					JSONObject obj = rowsArray.optJSONObject(i);
					if(obj != null) {
						String key = obj.optString("key");
						if(!key.equals("") && !key.equals("none")) keyset.add(key);
					}
				}
					
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return keyset;
	}
	
	/**
	 * Returns the <tt>IConstruct</tt> which is at the given position in the result object. 
	 * 
	 * @param result result string
	 * @param tm topic map
	 * @param tmom topic map object manager
	 * @param position position
	 * @return construct construct
	 */
	public static IConstruct JSONToConstruct(String result, ITopicMap tm, TopicMapObjectManager tmom, int position) {
		IConstruct construct = null;
		try{
			JSONObject obj;
			if(position >= 0) obj = getConstructObjectFromResult(result, position);
			else obj = new JSONObject(result);
			if(obj != null) {
				String parentId = "";
				JSONArray _iids;
				String id = null;
				String rev = null;
				Set<Locator> iids = CollectionFactory.createSet();
				String documentType = null;
				IConstruct parent = null;
				String tmId = null;
				id = (obj.optString("_id").equals("") ? null : obj.optString("_id"));
				construct = tmom.getCachedConstruct(id);
				if(construct != null) {
					return tmom.getConstruct(id);
				}
				if(!(parentId = obj.optString("parent")).equals("")) {
					parent = tmom.getCachedConstruct(parentId);
					if(parent == null) {
						parent = new ConstructImpl(parentId, tm);
					}
				}
				documentType = (obj.optString("documenttype").equals("") ? null : obj.optString("documenttype"));
				if((_iids = obj.optJSONArray("itemidentifiers")) != null) {
					for(int i = 0; i < _iids.length(); i++) iids.add(new LocatorImpl(_iids.getString(i)));
				}
				rev = (obj.optString("_rev").equals("") ? null : obj.optString("_rev"));
				tmId = (obj.optString("topicmap").equals("") ? null : obj.optString("topicmap"));
				if(documentType == null) construct = JSONToIAssociation(obj, id, rev, iids, tm, tmom);
				else if(documentType.equals(IConstant.TOPIC_MAP)) {
					construct = JSONToITopicMap(obj, id, rev, iids, tm.getSystem(), tmom);
				}
				else if(documentType.equals(IConstant.TOPIC)) construct = JSONToITopic(obj, id, rev, iids, tm, tmom);
				else if(documentType.equals(IConstant.ASSOCIATION)) construct = JSONToIAssociation(obj, id, rev, iids, tm, tmom);
				else if(documentType.equals(IConstant.NAME)) construct = JSONToIName(obj, id, rev, iids, tm, tmom);
				else if(documentType.equals(IConstant.OCCURRENCE)) construct = JSONToIOccurrence(obj, id, rev, iids, tm, tmom);
				else if(documentType.equals(IConstant.ROLE)) construct = JSONToIRole(obj, id, rev, iids, tm, tmom);
				else if(documentType.equals(IConstant.VARIANT)) construct = JSONToIVariant(obj, id, rev, iids, tm, tmom);
				if(construct != null && construct.getId() == null) return null;
				if(construct != null && !construct.getDocumentType().equals(IConstant.TOPIC_MAP) && !tmId.equals(tm.getId())) {
					return null;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return construct;
	}
	
	//create occurrence
	private static IOccurrence JSONToIOccurrence(JSONObject obj, String id, String rev, Set<Locator> iids, ITopicMap tm, TopicMapObjectManager tmom) {
		IOccurrence occ = null;
		try {
			String parentId = null;
			ITopic parent = null;
			parentId = (obj.optString("parent").equals("")) ? null : obj.optString("parent");
			if(parentId != null) {
				parent = (ITopic) tmom.getCachedConstruct(parentId);
				if(parent == null) {
					parent = new TopicImpl(parentId, tm);
					//tmom.addConstruct(parent);
				}
			}
			String value = obj.optString("value").equals("") ? null : obj.optString("value");
			String typeId = null;
			ITopic type = null;
			typeId = (obj.optString("type").equals("")) ? null : obj.optString("type");
			if(typeId != null) {
				type = (ITopic) tmom.getCachedConstruct(typeId);
				if(type == null) {
					type = new TopicImpl(typeId, tm);
					//tmom.addConstruct(type);
				}
			}
			String reifierId = null;
			ITopic reifier = null;
			reifierId = (obj.optString("reifier").equals("")) ? null : obj.optString("reifier");
			if(reifierId != null) {
				reifier = (ITopic) tmom.getCachedConstruct(reifierId);
				if(reifier == null) {
					reifier = new TopicImpl(reifierId, tm);
					//tmom.addConstruct(reifier);
				}
			}
			JSONArray _scope = null;
			Set<ITopic> scope = CollectionFactory.createSet();
			if((_scope = obj.optJSONArray("scope")) != null) {
				for(int i = 0; i < _scope.length(); i++) {
					ITopic topic = (ITopic) tmom.getCachedConstruct(_scope.getString(i));
					if(topic == null) {
						topic = new TopicImpl(_scope.getString(i), tm);
						//tmom.addConstruct(topic);
					}
					scope.add(topic);
				}
			}
			Locator datatype = obj.optString("datatype").equals("") ? null : new LocatorImpl(obj.optString("datatype"));
			occ = new OccurrenceImpl(id, rev, tm, iids, parent, type, scope, datatype, value, reifier);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return occ;
	}
	
	//create name
	private static IName JSONToIName(JSONObject obj, String id, String rev, Set<Locator> iids, ITopicMap tm, TopicMapObjectManager tmom) {
		IName name = null;
		try {
			String parentId = null;
			ITopic parent = null;
			parentId = (obj.optString("parent").equals("")) ? null : obj.optString("parent");
			if(parentId != null) {
				parent = (ITopic) tmom.getCachedConstruct(parentId);
				if(parent == null) {
					parent = new TopicImpl(parentId, tm);
					//tmom.addConstruct(parent);
				}
			}
			String value = obj.optString("value").equals("") ? null : obj.optString("value");
			String typeId = null;
			ITopic type = null;
			typeId = (obj.optString("type").equals("")) ? null : obj.optString("type");
			if(typeId != null) {
				type = (ITopic) tmom.getCachedConstruct(typeId);
				if(type == null) {
					type = new TopicImpl(typeId, tm);
					//tmom.addConstruct(type);
				}
			}
			String reifierId = null;
			ITopic reifier = null;
			reifierId = (obj.optString("reifier").equals("")) ? null : obj.optString("reifier");
			if(reifierId != null) {
				reifier = (ITopic) tmom.getCachedConstruct(reifierId);
				if(reifier == null) {
					reifier = new TopicImpl(reifierId, tm);
					//tmom.addConstruct(reifier);
				}
			}
			JSONArray _scope = null;
			Set<ITopic> scope = CollectionFactory.createSet();
			if((_scope = obj.optJSONArray("scope")) != null) {
				for(int i = 0; i < _scope.length(); i++) {
					ITopic topic = (ITopic) tmom.getCachedConstruct(_scope.getString(i));
					if(topic == null) {
						topic = new TopicImpl(_scope.getString(i), tm);
						//tmom.addConstruct(topic);
					}
					scope.add(topic);
				}
			}
			JSONArray _variants = null;
			Set<IVariant> variants = CollectionFactory.createSet();
			if((_variants = obj.optJSONArray("variants")) != null) {
				for(int i = 0; i < _variants.length(); i++) {
					IVariant variant = (IVariant) tmom.getCachedConstruct(_variants.getString(i));
					if(variant == null) {
						variant = new VariantImpl(_variants.getString(i), tm);
						//tmom.addConstruct(variant);
					}
					variants.add(variant);
				}
			}
			name = new NameImpl(id, rev, tm, iids, value, parent, type, scope, variants, reifier);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
	//create role
	private static IRole JSONToIRole(JSONObject obj, String id, String rev, Set<Locator> iids, ITopicMap tm, TopicMapObjectManager tmom) {
		IRole role = null;
		try {
			String parentId = null;
			IAssociation parent = null;
			parentId = (obj.optString("parent").equals("")) ? null : obj.optString("parent");
			if(parentId != null) {
				parent = (IAssociation) tmom.getCachedConstruct(parentId);
				if(parent == null) {
					parent = new AssociationImpl(parentId, tm);
					//tmom.addConstruct(parent);
				}
			}
			String playerId = null;
			ITopic player = null;
			playerId = (obj.optString("player").equals("")) ? null : obj.optString("player");
			if(playerId != null) {
				player = (ITopic) tmom.getCachedConstruct(playerId);
				if(player == null) {
					player = new TopicImpl(playerId, tm);
					//tmom.addConstruct(player);
				}
			}
			String reifierId = null;
			ITopic reifier = null;
			reifierId = (obj.optString("reifier").equals("")) ? null : obj.optString("reifier");
			if(reifierId != null) {
				reifier = (ITopic) tmom.getCachedConstruct(reifierId);
				if(reifier == null) {
					reifier = new TopicImpl(reifierId, tm);
					//tmom.addConstruct(reifier);
				}
			}
			String typeId = null;
			ITopic type = null;
			typeId = (obj.optString("type").equals("")) ? null : obj.optString("type");
			if(typeId != null) {
				type = (ITopic) tmom.getCachedConstruct(typeId);
				if(type == null) {
					type = new TopicImpl(typeId, tm);
					//tmom.addConstruct(type);
				}
			}
			role = new RoleImpl(id, rev, tm, iids, parent, player, type, reifier);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return role;
	}
	
	//create variant
	private static IVariant JSONToIVariant(JSONObject obj, String id, String rev, Set<Locator> iids, ITopicMap tm, TopicMapObjectManager tmom) {
		IVariant variant = null;
		try {
			String parentId = null;
			IName parent = null;
			parentId = (obj.optString("parent").equals("")) ? null : obj.optString("parent");
			if(parentId != null) {
				parent = (IName) tmom.getCachedConstruct(parentId);
				if(parent == null) {
					parent = new NameImpl(parentId, tm);
					//tmom.addConstruct(parent);
				}
			}
			String value = obj.optString("value").equals("") ? null : obj.optString("value");
			JSONArray _scope = null;
			Set<ITopic> scope = CollectionFactory.createSet();
			if((_scope = obj.optJSONArray("scope")) != null) {
				for(int i = 0; i < _scope.length(); i++) {
					ITopic topic = (ITopic) tmom.getCachedConstruct(_scope.getString(i));
					if(topic == null) {
						topic = new TopicImpl(_scope.getString(i), tm);
						//tmom.addConstruct(topic);
					}
					scope.add(topic);
				}
			}
			String reifierId = null;
			ITopic reifier = null;
			reifierId = (obj.optString("reifier").equals("")) ? null : obj.optString("reifier");
			if(reifierId != null) {
				reifier = (ITopic) tmom.getCachedConstruct(reifierId);
				if(reifier == null) {
					reifier = new TopicImpl(reifierId, tm);
					//tmom.addConstruct(reifier);
				}
			}
			Locator datatype = obj.optString("datatype").equals("") ? null : new LocatorImpl(obj.optString("datatype"));
			variant = new VariantImpl(id, rev, tm, iids, parent, value, reifier, scope, datatype);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return variant;
	}
	
	//create association
	private static IAssociation JSONToIAssociation(JSONObject obj, String id, String rev, Set<Locator> iids, ITopicMap tm, TopicMapObjectManager tmom) {
		IAssociation assoc = null;
		try {
			String typeId = null;
			ITopic type = null;
			typeId = (obj.optString("type").equals("")) ? null : obj.optString("type");
			if(typeId != null) {
				type = (ITopic) tmom.getCachedConstruct(typeId);
				if(type == null) {
					type = new TopicImpl(typeId, tm);
					//tmom.addConstruct(type);
				}
			}
			String reifierId = null;
			ITopic reifier = null;
			reifierId = (obj.optString("reifier").equals("")) ? null : obj.optString("reifier");
			if(reifierId != null) {
				reifier = (ITopic) tmom.getCachedConstruct(reifierId);
				if(reifier == null) {
					reifier = new TopicImpl(reifierId, tm);
					//tmom.addConstruct(reifier);
				}
			}
			JSONArray _roles = null;
			Set<IRole> roles = CollectionFactory.createSet();
			if((_roles = obj.optJSONArray("roles")) != null) {
				for(int i = 0; i < _roles.length(); i++) {
					IRole role = (IRole) tmom.getCachedConstruct(_roles.getString(i));
					if(role == null) {
						role = new RoleImpl(_roles.getString(i), tm);
						//tmom.addConstruct(role);
					}
					roles.add(role);
				}
			}
			JSONArray _scope = null;
			Set<ITopic> scope = CollectionFactory.createSet();
			if((_scope = obj.optJSONArray("scope")) != null) {
				for(int i = 0; i < _scope.length(); i++) {
					ITopic topic = (ITopic) tmom.getCachedConstruct(_scope.getString(i));
					if(topic == null) {
						topic = new TopicImpl(_scope.getString(i), tm);
						//tmom.addConstruct(topic);
					}
					scope.add(topic);
				}
			}
			assoc = new AssociationImpl(id, rev, tm, iids, type, reifier, roles, scope);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return assoc;
	}
	
	//create topic map
	private static ITopicMap JSONToITopicMap(JSONObject obj, String id, String rev, Set<Locator> iids, ITopicMapSystem sys, TopicMapObjectManager tmom) {
		ITopicMap topicMap = null;
		try{
			String reifier = null;
			reifier = (obj.optString("reifier").equals("")) ? null : obj.optString("reifier");
			Locator locator = obj.optString("locator").equals("") ? null : new LocatorImpl(obj.optString("locator"));
			if((topicMap = (ITopicMap) sys.getTopicMap(locator)) != null) return topicMap;
			DB db = null;
			try {
				db = new DB((String) sys.getProperty("DB"), Integer.valueOf((String) sys.getProperty("PORT")), id, sys);
			} catch(Exception e) {
				e.printStackTrace();
			}
			topicMap = new TopicMapImpl(sys, id, rev, iids, locator, db);
			ITopic tmReifier = null;
			if(reifier != null) {
				tmReifier = (ITopic) tmom.getCachedConstruct(reifier);
				if(tmReifier == null) {
					tmReifier = new TopicImpl(reifier, topicMap);
					//tmom.addConstruct(tmReifier);
				}
			}
			topicMap.setReifier(null);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return topicMap;
	}
	
	//create topic
	private static ITopic JSONToITopic(JSONObject obj, String id, String rev, Set<Locator> iids, ITopicMap tm, TopicMapObjectManager tmom) {
		ITopic topic = null;
		try {
			String reifiedId = null;
			IReifiable reified = null;
			if(!(reifiedId = obj.optString("reified")).equals("")) reified = new ReifiableImpl(reifiedId, tm);
			JSONArray _sids;
			Set<Locator> sids = CollectionFactory.createSet();
			if((_sids = obj.optJSONArray("subjectidentifiers")) != null) {
				for(int i = 0; i < _sids.length(); i++) sids.add((Locator) new LocatorImpl((String) _sids.get(i)));
			}
			JSONArray _slos;
			Set<Locator> slos = CollectionFactory.createSet();
			if((_slos = obj.optJSONArray("subjectlocators")) != null) {
				for(int i = 0; i < _slos.length(); i++) slos.add((Locator) new LocatorImpl((String) _slos.get(i)));
			}
			JSONArray _names;
			Set<IName> names = CollectionFactory.createSet();
			if((_names = obj.optJSONArray("names")) != null) {
				for(int i = 0; i < _names.length(); i++) {
					IName name = (IName) tmom.getCachedConstruct((String) _names.get(i));
					if(name == null) {
						name = new NameImpl((String) _names.get(i), tm);
						//tmom.addConstruct(name);
					}
					names.add(name);
				}
			}
			JSONArray _occs;
			Set<IOccurrence> occs = CollectionFactory.createSet();
			if((_occs = obj.optJSONArray("occurrences")) != null) {
				for(int i = 0; i < _occs.length(); i++) {
					IOccurrence occ = (IOccurrence) tmom.getCachedConstruct((String) _occs.get(i));
					if(occ == null) {
						occ = new OccurrenceImpl((String) _occs.getString(i), tm);
						//tmom.addConstruct(occ);
					}
					occs.add(occ);
				}
			}
			JSONArray _roles;
			Set<IRole> roles = CollectionFactory.createSet();
			if((_roles = obj.optJSONArray("roles")) != null) {
				for(int i = 0; i < _roles.length(); i++) {
					IRole role = (IRole) tmom.getCachedConstruct(_roles.getString(i));
					if(role == null) {
						role = new RoleImpl((String) _roles.getString(i), tm);
						//tmom.addConstruct(role);
					}
					roles.add(role);
				}
			}
			JSONArray _types;
			Set<ITopic> types = CollectionFactory.createSet();
			if((_types = obj.optJSONArray("types")) != null) {
				for(int i = 0; i < _types.length(); i++) {
					ITopic type = (ITopic) tmom.getCachedConstruct(_types.getString(i));
					if(type == null) {
						type = new TopicImpl(_types.getString(i), tm);
					}
					types.add(type);
				}
			}
			topic = new TopicImpl(id, rev, tm, iids, names, occs, roles, reified, sids, slos, types);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return topic;
	}
	
	/**
	 * Returns a {@link TopicMapDB} build from the result string.
	 * 
	 * @param result
	 * @return topicmapdb
	 */
	public static TopicMapDB JSONToTopicMapDB(String result) {
		TopicMapDB db = null;
		try {
			JSONObject obj = new JSONObject(result);
			if(obj != null) {
				Map<Locator, String> tms = CollectionFactory.createMap();
				String rev = null;
				rev = (obj.optString("_rev").equals("") ? null : obj.optString("_rev"));
				String[] names = JSONObject.getNames(obj);
				if(names != null && names.length > 0) {
					for(String name : names) {
						if(!name.equals("_id") && !name.equals("_rev")) {
							tms.put(((Locator) new LocatorImpl(name)), obj.getString(name));
						}
					}
				}
				if(rev != null) db = new TopicMapDB(tms, rev);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return db;
	}
}
