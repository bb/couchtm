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

import java.util.Set;
import java.util.Collections;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.couchtm.internal.api.Event;
import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IReifiable;
import de.topicmapslab.couchtm.internal.api.ITyped;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.IScoped;

import de.topicmapslab.couchtm.internal.utils.CollectionFactory;
import de.topicmapslab.couchtm.internal.utils.Converter;
import de.topicmapslab.couchtm.internal.utils.Check;
import de.topicmapslab.couchtm.internal.utils.MergeUtils;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * {@link IAssociation} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class AssociationImpl extends ConstructImpl implements IAssociation, IReifiable, ITyped, IScoped{
	
	private ITopicMap parent;
	private ITopic type;
	private Set<IRole> roles;
	private ITopic reifier;
	private Set<ITopic> scope;
	private boolean loaded;
	private IAssociation mergedIn;

	public AssociationImpl(String id, ITopicMap tm) {
		super(id, tm, IConstant.ASSOCIATION);
		roles = CollectionFactory.createIdentitySet(IConstant.ASSOC_ROLE_SIZE);
		scope = CollectionFactory.createSet();
		loaded = false;
		mergedIn = null;
	}
	
	public AssociationImpl(IAssociation assoc) {
		super(assoc);
		parent = (ITopicMap) assoc.getParent();
		type = (ITopic) assoc.getType();
		roles = CollectionFactory.createIdentitySet(IConstant.ASSOC_ROLE_SIZE);
		roles.addAll(Converter.setRoleToIRole(assoc.getRoles()));
		reifier = (ITopic) assoc.getReifier();
		scope = CollectionFactory.createSet();
		scope.addAll(Converter.setTopicToITopic(assoc.getScope()));
		loaded = assoc.getLoaded();
		mergedIn = (IAssociation) assoc.getMergedIn();
	}
	
	public AssociationImpl(String id, String rev, ITopicMap tm, Set<Locator> iids, ITopic type, ITopic reifier, Set<IRole> roles, Set<ITopic> scope) {
		super(id, tm, IConstant.ASSOCIATION, tm, iids, rev);
		this.type = type;
		this.reifier = reifier;
		this.roles = roles;
		this.scope = scope;
		this.parent = tm;
		loaded = true;
		mergedIn = null;
	}
	
	public AssociationImpl(String id, ITopicMap tm, ITopic type, Set<ITopic> scope) {
		super(id, tm, IConstant.ASSOCIATION, true);
		this.type = type;
		this.scope = scope;
		this.roles = CollectionFactory.createIdentitySet(IConstant.ASSOC_ROLE_SIZE);
		this.parent = tm;
		this.reifier = null;
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
		if(reifier == null && ((IAssociation) construct).getReifier() != null) {
			Topic tmpReifier = ((IAssociation) construct).getReifier();
			((IAssociation) construct).setReifier(null);
			setReifier(tmpReifier); 
		}
		else if(reifier != null && ((IAssociation) construct).getReifier() != null) {
			try {
				MergeUtils.mergeTopics(reifier, (ITopic) ((IAssociation) construct).getReifier(), true);
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
	public IAssociation getMergedIn() {
		if(mergedIn != null) return mergedIn.getMergedIn();
		return mergedIn;
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
	 * @see org.tmapi.core.Association#createRole(org.tmapi.core.Topic, org.tmapi.core.Topic)
	 */
	@Override
	public Role createRole(Topic type, Topic player) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.createRole(type, player);
		Check.typeNotNull(this, type);
        Check.playerNotNull(this, player);
        Check.sameTopicMap(this, type, player);
        IRole role = null;
        for(IRole _role : roles) {
        	if(_role.getPlayer().equals(player) && _role.getType().equals(type)) role = _role;
        }
        if(role == null) {
        	role = new RoleImpl(tm.getSystem().getNewId(), tm, this, (ITopic) player, (ITopic) type);
        	tmom.saveConstruct(role);
        	roles.add(role);
            ((ITopic) player).addRolePlayed(role);
            try{
            	_fireEvent(Event.ROLES_CHANGED, null, role);
            } catch(Exception e) {
            	e.printStackTrace();
            }
        }
		return role;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Association#getRoleTypes()
	 */
	@Override
	public Set<Topic> getRoleTypes() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getRoleTypes();
		Set<Topic> roleTypes = CollectionFactory.createSet();
		for(IRole role : roles) {
			roleTypes.add(role.getType());
		}
		return roleTypes;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Association#getRoles()
	 */
	@Override
	public Set<Role> getRoles() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getRoles();
		return Collections.unmodifiableSet(Converter.setIRoleToRole(roles));
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Association#getRoles(org.tmapi.core.Topic)
	 */
	@Override
	public Set<Role> getRoles(Topic type) {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getRoles(type);
		Check.typeNotNull(type);
		Set<Role> rls = CollectionFactory.createSet();
		for(Role role : roles) {
			if(role.getType().equals(type)) rls.add(role);
		}
		return rls;
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
	 * @see de.topicmapslab.couchtm.internal.api.IAssociation#setReifier(org.tmapi.core.Topic, boolean)
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
		try{
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
		if(scope.contains(theme)) return;
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
			scope.add((ITopic)theme);
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IAssociation#setParent(de.topicmapslab.couchtm.internal.api.ITopicMap)
	 */
	@Override
	public void setParent(ITopicMap tm) {
		setParent(tm, false);
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IAssociation#setParent(de.topicmapslab.couchtm.internal.api.ITopicMap, boolean)
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
			IRole[] rmRoles = roles.toArray(new RoleImpl[0]);
			for(IRole rmRole : rmRoles) {
				rmRole.remove();
			}
			roles.clear();
			if(reifier != null) {
				reifier.setReified(null);
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
		IAssociation assoc = tmom.getAssociation(id);
		if(parent == null) parent = (ITopicMap) assoc.getParent();
		if(type == null) type = (ITopic) assoc.getType();
		if(scope == null || scope.size() < 1) scope = Converter.setTopicToITopic(assoc.getScope());
		if(roles == null || roles.size() < 1) roles = Converter.setRoleToIRole(assoc.getRoles());
		if(reifier == null) reifier = (ITopic) assoc.getReifier();
		loaded = true;
		super.load(assoc);
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
		JSONObject assoc = super.asJSONObject();
		try {
			if(type != null) assoc.put("type", type.getId());
			if(reifier != null) assoc.put("reifier", reifier.getId());
			if(scope != null && scope.size() > 0) {
				JSONArray topics = new JSONArray();
				for(Topic topic : scope) topics.put(topic.getId());
				assoc.put("scope", topics);
			}
			if(roles != null && roles.size() > 0) {
				JSONArray rolesArray = new JSONArray();
				for(Role role : roles) rolesArray.put(role.getId());
				assoc.put("roles", rolesArray);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return assoc;
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
	 * @see de.topicmapslab.couchtm.internal.api.IAssociation#addRole(de.topicmapslab.couchtm.internal.api.IRole)
	 */
	@Override
	public void addRole(IRole role) {
		if(!loaded) load();
		if(mergedIn != null) mergedIn.addRole(role);
		roles.add(role);
		try {
			_fireEvent(Event.ROLES_CHANGED, null, role);
		} catch(Exception e) {
			roles.remove(role);
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IAssociation#removeRole(de.topicmapslab.couchtm.internal.api.IRole)
	 */
	@Override
	public void removeRole(IRole role) {
		if(!loaded) load();
		if(mergedIn != null) mergedIn.removeRole(role);
		IRole rmRole = null;
		for(IRole role2 : roles) {
			if(role2.getId().equals(role.getId())) {
				rmRole = role2;
				break;
			}
		}
		roles.remove(rmRole);
		try {
			_fireEvent(Event.ROLES_CHANGED, null, role);
		} catch(Exception e) {
			roles.add(role);
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isAssociation()
	 */
	@Override
	public boolean isAssociation() {
		return true;
	}
}
