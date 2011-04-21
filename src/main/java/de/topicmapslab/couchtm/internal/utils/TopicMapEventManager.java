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

import de.topicmapslab.couchtm.internal.api.IScoped;
import de.topicmapslab.couchtm.internal.api.ITyped;
import de.topicmapslab.couchtm.internal.api.Event;
import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IAssociation;
import de.topicmapslab.couchtm.internal.api.IEventHandler;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.internal.api.IReifiable;
import org.tmapi.core.IdentityConstraintException;

import de.topicmapslab.couchtm.utils.Feature;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;

import java.util.Set;

/**
 * Class to handle Events.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 *
 */
public class TopicMapEventManager implements IEventHandler{
	
	private final TopicMapObjectManager tmom;
	private final ITopicMap tm;
	private boolean autoMerge;
	
	public TopicMapEventManager(ITopicMap tm) {
		this.tm = tm;
		tmom = this.tm.getTopicMapObjectManager();
		try {
			autoMerge = tm.getSystem().getFeature(Feature.AUTOMERGE);
		} catch(Exception e) {
			autoMerge = false;
		}	
	}
	/**
	 * Pass the event to the appropriate handling method.
	 * 
	 * @param evt event
	 * @param sender construct that caused the event
	 * @param oldValue old value
	 * @param newValue new value
	 */
	@Override
	public void handleEvent(Event evt, IConstruct sender, Object oldValue,
			Object newValue) throws TMAPIException, IdentityConstraintException {
		switch(evt) {
			case ADD_IID: addIid(sender, (Locator) newValue); break;
			case REMOVE_IID: removeIid(sender, (Locator) oldValue); break;
			case ADD_SID: addSid((ITopic) sender, (Locator) newValue); break;
			case REMOVE_SID: removeSid((ITopic) sender, (Locator) oldValue); break;
			case ADD_SLO: addSlo((ITopic) sender, (Locator) newValue); break;
			case REMOVE_SLO: removeSlo((ITopic) sender, (Locator) oldValue); break;
			case SET_REIFIER: setReifier((IReifiable) sender, (ITopic) oldValue, (ITopic) newValue); break;
			case SET_REIFIED: setReified((ITopic) sender, (IReifiable) oldValue, (IReifiable) newValue); break;
			case ROLES_CHANGED: rolesChanged((IAssociation) sender, (IRole) newValue); break;
			case SET_TYPE: setType((ITyped) sender, (ITopic) newValue); break;
			case SCOPE_CHANGED: scopeChanged((IScoped) sender, (ITopic) oldValue, (ITopic) newValue); break;
			case VALUE_CHANGED: valueChanged(sender, oldValue, newValue); break;
			case PARENT_CHANGED: parentChanged(sender, (IConstruct) oldValue, (IConstruct) newValue);
		}	
	}
	
