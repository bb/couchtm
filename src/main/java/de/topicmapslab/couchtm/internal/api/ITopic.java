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

import org.tmapi.core.Topic;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Locator;

/**
 * {@link Topic} interface for CouchTM.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public interface ITopic extends Topic, IConstruct {
	
	/**
	 * Sets the topic's parent.
	 * 
	 * @param parent
	 */
	public void setParent(ITopicMap tm);
	
	/**
	 * Sets the topics's parent.
	 * 
	 * @param parent
	 * @param flag delete flag for possible later merging
	 */
	public void setParent(ITopicMap tm, boolean flag);
	
	/**
	 * Sets the construct, this topic is reifying.
	 * 
	 * @param construct
	 */
	public void setReified(Reifiable construct);
	
	/**
	 * Returns the <tt>ITopic</tt> this topic was merged in.
	 * 
	 * @return merged in variant
	 */
	public ITopic getMergedIn();
	
	/**
	 * Adds the given role to the roles the topic is playing.
	 * 
	 * @param role
	 */
	public void addRolePlayed(IRole role);
	
	/**
	 * Removes the given role from the roles the topic is playing.
	 * 
	 * @param role
	 */
	public void removeRolePlayed(IRole role);
	
	/**
	 * Removes the given occurrence from the topic's occurrences.
	 * 
	 * @param occ
	 */
	public void removeOccurrence(IOccurrence occ);
	
	/**
	 * Removes the given name from the topic's names.
	 * 
	 * @param name
	 */
	public void removeName(IName name);
	
	/**
	 * Sets the topic's reified construct without throwing an event.
	 * 
	 * @param reifiable
	 * @param skip
	 */
	public void setReified(IReifiable _reifiable, boolean skip);
	
	/**
	 * Adds a subject identifier without throwing an event.
	 * 
	 * @param sid
	 */
	public void addISubjectIdentifier(Locator sid);
	
}
