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

import java.util.Set;
import java.util.Map;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TMAPIException;

import de.topicmapslab.couchtm.internal.utils.CollectionFactory;
import de.topicmapslab.couchtm.internal.utils.SysDB;
import de.topicmapslab.couchtm.internal.utils.DB;
import de.topicmapslab.couchtm.internal.utils.IdGenerator;
import de.topicmapslab.couchtm.internal.utils.Check;
import de.topicmapslab.couchtm.internal.api.ITopicMapSystem;
import de.topicmapslab.couchtm.internal.api.ITopicMap;

/**
 * {@link ITopicMapSystem} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class TopicMapSystemImpl implements ITopicMapSystem {
	
	protected final Map<String, Boolean> _features;
    protected final Map<String, Object> _properties;
    protected Map<Locator, ITopicMap> locToTopicMap;
    protected final SysDB db;

    protected TopicMapSystemImpl(Map<String, Boolean> features, Map<String, Object> properties) throws TMAPIException {
        _features = features;
        _properties = properties;
        db = new SysDB((String) _properties.get("DB"), Integer.valueOf((String) _properties.get("PORT")));
        locToTopicMap = CollectionFactory.createMap();
    }
    
    protected TopicMapSystemImpl(Map<String, Boolean> features, Map<String, Object> properties, String db, int port) throws TMAPIException {
    	_features = features;
    	_properties = properties;
    	this.db = new SysDB(db, port);
        locToTopicMap = CollectionFactory.createMap();
    }
   
    /*
     * (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#close()
     */
	@Override
	public void close() {
		_features.clear();
        _properties.clear();
        for(Locator loc : locToTopicMap.keySet()){
        	locToTopicMap.get(loc).close();
        }
        db.releaseConnection();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystem#createLocator(java.lang.String)
	 */
	@Override
	public Locator createLocator(String reference) {
		return (Locator) (new LocatorImpl(reference));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystem#createTopicMap(org.tmapi.core.Locator)
	 */
	@Override
	public TopicMap createTopicMap(Locator locator) throws TopicMapExistsException {
		Check.locatorAbsolute(locator);
		if(getLocators().contains(locator)) {
			throw new TopicMapExistsException("A topic map with the IRI + '" + locator.getReference() + "' exists in the system");
		} else {
			try {
				Set<Locator> iids = CollectionFactory.createSet();
				String id = "ctm-"+getNewId();
				DB tmDB = new DB((String) _properties.get("DB"), Integer.valueOf((String) _properties.get("PORT")), id, this);
				ITopicMap tm = new TopicMapImpl(id, this, iids, locator, tmDB);
				//db.newTopicMapEntry(tm);
				ITopicMap tm2 = tmDB.saveTopicMap(tm);
				locToTopicMap.put(locator, tm);
				db.newTopicMapEntry(tm2);
				return (TopicMap) tm2;
			} catch(TMAPIException e) {
				//will only be thrown when the connection to the database is interrupted after this object was created
				throw new TopicMapExistsException("Database not reachable");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystem#createTopicMap(java.lang.String)
	 */
	@Override
	public TopicMap createTopicMap(String reference) throws TopicMapExistsException {
		return createTopicMap(createLocator(reference));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystem#getFeature(java.lang.String)
	 */
	@Override
	public boolean getFeature(String featureName) throws FeatureNotRecognizedException {
		final Boolean supported = _features.get(featureName);
        if (supported == null) {
            TopicMapSystemFactoryImpl.reportFeatureNotRecognized(featureName);
        }
        return supported.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystem#getLocators()
	 */
	@Override
	public Set<Locator> getLocators() {
		return db.getTopicMapLocators();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystem#getProperty(java.lang.String)
	 */
	@Override
	public Object getProperty(String propertyName) {
		return _properties.get(propertyName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystem#getTopicMap(java.lang.String)
	 */
	@Override
	public TopicMap getTopicMap(String reference) {
		return getTopicMap(createLocator(reference));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystem#getTopicMap(org.tmapi.core.Locator)
	 */
	@Override
	public TopicMap getTopicMap(Locator locator) {
		TopicMap tm = locToTopicMap.get(locator);
		if(tm != null) return tm;
		tm = (TopicMap) db.getTopicMap(locator, this);
		if(tm != null) locToTopicMap.put(locator, (ITopicMap) tm);
		return tm;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopicMapSystem#getNewId()
	 */
	public String getNewId() {
		return IdGenerator.nextId();
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopicMapSystem#removeTopicMap(org.tmapi.core.Locator)
	 */
	@Override
	public void removeTopicMap(Locator loc) {
		locToTopicMap.remove(loc);
		db.removeDb(loc);
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopicMapSystem#removeMap(de.topicmapslab.couchtm.internal.api.ITopicMap)
	 */
	@Override
	public void removeMap(ITopicMap tm) {
		locToTopicMap.remove(tm.getLocator());
	}

}
