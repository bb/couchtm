/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com)
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
 * 
 * Copied from tinyTIM.
 */
package de.topicmapslab.couchtm.internal.utils;

import java.util.Map;

import de.topicmapslab.couchtm.internal.api.IIntObjectMap;

/**
 * Default implementation of the {@link IIntObjectMap} which wraps a map.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 161 $ - $Date: 2008-09-18 15:02:34 +0200 (Do, 18 Sep 2008) $
 */
final class DefaultIntObjectMap<E> implements IIntObjectMap<E> {

    private final Map<Integer, E> _map;

    public DefaultIntObjectMap(Map<Integer, E> map) {
        _map = map;
    }

    /* (non-Javadoc)
     * @see de.topicmapslab.couchtm.internal.api.IIntObjectMap#get(int)
     */
    public E get(int key) {
        return _map.get(key);
    }

    /* (non-Javadoc)
     * @see de.topicmapslab.couchtm.internal.api.IIntObjectMapp#put(int, java.lang.Object)
     */
    public E put(int key, E value) {
        return _map.put(key, value);
    }

    /* (non-Javadoc)
     * @see de.topicmapslab.couchtm.internal.api.IIntObjectMap#clear()
     */
    public void clear() {
        _map.clear();
    }
}
