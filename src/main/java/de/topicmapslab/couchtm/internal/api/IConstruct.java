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
 * Copied from tinyTIM and modified.
 */
package de.topicmapslab.couchtm.internal.api;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.IdentityConstraintException;
import org.json.*;

import de.topicmapslab.couchtm.internal.utils.TopicMapEventManager;
import de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager;

/**
 * {@link Construct} interface for CouchTM.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public interface IConstruct extends Construct {
	
	/**
	 * Returns the revision of the document in the database representing this construct.
	 * 
	 * @return rev
	 */
	public String getRev();
	
	/**
	 * Returns the document type of the construct.
	 * 
	 * @return docType
	 */
	public String getDocumentType();
	
	/**
	 * Sets the revision of the construct.
	 * 
	 * @param rev
	 */
	public void setRev(String rev);
	
	/**
	 * Sets the <tt>TopicMapEventManager</tt> for this construct.
	 * @param tmem
	 */
	public void setTopicMapEventManager(TopicMapEventManager tmem);
	
	/**
	 * Sets the <tt>IConstruct</tt> this construct was merged in.
	 * 
	 * @param construct
	 */
	public void setMergedIn(IConstruct construct);

    /**
     * Returns <tt>true</tt> if this is a topic map.
     *
     * @return <tt>true</tt> if this is a topic map, <tt>false</tt> otherwise.
     */
    public boolean isTopicMap();

    /**
     * Returns <tt>true</tt> if this is a topic.
     *
     * @return <tt>true</tt> if this is a topic, <tt>false</tt> otherwise.
     */
    public boolean isTopic();

    /**
     * Returns <tt>true</tt> if this is an association.
     *
     * @return <tt>true</tt> if this is an association, <tt>false</tt> otherwise.
     */
    public boolean isAssociation();

    /**
     * Returns <tt>true</tt> if this is a role.
     *
     * @return <tt>true</tt> if this is a role, <tt>false</tt> otherwise.
     */
    public boolean isRole();

    /**
     * Returns <tt>true</tt> if this is an occurrence.
     *
     * @return <tt>true</tt> if this is an occurrence, <tt>false</tt> otherwise.
     */
    public boolean isOccurrence();

    /**
     * Returns <tt>true</tt> if this is a name.
     *
     * @return <tt>true</tt> if this is a name, <tt>false</tt> otherwise.
     */
    public boolean isName();

    /**
     * Returns <tt>true</tt> if this is a variant.
     *
     * @return <tt>true</tt> if this is a variant, <tt>false</tt> otherwise.
     */
    public boolean isVariant();
    
    /**
     * Returns the JSONString representation of his construct.
     * 
     * @return JSON string
     */
    public String toJSON();
    
    /**
     * Returns this construct's the representation as a JSONObject.
     * 
     * @return JSONObject
     */
    public JSONObject asJSONObject();
    
    /**
     * Adds an item identifier without throwing an event.
     * 
     * @param iid
     */
    public void addIItemIdentifier(Locator iid);
    
    /**
	 * Sets the construct's parent.
	 * 
	 * @param parent
	 */
    public void setParent(IConstruct construct);
    
    /**
	 * Sets the construct's parent.
	 * 
	 * @param parent
	 * @param flag delete flag for possible later merging
	 */
    public void setParent(IConstruct construct, boolean flag);
    
    /**
     * Sets the <tt>ITopicMap</tt> the construct belongs to.
     * 
     * @param tm
     */
    public void setITopicMap(ITopicMap tm);
    
    /**
     * Sets the <tt>TopicMapObjectManager</tt> of the construct.
     * 
     * @param tmom
     */
    public void setTopicMapObjectManager(TopicMapObjectManager tmom);
    
    /**
     * Fires an event.
     * 
     * @param evt
     * @param oldValue
     * @param newValue
     * @throws TMAPIException
     * @throws IdentityConstraintException
     */
    public void _fireEvent(Event evt, Object oldValue, Object newValue) throws TMAPIException, IdentityConstraintException;
    
    /**
     * Equals method for constructs.
     * 
     * @param second
     * @return equals
     */
    public boolean equals(IConstruct second);
    
    /**
     * Returns the <tT>IConstruct</tt> this construct was merged in.
     * @return
     */
    public IConstruct getMergedIn();
    
    /**
     * Returns whether the construct is fully loaded or not.
     * 
     * @return loaded
     */
    public boolean getLoaded();
    
    /**
     * Merges the construct with the given construct.
     * 
     * @param construct
     */
    public void mergeWith(IConstruct construct);
    
    /**
     * Removes the construct from the topic map.
     * 
     * @param tmRemove <tt>true</tt> if triggered by tm.remove();
     */
    public void remove(boolean tmClear);
  
}
