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

import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.IVariant;
import de.topicmapslab.couchtm.internal.api.IOccurrence;
import de.topicmapslab.couchtm.core.TopicImpl;
import de.topicmapslab.couchtm.core.NameImpl;
import de.topicmapslab.couchtm.core.VariantImpl;
import de.topicmapslab.couchtm.core.RoleImpl;
import de.topicmapslab.couchtm.core.AssociationImpl;
import de.topicmapslab.couchtm.core.OccurrenceImpl;
import java.util.Set;

import org.tmapi.core.TMAPIException;
import org.tmapi.core.ModelConstraintException;

/**
 * Initiates merging.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class MergeUtils {
	
	/**
	 * Calls merge with delete flag set to true.
	 * 
	 * @param first
	 * @param second
	 * @return construct
	 */
	public static IConstruct merge(IConstruct first, IConstruct second) throws TMAPIException {
		return merge(first, second, true);
	}
	
	/**
	 * Calls the appropriate method the initiate merging.
	 * 
	 * @param first first construct
	 * @param second second construct
	 * @param flag delete flag
	 * @throws TMAPIException
	 */
	public static IConstruct merge(IConstruct first, IConstruct second, boolean flag) throws TMAPIException{
		if(!first.getDocumentType().equals(second.getDocumentType())) throw new TMAPIException("Merging two different kinds of topic map constructs not possible");
		else if(first.getDocumentType().equals(IConstant.TOPIC_MAP)) mergeTopicMaps((ITopicMap) first, (ITopicMap) second);
		else if(first.getDocumentType().equals(IConstant.TOPIC)) mergeTopics((ITopic) first, (ITopic) second, 1, flag);
		else if(first.getDocumentType().equals(IConstant.NAME)) mergeNames((IName) first, (IName) second, flag);
		else if(first.getDocumentType().equals(IConstant.VARIANT)) mergeVariants((IVariant) first, (IVariant) second, flag);
		else if(first.getDocumentType().equals(IConstant.OCCURRENCE)) mergeOccurrences((IOccurrence) first, (IOccurrence) second, flag);
		else if(first.getDocumentType().equals(IConstant.ASSOCIATION)) mergeAssociations((IAssociation) first, (IAssociation) second, flag);
		else if(first.getDocumentType().equals(IConstant.ROLE)) mergeRoles((IRole) first, (IRole) second, flag);
		return null;
	}
	
	//merge names
	private static void mergeNames(IName first, IName second, boolean flag) {
		TopicMapObjectManager tmom = ((ITopicMap) first.getTopicMap()).getTopicMapObjectManager();
		first.mergeWith(second);
		tmom.saveConstruct(first);
		if(flag) tmom.deleteConstruct(second);
		second.setMergedIn(first);
		
	}
	
	//merge variants
	private static void mergeVariants(IVariant first, IVariant second, boolean flag) {
		TopicMapObjectManager tmom = ((ITopicMap) first.getTopicMap()).getTopicMapObjectManager();
		first.mergeWith(second);
		tmom.saveConstruct(first);
		if(flag) tmom.deleteConstruct(second);
		second.setMergedIn(first);
		
	}
	
	//merge occurrences
	private static void mergeOccurrences(IOccurrence first, IOccurrence second, boolean flag) {
		TopicMapObjectManager tmom = ((ITopicMap) first.getTopicMap()).getTopicMapObjectManager();
		first.mergeWith(second);
		tmom.saveConstruct(first);
		if(flag) tmom.deleteConstruct(second);
		second.setMergedIn(first);
		
	}
	
	//merge associations
	private static void mergeAssociations(IAssociation first, IAssociation second, boolean flag) {
		TopicMapObjectManager tmom = ((ITopicMap) first.getTopicMap()).getTopicMapObjectManager();
		first.mergeWith(second);
		tmom.saveConstruct(first);
		if(flag) tmom.deleteConstruct(second);
		second.setMergedIn(first);
		
	}
	
	//merge roles
	private static void mergeRoles(IRole first, IRole second, boolean flag) {
		TopicMapObjectManager tmom = ((ITopicMap) first.getTopicMap()).getTopicMapObjectManager();
		first.mergeWith(second);
		tmom.saveConstruct(first);
		((ITopic) second.getPlayer()).removeRolePlayed(second);
		((IAssociation) second.getParent()).removeRole(second); 
		if(flag) tmom.deleteConstruct(second);
		second.setMergedIn(first);
		
	}
	
	//merge topics without checking reified
	public static void mergeTopics(ITopic first, ITopic second, boolean flag) {
		mergeTopics(first, second, 0, flag);
	}
	
	//merge topics
	private static void mergeTopics(ITopic first, ITopic second, int checkReified, boolean flag) throws ModelConstraintException {
		if(first.getId().equals(second.getId())) return;
		if( checkReified == 1 && first.getReified() != null && second.getReified() != null && first.getReified().getId() != second.getReified().getId()) {
			throw new ModelConstraintException(first, "Mergen Topics not possible, different Reified items");
		}
		else {
			TopicMapObjectManager tmom = ((ITopicMap) first.getTopicMap()).getTopicMapObjectManager();
			first.mergeWith(second);
			tmom.saveConstruct(first);
			if(flag) tmom.deleteConstruct(second);
			second.setMergedIn(first);
		}
		
	}
	
	//merge topic maps
	private static void mergeTopicMaps(ITopicMap first, ITopicMap second) throws TMAPIException {
		if(second.getReifier() != null && first.getReifier() != null && !second.getReifier().equals(first.getReifier())) throw new ModelConstraintException(first, "The TopicMaps can not be merged because they have different reifiers");
		if(second.getReifier() != null) first.setReifier((ITopic) second.getReifier());
		TopicMapObjectManager tmom = first.getTopicMapObjectManager();
		Set<IConstruct> constructs = second.getTopicMapObjectManager().getConstructsByTm();
		for(IConstruct construct2 : constructs) {
			IConstruct construct = construct2;
			if(construct.getDocumentType().equals(IConstant.TOPIC_MAP)) continue;
			String doctype = construct.getDocumentType();
			if(doctype.equals("TOPIC")) construct = new TopicImpl((ITopic) construct);
			else if(doctype.equals("ASSOCIATION")) construct = new AssociationImpl((IAssociation) construct);
			else if(doctype.equals("ROLE")) construct = new RoleImpl((IRole) construct);
			else if(doctype.equals("NAME")) construct = new NameImpl((IName) construct);
			else if(doctype.equals("VARIANT")) construct = new VariantImpl((IVariant) construct);
			else if(doctype.equals("OCCURRENCE")) construct = new OccurrenceImpl((IOccurrence) construct);
			construct.setITopicMap(first);
			construct.setTopicMapObjectManager(first.getTopicMapObjectManager());
			construct.setTopicMapEventManager(first.getTopicMapEventManager());
			
			if(doctype.equals(IConstant.ASSOCIATION)) {
				((IAssociation) construct).setParent(first, true);
			}
			else if(doctype.equals(IConstant.TOPIC)) {
				((ITopic) construct).setParent(first, true);
				if(second.getReifier() != null && ((ITopic) construct).getReified() != null && ((ITopic) construct).getReified().getId().equals(second.getReifier().getId())) {
					((ITopic) construct).setReified(first);
				}
			}
			else if(doctype.equals(IConstant.ROLE)) tmom.saveConstruct(construct);
			else if(doctype.equals(IConstant.NAME)) tmom.saveConstruct(construct);
			else if(doctype.equals(IConstant.VARIANT)) tmom.saveConstruct(construct);
			else if(doctype.equals(IConstant.OCCURRENCE)) tmom.saveConstruct(construct);
		}
		tmom.saveTopicMap();
	}
	
}
