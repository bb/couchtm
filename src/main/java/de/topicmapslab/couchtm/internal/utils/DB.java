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

import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;

import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IVariant;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.IOccurrence;
import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.ITopicMapSystem;

/**
 * This class provides access to a database representing a topic map.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 *
 */
public class DB extends SysDB{	
	
	public DB(String url, int port, String dbName, ITopicMapSystem sys) throws TMAPIException{
		super(url, port, dbName, sys);
		setupDb();
	}
	
	/**
	 * Creation of the database.
	 */
	private void setupDb() {
		if(!dbExists(dbName)) {
			putMethod(dbName, "");
			makeViews();
		}
	}
	
	/**
	 * Creation of the needed views.
	 */
	private void makeViews() {
		String views = "{ \"_id\" : \"_design/couchtm\", \"language\" : \"javascript\" , \"views\" : { "
			//+ "\"getconstruct\" : { \"map\" : \"function(doc) { for(var i in doc.itemidentifiers) { emit(doc.itemidentifiers[i], doc) } }\" }, "
			+ "\"getconstructbylocator\" : { \"map\" : \"function(doc) { for(var i in doc.itemidentifiers) { emit([doc.topicmap, doc.itemidentifiers[i]], doc) } }\" }, "
			//+ "\"gettopicmaplocators\" : { \"map\" : \"function(doc) { if(doc.documenttype == '"+IConstant.TOPIC_MAP+"') { emit(doc.locator, doc) } }\" }, "
			+ "\"gettopicbysubjectidentifier\" : { \"map\" : \"function(doc) { if(doc.documenttype == '"+IConstant.TOPIC+"') { for(var i in doc.subjectidentifiers) { emit([doc.topicmap, doc.subjectidentifiers[i]], doc) } } } \" }, "
			+ "\"gettopicbysubjectlocator\" : { \"map\" : \"function(doc) { if(doc.documenttype == '"+IConstant.TOPIC+"') { for(var i in doc.subjectlocators) { emit([doc.topicmap, doc.subjectlocators[i]], doc) } } } \" }, "
			+ "\"gettopicsbytm\" : { \"map\" : \"function(doc) { if(doc.documenttype == '"+IConstant.TOPIC+"') { emit(doc.topicmap, doc) } }\" }, "
			+ "\"getassociationsbytm\" : { \"map\" : \"function(doc) { if(doc.documenttype == '"+IConstant.ASSOCIATION+"') { emit(doc.topicmap, doc) } }\" }, "
			+ "\"getassociationsbytypescope\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.ASSOCIATION+"') { scope = new Array(doc.scope.length); for(var i = 0; i < doc.scope.length; i++) { scope[i] = doc.scope[i]; } scope.sort(); emit([doc.topicmap, doc.type, scope], doc); } } \" }, "
			+ "\"getconstructsbytm\" : { \"map\" : \" function(doc) { emit(doc.topicmap, doc) } \" }, "
			+ "\"getnamesbytypevaluescope\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.NAME+"') { if(doc.scope) {scope = new Array(doc.scope.length); for(var i = 0; i < doc.scope.length; i++) { scope[i] = doc.scope[i]; } scope.sort();} else {scope = new Array('none');} emit([doc.topicmap, doc.type, doc.value, scope, doc.parent], doc); } } \" }, "
			+ "\"getoccurrencesbytypevaluedatatypescope\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.OCCURRENCE+"') { if(doc.scope) { scope = new Array(doc.scope.length); for(var i = 0; i < doc.scope.length; i++) { scope[i] = doc.scope[i]; } scope.sort();} else {scope = new Array('none');} emit([doc.topicmap, doc.type, doc.value, doc.datatype, scope, doc.parent], doc); } } \" }, "
			+ "\"getassociationsbytheme\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.ASSOCIATION+"') { if(doc.scope == null) { emit('none', doc); } for(var i in doc.scope) { emit(doc.scope[i], doc); } } } \" }, "
			+ "\"getnamesbytheme\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.NAME+"') { if(doc.scope == null) { emit('none', doc); } for(var i in doc.scope) { emit(doc.scope[i], doc); } } } \" }, "
			+ "\"getvariantsbytheme\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.VARIANT+"') { if(doc.scope == null) { emit('none', doc); } for(var i in doc.scope) { emit(doc.scope[i], doc); } } } \" }, "
			+ "\"getoccurrencesbytheme\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.OCCURRENCE+"') { if(doc.scope == null) { emit('none', doc); } for(var i in doc.scope) { emit(doc.scope[i], doc); } } } \" }, "
			+ "\"getoccurrencesbytype\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.OCCURRENCE+"') { emit(doc.type, doc);  } } \" }, "
			+ "\"getrolesbytype\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.ROLE+"') { emit(doc.type, doc);  } } \" }, "
			+ "\"getnamesbytype\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.NAME+"') { emit(doc.type, doc);  } } \" }, "
			+ "\"getassociationsbytype\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.ASSOCIATION+"') { emit(doc.type, doc);  } } \" }, "
			+ "\"gettopicsbytype\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.TOPIC+"') { if(doc.types == null) { emit('none', doc); } for(var i in doc.types) { emit(doc.types[i], doc); } } } \" }, "
			+ "\"getnamesbyvalue\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.NAME+"') { emit([doc.topicmap, doc.value], doc); } } \" }, "
			+ "\"getvariantsbydatatypevalue\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.VARIANT+"') { emit([doc.topicmap, doc.value, doc.datatype], doc); } } \" }, "
			+ "\"getoccurrencesbydatatypevalue\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.OCCURRENCE+"') { emit([doc.topicmap, doc.value, doc.datatype], doc); } }  \" }, "
			+ "\"getrolebytypeplayerparent\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.ROLE+"') { emit([doc.type, doc.player, doc.parent], doc); } } \" }, "
			+ "\"getvariantbyvaluedatatypescopeparent\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.VARIANT+"') { scope = new Array(doc.scope.length); for(var i = 0; i < doc.scope.length; i++) { scope[i] = doc.scope[i]; } scope.sort(); emit([doc.value, doc.datatype, scope, doc.parent], doc); } } \" }, "
			+ "\"getassociationbytypescoperoles\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.ASSOCIATION+"') { scope = new Array(doc.scope.length); for(var i = 0; i < doc.scope.length; i++) { scope[i] = doc.scope[i]; } scope.sort(); roles = new Array(doc.roles.length); for(var j = 0; j < doc.roles.length; j++) { roles[j] = doc.roles[j]; } roles.sort(); emit([doc.type, scope, roles], doc); } } \" }, "
			+ "\"gettopicsbyreified\" : { \"map\" : \" function(doc) { if(doc.documenttyoe == '"+IConstant.TOPIC+"') { emit(doc.reified, doc); } } \" }, "
			+ "\"gettopicbyitemidentifier\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.TOPIC+"') { for(var i in doc.itemidentifiers) { emit([doc.topicmap, doc.itemidentifiers[i]], doc); } } } \" }, "
			+ "\"getrolesbyplayer\" : { \"map\" : \" function(doc) { if(doc.documenttype == '"+IConstant.ROLE+"') { emit(doc.player, doc); } } \" }"
			+"} }";
		putMethod(dbName+"/_design/couchtm", views);
	}
	
