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

import org.tmapi.core.Role;
import org.tmapi.core.Topic;

/**
 * {@link Role} interface for CouchTM.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public interface IRole extends Role, IConstruct, IReifiable{
	
	/**
	 * Returns the <tt>IRole</tt> this role was merged in.
	 * 
	 * @return merged in role
	 */
	public IRole getMergedIn();
	
	/**
	 * Sets the role's parent.
	 * 
	 * @param parent
	 */
	public void setParent(IAssociation parent);
	/**
	 * Sets the role's parent.
	 * 
	 * @param parent
	 * @param flag delete flag for possible later merging
	 */
	public void setReifier(Topic topic, boolean skipTest);
}
