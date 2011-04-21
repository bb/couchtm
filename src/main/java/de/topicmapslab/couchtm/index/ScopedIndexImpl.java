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
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager;
import de.topicmapslab.couchtm.internal.utils.Converter;
import de.topicmapslab.couchtm.internal.utils.CollectionFactory;

import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.IVariant;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.IOccurrence;

import java.lang.IllegalArgumentException;


/**
 * {@link org.tmapi.index.ScopedIndex} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class ScopedIndexImpl extends IndexImpl implements ScopedIndex {

	public ScopedIndexImpl(TopicMapObjectManager tmom) {
		super(tmom);
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getAssociationThemes()
     */
	@Override
	public Collection<Topic> getAssociationThemes() {
		return Converter.setITopicToTopic(tmom.getAssociationThemes());
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getAssociations(org.tmapi.core.Topic)
     */
	@Override
	public Collection<Association> getAssociations(Topic theme) {
		return Converter.setIAssociationToAssociation(tmom.getAssociationsByTheme((ITopic) theme));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getAssociations(org.tmapi.core.Topic[], boolean)
     */
	@Override
	public Collection<Association> getAssociations(Topic[] theme, boolean matchall) {
		if(theme == null) throw new IllegalArgumentException("getAssociations(null, boolean) is illegal");
		Set<IAssociation> returnSet = CollectionFactory.createSet();
		Set<Set<IAssociation>> results = CollectionFactory.createSet();
		for(Topic topic : theme) {
			results.add(tmom.getAssociationsByTheme((ITopic) topic));
		}
		Set<IAssociation> removeAll = CollectionFactory.createSet();
		for(Set<IAssociation> set : results) returnSet.addAll(set);
		if(matchall) {
			for(IAssociation assoc : returnSet) {
				boolean flag = true;
				for(Set<IAssociation> set : results) {
					if(!set.contains(assoc)) flag = false;
				}
				if(!flag) removeAll.add(assoc);
			}
			returnSet.removeAll(removeAll);
		}
		return Converter.setIAssociationToAssociation(returnSet);
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getNameThemes()
     */
	@Override
	public Collection<Topic> getNameThemes() {
		return Converter.setITopicToTopic(tmom.getNameThemes());
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getNames(org.tmapi.core.Topic)
     */
	@Override
	public Collection<Name> getNames(Topic theme) {
		return Converter.setINameToName(tmom.getNamesByTheme((ITopic) theme));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getNames(org.tmapi.core.Topic[], boolean)
     */
	@Override
	public Collection<Name> getNames(Topic[] theme, boolean matchall) {
		if(theme == null) throw new IllegalArgumentException("getNames(null, boolean) is illegal");
		Set<IName> returnSet = CollectionFactory.createSet();
		Set<Set<IName>> results = CollectionFactory.createSet();
		for(Topic topic : theme) {
			results.add(tmom.getNamesByTheme((ITopic) topic));
		}
		Set<IName> removeAll = CollectionFactory.createSet();
		for(Set<IName> set : results) returnSet.addAll(set);
		if(matchall) {
			for(IName name : returnSet) {
				boolean flag = true;
				for(Set<IName> set : results) {
					if(!set.contains(name)) flag = false;
				}
				if(!flag) removeAll.add(name);
			}
			returnSet.removeAll(removeAll);
		}
		return Converter.setINameToName(returnSet);
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getOccurrenceThemes()
     */
	@Override
	public Collection<Topic> getOccurrenceThemes() {
		return Converter.setITopicToTopic(tmom.getOccurrenceThemes());
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getOccurrences(org.tmapi.core.Topic)
     */
	@Override
	public Collection<Occurrence> getOccurrences(Topic theme) {
		return Converter.setIOccurrenceToOccurrence(tmom.getOccurrencesByTheme((ITopic) theme));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getOccurrences(org.tmapi.core.Topic[], boolean)
     */
	@Override
	public Collection<Occurrence> getOccurrences(Topic[] theme, boolean matchall) {
		if(theme == null) throw new IllegalArgumentException("getOccurrences(null, boolean) is illegal");
		Set<IOccurrence> returnSet = CollectionFactory.createSet();
		Set<Set<IOccurrence>> results = CollectionFactory.createSet();
		for(Topic topic : theme) {
			results.add(tmom.getOccurrencesByTheme((ITopic) topic));
		}
		Set<IOccurrence> removeAll = CollectionFactory.createSet();
		for(Set<IOccurrence> set : results) returnSet.addAll(set);
		if(matchall) {
			for(IOccurrence occ : returnSet) {
				boolean flag = true;
				for(Set<IOccurrence> set : results) {
					if(!set.contains(occ)) flag = false;
				}
				if(!flag) removeAll.add(occ);
			}
			returnSet.removeAll(removeAll);
		}
		return Converter.setIOccurrenceToOccurrence(returnSet);
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getVariantThemes()
     */
	@Override
	public Collection<Topic> getVariantThemes() {
		Set<Topic> themes = CollectionFactory.createSet();
		themes.addAll(tmom.getVariantThemes());
		TypeInstanceIndex idx = new TypeInstanceIndexImpl(tmom);
		Collection<Name> names = idx.getNames(null);
		for(Name name : names) {
			if(name.getVariants().size() > 0) {
				themes.addAll(name.getScope());
			}
		}
		return themes;
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getVariants(org.tmapi.core.Topic)
     */
	@Override
	public Collection<Variant> getVariants(Topic theme) {
		if(theme == null) throw new IllegalArgumentException("getVariants(null) is illegal");
		Set<Variant> variants = CollectionFactory.createSet();
		variants.addAll(tmom.getVariantsByTheme((ITopic) theme));
		Set<Name> names = CollectionFactory.createSet();
		names.addAll(tmom.getNamesByTheme((ITopic) theme));
		for(Name name : names) {
			variants.addAll(name.getVariants());
		}
		return variants;
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getVariants(org.tmapi.core.Topic[], boolean)
     */
	@Override
	public Collection<Variant> getVariants(Topic[] theme, boolean matchall) {
		if(theme == null) throw new IllegalArgumentException("getVariants(null, boolean) is illegal");
		Set<IVariant> returnSet = CollectionFactory.createSet();
		Set<Set<IVariant>> results = CollectionFactory.createSet();
		for(Topic topic : theme) {
			Set<IVariant> variants = tmom.getVariantsByTheme((ITopic) topic);
			Set<IName> names = CollectionFactory.createSet();
			names.addAll(tmom.getNamesByTheme((ITopic) topic));
			for(Name name : names) variants.addAll(Converter.setVariantToIVariant(name.getVariants()));
			results.add(variants);
		}
		Collection<IVariant> removeAll = CollectionFactory.createSet();
		for(Set<IVariant> set : results) returnSet.addAll(set);
		if(matchall) {
			for(IVariant variant : returnSet) {
				boolean flag = true;
				for(Set<IVariant> set : results) {
					if(!set.contains(variant)) flag = false;
				}
				if(!flag) removeAll.add(variant);
			}
			returnSet.removeAll(removeAll);
		}
		return Converter.setIVariantToVariant(returnSet);
	}

}
