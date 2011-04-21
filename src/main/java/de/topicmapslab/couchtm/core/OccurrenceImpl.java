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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

import de.topicmapslab.couchtm.internal.api.Event;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.IOccurrence;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.IScoped;
import de.topicmapslab.couchtm.internal.api.IReifiable;
import de.topicmapslab.couchtm.internal.api.ITyped;
import de.topicmapslab.couchtm.internal.api.IConstruct;

import de.topicmapslab.couchtm.internal.utils.Check;
import de.topicmapslab.couchtm.internal.utils.CollectionFactory;
import de.topicmapslab.couchtm.internal.utils.Converter;
import de.topicmapslab.couchtm.internal.utils.MergeUtils;

/**
 * {@link IOccurrence} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class OccurrenceImpl extends ConstructImpl implements IOccurrence, ITyped, IScoped, IReifiable{
	
	private ITopic parent;
	private String value;
	private Locator datatype;
	private Set<ITopic> scope;
	private ITopic type;
	private ITopic reifier;
	private boolean loaded;
	private IOccurrence mergedIn;

	public OccurrenceImpl(String id, ITopicMap tm) {
		super(id, tm, IConstant.OCCURRENCE);
		scope = CollectionFactory.createSet();
		loaded = false;
		mergedIn = null;
	}
	
	public OccurrenceImpl(IOccurrence occ) {
		super(occ);
		parent = (ITopic) occ.getParent();
		value = occ.getValue();
		datatype = occ.getDatatype();
		scope = CollectionFactory.createSet();
		scope.addAll(Converter.setTopicToITopic(occ.getScope()));
		type = (ITopic) occ.getType();
		reifier = (ITopic) occ.getReifier();
		loaded = occ.getLoaded();
		mergedIn = (IOccurrence) occ.getMergedIn();
	}
	
	public OccurrenceImpl(String id, ITopicMap tm, ITopic parent, ITopic type, Set<ITopic> scope, Locator datatype, String value) {
		super(id, tm, IConstant.OCCURRENCE, parent, true);
		this.type = type;
		this.scope = scope;
		this.parent = parent;
		this.value = value;
		this.datatype = datatype;
		this.reifier = null;
		loaded = true;
		mergedIn = null;
	}
	
	
	public OccurrenceImpl(String id, String rev, ITopicMap tm, Set<Locator> iids, ITopic parent, ITopic type, Set<ITopic> scope, Locator datatype, String value, ITopic reifier) {
		super(id, tm, IConstant.OCCURRENCE, parent, iids, rev);
		this.type = type;
		this.scope = scope;
		this.parent = parent;
		this.value = value;
		this.datatype = datatype;
		this.reifier = reifier;
		loaded = true;
		mergedIn = null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#mergeWith(de.topicmapslab.couchtm.internal.api.IConstruct)
	 */
	@Override
	public void mergeWith(IConstruct construct) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.mergeWith(construct);
			return;
		}
		if(reifier == null && ((IOccurrence) construct).getReifier() != null) {
			Topic tmpReifier = ((IOccurrence) construct).getReifier();
			((IOccurrence) construct).setReifier(null);
			setReifier(tmpReifier); 
		}
		else if(reifier != null && ((IOccurrence) construct).getReifier() != null) {
			try {
				MergeUtils.mergeTopics(reifier, (ITopic) ((IOccurrence) construct).getReifier(), true);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		super.mergeWith(construct);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getMergedIn()
	 */
	@Override
	public IOccurrence getMergedIn() {
		if(mergedIn != null) return mergedIn.getMergedIn();
		return mergedIn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setMergedIn(de.topicmapslab.couchtm.internal.api.IConstruct)
	 */
	@Override
	public void setMergedIn(IConstruct mergedIn) {
		if(this.mergedIn != null) {
			mergedIn.setMergedIn(mergedIn);
			return;
		}
		super.setMergedIn(mergedIn);
		this.mergedIn = (IOccurrence) mergedIn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getLoaded()
	 */
	@Override
	public boolean getLoaded() {
		if(mergedIn != null) return mergedIn.getLoaded();
		return loaded;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getParent()
	 */
	@Override
	public Topic getParent() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getParent();
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Typed#getType()
	 */
	@Override
	public Topic getType() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getType();
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Typed#setType(org.tmapi.core.Topic)
	 */
	@Override
	public void setType(Topic type) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setType(type);
			return;
		}
		Check.typeNotNull(this, type);
		Check.sameTopicMap(this, type);
		this.type = (ITopic) type;
		try {
			_fireEvent(Event.SET_TYPE, null, type);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#decimalValue()
	 */
	@Override
	public BigDecimal decimalValue() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.decimalValue();
		Check.NAN(datatype);
		return new BigDecimal(value);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#floatValue()
	 */
	@Override
	public float floatValue() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.floatValue();
		Check.NAN(datatype);
		return Float.parseFloat(value);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#getDatatype()
	 */
	@Override
	public Locator getDatatype() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getDatatype();
		return datatype;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#getValue()
	 */
	@Override
	public String getValue() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getValue();
		String reference = datatype.getReference();
		if(reference.equals(IConstant.XSD_STRING)) return value;
		else if(reference.equals(IConstant.XSD_ANY_URI)) return (new LocatorImpl(value)).getReference();
		else return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#intValue()
	 */
	@Override
	public int intValue() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.intValue();
		Check.NAN(datatype);
		String intString = value.substring(0, value.indexOf(".") == -1 ? value.length() : value.indexOf("."));
		Integer _value = new Integer(intString);
		return _value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#integerValue()
	 */
	@Override
	public BigInteger integerValue() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.integerValue();
		Check.NAN(datatype);
		String intString = value.substring(0, value.indexOf(".") == -1 ? value.length() : value.indexOf("."));
		BigInteger _value = new BigInteger(intString);
		return _value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#locatorValue()
	 */
	@Override
	public Locator locatorValue() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.locatorValue();
		return new LocatorImpl(value);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#longValue()
	 */
	@Override
	public long longValue() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.longValue();
		Check.NAN(datatype);
		String intString = value.substring(0, value.indexOf(".") == -1 ? value.length() : value.indexOf("."));
		Long _value = new Long(intString);
		return _value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setValue(value);
			return;
		}
		Check.valueNotNull(this, value);
		datatype = new LocatorImpl(IConstant.XSD_STRING);
		this.value = value;
		try {
			_fireEvent(Event.VALUE_CHANGED, null, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#setValue(org.tmapi.core.Locator)
	 */
	@Override
	public void setValue(Locator value) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setValue(value);
			return;
		}
		Check.valueNotNull(this, value);
		datatype = new LocatorImpl(IConstant.XSD_ANY_URI);
		this.value = value.getReference();
		try {
			_fireEvent(Event.VALUE_CHANGED, null, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#setValue(java.math.BigDecimal)
	 */
	@Override
	public void setValue(BigDecimal value) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setValue(value);
			return;
		}
		Check.valueNotNull(this, value);
		datatype = new LocatorImpl(IConstant.XSD_DECIMAL);
		this.value = value.toPlainString();
		try {
			_fireEvent(Event.VALUE_CHANGED, null, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#setValue(java.math.BigInteger)
	 */
	@Override
	public void setValue(BigInteger value) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setValue(value);
			return;
		}
		Check.valueNotNull(this, value);
		datatype = new LocatorImpl(IConstant.XSD_INTEGER);
		this.value = value.toString();
		try {
			_fireEvent(Event.VALUE_CHANGED, null, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#setValue(long)
	 */
	@Override
	public void setValue(long value) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setValue(value);
			return;
		}
		datatype = new LocatorImpl(IConstant.XSD_LONG);
		this.value = (new Long(value)).toString();
		try {
			_fireEvent(Event.VALUE_CHANGED, null, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#setValue(float)
	 */
	@Override
	public void setValue(float value) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setValue(value);
			return;
		}
		datatype = new LocatorImpl(IConstant.XSD_FLOAT);
		this.value = (new Float(value)).toString();
		try {
			_fireEvent(Event.VALUE_CHANGED, null, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#setValue(int)
	 */
	@Override
	public void setValue(int value) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setValue(value);
			return;
		}
		datatype = new LocatorImpl(IConstant.XSD_INT);
		this.value = (new Integer(value)).toString();
		try {
			_fireEvent(Event.VALUE_CHANGED, null, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.DatatypeAware#setValue(java.lang.String, org.tmapi.core.Locator)
	 */
	@Override
	public void setValue(String value, Locator datatype) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setValue(value, datatype);
			return;
		}
		Check.valueNotNull(this, value);
		Check.datatypeNotNull(this, datatype);
		this.datatype = datatype;
		this.value = value;
		try {
			_fireEvent(Event.VALUE_CHANGED, null, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Reifiable#getReifier()
	 */
	@Override
	public Topic getReifier() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getReifier();
		return reifier;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IOccurrence#setReifier(org.tmapi.core.Topic, boolean)
	 */
	@Override
	public void setReifier(Topic reifier, boolean skipTest) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setReifier(reifier, true);
		}
		this.reifier = (ITopic) reifier;
		tmom.saveConstruct(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Reifiable#setReifier(org.tmapi.core.Topic)
	 */
	@Override
	public void setReifier(Topic _reifier) throws ModelConstraintException {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setReifier(reifier);
			return;
		}
		Check.sameTopicMap(this, _reifier);
		Check.reifierFree(this, (Topic) _reifier);
		if(reifier != null) reifier.setReified(null);
		try {
			_fireEvent(Event.SET_REIFIER, reifier, _reifier);
			if(_reifier != null) reifier = (ITopic) _reifier;
			else reifier = null;
			tmom.saveConstruct(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Scoped#addTheme(org.tmapi.core.Topic)
	 */
	@Override
	public void addTheme(Topic theme) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.addTheme(theme);
			return;
		}
		Check.themeNotNull(this, theme);
		Check.sameTopicMap(this, theme);
		scope.add((ITopic) theme);
		try {
			_fireEvent(Event.SCOPE_CHANGED, null, theme);
		} catch(Exception e) {
			scope.remove((ITopic) theme);
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Scoped#getScope()
	 */
	@Override
	public Set<Topic> getScope() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getScope();
		return Collections.unmodifiableSet(Converter.setITopicToTopic(scope));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Scoped#removeTheme(org.tmapi.core.Topic)
	 */
	@Override
	public void removeTheme(Topic theme) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.removeTheme(theme);
			return;
		}
		ITopic rmTheme = null;
		for(ITopic theme2 : scope) {
			if(theme2.getId().equals(theme.getId())) {
				rmTheme = theme2;
				break;
			}
		}
		scope.remove(rmTheme);
		try {
			_fireEvent(Event.SCOPE_CHANGED, theme, null);
		} catch(Exception e) {
			scope.add((ITopic) theme);
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#remove(boolean)
	 */
	@Override
	public void remove(boolean tmClear) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.remove(tmClear);
			return;
		}
		if(!tmClear) {
			if(parent != null) parent.removeOccurrence(this);
			if(reifier != null) {
				reifier.setReified(null);
				reifier = null;
			}
		}
		super.remove();
		loaded = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#remove()
	 */
	@Override
	public void remove() {
		remove(false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#load()
	 */
	@Override
	protected void load() {
		IOccurrence occ = tmom.getOccurrence(id);
		if(parent == null) parent = (ITopic) occ.getParent();
		if(value == null) value = occ.getValue();
		if(datatype == null) datatype = occ.getDatatype();
		if(type == null) type = (ITopic) occ.getType();
		if(reifier == null) reifier = (ITopic) occ.getReifier();
		if(scope == null || scope.size() < 1) scope = Converter.setTopicToITopic(occ.getScope());
		loaded = true;
		super.load(occ);
		tmom.addConstruct(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#asJSONObject()
	 */
	@Override
	public JSONObject asJSONObject() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.asJSONObject();
		JSONObject occ = super.asJSONObject();
		try {
			if(type != null) occ.put("type", type.getId());
			if(reifier != null) occ.put("reifier", reifier.getId());
			if(parent != null) occ.put("parent", parent.getId());
			if(value != null) occ.put("value", value);
			if(scope != null && scope.size() > 0) {
				JSONArray topics = new JSONArray();
				for(Topic topic : scope) topics.put(topic.getId());
				occ.put("scope", topics);
			}
			if(datatype != null) occ.put("datatype", datatype.getReference());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return occ;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#toJSON()
	 */
	@Override
	public String toJSON() {
		if(mergedIn != null) return mergedIn.toJSON();
		return asJSONObject().toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isOccurrence()
	 */
	@Override
	public boolean isOccurrence() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IOccurrence#setParent(de.topicmapslab.couchtm.internal.api.ITopic)
	 */
	@Override
	public void setParent(ITopic parent) {
		setParent(parent, false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IOccurrence#setParent(de.topicmapslab.couchtm.internal.api.ITopic, boolean)
	 */
	@Override
	public void setParent(ITopic parent, boolean flag) {
		if(!loaded) load();
		if(mergedIn != null ) {
			mergedIn.setParent(parent);
			return;
		}
		ITopic oldParent = parent;
		this.parent = parent;
		super.setParent(parent);
		try {
			if(!flag) _fireEvent(Event.PARENT_CHANGED, oldParent, parent);
			else _fireEvent(Event.PARENT_CHANGED, null, parent);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
