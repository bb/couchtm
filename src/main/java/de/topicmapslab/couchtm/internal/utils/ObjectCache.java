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

package de.topicmapslab.couchtm.internal.utils;

import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.IVariant;
import de.topicmapslab.couchtm.internal.api.IOccurrence;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IAssociation;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class to cache topic map constructs.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 *
 * @param <K> identifier
 * @param <V> construct
 */
public class ObjectCache<K extends Object, V extends IConstruct> extends LinkedHashMap<K,V>{
	
	//maximum size
	private final int MAX_ENTRIES;
	
	public ObjectCache(int max) {
		MAX_ENTRIES = max;
	}
}