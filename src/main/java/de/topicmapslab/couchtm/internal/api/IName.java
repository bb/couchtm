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

import org.tmapi.core.Name;
import org.tmapi.core.Topic;

/**
 * {@link Name} interface for CouchTM.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public interface IName extends Name, IConstruct, IReifiable{
	
	/**
	 * Return the <tt>IName</tt> this name was merged in.
	 * 
	 * @return name
	 */
	public IName getMergedIn();
	
	/**
	 * Removes the given <tt>IVariant</tt> from the name.
	 * 
	 * @param variant
	 */
	public void removeVariant(IVariant variant);
	
	/**
	 * Sets the name's reifier.
	 * 
	 * @param reifier
	 * @param skipTest skip throwing a reifier changed event
	 */
	public void setReifier(Topic reifier, boolean skipTest);
	
	/**
	 * Sets the name's parent.
	 * 
	 * @param parent
	 */
	public void setParent(ITopic parent);
	
	/**
	 * Sets the name's parent.
	 * 
	 * @param parent
	 * @param flag delete flag for possible later merging
	 */
	public void setParent(ITopic parent, boolean flag);
}
