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

import org.json.JSONObject;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;

import de.topicmapslab.couchtm.internal.api.Event;
import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.IReifiable;
import de.topicmapslab.couchtm.internal.api.ITyped;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.utils.Check;
import de.topicmapslab.couchtm.internal.utils.MergeCheck;
import de.topicmapslab.couchtm.internal.utils.MergeUtils;

/**
 * {@link IRole} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class RoleImpl extends ConstructImpl implements IRole, ITyped, IReifiable{
	
	private IAssociation parent;
	private ITopic player;
	private ITopic type;
	private ITopic reifier;
	private boolean loaded;
	private IRole mergedIn;

	public RoleImpl(String id, ITopicMap tm) {
		super(id, tm, IConstant.ROLE);
		loaded = false;
	}
	
	public RoleImpl(String id, String rev, ITopicMap tm, Set<Locator> iids, IAssociation parent, ITopic player, ITopic type, ITopic reifier) {
		super(id, tm, IConstant.ROLE, parent, iids, rev);
		this.parent = parent;
		this.player = player;
		this.type = type;
		this.reifier = reifier;
		loaded = true;
		mergedIn = null;
	}
	
	public RoleImpl(String id, ITopicMap tm, IAssociation parent, ITopic player, ITopic type) {
		super(id, tm, IConstant.ROLE, parent, true);
		this.player = player;
		this.type = type;
		this.parent = parent;
		this.reifier = null;
		loaded = true;
		mergedIn = null;
	}
	
	public RoleImpl(IRole role) {
		super(role);
		parent = (IAssociation) role.getParent();
		player = (ITopic) role.getPlayer();
		type = (ITopic) role.getType();
		reifier = (ITopic) role.getReifier();
		loaded = role.getLoaded();
		mergedIn = role.getMergedIn();
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
		if(reifier == null && ((IRole) construct).getReifier() != null) {
			Topic tmpReifier = ((IRole) construct).getReifier();
			((IRole) construct).setReifier(null);
			setReifier(tmpReifier); 
		}
		else if(reifier != null && ((IRole) construct).getReifier() != null) {
			try {
				MergeUtils.mergeTopics(reifier, (ITopic) ((IRole) construct).getReifier(), true);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		super.mergeWith(construct);
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
	public IRole getMergedIn() {
		if(mergedIn != null) return mergedIn.getMergedIn();
		return mergedIn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#setMergedIn(de.topicmapslab.couchtm.internal.api.IConstruct)
	 */
	@Override
	public void setMergedIn(IConstruct role) {
		if(mergedIn != null) {
			mergedIn.setMergedIn(role);
			return;
		}
		super.setMergedIn(role);
		mergedIn = (IRole) role;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#getParent()
	 */
	@Override
	public Association getParent() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getParent();
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Role#getPlayer()
	 */
	@Override
	public Topic getPlayer() {
		if(!loaded) load();
		if(mergedIn != null) return mergedIn.getPlayer();
		return player;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Role#setPlayer(org.tmapi.core.Topic)
	 */
	@Override
	public void setPlayer(Topic player) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setPlayer(player);
			return;
		}
		Check.playerNotNull(this, player);
		Check.sameTopicMap(this, player);
		if(this.player != null) this.player.removeRolePlayed(this);
		this.player = (ITopic) player;
		((ITopic) player).addRolePlayed(this);
		IConstruct existing = null;
		if((existing = MergeCheck.mergeCheckRole(this)) != null) {
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
	 * @see de.topicmapslab.couchtm.internal.api.IRole#setReifier(org.tmapi.core.Topic, boolean)
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
			if(player != null) player.removeRolePlayed(this);
			if(parent != null) parent.removeRole(this);
			if(reifier != null) {
				reifier.setReified(null);
			}
		}
		super.remove();
		loaded = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#load()
	 */
	@Override
	protected void load() {
		IRole role = tmom.getRole(id);
		if(parent == null) parent = (IAssociation) role.getParent();
		if(player == null) player = (ITopic) role.getPlayer();
		if(type == null) type = (ITopic) role.getType();
		if(reifier == null) reifier = (ITopic) role.getReifier();
		loaded = true;
		super.load(role);
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
		JSONObject role = super.asJSONObject();
		try {
			if(player != null) role.put("player", player.getId());
			if(type != null) role.put("type", type.getId());
			if(reifier != null) role.put("reifier", reifier.getId());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return role;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#toJSON()
	 */
	@Override
	public String toJSON() {
		return asJSONObject().toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IConstruct#isRole()
	 */
	@Override
	public boolean isRole() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.topicmapslab.couchtm.internal.api.IRole#setParent(de.topicmapslab.couchtm.internal.api.IAssociation)
	 */
	@Override
	public void setParent(IAssociation parent) {
		if(!loaded) load();
		if(mergedIn != null) {
			mergedIn.setParent(parent);
			return;
		}
		IAssociation oldParent = this.parent;
		this.parent = parent;
		super.setParent(parent);
		try {
			_fireEvent(Event.PARENT_CHANGED, oldParent, parent);
			parent.addRole(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