	//parent has changed
	private void parentChanged(IConstruct sender, IConstruct oldParent, IConstruct newParent) {
		IConstruct existing = null;
		if((existing = MergeCheck.check(sender)) != null) {
			try {
				if(oldParent != null) MergeUtils.merge(sender, existing);
				else MergeUtils.merge(existing, sender, false);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			tmom.saveConstruct(sender);
		}
	}
	
	//value has changed
	private void valueChanged(IConstruct sender, Object oldValue, Object newValue) {
		IConstruct existing = null;
		if((existing = MergeCheck.check(sender)) != null) {
			try {
				MergeUtils.merge(sender, existing);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			tmom.saveConstruct(sender);
		}
	}
	
	//scope has changed
	private void scopeChanged(IScoped sender, ITopic oldValue, ITopic newValue) {
		IConstruct existing = null;
		if((existing = MergeCheck.check(sender)) != null) {
			try {
				MergeUtils.merge(sender, existing);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			tmom.saveConstruct(sender);
		}
	}
	
	//type has changed
	private void setType(ITyped sender, ITopic type) {
		IConstruct existing = null;
		if((existing = MergeCheck.check(sender)) != null) {
			try {
				MergeUtils.merge(sender, existing);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			tmom.saveConstruct(sender);
		}
	}
	
	//roles have changed
	private void rolesChanged(IAssociation assoc, IRole role) {
		IConstruct existing = null;
		if((existing = MergeCheck.mergeCheckAssociation(assoc)) != null) {
			try {
				MergeUtils.merge(assoc, existing);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			tmom.saveConstruct(assoc);
		}
	}
	
	//reifier has changed
	private void setReifier(IReifiable sender, ITopic oldReifier, ITopic newReifier) {
		if(oldReifier != null) {
			oldReifier.setReified(null);
		}
		if(newReifier != null) {
			IReifiable newReifiersOldReified = (IReifiable) newReifier.getReified();
			if(newReifiersOldReified != null) newReifiersOldReified.setReifier(null);
			newReifier.setReified(sender);
		}
	}
	
	private void setReified(ITopic sender, IReifiable oldReified, IReifiable newReified) {
		if(oldReified != null && oldReified.getReifier() != null) {
			oldReified.setReifier(null, true);
		}
		if(newReified != null) {
			if(newReified.getReifier() != null) {
				((ITopic)newReified.getReifier()).setReified(null, true);
				
			}
			newReified.setReifier(sender, true);
		}
		
	}
	
	//subject identifier has been added
	private void addSid(ITopic sender, Locator newValue) throws TMAPIException, IdentityConstraintException {
		ITopic existing = null;
		Set<ITopic> existingTopics = tmom.getTopicsBySubjectIdentifier(newValue);
		existingTopics.addAll(tmom.getTopicsByItemIdentifier(newValue));
		for(ITopic topic : existingTopics) {
			if(!topic.getId().equals(sender.getId())) existing = topic;
		}
		if(existing == null) {
			tmom.saveConstruct(sender);
		}
		else {
			if(!autoMerge) throw new IdentityConstraintException(sender, existing, newValue, "Merging needed but not supported");
			else MergeUtils.merge(sender, existing);
		}
	}
	
	//subject identifier has been removed
	private void removeSid(ITopic sender, Locator oldValue) throws TMAPIException {
		if(((ITopic) sender).getSubjectLocators().size() == 0 && ((ITopic) sender).getSubjectIdentifiers().size() == 0 && sender.getItemIdentifiers().size() == 0) throw new TMAPIException("The Topic must have at least one Identifier");
		tmom.saveConstruct(sender);
	}
	
	//subject locator has been added
	private void addSlo(ITopic sender, Locator newValue) throws TMAPIException, IdentityConstraintException {
		ITopic existing = null;
		Set<ITopic> existingTopics = tmom.getTopicsBySubjectLocator(newValue);
		for(ITopic topic : existingTopics) {
			if(!topic.getId().equals(sender.getId())) existing = topic;
		}
		if(existing == null) {
			tmom.saveConstruct(sender);
			
		}
		else {
			if(!autoMerge) throw new IdentityConstraintException(sender, existing, newValue, "Merging needed but not supported");
			else MergeUtils.merge(sender, existing);
		}
	}
	
	//subject locator has been removed
	private void removeSlo(ITopic sender, Locator oldValue) throws TMAPIException {
		if(((ITopic) sender).getSubjectLocators().size() == 0 && ((ITopic) sender).getSubjectLocators().size() == 0 && sender.getItemIdentifiers().size() == 0) throw new TMAPIException("The Topic must have at least one Identifier");
		tmom.saveConstruct(sender);
	}
	
	//item identifier has been added
	private void addIid(IConstruct sender, Locator newValue) throws IdentityConstraintException{
		IConstruct existing = null;
		existing = tmom.getConstruct(newValue);
		if(sender.getDocumentType().equals(IConstant.TOPIC)) {
			Set<ITopic> existingTopics = tmom.getTopicsByItemIdentifier(newValue);
			existingTopics.addAll(tmom.getTopicsBySubjectIdentifier(newValue));
			for(ITopic topic : existingTopics) {
				if(!topic.getId().equals(sender.getId())) existing = topic;
			}
		}
		if(existing == null || existing.equals(sender)) tmom.saveConstruct(sender);
		else {
			if(!sender.getDocumentType().equals(IConstant.TOPIC) || !existing.getDocumentType().equals(IConstant.TOPIC)) throw new IdentityConstraintException(sender, existing, newValue, "Topic Maps constructs with the same item identifier are not allowed");
			else if(!autoMerge) {
				sender.removeItemIdentifier(newValue);
				throw new IdentityConstraintException(sender, existing, newValue, "Topics have to be merged, but automerge is off");
			} else {
				try {
					MergeUtils.merge(sender, existing);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//item identifier has been removed
	private void removeIid(IConstruct sender, Locator oldValue) throws TMAPIException {
		if(sender.getDocumentType().equals(IConstant.TOPIC) && ((ITopic) sender).getSubjectLocators().size() == 0 && ((ITopic) sender).getSubjectLocators().size() == 0 && sender.getItemIdentifiers().size() == 0) throw new TMAPIException("The Topic must have at least one Identifier");
		tmom.saveConstruct(sender);
	}
}
