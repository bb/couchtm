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
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.index.TypeInstanceIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.core.Association;
import org.tmapi.core.Variant;

import de.topicmapslab.couchtm.internal.api.Event;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.IOccurrence;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.IReifiable;

import de.topicmapslab.couchtm.internal.utils.MergeCheck;
import de.topicmapslab.couchtm.internal.utils.Converter;
import de.topicmapslab.couchtm.internal.utils.CollectionFactory;
import de.topicmapslab.couchtm.internal.utils.Check;
import de.topicmapslab.couchtm.internal.utils.MergeUtils;

/**
 * {@link ITopic} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class TopicImpl extends ConstructImpl implements ITopic{
	
	private ITopicMap parent;
	private Set<IName> names;
	private Set<IOccurrence> occurrences;
	private Set<IRole> rolesPlayed;
	private Set<ITopic> types;
	private IReifiable reified;
	private Set<Locator> subjectIdentifiers;
	private Set<Locator> subjectLocators;
	private boolean loaded;
	private ITopic mergedIn;
	
	public TopicImpl(String id, ITopicMap tm) {
		super(id, tm, IConstant.TOPIC);
		subjectIdentifiers = CollectionFactory.createSet(IConstant.TOPIC_SID_SIZE);
        occurrences = CollectionFactory.createIdentitySet(IConstant.TOPIC_OCCURRENCE_SIZE);
        names = CollectionFactory.createIdentitySet(IConstant.TOPIC_NAME_SIZE);
        rolesPlayed = CollectionFactory.createIdentitySet(IConstant.TOPIC_ROLE_SIZE);
        subjectLocators = CollectionFactory.createSet(IConstant.TOPIC_SLO_SIZE);
        types = CollectionFactory.createIdentitySet(IConstant.TOPIC_TYPE_SIZE);
        loaded = false;
        mergedIn = null;
	}
	
	public TopicImpl(String id, String rev, ITopicMap tm, Set<Locator> iids, Set<IName> names, Set<IOccurrence> occs, Set<IRole> roles, IReifiable reified, Set<Locator> sids, Set<Locator> slos, Set<ITopic> types) {
		super(id, tm, IConstant.TOPIC, tm, iids, rev);
		this.parent = tm;	
		this.names = names; 
		occurrences = occs; 
		rolesPlayed = roles;
		this.reified = reified;	
		subjectIdentifiers = sids;	
		subjectLocators = slos;	
		this.types = types;
		loaded = true;
		mergedIn = null;
	}
	
	//tm.createTopic
	public TopicImpl(String id, ITopicMap tm, Locator iid, Locator sid, Locator slo) {
		super(id, tm, IConstant.TOPIC, true);
		subjectIdentifiers = CollectionFactory.createSet(IConstant.TOPIC_SID_SIZE);
        occurrences = CollectionFactory.createIdentitySet(IConstant.TOPIC_OCCURRENCE_SIZE);
        names = CollectionFactory.createIdentitySet(IConstant.TOPIC_NAME_SIZE);
        rolesPlayed = CollectionFactory.createIdentitySet(IConstant.TOPIC_ROLE_SIZE);
        subjectLocators = CollectionFactory.createSet(IConstant.TOPIC_SLO_SIZE);
        types = CollectionFactory.createIdentitySet(IConstant.TOPIC_TYPE_SIZE);
		if(iid != null) super.iids.add(iid);
		if(sid != null) subjectIdentifiers.add(sid);
		if(slo != null) subjectLocators.add(slo);
		this.parent = tm;
		loaded = true;
		mergedIn = null;
	}
	
	public TopicImpl(ITopic topic) {
		super(topic);
		parent = (ITopicMap) topic.getParent();
		names = CollectionFactory.createSet();
		names.addAll(Converter.setNameToIName(topic.getNames()));
		occurrences = CollectionFactory.createIdentitySet(IConstant.TOPIC_OCCURRENCE_SIZE);
		occurrences.addAll(Converter.setOccurrenceToIOccurrence(topic.getOccurrences()));
		rolesPlayed = CollectionFactory.createIdentitySet(IConstant.TOPIC_ROLE_SIZE);
		rolesPlayed.addAll(Converter.setRoleToIRole(topic.getRolesPlayed()));
		types = CollectionFactory.createIdentitySet(IConstant.TOPIC_TYPE_SIZE);
		types.addAll(Converter.setTopicToITopic(topic.getTypes()));
		reified = (IReifiable) topic.getReified();
		subjectIdentifiers = CollectionFactory.createSet(IConstant.TOPIC_SID_SIZE);
		subjectIdentifiers.addAll(topic.getSubjectIdentifiers());
		subjectLocators = CollectionFactory.createSet(IConstant.TOPIC_SLO_SIZE);
		subjectLocators.addAll(topic.getSubjectLocators());
		loaded = topic.getLoaded();
		
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
		ITopic topic = (ITopic) construct;
		for(IName name : Converter.setNameToIName(topic.getNames())) {
			name.setParent(this);
			boolean flag = true;
			for(Name name2 :names) if(name.getId().equals(name2.getId())) flag = false;
			if(flag) names.add(name);
		}

		for(IOccurrence occ : Converter.setOccurrenceToIOccurrence(topic.getOccurrences())) {
			occ.setParent(this);
			boolean flag = true;
			for(Occurrence occ2 :occurrences) if(occ.getId().equals(occ2.getId())) flag = false;
			if(flag) occurrences.add(occ);
		}
		
		for(IRole role : Converter.setRoleToIRole(topic.getRolesPlayed())) {
			role.setPlayer(this);
			boolean flag = true;
			for(Role role2 :rolesPlayed) if(role.getId().equals(role2.getId())) flag = false;
			if(flag) rolesPlayed.add(role);
		}
		
		for(ITopic type : Converter.setTopicToITopic(topic.getTypes())) {
			boolean flag = true;
			for(ITopic type2 : types) if(type.getId().equals(type2)) flag = false;
			if(flag) types.add(type);
		}
		subjectIdentifiers.addAll(topic.getSubjectIdentifiers());
		subjectLocators.addAll(topic.getSubjectLocators());
		if(topic.getReified() != null) ((IReifiable)topic.getReified()).setReifier((Topic) this, true);
		TypeInstanceIndex idx = topic.getParent().getIndex(TypeInstanceIndex.class);
		for(ITopic typedTopic : Converter.setTopicToITopic(idx.getTopics(this))) {
			typedTopic.removeType(topic);
			typedTopic.addType(this);
		}
		for(Association assoc : idx.getAssociations(topic)) {
			assoc.setType(this);
		}
		for(Name name : idx.getNames(topic)) {
			name.setType(this);
		}
		for(Occurrence occ : idx.getOccurrences(topic)) {
			occ.setType(this);
		}
		for(Role role : idx.getRoles(topic)) {
			role.setType(this);
		}
		ScopedIndex sidx = topic.getParent().getIndex(ScopedIndex.class);
		for(Association assoc : sidx.getAssociations(topic)) {
			assoc.removeTheme(topic);
			assoc.addTheme(this);
		}
		for(Occurrence occ : sidx.getOccurrences(topic)) {
			occ.removeTheme(topic);
			occ.addTheme(this);
		}
		for(Name name : sidx.getNames(topic)) {
			name.removeTheme(topic);
			name.addTheme(this);
		}
		for(Variant variant : sidx.getVariants(topic)) {
			variant.removeTheme(topic);
			variant.addTheme(this);
		}
		
		//duplicate assoc suppression
		Collection<Role> typedRoles = idx.getRoles(this);
		Set<IRole> rolesByPlayer = tmom.getRolesByPlayer(this);
		Set<String> checked = CollectionFactory.createSet();
		for(Role role : typedRoles) {
			if(!checked.contains(role.getId())) {
				for(Role role2 : typedRoles) {
					if(!checked.contains(role2.getId()) && !role.getId().equals(role2.getId()) && role.getPlayer().getId().equals(role2.getPlayer().getId()) && !role.getParent().getId().equals(role2.getParent().getId())) {
						IAssociation assoc1 = (IAssociation) role.getParent();
						IAssociation assoc2 = (IAssociation) role2.getParent();
						checked.add(role.getId());
						checked.add(role2.getId());
						if(MergeCheck.assocSuppressionCheck(assoc1, assoc2)) {
							try {
								MergeUtils.merge(assoc1, assoc2);
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		for(Role role : rolesByPlayer) {
			if(!checked.contains(role.getId())) {
				for(Role role2 : rolesByPlayer) {
					if(!checked.contains(role2.getId()) && !role.getId().equals(role2.getId()) && role.getType().getId().equals(role2.getType().getId()) && !role.getParent().getId().equals(role2.getParent().getId())) {
						IAssociation assoc1 = (IAssociation) role.getParent();
						IAssociation assoc2 = (IAssociation) role2.getParent();
						checked.add(role.getId());
						checked.add(role2.getId());
						if(MergeCheck.assocSuppressionCheck(assoc1, assoc2)) {
							try {
								MergeUtils.merge(assoc1, assoc2);
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		super.mergeWith(construct);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopic#setReified(org.tmapi.core.Reifiable)
	 */
	@Override
	public void setReified(Reifiable _reified) {
		if(!loaded) load();
		if(mergedIn != null) {	
			mergedIn.setReified(_reified);
			return;
		}
		try {
			_fireEvent(Event.SET_REIFIED, reified, _reified);
		} catch(Exception e) {
			e.printStackTrace();
		}
		reified = (IReifiable) _reified;
		IConstruct existing = null;
		if((existing = MergeCheck.mergeCheckTopic(this)) != null) {
			try {
				MergeUtils.merge(this, existing);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			tmom.saveConstruct(this);
		}	
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopic#setReified(de.topicmapslab.couchtm.internal.api.IReifiable, boolean)
	 */
	public void setReified(IReifiable _reified, boolean skip) {
		reified = _reified;
		IConstruct existing = null;
		if((existing = MergeCheck.mergeCheckTopic(this)) != null) {
			try {
				MergeUtils.merge(this, existing);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			tmom.saveConstruct(this);
		}
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
	public ITopic getMergedIn() {
		if(mergedIn != null) return mergedIn.getMergedIn();
		return mergedIn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setMergedIn(de.topicmapslab.couchtm.internal.api.IConstruct)
	 */
	@Override
	public void setMergedIn(IConstruct topic) {
		if(mergedIn != null) {
			mergedIn.setMergedIn(topic);
			return;
		}
		super.setMergedIn(topic);
		this.mergedIn = (ITopic) topic;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getParent()
	 */
	@Override
	public TopicMap getParent() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getParent();
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#addSubjectIdentifier(org.tmapi.core.Locator)
	 */
	@Override
	public void addSubjectIdentifier(Locator sid) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.addSubjectIdentifier(sid);
			return;
		}
		Check.subjectIdentifierNotNull(this, sid);
		 if (subjectIdentifiers != null && subjectIdentifiers.contains(sid)) {
	            return;
	        }
	        
	        if (subjectIdentifiers == null) {
	            subjectIdentifiers = CollectionFactory.createSet(IConstant.CONSTRUCT_IID_SIZE);
	        }
	        subjectIdentifiers.add(sid);
	        try {
	        	_fireEvent(Event.ADD_SID, null, sid);
	        } catch(IdentityConstraintException e) {
	        	subjectIdentifiers.remove(sid);
	        	throw e;        	
	        }
	        catch(Exception e) {
	        	subjectIdentifiers.remove(sid);
	        	e.printStackTrace();
	        }		
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#addSubjectLocator(org.tmapi.core.Locator)
	 */
	@Override
	public void addSubjectLocator(Locator slo) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.addSubjectLocator(slo);
			return;
		}
		Check.subjectLocatorNotNull(this, slo);
		if (subjectLocators != null && subjectLocators.contains(slo)) {
            return;
        }
        
        if (subjectLocators == null) {
        	subjectLocators = CollectionFactory.createSet(IConstant.CONSTRUCT_IID_SIZE);
        }
        subjectLocators.add(slo);
        try {
        	_fireEvent(Event.ADD_SLO, null, slo);
        } catch(IdentityConstraintException e) {;
        	subjectLocators.remove(slo);
        	throw e;
        }
        catch(Exception e) {
        	subjectLocators.remove(slo);
        	e.printStackTrace();
        }		
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#addType(org.tmapi.core.Topic)
	 */
	@Override
	public void addType(Topic type) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.addType(type);
			return;
		}
		Check.typeNotNull(this, type);
        Check.sameTopicMap(this, type);
        if(types == null) types = CollectionFactory.createIdentitySet(IConstant.TOPIC_TYPE_SIZE);
        boolean flag = true;
        for(Topic _type : types) {
        	if(_type.getId().equals(type.getId())) flag = false;
        }
        if(flag) {
        	types.add((ITopic) type);
            tmom.saveConstruct(this);
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#createName(java.lang.String, org.tmapi.core.Topic[])
	 */
	@Override
	public Name createName(String value, Topic... scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createName(value, scope);
		Check.scopeNotNull(this, scope);
		Set<Topic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) {
			_scope.add(topic);
		}
		return createName(value, _scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#createName(java.lang.String, java.util.Collection)
	 */
	@Override
	public Name createName(String value, Collection<Topic> scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createName(value, scope);
		Check.scopeNotNull(this, scope);
		ITopic type = tmom.getTopicBySubjectIdentifier(new LocatorImpl("http://psi.topicmaps.org/iso13250/model/topic-name"));
		if(type == null) type = (ITopic) tm.createTopicBySubjectIdentifier(new LocatorImpl("http://psi.topicmaps.org/iso13250/model/topic-name"));
		return createName(type, value, scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#createName(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Topic[])
	 */
	@Override
	public Name createName(Topic type, String value, Topic... scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createName(type, value, scope);
		Check.scopeNotNull(this, scope);
		Set<Topic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) {
			_scope.add(topic);
		}
		return createName(type, value, _scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#createName(org.tmapi.core.Topic, java.lang.String, java.util.Collection)
	 */
	@Override
	public Name createName(Topic type, String value, Collection<Topic> scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createName(type, value, scope);
		Check.typeNotNull(this, type);
        Check.scopeNotNull(this, scope);
        Check.valueNotNull(this, value);
        Check.sameTopicMap(this, type);
        Check.sameTopicMap(this, scope);
        Set<ITopic> _scope= CollectionFactory.createSet();
		for(Topic topic : scope) _scope.add((ITopic) topic);
		IName name = MergeCheck.createCheckName((ITopic) type, value, _scope, this, tmom);
        if(name == null) {
        	name = new NameImpl(tm.getSystem().getNewId(), tm, value, this, (ITopic) type, _scope);
        	tmom.saveConstruct(name);
        	names.add(name);
        	tmom.saveConstruct(this);
        } else {
        	boolean flag = true;
        	for(IName _name : names) {
        		if(_name.getId().equals(name.getId())) flag = false;
        	}
        	if(flag) {
        		names.add(name);
            	tmom.saveConstruct(this);
        	}
        }
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Topic[])
	 */
	@Override
	public Occurrence createOccurrence(Topic type, String value, Topic... scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createOccurrence(type, value, scope);
		Check.scopeNotNull(this, scope);
		Set<Topic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) {
			_scope.add(topic);
		}
		Locator datatype = new LocatorImpl("http://www.w3.org/2001/XMLSchema#string");
		return createOccurrence(type, value, datatype, _scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, java.util.Collection)
	 */
	@Override
	public Occurrence createOccurrence(Topic type, String value,
			Collection<Topic> scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createOccurrence(type, value, scope);
		Locator datatype = new LocatorImpl("http://www.w3.org/2001/XMLSchema#string");
		return createOccurrence(type, value, datatype, scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, org.tmapi.core.Locator, org.tmapi.core.Topic[])
	 */
	@Override
	public Occurrence createOccurrence(Topic type, Locator value, Topic... scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createOccurrence(type, value, scope);
		Check.valueNotNull(this, value);
		Check.scopeNotNull(this, scope);
		Set<Topic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) {
			_scope.add(topic);
		}
		Locator datatype = new LocatorImpl("http://www.w3.org/2001/XMLSchema#anyURI");
		return createOccurrence(type, value.getReference(), datatype, _scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, org.tmapi.core.Locator, java.util.Collection)
	 */
	@Override
	public Occurrence createOccurrence(Topic type, Locator value,
			Collection<Topic> scope) {
		if(!loaded) load();
		if(mergedIn != null) mergedIn.createOccurrence(type, value, scope);
		Check.valueNotNull(this, value);
		Locator datatype = new LocatorImpl("http://www.w3.org/2001/XMLSchema#anyURI");
		return createOccurrence(type, value.getReference(), datatype, scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Locator, org.tmapi.core.Topic[])
	 */
	@Override
	public Occurrence createOccurrence(Topic type, String value, Locator datatype,
			Topic... scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createOccurrence(type, value, datatype, scope);
		Check.scopeNotNull(this, scope);
		Check.datatypeNotNull(this, datatype);
		Set<Topic> _scope = CollectionFactory.createSet();
		for(Topic topic : scope) {
			_scope.add(topic);
		}
		return createOccurrence(type, value, datatype, _scope);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Locator, java.util.Collection)
	 */
	@Override
	public Occurrence createOccurrence(Topic type, String value, Locator datatype,
			Collection<Topic> scope) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createOccurrence(type, value, datatype, scope);
		if(datatype.getReference().equals("Occurrence")) throw new ModelConstraintException(this, "createOccurrence(topic, \"Occurrence\", (Locator)null) is illegal");
		Check.typeNotNull(this, type);
        Check.scopeNotNull(this, scope);
        Check.valueNotNull(this, value);
        Check.sameTopicMap(this, type);
        Check.sameTopicMap(this, scope);
        Set<ITopic> _scope= CollectionFactory.createSet();
		for(Topic topic : scope) _scope.add((ITopic) topic);
        IOccurrence occ = MergeCheck.createCheckOccurrence((ITopic) type, value, datatype, _scope, this, tmom);
        if(occ == null) {
        	occ = new OccurrenceImpl(tm.getSystem().getNewId(), tm, this, (ITopic) type, _scope, datatype, value);
        	tmom.saveConstruct(occ);
        	occurrences.add(occ);
        	tmom.saveConstruct(this);
        } else {
        	boolean flag = true;
        	for(Occurrence _occ : occurrences) {
        		if(_occ.getId().equals(occ.getId())) flag = false;
        	}
        	if(flag) {
        		occurrences.add(occ);
            	tmom.saveConstruct(this);
        	}
        }
		return occ;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getNames()
	 */
	@Override
	public Set<Name> getNames() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getNames();
		return Collections.unmodifiableSet(Converter.setINameToName(names));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getNames(org.tmapi.core.Topic)
	 */
	@Override
	public Set<Name> getNames(Topic type) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getNames(type);
		Check.typeNotNull(type);
        Set<Name> _names = CollectionFactory.createSet();
        for (Name name: names) {
            if (type.getId().equals(name.getType().getId())) {
                _names.add(name);
            }
        }
        return _names;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getOccurrences()
	 */
	@Override
	public Set<Occurrence> getOccurrences() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getOccurrences();
		return Collections.unmodifiableSet(Converter.setIOccurrenceToOccurrence(occurrences));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getOccurrences(org.tmapi.core.Topic)
	 */
	@Override
	public Set<Occurrence> getOccurrences(Topic type) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getOccurrences(type);
		Check.typeNotNull(type);
		Set<Occurrence> occs = CollectionFactory.createSet();
        for (Occurrence occ: occurrences) {
            if (type.getId().equals(occ.getType().getId())) {
                occs.add(occ);
            }
        }
        return occs;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getReified()
	 */
	@Override
	public Reifiable getReified() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getReified();
		return reified;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getRolesPlayed()
	 */
	@Override
	public Set<Role> getRolesPlayed() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getRolesPlayed();
		return Collections.unmodifiableSet(Converter.setIRoleToRole(rolesPlayed));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getRolesPlayed(org.tmapi.core.Topic)
	 */
	@Override
	public Set<Role> getRolesPlayed(Topic type) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getRolesPlayed(type);
		Check.typeNotNull(type);
        if (rolesPlayed == null || rolesPlayed.size() < 1) {
            return Collections.emptySet();
        }
        Set<Role> roles = CollectionFactory.createSet(rolesPlayed.size());
        for (Role role: rolesPlayed) {
            if (type.getId().equals(role.getType().getId())) {
                roles.add(role);
            }
        }
        return roles;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getRolesPlayed(org.tmapi.core.Topic, org.tmapi.core.Topic)
	 */
	@Override
	public Set<Role> getRolesPlayed(Topic type, Topic assoc) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getRolesPlayed(type, assoc);
		Check.typeNotNull(type);
		if (assoc == null) {
            throw new IllegalArgumentException("The association type must not be null");
        }
        if (rolesPlayed == null) {
            return Collections.emptySet();
        }
        Set<Role> roles = CollectionFactory.createSet(rolesPlayed.size());
        for (Role role: rolesPlayed) {
            if (type.equals(role.getType()) && assoc.equals(role.getParent().getType())) {
                roles.add(role);
            }
        }
        return roles;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getSubjectIdentifiers()
	 */
	@Override
	public Set<Locator> getSubjectIdentifiers() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getSubjectIdentifiers();
		return Collections.unmodifiableSet(subjectIdentifiers);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getSubjectLocators()
	 */
	@Override
	public Set<Locator> getSubjectLocators() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getSubjectLocators();
		return Collections.unmodifiableSet(subjectLocators);
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#getTypes()
	 */
	@Override
	public Set<Topic> getTypes() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getTypes();
		return types == null ? Collections.<Topic>emptySet() : Collections.unmodifiableSet(Converter.setITopicToTopic(types));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#mergeIn(org.tmapi.core.Topic)
	 */
	@Override
	public void mergeIn(Topic other) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.mergeIn(other);
			return;
		}
		if(other.equals(this)) return;
		try {
			MergeUtils.merge(this, (IConstruct) other);
		} catch(ModelConstraintException e) {
			e.printStackTrace();
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#removeSubjectIdentifier(org.tmapi.core.Locator)
	 */
	@Override
	public void removeSubjectIdentifier(Locator sid) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.removeSubjectIdentifier(sid);
			return;
		}
		if (subjectIdentifiers == null || !subjectIdentifiers.contains(sid)) {
            return;
        }
		subjectIdentifiers.remove(sid);
		tmom.removeEntry(sid);
		try {
			_fireEvent(Event.REMOVE_SID, sid, null);
		} catch(Exception e) {
			tmom.addEntry(sid, this);
			subjectIdentifiers.add(sid);
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#removeSubjectLocator(org.tmapi.core.Locator)
	 */
	@Override
	public void removeSubjectLocator(Locator slo) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.removeSubjectLocator(slo);
			return;
		}
		if (subjectLocators == null || !subjectLocators.contains(slo)) {
            return;
        }
		subjectLocators.remove(slo);
		try {
			_fireEvent(Event.REMOVE_SLO, slo, null);
		} catch(Exception e) {
			subjectLocators.add(slo);
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Topic#removeType(org.tmapi.core.Topic)
	 */
	@Override
	public void removeType(Topic type) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.removeType(type);
			return;
		}
		ITopic rmType = null;
		for(ITopic type2 : types) {
			if(type2.getId().equals(type.getId())) {
				rmType = type2;
				break;
			}
		}
		types.remove(rmType);
		tmom.saveConstruct(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopic#setParent(de.topicmapslab.couchtm.internal.api.ITopicMap)
	 */
	@Override
	public void setParent(ITopicMap tm) {
		setParent(tm, false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopic#setParent(de.topicmapslab.couchtm.internal.api.ITopicMap, boolean)
	 */
	@Override
	public void setParent(ITopicMap tm, boolean flag) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setParent(tm);
			return;
		}
		super.setParent(tm);
		ITopicMap oldParent = parent;
		try {
			oldParent = parent;
			parent = tm;
			if(!flag) _fireEvent(Event.PARENT_CHANGED, oldParent, tm);
			else _fireEvent(Event.PARENT_CHANGED, null, tm);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#_fireEvent(de.topicmapslab.couchtm.internal.api.Event, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void _fireEvent(Event evt, Object oldValue, Object newValue) throws TMAPIException{
		if(mergedIn != null) {
			mergedIn._fireEvent(evt, oldValue, newValue);
		}
		if (tmem != null) {
            tmem.handleEvent(evt, this, oldValue, newValue);
        }
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#load()
	 */
	@Override
	protected void load() {
		if(loaded) return;
		ITopic topic = tmom.getTopic(id);
		if(parent == null) parent = (ITopicMap) topic.getParent();
		if(reified == null) reified = (IReifiable) topic.getReified();
		if(names == null || names.size() < 1) names = Converter.setNameToIName(topic.getNames());
		if(occurrences == null || occurrences.size() < 1) occurrences = Converter.setOccurrenceToIOccurrence(topic.getOccurrences());
		if(rolesPlayed == null || rolesPlayed.size() < 1) rolesPlayed = Converter.setRoleToIRole(topic.getRolesPlayed());
		if(types == null || types.size() < 1) types = Converter.setTopicToITopic(topic.getTypes());
		if(subjectIdentifiers == null || subjectIdentifiers.size() < 1) subjectIdentifiers.addAll(topic.getSubjectIdentifiers());
		if(subjectLocators == null || subjectLocators.size() < 1) subjectLocators.addAll(topic.getSubjectLocators());
		loaded = true;
		super.load(topic);
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
		JSONObject topic = super.asJSONObject();
		try {
			if(reified != null) topic.put("reified", reified.getId());
			if(subjectIdentifiers.size() > 0) {
				JSONArray sids = new JSONArray();
				for(Locator locator : subjectIdentifiers) sids.put(locator.getReference());
				topic.put("subjectidentifiers", sids);
			}
			if(subjectLocators.size() > 0) {
				JSONArray slos = new JSONArray();
				for(Locator locator : subjectLocators) slos.put(locator.getReference());
				topic.put("subjectlocators", slos);
			}
			if(names.size() > 0) {
				JSONArray names = new JSONArray();
				for(IName name : this.names) names.put(name.getId());
				topic.put("names", names);
			}
			if(occurrences.size() > 0) {
				JSONArray occs = new JSONArray();
				for(IOccurrence occ : occurrences) occs.put(occ.getId());
				topic.put("occurrences", occs);
			}
			if(rolesPlayed.size() > 0) {
				JSONArray roles = new JSONArray();
				for(IRole role : rolesPlayed) roles.put(role.getId());
				topic.put("roles", roles);
			}
			if(types.size() > 0) {
				JSONArray _types = new JSONArray();
				for(ITopic type : types) _types.put(type.getId());
				topic.put("types", _types);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return topic;
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
	 * @see de.topicmapslab.couchtm.internal.api.ITopic#addRolePlayed(de.topicmapslab.couchtm.internal.api.IRole)
	 */
	@Override
	public void addRolePlayed(IRole role) {
		rolesPlayed.add(role);
		tmom.saveConstruct(this);
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopic#removeRolePlayed(de.topicmapslab.couchtm.internal.api.IRole)
	 */
	@Override
	public void removeRolePlayed(IRole role) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.removeRolePlayed(role);
			return;
		}
		Role rmRole = null;
		for(Role role2 : rolesPlayed) {
			if(role2.getId().equals(role.getId())) {
				rmRole = role2;
				break;
			}
		}
		rolesPlayed.remove(rmRole);
		tmom.saveConstruct(this);
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopic#removeOccurrence(de.topicmapslab.couchtm.internal.api.IOccurrence)
	 */
	@Override
	public void removeOccurrence(IOccurrence occ) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.removeOccurrence(occ);
			return;
		}
		IOccurrence rmOcc = null;
		for(IOccurrence occ2 : occurrences) {
			if(occ2.getId().equals(occ.getId())) {
				rmOcc = occ2;
				break;
			}
		}
		occurrences.remove(rmOcc);
		tmom.saveConstruct(this);
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopic#removeName(de.topicmapslab.couchtm.internal.api.IName)
	 */
	@Override
	public void removeName(IName name) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.removeName(name);
			return;
		}
		IName rmName = null;
		for(IName name2 : names) {
			if(name2.getId().equals(name2.getId())){
				rmName = name2;
				break;
			}
		}
		names.remove(rmName);
		tmom.saveConstruct(this);
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
			Check.topicInUse(this);
		    IName[] rmNames = names.toArray(new NameImpl[0]);
		    IOccurrence[] rmOccs = occurrences.toArray(new OccurrenceImpl[0]);
		    for(IOccurrence rmOcc : rmOccs) {
			    rmOcc.remove();
		    }
		    for(IName rmName : rmNames) {
			    rmName.remove();
		    }
		    names.clear();
		    occurrences.clear();
		}
		super.remove();
		loaded = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isTopic()
	 */
	@Override
	public boolean isTopic() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.ITopic#addISubjectIdentifier(org.tmapi.core.Locator)
	 */
	@Override
	public void addISubjectIdentifier(Locator sid) {
		subjectIdentifiers.add(sid);
		
	}
}
