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

package de.topicmapslab.couchtm.internal.api;

import org.tmapi.core.TopicMap;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.couchtm.internal.api.ITopicMapSystem;
import de.topicmapslab.couchtm.internal.utils.TopicMapEventManager;

import de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager;

/**
 * {@link TopicMap} interface for CouchTM.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 *
 */
public interface ITopicMap extends TopicMap, IConstruct, IReifiable{
	
	/**
	 * Returns the TopicMap's <tt>TopicMapObjectManager</tt>.
	 * 
	 * @return tmom
	 */
	public TopicMapObjectManager getTopicMapObjectManager();
	
	/**
	 * Returns the TopicMap's <tt>TopicMapEventManager</tt>.
	 * 
	 * @return tmem
	 */
	public TopicMapEventManager getTopicMapEventManager();
	
	
	/**
	 * Returns the TopicMap's <tt>ITopicMapSystem</tt>.
	 * 
	 * @return sys
	 */
	public ITopicMapSystem getSystem();
	
	/**
	 * Returns the topic map's locator.
	 * 
	 * @return
	 */
	public Locator getLocator();
	
	/**
	 * Sets the variant's reifier.
	 * 
	 * @param reifier
	 * @param skipTest skip throwing a reifier changed event
	 */
	public void setReifier(Topic reifier, boolean skipTest);
	
	///**
	// * Returns the topic map.
	// */
	//public TopicMap getTopicMap();

}
