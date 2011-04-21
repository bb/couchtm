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

import org.json.*;

import org.tmapi.core.TopicMap;
import org.tmapi.core.Locator;
import org.tmapi.core.Construct;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.IdentityConstraintException;

import de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager;
import de.topicmapslab.couchtm.internal.utils.TopicMapEventManager;
import de.topicmapslab.couchtm.internal.utils.Check;
import de.topicmapslab.couchtm.internal.utils.CollectionFactory;
import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.Event;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.IConstant;

import java.util.Set;
import java.util.Collections;
import java.util.UUID;

/**
 * {@link IConstruct} implementation, base class for topic map constructs.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class ConstructImpl implements IConstruct{
	
	protected String id;
	private String rev;
	private String documentType;
	private Construct parent;
	protected ITopicMap tm;
	protected Set<Locator> iids;
	protected TopicMapObjectManager tmom;
	protected TopicMapEventManager tmem;
	private IConstruct mergedIn;
	private boolean loaded;
	
	//new topic map without rev
	public ConstructImpl(String id, TopicMapObjectManager tmom, Set<Locator> iids) {
		this.id = id;
		this.tmom = tmom;
		this.iids = iids;
		this.documentType = IConstant.TOPIC_MAP;
		//tm = null;
		tm = (ITopicMap) this;
		parent = null;
		loaded = true;
		mergedIn = null;
	}

	//new topic map with rev
	public ConstructImpl(String id, TopicMapObjectManager tmom, Set<Locator> iids, String rev) {
		this(id, tmom, iids);
		this.rev = rev;
	}

	public ConstructImpl(String id, ITopicMap tm) {
		this.id = id;
		this.tm = tm;
		tmom = tm.getTopicMapObjectManager();
		tmem = tm.getTopicMapEventManager();
		iids = CollectionFactory.createSet(IConstant.CONSTRUCT_IID_SIZE);
		parent = tm;
		loaded = false;
		mergedIn = null;
	}

	public ConstructImpl(String id, ITopicMap tm, String documentType, IConstruct parent, boolean loaded) {
		this.id = id;
		this.documentType = documentType;
		this.tm = tm;
		this.parent = parent;
		tmom = tm.getTopicMapObjectManager();
		tmem = tm.getTopicMapEventManager();
		iids = CollectionFactory.createSet(IConstant.CONSTRUCT_IID_SIZE);
		this.loaded = loaded;
		mergedIn = null;
	}
	
	public ConstructImpl(String id, ITopicMap tm, String documentType, boolean loaded) {
		this(id, tm, documentType);
		this.loaded = loaded;
	}
	
	public ConstructImpl(String id, ITopicMap tm, String documentType) {
		this.id = id;
		this.documentType = documentType;
		this.tm = tm;
		tmom = tm.getTopicMapObjectManager();
		tmem = tm.getTopicMapEventManager();
		iids = CollectionFactory.createSet(IConstant.CONSTRUCT_IID_SIZE);
		loaded = false;
		mergedIn = null;
	}

	public ConstructImpl(String id, ITopicMap tm, String documentType, IConstruct parent, Set<Locator> iids, String rev) {
		this(id, tm, documentType);
		this.parent = parent;
		this.iids = iids;
		this.rev = rev;
		loaded = true;
	}

	public ConstructImpl(IConstruct construct) {
		id = construct.getId();
		rev = construct.getRev();
		documentType = construct.getDocumentType();
		parent = construct.getParent();
		tm = (ITopicMap) construct.getTopicMap();
		iids = CollectionFactory.createSet(IConstant.CONSTRUCT_IID_SIZE);
		iids.addAll(construct.getItemIdentifiers());
		tmom = tm.getTopicMapObjectManager();
		tmem = tm.getTopicMapEventManager();
		mergedIn = construct.getMergedIn();
		loaded = construct.getLoaded();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setMergedIn(de.topicmapslab.couchtm.internal.api.IConstruct)
	 */
	@Override
	public void setMergedIn(IConstruct construct) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setMergedIn(construct);
			return;
		}
		this.id = construct.getId();
		mergedIn = construct;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setTopicMapEventManager(de.topicmapslab.couchtm.internal.utils.TopicMapEventManager)
	 */
	@Override
	public void setTopicMapEventManager(TopicMapEventManager tmem) {
		if(mergedIn != null) {
			mergedIn.setTopicMapEventManager(tmem);
			return;
		}
		this.tmem = tmem;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setTopicMapObjectManager(de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager)
	 */
	@Override
	public void setTopicMapObjectManager(TopicMapObjectManager tmom) {
		if(mergedIn != null) {
			mergedIn.setTopicMapObjectManager(tmom);
			return;
		}
		this.tmom = tmom;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getDocumentType()
	 */
	@Override
	public String getDocumentType() {
		if(mergedIn != null) return mergedIn.getDocumentType();
		return documentType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getRev()
	 */
	@Override
	public String getRev() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getRev();
		return rev;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setRev(java.lang.String)
	 */
	@Override
	public void setRev(String rev) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setRev(rev);
			return;
		}
		this.rev = rev;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Construct#addItemIdentifier(org.tmapi.core.Locator)
	 */
	@Override
	public void addItemIdentifier(Locator iid) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.addItemIdentifier(iid);
			return;
		}
		Check.itemIdentifierNotNull(this, iid);
        if (iids != null && iids.contains(iid)) {
            return;
        }    
        if (iids == null) {
            iids = CollectionFactory.createSet(IConstant.CONSTRUCT_IID_SIZE);
        }
        iids.add(iid);
        try {
        	_fireEvent(Event.ADD_IID, null, iid);
        } catch(IdentityConstraintException e) {
        	iids.remove(iid);
        	throw e;
        } catch(Exception e) {
        	iids.remove(iid);
        	e.printStackTrace();
        }
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#addIItemIdentifier(org.tmapi.core.Locator)
	 */
	public void addIItemIdentifier(Locator iid) {
		iids.add(iid);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Construct#removeItemIdentifier(org.tmapi.core.Locator)
	 */
	@Override
	public void removeItemIdentifier(Locator iid) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.removeItemIdentifier(iid);
			return;
		}
		if (iids == null || !iids.contains(iid)) {
            return;
        }
		tmom.removeEntry(iid);
		iids.remove(iid);
		try {
			_fireEvent(Event.REMOVE_IID, iid, null);
		} catch(Exception e) {
			iids.add(iid);
			tmom.addEntry(iid, this);
			e.printStackTrace();
		}
        
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Construct#getTopicMap()
	 */
	@Override
	public TopicMap getTopicMap() {
		if(mergedIn != null) return mergedIn.getTopicMap();
		return tm;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Construct#getParent()
	 */
	@Override
	public Construct getParent() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getParent();
		return parent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Construct#getItemIdentifiers()
	 */
	@Override
	public Set<Locator> getItemIdentifiers() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getItemIdentifiers();
		return iids == null ? Collections.<Locator>emptySet()
                : Collections.unmodifiableSet(iids);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Construct#getId()
	 */
	@Override
	public String getId() {
		if(mergedIn != null) return mergedIn.getId();
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isAssociation()
	 */
	@Override
	public boolean isAssociation() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isName()
	 */
	@Override
	public boolean isName() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isOccurrence()
	 */
	@Override
	public boolean isOccurrence() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isRole()
	 */
	@Override
	public boolean isRole() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isTopic()
	 */
	@Override
	public boolean isTopic() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isTopicMap()
	 */
	@Override
	public boolean isTopicMap() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isVariant()
	 */
	@Override
	public boolean isVariant() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Construct#remove()
	 */
	@Override
	public void remove() {
		if(mergedIn != null) {
			mergedIn.remove();
			return;
		}
		tmom.deleteConstruct(this);
		loaded = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#remove(boolean)
	 */
	@Override
	public void remove(boolean tmClear) {
		remove();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setParent(de.topicmapslab.couchtm.internal.api.IConstruct, boolean)
	 */
	public void setParent(IConstruct parent, boolean flag) {
		setParent(parent);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setParent(de.topicmapslab.couchtm.internal.api.IConstruct)
	 */
	public void setParent(IConstruct parent) {
		if(mergedIn != null) {
			mergedIn.setParent(parent);
			return;
		}
		this.parent = parent;
	}
	
	protected void load() {
		if(loaded) return;
		IConstruct construct = tmom.getConstruct(id);
		load(construct);
	}
	
	protected void load(IConstruct construct) {
		if(loaded) return;
		if(parent == null) parent = construct.getParent();
		if(iids == null || iids.size() < 1) iids.addAll(construct.getItemIdentifiers());
		if(rev == null) rev = construct.getRev();
		if(documentType == null) documentType = construct.getDocumentType();
		loaded = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#_fireEvent(de.topicmapslab.couchtm.internal.api.Event, java.lang.Object, java.lang.Object)
	 */
	public void _fireEvent(Event evt, Object oldValue, Object newValue) throws TMAPIException, IdentityConstraintException{
        if (tmem != null) {
            tmem.handleEvent(evt, this, oldValue, newValue);
        }
    }
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#asJSONObject()
	 */
	public JSONObject asJSONObject() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.asJSONObject();
		JSONObject construct = new JSONObject();
		try {	
			if(rev != null) construct.put("_rev", rev);
			if(documentType != null) construct.put("documenttype", documentType);
			if(parent != null) construct.put("parent", parent.getId());
			if(tm != null) construct.put("topicmap", tm.getId());
			if(iids.size() > 0) {
				JSONArray iids = new JSONArray();
				for(Locator locator : this.iids) iids.put(locator.getReference());
				construct.put("itemidentifiers", iids);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return construct;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setITopicMap(de.topicmapslab.couchtm.internal.api.ITopicMap)
	 */
	@Override
	public void setITopicMap(ITopicMap tm) {
		if(mergedIn != null) {
			mergedIn.setITopicMap(tm);
			return;
		}
		this.tm = tm;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#toJSON()
	 */
	public String toJSON() {
		if(mergedIn != null) return mergedIn.toJSON();
		return asJSONObject().toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#equals(de.topicmapslab.couchtm.internal.api.IConstruct)
	 */
	@Override
	public boolean equals(IConstruct second) {
		if(mergedIn != null) return mergedIn.equals(second);
		return(id.equals(second.getId()));
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if(mergedIn != null) return mergedIn.hashCode();
		return UUID.fromString(id.substring(4)).hashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getMergedIn()
	 */
	@Override
	public IConstruct getMergedIn() {
		if(mergedIn != null) return mergedIn.getMergedIn();
		return mergedIn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getLoaded()
	 */
	@Override
	public boolean getLoaded() {
		if(mergedIn != null) return mergedIn.getLoaded();
		return loaded;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#mergeWith(de.topicmapslab.couchtm.internal.api.IConstruct)
	 */
	@Override
	public void mergeWith(IConstruct construct) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.mergeWith(construct);
			return;
		}
		iids.addAll(construct.getItemIdentifiers());
	}
}
