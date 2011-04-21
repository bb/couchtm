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
import java.util.Iterator;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;

import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.IOccurrence;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IVariant;

import de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager;

/**
 * Methods of this class are called before a new or changed construct is saved.
 * If merging is needed, the existing construct that need to be merged with the
 * new/existing one is returned.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class MergeCheck {
	
	/**
	 * Checks whether two <tt>IAssociation</tt> contain the same roles.
	 * 
	 * @param assoc1
	 * @param assoc2
	 * @return boolean
	 */
	public static boolean assocSuppressionCheck(IAssociation assoc1, IAssociation assoc2) {
		boolean returnFlag = true;
		IAssociation smaller = (assoc1.getRoles().size() >= assoc2.getRoles().size() ? assoc2 : assoc1);
		IAssociation bigger = (smaller.getId().equals(assoc1.getId()) ? assoc2 : assoc1);
		for(IRole role : Converter.setRoleToIRole(smaller.getRoles())) {
			boolean flag = false;
			for(IRole role2 : Converter.setRoleToIRole(bigger.getRoles())) {
				if(role.getPlayer().getId().equals(role2.getPlayer().getId()) || role.getType().getId().equals(role2.getType().getId())) flag = true;
			}
			if(!flag) {
				returnFlag = false;
				break;
			}
		}
		return returnFlag;
	}
	
	/**
	 * Returns the <tt>Ivariant</tt> that matches the given values.
	 * 
	 * @param value value
	 * @param datatype datalype
	 * @param scope scope
	 * @param parent parent
	 * @param tm topic map
	 * @return variant
	 */
	public static IVariant createCheckVariant(String value, Locator datatype, Set<ITopic> scope, IName parent, ITopicMap tm) {
		Set<IVariant> variants = tm.getTopicMapObjectManager().getVariantByValueDatatypeScopeParent(value, datatype, scope, parent);
		IVariant variant = null;
		Iterator<IVariant> it = variants.iterator();
		while(it.hasNext()) variant = it.next();
		return variant;
	}
	
	/**
	 * Returns the <tt>IRole</tt> that matches the given values.
	 * 
	 * @param type type
	 * @param player player
	 * @param parent parent
	 * @param tm topic map
	 * @return role
	 */
	public static IRole createCheckRole(ITopic type, ITopic player, IAssociation parent, ITopicMap tm) {
		Set<IRole> roles = tm.getTopicMapObjectManager().getRoleByTypePlayerParent(type, player, parent);
		IRole role = null;
		Iterator<IRole> it = roles.iterator();
		while(it.hasNext()) role = it.next();
		return role;
	}
	
	/**
	 * Returns the <tt>ITopic</tt> with the given item identifier or throws a {@link ModelConstraintException}
	 * if there is a construct that is not a topic but has that item identifier.
	 * 
	 * @param iid item identifier
	 * @param tm topic map
	 * @param tmom topic map object manager
	 * @return topic
	 * @throws ModelConstraintException
	 */
	public static ITopic createCheckTopicIID(Locator iid, TopicMapObjectManager tmom) throws ModelConstraintException{
		IConstruct construct = tmom.getConstruct(iid);
		if(construct != null) {
			if(!construct.getDocumentType().equals(IConstant.TOPIC)) throw new ModelConstraintException(tmom.getTM(), "A Construct with the Locator that is not a Topic already exists");
			ITopic topic = (ITopic) construct;
			return (ITopic) topic;
		}
		return null;
	}
	
	/**
	 * Returns the <tt>ITopic</tt> with the given subject locator.
	 * 
	 * @param slo subject locator
	 * @param tm topic map
	 * @param tmom topic map object manager
	 * @return topic
	 * @throws ModelConstraintException
	 */
	public static ITopic createCheckTopicSLO(Locator slo, TopicMapObjectManager tmom) throws ModelConstraintException {
		return tmom.getTopicBySubjectLocator(slo);
	}
	
	/**
	 * Returns the <tt>ITopic</tt> with the given subject identifier or throws a {@link ModelConstraintException}
	 * if there is a construct that is not a topic but has that locator as item identifier.
	 * 
	 * @param sid subject identifier
	 * @param tm topic map
	 * @param tmom topic map object manager
	 * @return topic
	 * @throws ModelConstraintException
	 */
	public static ITopic createCheckTopicSID(Locator sid, TopicMapObjectManager tmom) throws ModelConstraintException {
		IConstruct construct = tmom.getConstruct(sid);
		if(construct != null) {
			if(!construct.getDocumentType().equals(IConstant.TOPIC)) throw new ModelConstraintException(tmom.getTM(), "A Construct with the Locator that is not a Topic already exists");
			return (ITopic) construct;
		}
		return null;
	}
	
	/**
	 * Returns the <tt>IAssociation</tt> that matches the given values.
	 * 
	 * @param type type
	 * @param scope scope
	 * @param tmom topic map object manager
	 * @return assoc
	 */
	public static IAssociation createCheckAssociation(ITopic type, Set<ITopic> scope, TopicMapObjectManager tmom) {
		IAssociation assoc = null;
		Set<IAssociation> assocs = tmom.getAssociationByTypeScope(type, scope);
		for(IAssociation _assoc : assocs) {
			if(_assoc.getRoles().size() == 0) {
				assoc = _assoc;
				break;
			}
		}
		return assoc;
	}
	
	/**
	 * returns the <tt>IName</tt> that matches the given values.
	 * 
	 * @param type type
	 * @param value value
	 * @param scope scope
	 * @param parent parent
	 * @param tmom topic map object manager
	 * @return name
	 */
	public static IName createCheckName(ITopic type, String value, Set<ITopic> scope, ITopic parent, TopicMapObjectManager tmom) {
		IName name = null;
		Set<IName> names = tmom.getNamesByTypeValueScope(type, value, scope, parent);
		for(IName _name : names) {
			if(_name.getParent().getId().equals(parent.getId())) {
				name = _name;
				break;
			}
		}
		return name;
	}
	
	/**
	 * Return the <tt>IOccurrence</tt> that matches the given values.
	 * 
	 * @param type type
	 * @param value value
	 * @param datatype datatype
	 * @param scope scope
	 * @param parent parent
	 * @param tmom topic map object manager
	 * @return occurrence
	 */
	public static IOccurrence createCheckOccurrence(ITopic type, String value, Locator datatype, Set<ITopic> scope, ITopic parent, TopicMapObjectManager tmom) {
		IOccurrence occ = null;
		Set<IOccurrence> occs = tmom.getOccurrencesByTypeValueDatatypeScope(type, value, datatype, scope, parent);
		for(IOccurrence _occ : occs) {
			if(_occ.getParent().getId().equals(parent.getId())) {
				occ = _occ;
				break;
			}
		}
		return occ;
	}
	
	/**
	 * Returns the <tt>ITopic</tt> that needs to be merged with the given topic.
	 * 
	 * @param topic changed topic
	 * @return topic existing topic
	 */
	public static ITopic mergeCheckTopic(ITopic topic) {
		TopicMapObjectManager tmom = ((ITopicMap) topic.getTopicMap()).getTopicMapObjectManager();
		ITopic existing = null;
		for(Locator loc : topic.getItemIdentifiers()) {
			Set<ITopic> topics = tmom.getTopicsByItemIdentifier(loc);
			topics.addAll(tmom.getTopicsBySubjectIdentifier(loc));
			boolean flag = false;
			for(ITopic tmp : topics) {
				if(!tmp.getId().equals(topic.getId())) {
					existing = tmp;
					flag = true;
					break;
				}
			}
			if(flag) break;
		}
		if(existing == null) {
			for(Locator loc : topic.getSubjectIdentifiers()) {
				Set<ITopic> topics = tmom.getTopicsBySubjectIdentifier(loc);
				topics.addAll(tmom.getTopicsByItemIdentifier(loc));
				boolean flag = false;
				for(ITopic tmp : topics) {
					if(!tmp.getId().equals(topic.getId())) {
						existing = tmp;
						flag = true;
						break;
					}
				}
				if(flag) break;
			}
		}
		if(existing == null) {
			for(Locator loc : topic.getSubjectLocators()) {
				Set<ITopic> topics = tmom.getTopicsBySubjectLocator(loc);
				boolean flag = false;
				for(ITopic tmp : topics) {
					if(!tmp.getId().equals(topic.getId())) {
						existing = tmp;
						flag = true;
						break;
					}
				}
				if(flag) break;
			}
		}
		if(existing == null) {
			Set<ITopic> topics = tmom.getTopicsByReified((IConstruct) topic.getReified());
			for(ITopic tmp : topics) {
				if(!tmp.getId().equals(topic.getId())) {
					existing = tmp;
					break;
				}
			}
		}
		return existing;
	}
	
	/**
	 * Returns the <tt>IName</tt> the given name needs to be merged with.
	 * 
	 * @param name changed name
	 * @return name existing name
	 */
	public static IName mergeCheckName(IName name) {
		TopicMapObjectManager tmom = ((ITopicMap) name.getTopicMap()).getTopicMapObjectManager();
		Set<IName> names = tmom.getNamesByTypeValueScope((ITopic) name.getType(), name.getValue(), Converter.setTopicToITopic(name.getScope()), (ITopic) name.getParent());
		IName _name = null;
		Iterator<IName> it = names.iterator();
		while(it.hasNext()) {
			IName tmp = it.next();
			if(!tmp.getId().equals(name.getId())) _name = tmp;
		}
		return _name;
	}
	
	/**
	 * Returns the <tt>IVariant</tt> the given variant needs to be merged with.
	 * 
	 * @param variant changed variant
	 * @return variant existing variant
	 */
	public static IVariant mergeCheckVariant(IVariant variant) {
		TopicMapObjectManager tmom = ((ITopicMap) variant.getTopicMap()).getTopicMapObjectManager();
		Set<IVariant>  variants = tmom.getVariantByValueDatatypeScopeParent(variant.getValue(), variant.getDatatype(), Converter.setTopicToITopic(variant.getScope()), (IName) variant.getParent());
		IVariant _variant = null;
		Iterator<IVariant> it = variants.iterator();
		while(it.hasNext()) {
			IVariant tmp = it.next();
			if(!tmp.getId().equals(variant.getId())) _variant = tmp;
		}return _variant;
	}
	
	/**
	 * Returns the <tt>IOccurrence</tt> the given occurrence needs to be merged with.
	 * 
	 * @param occ changed occurrence
	 * @return occ existing occurrence
	 */
	public static IOccurrence mergeCheckOccurrence(IOccurrence occ) {
		TopicMapObjectManager tmom = ((ITopicMap) occ.getTopicMap()).getTopicMapObjectManager();
		Set<IOccurrence> occs = tmom.getOccurrencesByTypeValueDatatypeScope((ITopic) occ.getType(), occ.getValue(), occ.getDatatype(), Converter.setTopicToITopic(occ.getScope()), (ITopic) occ.getParent());
		IOccurrence _occ = null;
		Iterator<IOccurrence> it = occs.iterator();
		while(it.hasNext()) {
			IOccurrence tmp = it.next();
			if(!tmp.getId().equals(occ.getId())) _occ = tmp;
		}
		return _occ;
	}
	
	/**
	 * Returns the <tt>IAssociation</tt> the given association needs to be merged with.
	 * 
	 * @param assoc changed association
	 * @return assoc existing association
	 */
	public static IAssociation mergeCheckAssociation(IAssociation assoc) {
		TopicMapObjectManager tmom = ((ITopicMap) assoc.getTopicMap()).getTopicMapObjectManager();
		Set<IAssociation> assocs = tmom.getAssociationsByTypeScopeRoles((ITopic) assoc.getType(), Converter.setTopicToITopic(assoc.getScope()), Converter.setRoleToIRole(assoc.getRoles()));
		IAssociation _assoc = null;
		Iterator<IAssociation> it = assocs.iterator();
		while(it.hasNext()) {
			IAssociation tmp = it.next();
			if(!tmp.getId().equals(assoc.getId())) _assoc = tmp;
		}
		return _assoc;
	}
	
	/**
	 * Returns the <tt>IRole</tt> the given role needs to be merged with.
	 *  
	 * @param role changed role
	 * @returnrole exsisting role
	 */
	public static IRole mergeCheckRole(IRole role) {
		TopicMapObjectManager tmom = ((ITopicMap) role.getTopicMap()).getTopicMapObjectManager();
		Set<IRole> roles = tmom.getRoleByTypePlayerParent((ITopic) role.getType(), (ITopic) role.getPlayer(), (IAssociation) role.getParent());
		IRole _role = null;
		Iterator<IRole> it = roles.iterator();
		while(it.hasNext()) {
			IRole tmp = it.next();
			if(!tmp.getId().equals(role.getId())) _role = tmp;
		}
		return _role; 
	}
	
	/**
	 * Passes the merge check for a construct to the appropriate method.
	 * 
	 * @param construct changed construct
	 * @return construct existing construct
	 */
	public static IConstruct check(IConstruct construct) {
		String documentType = construct.getDocumentType();
		if(documentType.equals(IConstant.TOPIC)) {
			return mergeCheckTopic((ITopic) construct);
		}
		else if(documentType.equals(IConstant.NAME)) {
			return mergeCheckName((IName) construct);
		}
		else if(documentType.equals(IConstant.VARIANT)) {
			return mergeCheckVariant((IVariant) construct);
		}
		else if(documentType.equals(IConstant.OCCURRENCE)) {
			return mergeCheckOccurrence((IOccurrence) construct);
		}
		else if(documentType.equals(IConstant.ROLE)) {
			return mergeCheckRole((IRole) construct);
		}
		else if(documentType.equals(IConstant.ASSOCIATION)) {
			return mergeCheckAssociation((IAssociation) construct);
		}
		return null;
	}
}