	/**
	 * conmpacts the database.
	 */
	public void compactDB() {
		postMethod(dbName+"/"+"_compact", "");
		postMethod(dbName+"/"+"_compact/couchtm", "");
	}
	
	/**
	 * Checks whether a database with the given name already exists.
	 * 
	 * @param name databasename to be checked
	 * @return exists
	 */
	public boolean dbExists(String name) {
		String result = getTopLevel("_all_dbs");
		//System.out.println("result: "+result);
		Set<String> dbs = JSONToObject.toSet(result);
		for(String db : dbs) {
			if(name.equals(db)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Saves an <tt>ITopicMap</tt>.
	 * 
	 * @param tm topic ma to be saved
	 * @return tm saved topic map
	 */
	public ITopicMap saveTopicMap(ITopicMap tm) {
		String result = putMethod(dbName+"/"+tm.getId(), tm.toJSON());
		tm.setRev(JSONToObject.getRev(result));
		return tm;
	}
	
	/**
	 * Saves an <tt>ITopic</tt>.
	 * 
	 * @param topic topic to be saved
	 * @return topic saved topic
	 */
	public ITopic saveTopic(ITopic topic) {
		String result = putMethod(dbName+"/"+topic.getId(), topic.toJSON());
		topic.setRev(JSONToObject.getRev(result));
		return topic;
	}
	
	/**
	 * Saved an <tt>IName</tt>.
	 * 
	 * @param name name to be saved
	 * @return name saved name
	 */
	public IName saveName(IName name) {
		String result = putMethod(dbName+"/"+name.getId(), name.toJSON());
		name.setRev(JSONToObject.getRev(result));
		return name;
	}
	
	/**
	 * Saves an <tt>IVariant</tt>.
	 * 
	 * @param variant variant to be saved
	 * @return variant saved variant
	 */
	public IVariant saveVariant(IVariant variant) {
		String result = putMethod(dbName+"/"+variant.getId(), variant.toJSON());
		variant.setRev(JSONToObject.getRev(result));
		return variant;
	}
	
	/**
	 * Saves an <tt>IOccurrence</tt>.
	 * 
	 * @param occurrence occurrence to be saved
	 * @return occurrence saved occurrence
	 */
	public IOccurrence saveOccurrence(IOccurrence occurrence) {
		String result = putMethod(dbName+"/"+occurrence.getId(), occurrence.toJSON());
		occurrence.setRev(JSONToObject.getRev(result));
		return occurrence;
	}
	
	/**
	 * Saves an <tt>IRole</tt>.
	 * 
	 * @param role role to be saved
	 * @return role saved role
	 */
	public IRole saveRole(IRole role) {
		String result = putMethod(dbName+"/"+role.getId(), role.toJSON());
		role.setRev(JSONToObject.getRev(result));
		return role;
	}
	
	/**
	 * Saves an <tt>IAssociation</tt>.
	 * 
	 * @param association association to be saved
	 * @return association saved association
	 */
	public IAssociation saveAssociation(IAssociation association) {
		String result = putMethod(dbName+"/"+association.getId(), association.toJSON());
		association.setRev(JSONToObject.getRev(result));
		return association;
	}
	
	/**
	 * Retrieves a set of <tt>IRole</tt> from the database which the
	 * given <tt>ITopic</tt> plays.
	 * 
	 * @param player player
	 * @param tm topic map
	 * @return roles roles
	 */
	public Set<IRole> getRolesByPlayer(ITopic player, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getrolesbyplayer", "\""+player.getId()+"\"");
		Set<IRole> roles = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IRole role = (IRole) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(role == null) i = -1;
			else {
				roles.add(role);
				i++;
			}
		}
		return roles;
	}
	
	/**
	 * Retrieves a set of <tt>IRole</tt> from the database which match the given
	 * type, player and parent.
	 * 
	 * @param type type
	 * @param player player
	 * @param parent parent
	 * @param tm topic map
	 * @return roles roles
	 */
	public Set<IRole> getRoleByTypePlayerParent(ITopic type, ITopic player, IAssociation parent, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getrolebytypeplayerparent", "[\""+type.getId()+"\", \""+player.getId()+"\",\""+parent.getId()+"\"]");
		Set<IRole> roles = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IRole role = (IRole) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(role == null) i = -1;
			else {
				roles.add(role);
				i++;
			}
		}
		return roles;
	}
	
