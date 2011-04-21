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

import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.Locator;

/**
 * {@link TopicMapSystem} interface for CouchTM.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public interface ITopicMapSystem extends TopicMapSystem{
	
	/**
	 * Returns a new ID.
	 * 
	 * @return id
	 */
	public String getNewId();
	
	/**
	 * Removes the topic map with the given <tt>Locator</tt> from the system.
	 * 
	 * @param locator
	 */
	public void removeTopicMap(Locator loc);
	
	/**
	 * removes the given <tt>ITopicMap</tt> from the system.
	 * 
	 * @param tm
	 */
	public void removeMap(ITopicMap tm);
}
