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

import org.tmapi.core.Association;
import org.tmapi.core.Topic;

/**
 * {@link Association} interface for CouchTM.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public interface IAssociation extends Association, IConstruct, IReifiable{
	
	/**
	 * Returns the <tt>IAssociation</tt> this association was merged in.
	 * 
	 * @return merged in association
	 */
	public IAssociation getMergedIn();

	/**
	 * Sets the association's parent.
	 * 
	 * @param parent
	 */
	public void setParent(ITopicMap tm);
	
	/**
	 * Sets the association's parent.
	 * 
	 * @param parent
	 * @param flag delete flag for possible later merging
	 */
	public void setParent(ITopicMap tm, boolean flag);
	
	/**
	 * Sets the variant's reifier.
	 * 
	 * @param reifier
	 * @param skipTest skip throwing a reifier changed event
	 */
	public void setReifier(Topic reifier, boolean skipTest);
	
	/**
	 * Removes the given <tt>IRole</tt> from the association.
	 * 
	 * @param role
	 */
	public void removeRole(IRole role);
	
	/**
	 * Adds the given <tt>IRole</tt> to the association.
	 * 
	 * @param role
	 */
	public void addRole(IRole role);
}
