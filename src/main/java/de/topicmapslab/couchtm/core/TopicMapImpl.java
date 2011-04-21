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

package de.topicmapslab.couchtm.core;

import java.util.Collection;
import java.util.Set;

import org.json.*;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;

import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.IOccurrence;
import de.topicmapslab.couchtm.internal.api.Event;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.ITopicMapSystem;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.IVariant;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.IReifiable;
import de.topicmapslab.couchtm.internal.utils.MergeUtils;
import de.topicmapslab.couchtm.internal.utils.TopicMapEventManager;
import de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager;
import de.topicmapslab.couchtm.internal.utils.Check;
import de.topicmapslab.couchtm.internal.utils.MergeCheck;
import de.topicmapslab.couchtm.internal.utils.CollectionFactory;
import de.topicmapslab.couchtm.internal.utils.Converter;
import de.topicmapslab.couchtm.internal.utils.DB;

import de.topicmapslab.couchtm.core.TopicImpl;
import de.topicmapslab.couchtm.index.*;

/**
 * {@link ITopicMap} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class TopicMapImpl extends ConstructImpl implements ITopicMap, IReifiable {
	
	private ITopicMapSystem sys;
	private TopicMapEventManager tmem;
	private ITopic reifier;
	private Locator locator;
	private boolean removed;
	
	public TopicMapImpl(String id, ITopicMapSystem sys, Set<Locator> iids, Locator locator, DB db) {
		super(id, new TopicMapObjectManager(1000000, db), iids);
		this.sys = sys;
		tmem = new TopicMapEventManager(this);
		this.locator = locator;
		reifier = null;
		super.setTopicMapEventManager(tmem);
		tmom.setTopicMap(this, locator);
		removed = false;
	}
	
	public TopicMapImpl(ITopicMapSystem sys, String id, String rev, Set<Locator> iids, Locator locator, DB db) {
		super(id, new TopicMapObjectManager(1000000, db), iids, rev);
		this.sys = sys;
		tmem = new TopicMapEventManager(this);
		this.locator = locator;
		reifier = null;
		super.setTopicMapEventManager(tmem);
		tmom.setTopicMap(this, locator);
		removed = false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#getLocator()
	 */
	@Override
	public Locator getLocator() {
		return locator;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopicMap#getTopicMapEventManager()
	 */
	@Override
	public TopicMapEventManager getTopicMapEventManager() {
		return tmem;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopicMap#getSystem()
	 */
	@Override
	public ITopicMapSystem getSystem() {
		return sys;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopicMap#getTopicMapObjectManager()
	 */
	@Override
	public TopicMapObjectManager getTopicMapObjectManager() {
		return tmom;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#close()
	 */
	@Override
	public void close() {
		if(removed) {
			throw new TMAPIRuntimeException("Topic map was removed.");
		}
		tmom.compactDB();
		sys.removeMap(this);
		tmom.close();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#createAssociation(org.tmapi.core.Topic, org.tmapi.core.Topic[])
	 */
	@Override
	public Association createAssociation(Topic type, Topic... scope) {
		Check.scopeNotNull(this, scope);
		Set<Topic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) {
			_scope.add(topic);
		}
		return createAssociation(type, _scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#createAssociation(org.tmapi.core.Topic, java.util.Collection)
	 */
	@Override
	public Association createAssociation(Topic type, Collection<Topic> scope) {
		Check.typeNotNull(this, type);
        Check.scopeNotNull(this, scope);
        Check.sameTopicMap(this, type);
        Check.sameTopicMap(this, scope);
        Set<ITopic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) {
			_scope.add((ITopic) topic);
		}
        IAssociation assoc = MergeCheck.createCheckAssociation((ITopic) type, _scope, tmom);
        if(assoc == null) {
        	assoc = new AssociationImpl(sys.getNewId(), this, (ITopic) type, _scope);
        	tmom.saveConstruct(assoc);
        	return assoc;
        }
		return assoc;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#createLocator(java.lang.String)
	 */
	@Override
	public Locator createLocator(String reference) {
		Locator loc = new LocatorImpl(reference);
		Check.locatorAbsolute(loc);
		return loc;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#createTopic()
	 */
	@Override
	public Topic createTopic() {
		String topicId = getSystem().getNewId();
		ITopic topic = new TopicImpl(topicId, this, new LocatorImpl("urn:x-couchtm:"+topicId), null, null);
		return (Topic) tmom.saveConstruct(topic);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#createTopicByItemIdentifier(org.tmapi.core.Locator)
	 */
	@Override
	public Topic createTopicByItemIdentifier(Locator iid) {
		Check.itemIdentifierNotNull(this, iid);
		ITopic topic = MergeCheck.createCheckTopicIID(iid, tmom);
		if(topic == null) {
			topic = new TopicImpl(sys.getNewId(), this, iid, null, null);
			tmom.saveConstruct(topic);
			return topic;
		}
		if(!topic.getItemIdentifiers().contains(iid)){
			topic.addIItemIdentifier(iid);
			tmom.saveConstruct(this);
		}
		return topic;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#createTopicBySubjectIdentifier(org.tmapi.core.Locator)
	 */
	@Override
	public Topic createTopicBySubjectIdentifier(Locator sid) {
		Check.itemIdentifierNotNull(this, sid);
		ITopic topic = MergeCheck.createCheckTopicSID(sid, tmom);
		if(topic == null) {
			topic = new TopicImpl(sys.getNewId(), this, null, sid, null);
			tmom.saveConstruct(topic);
			return topic;
		}
		if(!topic.getSubjectIdentifiers().contains(sid)){
			topic.addISubjectIdentifier(sid);
			tmom.saveConstruct(this);
		}
		return topic;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#createTopicBySubjectLocator(org.tmapi.core.Locator)
	 */
	@Override
	public Topic createTopicBySubjectLocator(Locator slo) {
		Check.subjectLocatorNotNull(this, slo);
		ITopic topic = MergeCheck.createCheckTopicSLO(slo, tmom);
		if(topic == null) {
			topic = new TopicImpl(sys.getNewId(), this, null, null, slo);
			tmom.saveConstruct(topic);
			return topic;
		}
		return topic;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#getAssociations()
	 */
	@Override
	public Set<Association> getAssociations() {
		return Converter.setIAssociationToAssociation(tmom.getAssociationsByTm());
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#getConstructById(java.lang.String)
	 */
	@Override
	public Construct getConstructById(String id) {
		return this.getId().equals(id) ? this : (Construct) tmom.getConstruct(id);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#getConstructByItemIdentifier(org.tmapi.core.Locator)
	 */
	@Override
	public Construct getConstructByItemIdentifier(Locator locator) {
		Construct construct = this.getItemIdentifiers().contains(locator) ? this : tmom.getConstruct(locator);
		if(construct != null && construct.getItemIdentifiers().contains(locator)) {
			return construct;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#getIndex(java.lang.Class)
	 */
	@Override
	public <I extends Index> I getIndex(Class<I> indexInterface) {
		if (indexInterface.getName().equals("org.tmapi.index.TypeInstanceIndex")) {
            return (I) new TypeInstanceIndexImpl(tmom);
        }
        if (indexInterface.getName().equals("org.tmapi.index.ScopedIndex")) {
            return (I) new ScopedIndexImpl(tmom);
        }
        if (indexInterface.getName().equals("org.tmapi.index.LiteralIndex")) {
            return (I) new LiteralIndexImpl(tmom);
        }
        throw new UnsupportedOperationException("Index '" + indexInterface.getName() + "'  is unknown");
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#getTopicBySubjectIdentifier(org.tmapi.core.Locator)
	 */
	@Override
	public Topic getTopicBySubjectIdentifier(Locator sid) {
		return (Topic) tmom.getTopicBySubjectIdentifier(sid);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#getTopicBySubjectLocator(org.tmapi.core.Locator)
	 */
	@Override
	public Topic getTopicBySubjectLocator(Locator slo) {
		return (Topic) tmom.getTopicBySubjectLocator(slo);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#getTopics()
	 */
	@Override
	public Set<Topic> getTopics() {
		return Converter.setITopicToTopic(tmom.getTopicsByTm());
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMap#mergeIn(org.tmapi.core.TopicMap)
	 */
	@Override
	public void mergeIn(TopicMap topicMap) {
		if(this.id.equals(topicMap.getId())) return;
		try {
			MergeUtils.merge(this, (ITopicMap) topicMap);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Reifiable#getReifier()
	 */
	@Override
	public Topic getReifier() {
		return (Topic) reifier;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IReifiable#setReifier(org.tmapi.core.Topic, boolean)
	 */
	@Override
	public void setReifier(Topic reifier, boolean skipTest) {
		this.reifier = (ITopic) reifier;
		tmom.saveConstruct(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Reifiable#setReifier(org.tmapi.core.Topic)
	 */
	@Override
	public void setReifier(Topic _reifier) throws ModelConstraintException {
		Check.sameTopicMap(this, _reifier);
		Check.reifierFree(this, _reifier);
		if(reifier != null) reifier.setReified(null);
		try {
			_fireEvent(Event.SET_REIFIER, reifier, _reifier);
			if(_reifier != null) reifier = (ITopic) _reifier;
			else reifier = null;
			tmom.saveConstruct(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#remove()
	 */
	@Override
	public void remove() {
		super.remove();
		sys.removeTopicMap(locator);
		tmom.close();
		removed = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#load()
	 */
	@Override
	protected void load() {
		//topic maps are always loaded
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#asJSONObject()
	 */
	@Override
	public JSONObject asJSONObject() {
		JSONObject topicMap = super.asJSONObject();
		try {
			if(reifier != null) topicMap.put("reifier", reifier.getId());
			if(locator != null) topicMap.put("locator", locator.getReference());
		} catch(Exception e) {
			e.printStackTrace(); 
		}
		return topicMap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#toJSON()
	 */
	@Override
	public String toJSON() {
		return asJSONObject().toString();
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isTopicMap()
	 */
	@Override
	public boolean isTopicMap() {
		return true;
	}

	@Override
	public void clear() {
		reifier = null;
		Set<IConstruct> constructs = tmom.getConstructsByTm();
		for(IConstruct construct : constructs) {
			String docType = construct.getDocumentType();
			if(docType.equals(IConstant.TOPIC)) ((ITopic) construct).remove(true);
			else if(docType.equals(IConstant.NAME)) ((IName) construct).remove(true);
			else if(docType.equals(IConstant.VARIANT)) ((IVariant) construct).remove(true);
			else if(docType.equals(IConstant.OCCURRENCE)) ((IOccurrence) construct).remove(true);
			else if(docType.equals(IConstant.ASSOCIATION)) ((IAssociation) construct).remove(true);
			else if(docType.equals(IConstant.ROLE)) ((IRole) construct).remove(true);
		}
		tmom.clear(this);
		tmom.saveTopicMap();
	}
	/**
	 * Returns self. Hack fpr RTM Tests.
	 */
	@Override
	public TopicMap getTopicMap() {
		return this;
	}
}
