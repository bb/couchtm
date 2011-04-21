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

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.couchtm.utils.Feature;
import de.topicmapslab.couchtm.utils.Property;

import de.topicmapslab.couchtm.internal.utils.CollectionFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * {@link TopicMapSystemFactory} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class TopicMapSystemFactoryImpl extends TopicMapSystemFactory {
	
	private static final FeatureInfo[] _FEATURES = new FeatureInfo[] {
		new FeatureInfo(Feature.AUTOMERGE, true, true),
		new FeatureInfo(Feature.READ_ONLY, false, true),
		new FeatureInfo(Feature.TYPE_INSTANCE_ASSOCIATIONS, false, true)
	};
	
	private Map<String, Object> _properties;
    private Map<String, Boolean> _features;
	
	public TopicMapSystemFactoryImpl() {
        _properties = CollectionFactory.createMap();
        _features = CollectionFactory.createMap(_FEATURES.length);
        for (FeatureInfo feature: _FEATURES) {
            _features.put(feature.name, feature.defaultValue);
        }
        //default value
        _properties.put("DB", "localhost");
        _properties.put("PORT", "5984");
        _properties.put("printflag", "false");
    }
	
	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystemFactory#getFeature(java.lang.String)
	 */
	@Override
	public boolean getFeature(String featureName) throws FeatureNotRecognizedException {
		final Boolean supported = _features.get(featureName);
        if (supported == null) {
            reportFeatureNotRecognized(featureName);
        }
        return supported.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystemFactory#getProperty(java.lang.String)
	 */
	@Override
	public Object getProperty(String propertyName) {
		return _properties.get(propertyName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystemFactory#hasFeature(java.lang.String)
	 */
	@Override
	public boolean hasFeature(String featureName) {
		return _features.containsKey(featureName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystemFactory#newTopicMapSystem()
	 */
	@Override
	public TopicMapSystem newTopicMapSystem() throws TMAPIException {
		return new TopicMapSystemImpl(CollectionFactory.createMap(_features), CollectionFactory.createMap(_properties));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystemFactory#setFeature(java.lang.String, boolean)
	 */
	@Override
	public void setFeature(String featureName, boolean enabled)
			throws FeatureNotSupportedException, FeatureNotRecognizedException {
		if (!_features.containsKey(featureName)) {
            reportFeatureNotRecognized(featureName);
        }
        FeatureInfo feature = null;
        for (FeatureInfo feat: _FEATURES) {
            if (feat.name.equals(featureName)) {
                feature = feat;
                break;
            }
        }
        if (feature.fixed && feature.defaultValue != enabled) {
            throw new FeatureNotSupportedException("The feature '" + featureName + "' cannot be changed.");
        }
        _features.put(featureName, enabled);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.TopicMapSystemFactory#setProperty(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setProperty(String propertyName, Object value) {
		if (value != null) {
            if (Property.TMSHARE.equals(propertyName)) {
                if (!(value instanceof Set)) {
                    value = Collections.singleton(value);
                }
            }
            _properties.put(propertyName, value);
        }
        else {
            _properties.remove(propertyName);
        }
	}
	
	static void reportFeatureNotRecognized(String featureName) throws FeatureNotRecognizedException {
        throw new FeatureNotRecognizedException("The feature '" + featureName + "' is unknown");
    }
	
	private static class FeatureInfo {
        final String name;
        final boolean defaultValue;
        final boolean fixed;

        FeatureInfo(String name, boolean defaultValue, boolean fixed) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.fixed = fixed;
        }
    }

}
