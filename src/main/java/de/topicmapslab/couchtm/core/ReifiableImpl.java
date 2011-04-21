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

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

import de.topicmapslab.couchtm.internal.api.IReifiable;
import de.topicmapslab.couchtm.internal.api.ITopicMap;

/**
 * {@link IReifiable} implementation, placeholder class.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class ReifiableImpl extends ConstructImpl implements IReifiable{

	public ReifiableImpl(String id, ITopicMap tm) {
		super(id, tm);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Reifiable#getReifier()
	 */
	@Override
	public Topic getReifier() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Reifiable#setReifier(org.tmapi.core.Topic)
	 */
	@Override
	public void setReifier(Topic arg0) throws ModelConstraintException {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IReifiable#setReifier(org.tmapi.core.Topic, boolean)
	 */
	@Override
	public void setReifier(Topic reifier, boolean skipTest) {
		
	}

}