	/**
	 * Retrieves a set of <tt>IVariant</tt> from the database which match the given
	 * value, datatype, scope and parent.
	 * 
	 * @param value value
	 * @param datatype datatype
	 * @param scope array of topic IDs
	 * @param parent parent
	 * @param tm topic map
	 * @return variants variants
	 */
	public Set<IVariant> getVariantByValueDatatypeScopeParent(String value, Locator datatype, String[] scope, IName parent, ITopicMap tm) {
		String _scope = "";
		for(String id : scope) {
			_scope += "\""+id+"\", ";
		}
		_scope = _scope.substring(0, _scope.length() - 2);
		String result = getMethod("_design/couchtm/_view/getvariantbyvaluedatatypescopeparent", "[\""+value+"\",\""+datatype.getReference()+"\",["+_scope+"],\""+parent.getId()+"\"]");
		Set<IVariant> variants = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IVariant variant = (IVariant) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(variant == null) i = -1;
			else {
				variants.add(variant);
				i++;
			}
		}
		return variants;
	}	

	/**
	 * Retrieves an <tt>IConstruct</tt> with the given ID from the database.
	 * 
	 * @param id ID
	 * @param tm topic map
	 * @return construct construct
	 */
	public IConstruct getConstruct(String id, ITopicMap tm) {
		String result = getMethod(id, null);
		return JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), -1);
	}
	
	/**
	 * Retrieves a set of <tt>IOccurrence</tt> from the database which match the given
	 * value and datatype.
	 * 
	 * @param value value
	 * @param datatype datatype
	 * @param tm topic map
	 * @return occs occurrences
	 */
	public Set<IOccurrence> getOccurrencesByValue(String value, Locator datatype, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getoccurrencesbydatatypevalue", "[\""+tm.getId()+"\", \""+value+"\", \""+datatype.getReference()+"\"]");
		Set<IOccurrence> occs = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IOccurrence occ = (IOccurrence) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(occ == null) i = -1;
			else {
				occs.add(occ);
				i++;
			}
		}
		return occs;
	}
	
	/**
	 * Retrieves a set of <tt>IVariant</tt> from the database which match the given
	 * value and datatype.
	 * 
	 * @param value value
	 * @param datatype datatype
	 * @param tm topic map
	 * @return variants variants
	 */
	public Set<IVariant> getVariantsByValue(String value, Locator datatype, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getvariantsbydatatypevalue", "[\""+tm.getId()+"\", \""+value+"\", \""+datatype.getReference()+"\"]");
		Set<IVariant> variants = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IVariant variant = (IVariant) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(variant == null) i = -1;
			else {
				variants.add(variant);
				i++;
			}
		}
		return variants;
	}
	
	/**
	 * Retrieves a set of <tt>IName</tt> with the given value from the database
	 * 
	 * @param value value
	 * @param tm topic map
	 * @return names names
	 */
	public Set<IName> getNamesByValue(String value, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getnamesbyvalue", "[\""+tm.getId()+"\", \""+value+"\"]");
		Set<IName> names = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IName name = (IName) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(name == null) i = -1;
			else {
				names.add(name);
				i++;
			}
			
		}
		return names;
	}
	
	/**
	 * Retrieves all<tt>ITopic</tt> from the database which are used
	 * as association themes.
	 * 
	 * @param tm topic map
	 * @return topics themes
	 */
	public Set<ITopic> getAssociationThemes(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getassociationsbytheme", null);
		Set<ITopic> themes = CollectionFactory.createSet();
		Set<String> keyset = JSONToObject.getKeyset(result);
		for(String id : keyset) {
			themes.add((ITopic) getConstruct(id, tm));
		}	
		return themes;
	}
	
	/**
	 * Retrieves all <tt>ITopic</tt> from the database which are used
	 * as name themes.
	 * 
	 * @param tm topic map
	 * @return topics themes
	 */
	public Set<ITopic> getNameThemes(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getnamesbytheme", null);
		Set<ITopic> themes = CollectionFactory.createSet();
		Set<String> keyset = JSONToObject.getKeyset(result);
		for(String id : keyset) {
			themes.add((ITopic) getConstruct(id, tm));
		}	
		return themes;
	}
	
	/**
	 * Retrieves all <tt>ITopic</tt> from the database which are used
	 * as variant themes.
	 * 
	 * @param tm topic map
	 * @return topics themes
	 */
	public Set<ITopic> getVariantThemes(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getvariantsbytheme", null);
		Set<ITopic> themes = CollectionFactory.createSet();
		Set<String> keyset = JSONToObject.getKeyset(result);
		for(String id : keyset) {
			themes.add((ITopic) getConstruct(id, tm));
		}	
		return themes;
	}
	
	/**
	 * Retrieves all <tt>ITopic</tt> from the database which are used 
	 * as occurrence themes.
	 * 
	 * @param tm topic map
	 * @return topics themes
	 */
	public Set<ITopic> getOccurrenceThemes(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getoccurrencesbytheme", null);
		Set<ITopic> themes = CollectionFactory.createSet();
		Set<String> keyset = JSONToObject.getKeyset(result);
		for(String id : keyset) {
			themes.add((ITopic) getConstruct(id, tm));
		}	
		return themes;
	}
	
	/**
	 * Retrieves all <tt>ITopic</tt> from the database which are used as occurrence types.
	 * 
	 * @param tm topic map
	 * @return topics types
	 */
	public Set<ITopic> getOccurrenceTypes(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getoccurrencesbytype", null);
		Set<ITopic> types = CollectionFactory.createSet();
		Set<String> keyset = JSONToObject.getKeyset(result);
		for(String id : keyset) {
			types.add((ITopic) getConstruct(id, tm));
		}	
		return types;
	}
	
	/**
	 * Retrieves all <tt>ITopic</tt> from the database which are used as name types.
	 * 
	 * @param tm topic map
	 * @return topics types
	 */
	public Set<ITopic> getNameTypes(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getnamesbytype", null);
		Set<ITopic> types = CollectionFactory.createSet();
		Set<String> keyset = JSONToObject.getKeyset(result);
		for(String id : keyset) {
			types.add((ITopic) getConstruct(id, tm));
		}	
		return types;
	}
	
	/**
	 * Retrieves all <tt>ITopic</tt> from the database which are used as role types.
	 * 
	 * @param tm topic map
	 * @return topics types
	 */
	public Set<ITopic> getRoleTypes(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getrolesbytype", null);
		Set<ITopic> types = CollectionFactory.createSet();
		Set<String> keyset = JSONToObject.getKeyset(result);
		for(String id : keyset) {
			types.add((ITopic) getConstruct(id, tm));
		}	
		return types;
	}
	
	/**
	 * Retrieves all <tt>ITopic</tt> from the database which are used a association types.
	 * 
	 * @param tm topic map
	 * @return topics types
	 */
	public Set<ITopic> getAssociationTypes(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getassociationsbytype", null);
		Set<ITopic> types = CollectionFactory.createSet();
		Set<String> keyset = JSONToObject.getKeyset(result);
		for(String id : keyset) {
			types.add((ITopic) getConstruct(id, tm));
		}	
		return types;
	}
	
	/**
	 * Retrieves all <tt>ITopic</tt> from the database which are used as topic types.
	 * 
	 * @param tm topic map
	 * @return topics types
	 */
	public Set<ITopic> getTopicTypes(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/gettopicsbytype", null);
		Set<ITopic> types = CollectionFactory.createSet();
		Set<String> keyset = JSONToObject.getKeyset(result);
		for(String id : keyset) {
			types.add((ITopic) getConstruct(id, tm));
		}	
		return types;
	}
	
	/**
	 * Retrieves all <tt>IAssociation</tt> with the given theme from the database.
	 * @param theme theme
	 * @param tm topic map
	 * @return assocs associations
	 */
	public Set<IAssociation> getAssociationsByTheme(ITopic theme, ITopicMap tm) {
		String themeId = theme == null ? "none" : theme.getId();
		String result = getMethod("_design/couchtm/_view/getassociationsbytheme", "\""+themeId+"\"");
		Set<IAssociation> assocs = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IAssociation assoc = (IAssociation) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(assoc == null) i = -1;
			else {
				assocs.add(assoc);
				i++;
			}
			
		}
		return assocs;
	}
	
	/**
	 * Retrieves all <tt>IName</tt> with the given theme from the database.
	 * 
	 * @param theme theme
	 * @param tm topic map
	 * @return names names
	 */
	public Set<IName> getNamesByTheme(ITopic theme, ITopicMap tm) {
		String themeId = theme == null ? "none" : theme.getId();
		String result = getMethod("_design/couchtm/_view/getnamesbytheme", "\""+themeId+"\"");
		Set<IName> names = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IName name = (IName) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(name == null) i = -1;
			else {
				names.add(name);
				i++;
			}	
		}
		return names;
	}
	
	/**
	 * Retrieves all <tt>IVariant</tt> with the given theme from the database.
	 * 
	 * @param theme theme
	 * @param tm topic map
	 * @return variants variants
	 */
	public Set<IVariant> getVariantsByTheme(ITopic theme, ITopicMap tm) {
		String themeId = theme == null ? "none" : theme.getId();
		String result = getMethod("_design/couchtm/_view/getvariantsbytheme", "\""+themeId+"\"");
		Set<IVariant> variants = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IVariant variant = (IVariant) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(variant == null) i = -1;
			else {
				variants.add(variant);
				i++;
			}		
		}
		return variants;
	}
	
	/**
	 * Retrieves all <tt>IOccurrence</tt> with the given theme from the database.
	 * 
	 * @param theme theme
	 * @param tm topic map
	 * @return occs occurrences
	 */
	public Set<IOccurrence> getOccurrencesByTheme(ITopic theme, ITopicMap tm) {
		String themeId = theme == null ? "none" : theme.getId();
		String result = getMethod("_design/couchtm/_view/getoccurrencesbytheme", "\""+themeId+"\"");
		Set<IOccurrence> occs = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IOccurrence occ = (IOccurrence) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(occ == null) i = -1;
			else {
				occs.add(occ);
				i++;
			}	
		}
		return occs;
	}
	
	/**
	 * Retrieves all <tt>IOccurrence</tt> with the given type from the database.
	 * 
	 * @param type type
	 * @param tm topic map
	 * @return occs occurrences
	 */
	public Set<IOccurrence> getOccurrencesByType(ITopic type, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getoccurrencesbytype", "\""+type.getId()+"\"");
		Set<IOccurrence> occs = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IOccurrence occ = (IOccurrence) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(occ == null) i = -1;
			else {
				occs.add(occ);
				i++;
			}
		}
		return occs;
	}
	
	/**
	 * Retrieves all <tt>IName</tt> with the given type from the database.
	 * 
	 * @param type type
	 * @param tm topic map
	 * @return names names
	 */
	public Set<IName> getNamesByType(ITopic type, ITopicMap tm) {
		String typeId;
		if(type == null) {
			typeId = null;
		} else {
			typeId = "\""+type.getId()+"\"";
		}
		String result = getMethod("_design/couchtm/_view/getnamesbytype", typeId);
		Set<IName> names = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IName name = (IName) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(name == null) i = -1;
			else {
				names.add(name);
				i++;
			}	
		}
		return names;
	}
	
	/**
	 * Retrieves all <tt>IRole</tt> with the given type from the database.
	 * 
	 * @param type type
	 * @param tm topic map
	 * @return roles roles
	 */
	public Set<IRole> getRolesByType(ITopic type, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getrolesbytype", "\""+type.getId()+"\"");
		Set<IRole> roles = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IRole role = (IRole) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(role == null) i = -1;
			else {
				roles.add(role);
				i++;
			}		
		}
		return roles;
	}
	
	/**
	 * Retrieves all <tt>IAssociation</tt> with the given type from the database.
	 * 
	 * @param type type
	 * @param tm topic map
	 * @return assocs associations
	 */
	public Set<IAssociation> getAssociationsByType(ITopic type, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getassociationsbytype", "\""+type.getId()+"\"");
		Set<IAssociation> assocs = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IAssociation assoc = (IAssociation) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(assoc == null) i = -1;
			else {
				assocs.add(assoc);
				i++;
			}		
		}
		return assocs;
	}
	
	/**
	 * Retrieves all <tt>ITopic</tt> with the given type from the database.
	 * 
	 * @param type type
	 * @param tm topic map
	 * @return topic topics
	 */
	public Set<ITopic> getTopicsByType(ITopic type, ITopicMap tm) {
		String typeId = type == null ? "none" : type.getId();
		String result = getMethod("_design/couchtm/_view/gettopicsbytype", "\""+typeId+"\"");
		Set<ITopic> topics = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			ITopic topic = (ITopic) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(topic == null) i = -1;
			else {
				topics.add(topic);
				i++;
			}	
		}
		return topics;
	}
	
	/**
	 * Retrieves all <tt>ITopic</tt> in the topic map from the database.
	 * 
	 * @param tm topic map
	 * @return topics topics
	 */
	public Set<ITopic> getTopicsByTm(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/gettopicsbytm", "\""+tm.getId()+"\"");
		Set<ITopic> topics = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			ITopic topic = (ITopic) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(topic == null) i = -1;
			else {
				topics.add(topic);
				i++;
			}	
		}
		return topics;
	}
	
	/**
	 * Retrieves all <tt>IAssociaiton</tt> in the topic map from the database.
	 *  
	 * @param tm topic map
	 * @return assocs associations
	 */
	public Set<IAssociation> getAssociationsByTm(ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getassociationsbytm", "\""+tm.getId()+"\"");
		Set<IAssociation> associations = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IAssociation assoc = (IAssociation) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(assoc == null) i = -1;
			else {
				associations.add(assoc);
				i++;
			}
		}
		return associations;
	}
	
	/**
	 * Retrieves all <tt>IAssociation</tt> with the given scope an type from the database.
	 * 
	 * @param typeId type
	 * @param scope array of topic IDs
	 * @param tm topic map
	 * @return assocs associations
	 */
	public Set<IAssociation> getAssociationsByTypeScope(String typeId, String[] scope, ITopicMap tm) {
		String _scope = "";
		for(String str : scope) {
			_scope += "\""+str+"\", ";
		}
		if(scope.length != 0) _scope = _scope.substring(0, _scope.length() - 2);
		String result = getMethod("_design/couchtm/_view/getassociationsbytypescope", "[\""+tm.getId()+"\", \""+typeId+"\", ["+_scope+"]]");
		Set<IAssociation> associations = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IAssociation assoc = (IAssociation) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(assoc == null) i = -1;
			else {
				associations.add(assoc);
				i++;
			}
		}
		return associations;
	}
	
	/**
	 * Retrieves all <tt>IAssociaiton</tt> with the given type, scope and roles from the database.
	 * 
	 * @param type type
	 * @param scope array of topic IDs
	 * @param roles array of role IDs
	 * @return assocs associations
	 */
	public Set<IAssociation> getAssociationsByTypeScopeRoles(ITopic type, String[] scope, String[] roles) {
		String _scope = "";
		for(String str : scope) {
			_scope += "\""+str+"\", ";
		}
		if(scope.length != 0) _scope = _scope.substring(0, _scope.length() - 2);
		String _roles = "";
		for(String str : roles) {
			_roles += "\""+str+"\", ";
		}
		if(roles.length != 0) _scope = _roles.substring(0, _roles.length() - 2);
		String result = getMethod("_design/couchtm/_view/getassociationbytypescoperoles", "[\""+type.getId()+"\", ["+_scope+"], ["+_roles+"]]");
		ITopicMap tm = (ITopicMap) type.getParent();
		Set<IAssociation> associations = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IAssociation assoc = (IAssociation) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(assoc == null) i = -1;
			else {
				associations.add(assoc);
				i++;
			}
		}
		return associations;
	}

	/**
	 * Retrieves the <tt>ITopic</tt> that reifies the given <tt>IConstruct</tt> from the database.
	 *  
	 * @param reified construct
	 * @param tm topic map
	 * @return topic topic
	 */
	public Set<ITopic> getTopicsByReified(IConstruct reified, ITopicMap tm) {
		Set<ITopic> topics = CollectionFactory.createSet();
		if(reified == null) return topics;
		String result = getMethod("_design/couchtm/_view/gettopicsbyreified","\""+reified.getId()+"\"");
		int i = 0;
		while(i >= 0) {
			ITopic topic = (ITopic) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager());
			if(topic == null) i = -1;
			else {
				topics.add(topic);
				i++;
			}
		}
		return topics;
	}
	
	/**
	 * Retrieves the <tt>ITopic</tt> with the given item identifier from the database.
	 * 
	 * @param iid item identifier
	 * @param tm topic map
	 * @return topic topic
	 */
	public Set<ITopic> getTopicsByItemIdentifier(Locator iid, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/gettopicbyitemidentifier","[\""+tm.getId()+"\", \""+iid.getReference()+"\"]");
		Set<ITopic> topics = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			ITopic topic = (ITopic) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(topic == null) i = -1;
			else {
				topics.add(topic);
				i++;
			}
		}
		return topics;
	}
	
	/**
	 * Retrieves the <tt>ITopic</tt> with the given subject identifier from the database.
	 * 
	 * @param sid subject identifier
	 * @param tm topic map
	 * @return topic topic
	 */
	public Set<ITopic> getTopicsBySubjectIdentifier(Locator sid, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/gettopicbysubjectidentifier","[\""+tm.getId()+"\", \""+sid.getReference()+"\"]");
		Set<ITopic> topics = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			ITopic topic = (ITopic) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(topic == null) i = -1;
			else {
				topics.add(topic);
				i++;
			}
		}
		return topics;
	}
	
	/**
	 * Retrieves the <tt>ITopic</tT> with the given subject locator from the database.
	 * 
	 * @param slo subject locator
	 * @param tm topic map
	 * @return topic topic
	 */
	public Set<ITopic> getTopicsBySubjectLocator(Locator slo, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/gettopicbysubjectlocator","[\""+tm.getId()+"\", \""+slo.getReference()+"\"]");
		Set<ITopic> topics = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			ITopic topic = (ITopic) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(topic == null) i = -1;
			else {
				topics.add(topic);
				i++;
			}
		}
		return topics;
	}
	
	/**
	 * Retrieves all <tt>IConstruct</tt> in the topic map from the database.
	 * 
	 * @param tm topic map
	 * @return constructs construct
	 */
	public Set<IConstruct> getConstructsByTm(ITopicMap tm) {
		Set<IConstruct> constructs = CollectionFactory.createSet();
		String result = getMethod("_design/couchtm/_view/getconstructsbytm", "\""+tm.getId()+"\"");
		int i = 0;
		while(i >= 0) {
			IConstruct construct = (IConstruct) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(construct == null) i = -1;
			else {
				constructs.add(construct);
				i++;
			}
		}
		return constructs;
	}
	
	/**
	 * Retrieves all <tt>IName</tt> with the given type, value, scope ant parent from the database.
	 * 
	 * @param type type
	 * @param value value
	 * @param scope array of topic IDs
	 * @param tm
	 * @param parent parent
	 * @return names names
	 */
	public Set<IName> getNamesByTypeValueScope(String type, String value, String[] scope, ITopicMap tm, String parent) {
		String _scope = "";
		for(String str : scope) {
			_scope += "\""+str+"\", ";
		}
		if(_scope.length() != 0) _scope = _scope.substring(0, _scope.length() - 2);
		else _scope = "\"none\"";
		String result = getMethod("_design/couchtm/_view/getnamesbytypevaluescope", "[\""+tm.getId()+"\", \""+type+"\", \""+value+"\", ["+_scope+"], \""+parent+"\"]");
		Set<IName> names = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IName name = (IName) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(name == null) i = -1;
			else {
				names.add(name);
				i++;
			}
		}
		return names;
	}
	
	/**
	 * Retrieves all <tt>IOccurrence</tt> with the given type, value, datatype, scope and parent from the database.
	 * 
	 * @param type type
	 * @param value value
	 * @param datatype datatype
	 * @param scope array of topic IDs
	 * @param tm
	 * @param parent parent
	 * @return
	 */
	public Set<IOccurrence> getOccurrencesByTypeValueDatatypeScope(String type, String value, String datatype, String[] scope, ITopicMap tm, String parent) {
		String _scope = "";
		for(String str : scope) {
			_scope += "\""+str+"\", ";
		}
		if(scope.length != 0) _scope = _scope.substring(0, _scope.length() - 2);
		else _scope = "\"none\"";
		String result = getMethod("_design/couchtm/_view/getoccurrencesbytypevaluedatatypescope", "[\""+tm.getId()+"\", \""+type+"\", \""+value+"\", \""+datatype+"\", ["+_scope+"], \""+parent+"\"]");
		Set<IOccurrence> occs = CollectionFactory.createSet();
		int i = 0;
		while(i >= 0) {
			IOccurrence occ = (IOccurrence) JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager(), i);
			if(occ == null) i = -1;
			else {
				occs.add(occ);
				i++;
			}
		}
		return occs;
	}
	
	/**
	 * Retrieves the <tt>IConstruct</tt> with the given item identifier from the database.
	 * 
	 * @param locator item identifier
	 * @param tm topic map
	 * @return construct construct
	 */
	public IConstruct getConstructByTm(Locator locator, ITopicMap tm) {
		String result = getMethod("_design/couchtm/_view/getconstructbylocator","[\""+tm.getId()+"\", \""+locator.getReference()+"\"]");
		return JSONToObject.JSONToConstruct(result, tm, tm.getTopicMapObjectManager());	
	}
	
	/**
	 * Deletes the document with the given id and revision.
	 * 
	 * @param id id
	 * @param rev revision
	 */
	public void deleteDocument(String id, String rev) {
		deleteMethod(id, rev);
	}
}