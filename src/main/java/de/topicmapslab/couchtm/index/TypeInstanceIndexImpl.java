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

package de.topicmapslab.couchtm.index;

import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.utils.CollectionFactory;
import de.topicmapslab.couchtm.internal.utils.Converter;
import de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager;

/**
 * {@link TypeInstanceIndex} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class TypeInstanceIndexImpl extends IndexImpl implements TypeInstanceIndex{
	
	public TypeInstanceIndexImpl(TopicMapObjectManager tmom) {
		super(tmom);
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.TypeInstanceIndex#getAssociationTypes()
     */
	@Override
	public Collection<Topic> getAssociationTypes() {
		return Converter.setITopicToTopic(tmom.getAssociationTypes());
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.TypeInstanceIndex#getAssociations(org.tmapi.core.Topic)
     */
	@Override
	public Collection<Association> getAssociations(Topic type) {
		return Converter.setIAssociationToAssociation(tmom.getAssociationsByType((ITopic) type));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ITypeInstanceIndex#getNameTypes()
     */
	@Override
	public Collection<Topic> getNameTypes() {
		return Converter.setITopicToTopic(tmom.getNameTypes());
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ITypeInstanceIndex#getNames(org.tmapi.core.Topic)
     */
	@Override
	public Collection<Name> getNames(Topic type) {
		return Converter.setINameToName(tmom.getNamesByType((ITopic) type));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ITypeInstanceIndex#getOccurrenceTypes()
     */
	@Override
	public Collection<Topic> getOccurrenceTypes() {
		return Converter.setITopicToTopic(tmom.getOccurrenceTypes());
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ITypeInstanceIndex#getOccurrences(org.tmapi.core.Topic)
     */
	@Override
	public Collection<Occurrence> getOccurrences(Topic type) {
		return Converter.setIOccurrenceToOccurrence(tmom.getOccurrencesByType((ITopic) type));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.TypeInstanceIndex#getRoleTypes()
     */
	@Override
	public Collection<Topic> getRoleTypes() {
		return Converter.setITopicToTopic(tmom.getRoleTypes());
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.TypeInstanceIndex#getRoles(org.tmapi.core.Topic)
     */
	@Override
	public Collection<Role> getRoles(Topic type) {
		return Converter.setIRoleToRole(tmom.getRolesByType((ITopic) type));
	}
	
	/* (non-Javadoc)
     * @see org.tmapi.index.ITypeInstanceIndex#getTopicTypes()
     */
	@Override
	public Collection<Topic> getTopicTypes() {
		return Converter.setITopicToTopic(tmom.getTopicTypes());
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic[])
     */
	@Override
	public Collection<Topic> getTopics(Topic type) {
		return Converter.setITopicToTopic(tmom.getTopicsByType((ITopic) type));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic[], boolean)
     */
	@Override
	public Collection<Topic> getTopics(Topic[] types, boolean matchall) {
		Set<ITopic> returnSet = CollectionFactory.createSet();
		Set<Set<ITopic>> results = CollectionFactory.createSet();
		for(Topic type : types) {
			results.add(tmom.getTopicsByType((ITopic) type));
		}
		Set<ITopic> removeAll = CollectionFactory.createSet();
		for(Set<ITopic> set : results) returnSet.addAll(set);
		if(matchall) {
			for(ITopic type : returnSet) {
				boolean flag = true;
				for(Set<ITopic> set : results) {
					if(!set.contains(type)) flag = false;
				}
				if(!flag) removeAll.add(type);
			}
			returnSet.removeAll(removeAll);
		}
		return Converter.setITopicToTopic(returnSet);
	}

}
