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

package de.topicmapslab.couchtm.index;

import de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager;

import org.tmapi.index.Index;

/**
 * Base class for all {@link Index} implementations.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 *
 */
public class IndexImpl implements Index{
	
	protected final TopicMapObjectManager tmom;
	
	public IndexImpl(TopicMapObjectManager tmom) {
		this.tmom = tmom;
	}
	
	/* (non-Javadoc)
     * @see org.tmapi.index.Index#close()
     */
	@Override
	public void close() {
		//nothing to do		
	}
	
	/* (non-Javadoc)
     * @see org.tmapi.index.Index#isAutoUpdated()
     */
	@Override
	public boolean isAutoUpdated() {
		return true;
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.Index#isOpen()
     */
	@Override
	public boolean isOpen() {
		return true;
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.Index#open()
     */
	@Override
	public void open() {
		//nothing to do
		
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.Index#reindex()
     */
	@Override
	public void reindex() {
		//nothing to do	
	}

}
