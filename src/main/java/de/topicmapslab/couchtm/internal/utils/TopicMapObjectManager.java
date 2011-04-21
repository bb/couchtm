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

import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.IVariant;
import de.topicmapslab.couchtm.internal.api.IOccurrence;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.IConstant;

import java.util.Set;
import java.util.Iterator;

import org.tmapi.core.Locator;

import java.util.Map;
import java.util.HashMap;

/**
 * Class to providing various methods to access topic map constructs.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class TopicMapObjectManager {
	
	private final DB db;
	private final ObjectCache<String, IConstruct> idToConstruct;
	private final Map<Locator, IConstruct> iidToConstruct;
	private ITopicMap tm;
	
	public TopicMapObjectManager(int max, DB db) {
		this.db = db;
		idToConstruct = new ObjectCache<String, IConstruct>(max);
		iidToConstruct = new HashMap<Locator, IConstruct>();
	}
	
	/**
	 * Sets the topic map the object belongs to.
	 * 
	 * @param tm topic map
	 * @param loc topic map locator
	 */
	public void setTopicMap(ITopicMap tm, Locator loc) {
		this.tm = tm;
		idToConstruct.put(tm.getId(), tm);
		iidToConstruct.put(loc, tm);
		for(Locator loc2 : tm.getItemIdentifiers()) {
			iidToConstruct.put(loc2, tm);
		}
		
	}
	
	/**
	 * Returns the <tt>ITopicMap</tt>.
	 * 
	 * @return tm
	 */
	public ITopicMap getTM() {
		return tm;
	}
	
	/**
	 * Returns the database this object belongs to.
	 * 
	 * @return db database
	 */
	public DB getDb() {
		return db;
	}

	/**
	 * Removes an entry from the iidToConstruct map.
	 * 
	 * @param locator
	 */
	public void removeEntry(Locator locator) {
		iidToConstruct.remove(locator);
	}
	
	/**
	 * Adds an entry to the iidToConstruct map.
	 * 
	 * @param locator
	 * @param construct
	 */
	public void addEntry(Locator locator, IConstruct construct) {
		iidToConstruct.put(locator, construct);
	}
	
	public void compactDB() {
		db.compactDB();
	}
	
	public void clear(ITopicMap tm) {
		idToConstruct.clear();
		iidToConstruct.clear();
		if(tm != null) {
			idToConstruct.put(tm.getId(), tm);
			for(Locator iid : tm.getItemIdentifiers()) {
				iidToConstruct.put(iid, tm);
			}
		}
	}
	
	/**
	 * Deletes an <tt>IConstruct</tt> from both maps.
	 * 
	 * @param construct
	 */
	public void deleteConstruct(IConstruct construct) {
		idToConstruct.remove(construct.getId());
		for(Locator locator : construct.getItemIdentifiers()) {
			iidToConstruct.remove(locator);
		}
		if(construct.getDocumentType().equals(IConstant.TOPIC)) deleteTopic((ITopic) construct);
		else db.deleteDocument(construct.getId(), construct.getRev());
		construct = null;
	}
	
	/**
	 * Deletes an <tt>ITopic</tt> from both maps.
	 * 
	 * @param topic
	 */
	private void deleteTopic(ITopic topic) {
		for(Locator sid : topic.getSubjectIdentifiers()) {
			iidToConstruct.remove(sid);
		}
		for(Locator slo : topic.getSubjectLocators()) {
			iidToConstruct.remove(slo);
		}
		db.deleteDocument(topic.getId(), topic.getRev());
	}
	
	/**
	 * Adds an <tt>IConstruct</tt> to both maps.
	 * 
	 * @param construct
	 */
	public void addConstruct(IConstruct construct) {
		if(!idToConstruct.containsKey(construct.getId())) idToConstruct.put(construct.getId(), construct);
		for(Locator locator : construct.getItemIdentifiers()) {
			if(!iidToConstruct.containsKey(locator)) iidToConstruct.put(locator, construct);
		}
	}
	
	/**
	 * Gets an <tt>IConstruct</tt> from the idToConstruct map.
	 * @param id
	 * @return
	 */
	public IConstruct getCachedConstruct(String id) {
		return idToConstruct.get(id);
	}
	
	/**
	 * Returns a set of <tt>IOccurrence</tt> which match the given value and datatype.
	 * 
	 * @param value
	 * @param datatype
	 * @return
	 */
	public Set<IOccurrence> getOccurrencesByValue(Locator value, Locator datatype) {
		Set<IOccurrence> occs = db.getOccurrencesByValue(value.getReference(), datatype, tm);
		for(IOccurrence occ : occs) addConstruct(occ);
		return occs;
	}
	
	/**
	 * Returns a set of <tt>IOccurrence</tT> which match the given value and datatype.
	 * 
	 * @param value
	 * @param datatype
	 * @return
	 */
	public Set<IOccurrence> getOccurrencesByValue(String value, Locator datatype) {
		Set<IOccurrence> occs = db.getOccurrencesByValue(value, datatype, tm);
		for(IOccurrence occ : occs) addConstruct(occ);
		return occs;
	}
	
	/**
	 * Returns a set of <tt>IVariant</tt> which match the given value and datatype.
	 * 
	 * @param value
	 * @param datatype
	 * @return
	 */
	public Set<IVariant> getVariantsByValue(Locator value, Locator datatype) {
		Set<IVariant> variants = db.getVariantsByValue(value.getReference(), datatype, tm);
		for(IVariant variant : variants) addConstruct(variant);
		return variants;
	}
	
	/**
	 * Returns a set of <tt>IVariant</tt> which match the given value and datatype.
	 * 
	 * @param value
	 * @param datatype
	 * @return
	 */
	public Set<IVariant> getVariantsByValue(String value, Locator datatype) {
		Set<IVariant> variants = db.getVariantsByValue(value, datatype, tm);
		for(IVariant variant : variants) addConstruct(variant);
		return variants;
	}
	
	/**
	 * Returns all <tt>IAssociation</tt> with the given theme.
	 * 
	 * @param theme
	 * @return
	 */
	public Set<IAssociation> getAssociationsByTheme(ITopic theme) {
		Set<IAssociation> assocs = db.getAssociationsByTheme(theme, tm);
		for(IAssociation assoc : assocs) addConstruct(assoc);
		return assocs;
	}
	
	/**
	 * Returns all <tt>IName</tt> with the given theme.
	 * 
	 * @param theme
	 * @return
	 */
	public Set<IName> getNamesByTheme(ITopic theme) {
		Set<IName> names = db.getNamesByTheme(theme, tm);
		for(IName name : names) addConstruct(name);
		return names;
	}
	
	/**
	 * Returns all <tt>IVariant</tt> with the given theme.
	 * 
	 * @param theme
	 * @return
	 */
	public Set<IVariant> getVariantsByTheme(ITopic theme) {
		Set<IVariant> variants = db.getVariantsByTheme(theme, tm);
		for(IVariant variant : variants) addConstruct(variant);
		return variants;
	}
	
	/**
	 * Returns all <tt>IVariant</tt> with the given theme.
	 * 
	 * @param theme
	 * @return
	 */
	public Set<IOccurrence> getOccurrencesByTheme(ITopic theme) {
		Set<IOccurrence> occs = db.getOccurrencesByTheme(theme, tm);
		for(IOccurrence occ : occs) addConstruct(occ);
		return occs;
	}
	
	/**
	 * Returns all <tt>IOccurrence</tt> with the given type.
	 * 
	 * @param type
	 * @return
	 */
	public Set<IOccurrence> getOccurrencesByType(ITopic type) {
		Set<IOccurrence> occs = db.getOccurrencesByType(type, tm);
		for(IOccurrence occ : occs) addConstruct(occ);
		return occs;
	}
	
	/**
	 * Returns all <tt>Iname</tt> with the given type.
	 * 
	 * @param type
	 * @return
	 */
	public Set<IName> getNamesByType(ITopic type) {
		Set<IName> names = db.getNamesByType(type, tm);
		for(IName name : names) addConstruct(name);
		return names;
	}
	
	/**
	 * Returns all <tt>Irole</tt> with the given type.
	 * 
	 * @param type
	 * @return
	 */
	public Set<IRole> getRolesByType(ITopic type) {
		Set<IRole> roles = db.getRolesByType(type, tm);
		for(IRole role : roles) addConstruct(role);
		return roles;
	}
	
	/**
	 * Returns all <tt>IAssociation</tt> with the given type.
	 * 
	 * @param type
	 * @return
	 */
	public Set<IAssociation> getAssociationsByType(ITopic type) {
		Set<IAssociation> assocs = db.getAssociationsByType(type, tm);
		for(IAssociation assoc : assocs) addConstruct(assoc);
		return assocs;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> with the given type.
	 * 
	 * @param type
	 * @return
	 */
	public Set<ITopic> getTopicsByType(ITopic type) {
		Set<ITopic> topics = db.getTopicsByType(type, tm);
		for(ITopic topic : topics) addConstruct(topic);
		return topics;
	}
	
	/**
	 * Returns all <tt>IName</tt> with the given value.
	 * 
	 * @param value
	 * @return
	 */
	public Set<IName> getNamesByValue(String value) {
		Set<IName> names = db.getNamesByValue(value, tm);
		for(IName name : names) addConstruct(name);
		return names;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> which are used as association themes.
	 * 
	 * @return
	 */
	public Set<ITopic> getAssociationThemes() {
		Set<ITopic> themes = db.getAssociationThemes(tm);
		for(ITopic topic : themes) addConstruct(topic);
		return themes;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> which are used a name themes.
	 * 
	 * @return
	 */
	public Set<ITopic> getNameThemes() {
		Set<ITopic> themes = db.getNameThemes(tm);
		for(ITopic topic : themes) addConstruct(topic);
		return themes;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> which are used a variant themes.
	 * 
	 * @return
	 */
	public Set<ITopic> getVariantThemes() {
		Set<ITopic> themes = db.getVariantThemes(tm);
		for(ITopic topic : themes) addConstruct(topic);
		return themes;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> which are used a occurrence themes.
	 * 
	 * @return
	 */
	public Set<ITopic> getOccurrenceThemes() {
		Set<ITopic> themes = db.getOccurrenceThemes(tm);
		for(ITopic topic : themes) addConstruct(topic);
		return themes;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> which are used a occurrence types.
	 * 
	 * @return
	 */
	public Set<ITopic> getOccurrenceTypes() {
		Set<ITopic> types = db.getOccurrenceTypes(tm);
		for(ITopic topic : types) addConstruct(topic);
		return types;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> which are used a role types.
	 * 
	 * @return
	 */
	public Set<ITopic> getRoleTypes() {
		Set<ITopic> types = db.getRoleTypes(tm);
		for(ITopic topic : types) addConstruct(topic);
		return types;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> which are used a name types.
	 * 
	 * @return
	 */
	public Set<ITopic> getNameTypes() {
		Set<ITopic> types = db.getNameTypes(tm);
		for(ITopic topic : types) addConstruct(topic);
		return types;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> which are used a association types.
	 * 
	 * @return
	 */
	public Set<ITopic> getAssociationTypes() {
		Set<ITopic> types = db.getAssociationTypes(tm);
		for(ITopic topic : types) addConstruct(topic);
		return types;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> which are used a topic types.
	 * 
	 * @return
	 */
	public Set<ITopic> getTopicTypes() {
		Set<ITopic> types = db.getTopicTypes(tm);
		for(ITopic topic : types) addConstruct(topic);
		return types;
	}
	
	/**
	 * Returns all <tt>IVariant</tt> with the given values.
	 * 
	 * @param value
	 * @param datatype
	 * @param scope
	 * @param parent
	 * @return
	 */
	public Set<IVariant> getVariantByValueDatatypeScopeParent(String value, Locator datatype, Set<ITopic> scope, IName parent) {
		String[] _scope = new String[scope.size()];
		int i = 0;
		Iterator<ITopic> it = scope.iterator();
		while(it.hasNext()) {
			_scope[i] = ((ITopic) it.next()).getId();
			i++;
		}
		java.util.Arrays.sort(_scope);
		Set<IVariant> variants = db.getVariantByValueDatatypeScopeParent(value, datatype, _scope, parent, tm);
		for(IVariant variant : variants) addConstruct(variant);
		return variants;
	}
	
	/**
	 * Returns all <tt>IRole</tt> with the given values.
	 * 
	 * @param type
	 * @param player
	 * @param parent
	 * @return
	 */
	public Set<IRole> getRoleByTypePlayerParent(ITopic type, ITopic player, IAssociation parent) {
		Set<IRole> roles = db.getRoleByTypePlayerParent(type, player, parent, tm);
		for(IRole role : roles) addConstruct(role);
		return roles;
	}

	//nur von merge topic maps gebraucht
	/**
	 * Returns all <tt>IConstruct</tt> in the topic map.
	 */
	public Set<IConstruct> getConstructsByTm() {
		Set<IConstruct> constructs = db.getConstructsByTm(tm);
		for(IConstruct construct : constructs) {
			if(construct.getDocumentType().equals(IConstant.TOPIC)) addConstruct((ITopic) construct);
			else addConstruct(construct);
		}
		return constructs;
	}
	
	/**
	 * Returns the <tt>IConstruct</tt> with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public IConstruct getConstruct(String id) {
		IConstruct construct = idToConstruct.get(id);
		if(construct == null){
			if((construct = db.getConstruct(id, tm)) != null) {
				if(construct.getDocumentType().equals(IConstant.TOPIC_MAP)) {
					return tm;
				}
				else addConstruct(construct);
			}	
		}
		return construct;
	}
	
	/**
	 * Returns all <tt>IRole</tt> for the given player.
	 * 
	 * @param player
	 * @return
	 */
	public Set<IRole> getRolesByPlayer(ITopic player) {
		Set<IRole> roles = db.getRolesByPlayer(player, tm);
		for(IRole role : roles) addConstruct(role);
		return roles;
	}
	
	/**
	 * Returns the <tt>IConstruct</tt> with the given item identifier.
	 * 
	 * @param locator
	 * @return
	 */
	public IConstruct getConstruct(Locator locator) {
		IConstruct construct = iidToConstruct.get(locator);	
		if(construct == null){
			if((construct = db.getConstructByTm(locator, tm)) != null) addConstruct(construct);
			if(construct != null && !construct.getItemIdentifiers().contains(locator)) construct = null;
		}
		return construct;
	}
	
	/**
	 * Returns the <tt>ITopic</tt> with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public ITopic getTopic(String id) {
		IConstruct construct = idToConstruct.get(id);
		ITopic topic = null;
		if(construct != null && construct.getDocumentType().equals(IConstant.TOPIC)) topic = (ITopic) construct;
		if(topic == null) {
			if((topic = (ITopic) db.getConstruct(id, tm)) != null) addConstruct(topic);
			
		}
		return topic;
	}
	
	/**
	 * Returns the <tt>ITopic</tt> that reifies the given <tt>IConstruct</tt>.
	 * 
	 * @param reified
	 * @return
	 */
	public Set<ITopic> getTopicsByReified(IConstruct reified) {
		Set<ITopic> topics = db.getTopicsByReified(reified, tm);
		for(ITopic topic : topics) addConstruct(topic);
		return topics;
	}
	
	/**
	 * Returns the <tt>ITopic</tt> with the given item identifier.
	 * @param locator
	 * @return
	 */
	public Set<ITopic> getTopicsByItemIdentifier(Locator locator) {
		Set<ITopic> topics = db.getTopicsByItemIdentifier(locator, tm);
		for(ITopic topic : topics) addConstruct(topic);
		return topics;
	}
	
	/**
	 * Returns the <tt>ITopic</tt> with the given subject identifier.
	 * 
	 * @param locator
	 * @return
	 */
	public Set<ITopic> getTopicsBySubjectIdentifier(Locator locator) {
		Set<ITopic> topics = db.getTopicsBySubjectIdentifier(locator, tm);
		for(ITopic topic : topics) addConstruct(topic);
		return topics;
	}
	
	/**
	 * Returns the <tt>ITopic</tt> with the given subject locator.
	 * 
	 * @param locator
	 * @return
	 */
	public Set<ITopic> getTopicsBySubjectLocator(Locator locator) {
		Set<ITopic> topics = db.getTopicsBySubjectLocator(locator, tm);
		for(ITopic topic : topics) addConstruct(topic);
		return topics;
	}
	
	/**
	 * Returns the <tt>ITopic</tt> with the given subject identifier.
	 * 
	 * @param locator
	 * @return
	 */
	public ITopic getTopicBySubjectIdentifier(Locator locator) {
		IConstruct construct = iidToConstruct.get(locator);
		ITopic topic = null;
		if(construct != null && construct.getDocumentType().equals(IConstant.TOPIC)) topic = (ITopic) construct;
		if(topic == null) {
			Set<ITopic> topics = db.getTopicsBySubjectIdentifier(locator, tm);
			Iterator<ITopic> it = topics.iterator();
			while(it.hasNext()) topic = it.next();
		}
		if(topic != null && !topic.getSubjectIdentifiers().contains(locator)) topic = null;
		if(topic != null) addConstruct(topic);
		return topic;
	}
	
	/**
	 * Returns the <tt>ITopic</tt> with the given subject locator.
	 * 
	 * @param locator
	 * @return
	 */
	public ITopic getTopicBySubjectLocator(Locator locator) {
		ITopic topic = null;
		Set<ITopic> topics = db.getTopicsBySubjectLocator(locator, tm);
		Iterator<ITopic> it = topics.iterator();
		while(it.hasNext()) topic = it.next();
		if(topic != null) addConstruct(topic);
		return topic;
	}
	
	/**
	 * Returns the <tt>Iname</tt> with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public IName getName(String id) {
		IConstruct construct = idToConstruct.get(id);
		IName nm = null;
		if(construct != null && construct.getDocumentType().equals(IConstant.NAME)) nm = (IName) construct;
		if(nm == null) {
			if((nm = (IName) db.getConstruct(id, tm)) != null) addConstruct(nm);
		}
		return nm;
	}
	
	/**
	 * Returns the <tt>Ivariant</tt> with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public IVariant getVariant(String id) {
		IConstruct construct = idToConstruct.get(id);
		IVariant vrnt = null;
		if(construct != null && construct.getDocumentType().equals(IConstant.VARIANT)) vrnt = (IVariant) construct;
		if(vrnt == null) {
			if((vrnt = (IVariant) db.getConstruct(id, tm)) != null) addConstruct(vrnt);
		}
		return vrnt;
	}
	
	/**
	 * Returns the <tt>IOccurrence</tt> with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public IOccurrence getOccurrence(String id) {
		IConstruct construct = idToConstruct.get(id);
		IOccurrence occ = null;
		if(construct != null && construct.getDocumentType().equals(IConstant.OCCURRENCE)) occ = (IOccurrence) construct;
		if(occ == null) {
			if((occ = (IOccurrence) db.getConstruct(id, tm)) != null) addConstruct(occ);
		}
		return occ;
	}
	
	/**
	 * Returns the <tt>IRole</tt> with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public IRole getRole(String id) {
		IConstruct construct = idToConstruct.get(id);
		IRole rl = null;
		if(construct != null && construct.getDocumentType().equals(IConstant.ROLE)) rl = (IRole) construct;
		if(rl == null) {
			if((rl = (IRole) db.getConstruct(id, tm)) != null) addConstruct(rl);
		}
		return rl;
	}
	
	/**
	 * Returns the <tt>IAssociaiton</tt> with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public IAssociation getAssociation(String id) {
		IConstruct construct = idToConstruct.get(id);
		IAssociation assoc = null;
		if(construct != null && construct.getDocumentType().equals(IConstant.ASSOCIATION)) assoc = (IAssociation) construct;
		if(assoc == null) {
			if((assoc = (IAssociation) db.getConstruct(id, tm)) != null) addConstruct(assoc);
			
		}
		return assoc;
	}
	
	/** 
	 * Returns the <tt>IAssociation</tt> with  the given values.
	 * 
	 * @param type
	 * @param scope
	 * @return
	 */
	public Set<IAssociation> getAssociationByTypeScope(ITopic type, Set<ITopic> scope) {
		String[] _scope = new String[scope.size()];
		int i = 0;
		Iterator<ITopic> it = scope.iterator();
		while(it.hasNext()) {
			_scope[i] = ((ITopic) it.next()).getId();
			i++;
		}
		java.util.Arrays.sort(_scope);
		Set<IAssociation> assocs = db.getAssociationsByTypeScope(type.getId(), _scope, tm);
		for(IAssociation assoc : assocs) addConstruct(assoc);
		return assocs;
	}
	
	/**
	 * Returns the <tt>IAssociation</tt> with  the given values.
	 * 
	 * @param type
	 * @param scope
	 * @param roles
	 * @return
	 */
	public Set<IAssociation> getAssociationsByTypeScopeRoles(ITopic type, Set<ITopic> scope, Set<IRole> roles) {
		String[] _scope = new String[scope.size()];
		int i = 0;
		Iterator<ITopic> it = scope.iterator();
		while(it.hasNext()) {
			_scope[i] = ((ITopic) it.next()).getId();
			i++;
		}
		java.util.Arrays.sort(_scope);
		String[] _roles = new String[roles.size()];
		i = 0;
		Iterator<IRole> it2 = roles.iterator();
		while(it2.hasNext()) {
			_roles[i] = ((IRole) it2.next()).getId();
			i++;
		}
		java.util.Arrays.sort(_roles);
		Set<IAssociation> assocs = db.getAssociationsByTypeScopeRoles(type, _scope, _roles);
		for(IAssociation assoc : assocs) addConstruct(assoc);
		return assocs;
	}
	
	/**
	 * Returns all <tt>IAssociation</tt> in the topic map.
	 * 
	 * @return
	 */
	public Set<IAssociation> getAssociationsByTm() {
		Set<IAssociation> assocs = db.getAssociationsByTm(tm);
		for(IAssociation assoc : assocs) addConstruct(assoc);
		return assocs;
	}
	
	/**
	 * Returns all <tt>ITopic</tt> in the topic map.
	 * 
	 * @return
	 */
	public Set<ITopic> getTopicsByTm() {
		Set<ITopic> topics = db.getTopicsByTm(tm);
		for(ITopic topic : topics) addConstruct(topic);
		return topics;
	}
	
	/**
	 * Returns all <tt>IName</tt> with the given values.
	 * 
	 * @param type
	 * @param value
	 * @param scope
	 * @param parent
	 * @return
	 */
	public Set<IName> getNamesByTypeValueScope(ITopic type, String value, Set<ITopic> scope, ITopic parent) {
		String[] _scope = new String[scope.size()];
		int i = 0;
		Iterator<ITopic> it = scope.iterator();
		while(it.hasNext()) {
			_scope[i] = ((ITopic) it.next()).getId();
			i++;
		}
		java.util.Arrays.sort(_scope);
		Set<IName> names = db.getNamesByTypeValueScope(type.getId(), value, _scope, tm, parent.getId());
		for(IName name : names) addConstruct(name);
		return names;
	}
	
	/**
	 * Returns all <tt>IOccurrence</tt> with the given values.
	 * 
	 * @param type
	 * @param value
	 * @param datatype
	 * @param scope
	 * @param parent
	 * @return
	 */
	public Set<IOccurrence> getOccurrencesByTypeValueDatatypeScope(ITopic type, String value, Locator datatype, Set<ITopic> scope, ITopic parent) {
		String[] _scope = new String[scope.size()];
		int i = 0;
		Iterator<ITopic> it = scope.iterator();
		while(it.hasNext()) {
			_scope[i] = ((ITopic) it.next()).getId();
			i++;
		}
		java.util.Arrays.sort(_scope);
		Set<IOccurrence> occs = db.getOccurrencesByTypeValueDatatypeScope(type.getId(), value, datatype.getReference(), _scope, tm, parent.getId());
		for(IOccurrence occ : occs) addConstruct(occ);
		return occs;
	}
	
	/**
	 * Saves the topic map.
	 * 
	 * @return tm
	 */
	public ITopicMap saveTopicMap() {
		db.saveTopicMap(tm);
		return tm;
	}
	
	//save topic
	private ITopic saveTopic(ITopic topic) {
		ITopic tpc = db.saveTopic(topic);
		updateCache(tpc);
		return tpc;
	}

	//save name
	private IName saveName(IName name) {
		IName nm = db.saveName(name);
		updateCache(nm);
		return nm;
	}

	//save variant
	private IVariant saveVariant(IVariant variant) {
		IVariant vrnt = db.saveVariant(variant);
		updateCache(vrnt);
		return vrnt;
	}

	//Save occurrence
	private IOccurrence saveOccurrence(IOccurrence occurrence) {
		IOccurrence occ = db.saveOccurrence(occurrence);
		updateCache(occ);
		return occ;
	}

	//save role
	private IRole saveRole(IRole role) {
		IRole rl = db.saveRole(role);
		updateCache(rl);
		return rl;
	}

	//Save association
	private IAssociation saveAssociation(IAssociation association) {
		IAssociation assoc = db.saveAssociation(association);
		updateCache(assoc);
		return assoc;
	}
	
	private void updateCache(IConstruct construct) {
		idToConstruct.remove(construct.getId());
		idToConstruct.put(construct.getId(), construct);
		for(Locator loc : construct.getItemIdentifiers()) {
			iidToConstruct.remove(loc);
		}
		for(Locator loc : construct.getItemIdentifiers()) {
			iidToConstruct.put(loc, construct);
		}
		if(construct.getDocumentType().equals("TOPIC")) {
			for(Locator loc : ((ITopic) construct).getSubjectIdentifiers()) {
				iidToConstruct.remove(loc);
			}
			for(Locator loc : ((ITopic) construct).getSubjectIdentifiers()) {
				iidToConstruct.put(loc, construct);
			}
			for(Locator loc : ((ITopic) construct).getSubjectLocators()) {
				iidToConstruct.remove(loc);
			}
			for(Locator loc : ((ITopic) construct).getSubjectIdentifiers()) {
				iidToConstruct.put(loc, construct);
			}
		}
	}
	
	/**
	 * Saves the construct to the database.
	 * 
	 * @param construct construct to be saved
	 * @return construct saved construct
	 */
	public IConstruct saveConstruct(IConstruct construct) {
		if(construct.getDocumentType().equals(IConstant.TOPIC_MAP)) return saveTopicMap();
		else if(construct.getDocumentType().equals(IConstant.TOPIC)) return saveTopic((ITopic) construct);
		else if(construct.getDocumentType().equals(IConstant.NAME)) return saveName((IName) construct);
		else if(construct.getDocumentType().equals(IConstant.VARIANT)) return saveVariant((IVariant) construct);
		else if(construct.getDocumentType().equals(IConstant.OCCURRENCE)) return saveOccurrence((IOccurrence) construct);
		else if(construct.getDocumentType().equals(IConstant.ROLE)) return saveRole((IRole) construct);
		else if(construct.getDocumentType().equals(IConstant.ASSOCIATION)) return saveAssociation((IAssociation) construct);
		return null;
	}
	
	/**
	 * Clears the cache and initialized database connection release.
	 */
	public void close() {
		idToConstruct.clear();
		iidToConstruct.clear();
		db.releaseConnection();
	}
	
}
