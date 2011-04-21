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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.couchtm.internal.api.Event;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.IVariant;
import de.topicmapslab.couchtm.internal.api.IReifiable;
import de.topicmapslab.couchtm.internal.api.IScoped;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.ITyped;
import de.topicmapslab.couchtm.internal.api.IConstruct;

import de.topicmapslab.couchtm.internal.utils.CollectionFactory;
import de.topicmapslab.couchtm.internal.utils.Converter;
import de.topicmapslab.couchtm.internal.utils.Check;
import de.topicmapslab.couchtm.internal.utils.MergeCheck;
import de.topicmapslab.couchtm.internal.utils.MergeUtils;

/**
 * {@link IName} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class NameImpl extends ConstructImpl implements IName, IReifiable, IScoped, ITyped{
	
	private ITopic parent; 
	private String value; 
	private ITopic type; 
	private Set<ITopic> scope; 
	private Set<IVariant> variants; 
	private ITopic reifier; 
	private boolean loaded;
	private IName mergedIn;

	public NameImpl(String id, ITopicMap tm) {
		super(id, tm, IConstant.NAME);
		scope = CollectionFactory.createSet();
		variants = CollectionFactory.createIdentitySet(IConstant.NAME_VARIANT_SIZE);
		loaded = false;
		mergedIn = null;
	}
	
	public NameImpl(String id, ITopicMap tm, String value, ITopic parent, ITopic type, Set<ITopic> scope) {
		super(id, tm, IConstant.NAME, parent, true);
		this.value = value;
		this.scope = scope;
		this.parent = parent;
		this.type = type;
		this.reifier = null;
		variants = CollectionFactory.createIdentitySet(IConstant.NAME_VARIANT_SIZE);
		loaded = true;
		mergedIn = null;
	}
	
	public NameImpl(IName name) {
		super(name);
		parent = (ITopic) name.getParent();
		value = name.getValue();
		type = (ITopic) name.getType();
		scope = CollectionFactory.createSet();
		scope.addAll(Converter.setTopicToITopic(name.getScope()));
		variants = CollectionFactory.createSet();
		variants.addAll(Converter.setVariantToIVariant(name.getVariants()));
		reifier = (ITopic) name.getReifier();
		loaded = name.getLoaded();
		mergedIn = (IName) name.getMergedIn();
	}
	
	public NameImpl(String id, String rev, ITopicMap tm, Set<Locator> iids, String value, ITopic parent, ITopic type, Set<ITopic> scope, Set<IVariant> variants, ITopic reifier) {
		super(id, tm, IConstant.NAME, parent, iids, rev);
		this.value = value;
		this.parent = parent;
		this.type = type;
		this.scope = scope;
		this.variants = variants;
		this.reifier = reifier;
		loaded = true;
		mergedIn = null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.core.ConstructImpl#mergeWith(de.topicmapslab.couchtm.internal.api.IConstruct)
	 */
	@Override
	public void mergeWith(IConstruct construct) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.mergeWith(construct);
			return;
		}
		IName name = (IName) construct;
		for(IVariant variant : Converter.setVariantToIVariant(name.getVariants())) {
			variant.setParent(this);
			variants.add(variant);
		}
		if(reifier == null && ((IName) construct).getReifier() != null) {
			Topic tmpReifier = ((IName) construct).getReifier();
			((IName) construct).setReifier(null);
			setReifier(tmpReifier); 
		}
		else if(reifier != null && ((IName) construct).getReifier() != null) {
			try {
				MergeUtils.mergeTopics(reifier, (ITopic) ((IName) construct).getReifier(), true);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		super.mergeWith(name);
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
	 * @see org.tmapi.core.Name#createVariant(java.lang.String, org.tmapi.core.Topic[])
	 */
	@Override
	public Variant createVariant(String value, Topic... scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createVariant(value, scope);
		Check.scopeNotNull(this, scope);
		Check.valueNotNull(this, value);
		Set<Topic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) _scope.add(topic);
		return createVariant(value, _scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Name#createVariant(java.lang.String, java.util.Collection)
	 */
	@Override
	public Variant createVariant(String value, Collection<Topic> scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createVariant(value, scope);
		Check.valueNotNull(this, value);
		Locator datatype = new LocatorImpl("http://www.w3.org/2001/XMLSchema#string");
		return createVariant(value, datatype, scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Name#createVariant(org.tmapi.core.Locator, org.tmapi.core.Topic[])
	 */
	@Override
	public Variant createVariant(Locator value, Topic... scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createVariant(value, scope);
		Check.scopeNotNull(this, scope);
		Check.valueNotNull(this, value);
		Set<Topic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) _scope.add(topic);
		return createVariant(value, _scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Name#createVariant(org.tmapi.core.Locator, java.util.Collection)
	 */
	@Override
	public Variant createVariant(Locator value, Collection<Topic> scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createVariant(value, scope);
		Check.valueNotNull(this, value);
		Locator datatype = new LocatorImpl("http://www.w3.org/2001/XMLSchema#anyURI");
		return createVariant(value.getReference(), datatype, scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Name#createVariant(java.lang.String, org.tmapi.core.Locator, org.tmapi.core.Topic[])
	 */
	@Override
	public Variant createVariant(String value, Locator datatype, Topic... scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createVariant(value, datatype, scope);
		Check.scopeNotNull(this, scope);
		Check.valueNotNull(this, value, datatype);
		Set<Topic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) _scope.add(topic);
		return createVariant(value, datatype, _scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Name#createVariant(java.lang.String, org.tmapi.core.Locator, java.util.Collection)
	 */
	@Override
	public Variant createVariant(String value, Locator datatype,
			Collection<Topic> scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createVariant(value, datatype, scope);
		Check.valueNotNull(this, value, datatype);
		if(this.scope.containsAll(scope) && scope.containsAll(this.scope)) throw new ModelConstraintException(this, "The Variant's scope has to be a true subset of the Name's scope");
		Check.scopeNotNull(this, scope);
		Set<ITopic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) _scope.add((ITopic) topic);
		IVariant variant = MergeCheck.createCheckVariant(value, datatype, _scope, this, tm);
		if(variant == null) {
			variant = new VariantImpl(tm.getSystem().getNewId(), tm, this, value, datatype, _scope);
			tmom.saveConstruct(variant);
			variants.add(variant);
			tmom.saveConstruct(this);
		}
		return variant;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Name#getValue()
	 */
	@Override
	public String getValue() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getValue();
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Name#getVariants()
	 */
	@Override
	public Set<Variant> getVariants() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getVariants();
		return Collections.unmodifiableSet(Converter.setIVariantToVariant(variants));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Name#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setValue(value);
			return;
		}
		Check.valueNotNull(this, value);
		this.value = value;
		try {
			_fireEvent(Event.VALUE_CHANGED, null, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
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
	 * @see de.topicmapslab.couchtm.internal.api.IName#setReifier(org.tmapi.core.Topic, boolean)
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
			//never happening
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
			if(mergedIn != null) mergedIn.remove();
			IVariant[] rmVariants = variants.toArray(new VariantImpl[0]);
			for(IVariant rmVariant : rmVariants) {
				rmVariant.remove();
			}
			variants.clear();
			if(reifier != null) {
				reifier.setReified(null);
				reifier = null;
			}
			if(parent != null) parent.removeName(this);
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
		if(loaded) return;
		IName name = tmom.getName(id);
		if(parent == null) parent = (ITopic) name.getParent();
		if(value == null) value = name.getValue();
		if(reifier == null) reifier = (ITopic) name.getReifier();
		if(type == null) type = (ITopic) name.getType();
		if(scope == null || scope.size() < 1) scope = Converter.setTopicToITopic(name.getScope());
		if(variants == null || variants.size() < 1) variants = Converter.setVariantToIVariant(name.getVariants());
		loaded = true;
		super.load(name);
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
		JSONObject name = super.asJSONObject();
		try {
			if(type != null) name.put("type", type.getId());
			if(reifier != null) name.put("reifier", reifier.getId());
			if(parent != null) name.put("parent", parent.getId());
			if(value != null) name.put("value", value);
			if(scope != null && scope.size() > 0) {
				JSONArray topics = new JSONArray();
				for(Topic topic : scope) topics.put(topic.getId());
				name.put("scope", topics);
			}
			if(variants != null && variants.size() > 0) {
				JSONArray variantsArray = new JSONArray();
				for(Variant variant : variants) variantsArray.put(variant.getId());
				name.put("variants", variantsArray);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return name;
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
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getLoaded()
	 */
	@Override
	public boolean getLoaded() {
		if(mergedIn != null) return mergedIn.getLoaded();
		return loaded;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getMergedIn()
	 */
	@Override
	public IName getMergedIn() {
		if(mergedIn != null) return mergedIn.getMergedIn();
		return mergedIn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setMergedIn(de.topicmapslab.couchtm.internal.api.IConstruct)
	 */
	@Override
	public void setMergedIn(IConstruct name) {
		if(mergedIn != null) {
			mergedIn.setMergedIn(name);
			return;
		}
		super.setMergedIn(name);
		mergedIn = (IName) name;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IName#removeVariant(de.topicmapslab.couchtm.internal.api.IVariant)
	 */
	@Override
	public void removeVariant(IVariant variant) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.removeVariant(variant);
			return;
		}
		IVariant rmVariant = null;
		for(IVariant variant2 : variants) {
			if(variant2.getId().equals(variant.getId())) {
				rmVariant = variant2;
				break;
			}
		}
		variants.remove(rmVariant);
		tmom.saveConstruct(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isName()
	 */
	@Override
	public boolean isName() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IName#setParent(de.topicmapslab.couchtm.internal.api.ITopic)
	 */
	@Override
	public void setParent(ITopic parent) {
		setParent(parent, false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IName#setParent(de.topicmapslab.couchtm.internal.api.ITopic, boolean)
	 */
	@Override
	public void setParent(ITopic parent, boolean flag) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setParent(parent);
			return;
		}
		ITopic oldParent = this.parent;
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
