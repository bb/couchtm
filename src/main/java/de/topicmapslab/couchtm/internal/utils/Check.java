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
 * Copied from tinyTIM, modifications made.
 */
package de.topicmapslab.couchtm.internal.utils;

import java.util.Collection;
import java.util.Set;
import java.net.URI;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;
import org.tmapi.core.TopicMap;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.IConstruct;
import de.topicmapslab.couchtm.internal.api.IConstant;

/**
 * Provides various argument constraint checks.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 299 $ - $Date: 2009-07-02 13:07:24 +0200 (Do, 02 Jul 2009) $
 */
public final class Check {

    private Check() {
        // noop.
    }

    /**
     * Throws a {@link ModelConstraintException} with the specified <tt>sender</tt>
     * and <tt>msg</tt>
     *
     * @param sender The sender
     * @param msg The error message
     */
    private static void _reportModelConstraintViolation(Construct sender, String msg) {
        throw new ModelConstraintException(sender, msg);
    }

    /**
     * Throws an {@link IllegalArgumentException}.
     *
     * @param msg
     */
    private static void _reportIllegalArgument(String msg) {
        throw new IllegalArgumentException(msg);
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>scope</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender.
     * @param scope The scope.
     */
    public static void scopeNotNull(Construct sender, Topic[] scope) {
        if (scope == null) {
            _reportModelConstraintViolation(sender, "The scope must not be null");
        }
    }
    
    /**
     * Throws a {@link ModelConstraintException} iff the <tt>scope</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender.
     * @param scope The scope.
     */
    public static void scopeNotNull(Construct sender, Collection<Topic> scope) {
        if (scope == null) {
            _reportModelConstraintViolation(sender, "The scope must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>type</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender.
     * @param type The type.
     */
    public static void typeNotNull(Construct sender, Topic type) {
        if (type == null) {
            _reportModelConstraintViolation(sender, "The type must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>value</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender.
     * @param value The value.
     */
    public static void valueNotNull(Construct sender, Object value) {
        if (value == null) {
            _reportModelConstraintViolation(sender, "The value must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>value</tt> or
     * the <tt>datatype</tt> is <tt>null</tt>.
     *
     * @param sender The sender.
     * @param value The value.
     * @param datatype The datatype.
     */
    public static void valueNotNull(Construct sender, Object value, Locator datatype) {
        valueNotNull(sender, value);
        if (datatype == null) {
            _reportModelConstraintViolation(sender, "The datatype must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>player</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender.
     * @param player The player.
     */
    public static void playerNotNull(Construct sender, Topic player) {
        if (player == null) {
            _reportModelConstraintViolation(sender, "The role player must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>iid</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender
     * @param iid The item identifier.
     */
    public static void itemIdentifierNotNull(Construct sender, Locator iid) {
        if (iid == null) {
            _reportModelConstraintViolation(sender, "The item identifier must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>sid</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender
     * @param sid The subject identifier.
     */
    public static void subjectIdentifierNotNull(Construct sender, Locator sid) {
        if (sid == null) {
            _reportModelConstraintViolation(sender, "The subject identifier must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>slo</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender
     * @param slo The subject locator.
     */
    public static void subjectLocatorNotNull(Construct sender, Locator slo) {
        if (slo == null) {
            _reportModelConstraintViolation(sender, "The subject locator must not be null");
        }
    }

    /**
     * Throws an {@link ModelConstraintException} iff the <tt>theme</tt> is 
     * <tt>null</tt>.
     *
     * @param theme The theme.
     */
    public static void themeNotNull(Construct sender, Topic theme) {
        if (theme == null) {
            _reportModelConstraintViolation(sender, "The theme must not be null");
        }
    }

    /**
     * Reports a {@link ModelConstraintException} iff the <tt>sender<tt> and
     * the <tt>construct</tt> do not belong to the same topic map.
     *
     * @param sender The sender.
     * @param construct The construct.
     */
    public static void sameTopicMap(Construct sender, Construct construct) {
        if (construct == null) {
            return;
        }
        TopicMap tm = ((IConstruct) sender).getDocumentType().equals(IConstant.TOPIC_MAP) ? (ITopicMap) sender : sender.getTopicMap();
        _sameTopicMap(sender, tm, construct);
    }

    /**
     * Checks whether all <tt>constructs</tt> are in the <tt>construct</tt>'s topic map.
     * 
     * @param sender
     * @param constructs
     */
    public static void sameTopicMap(Construct sender, Construct...constructs) {
        if (constructs == null || constructs.length == 0) {
            return;
        }
        TopicMap tm = ((IConstruct) sender).getDocumentType().equals(IConstant.TOPIC_MAP) ? (ITopicMap) sender : sender.getTopicMap();
        for (Construct construct: constructs) {
            _sameTopicMap(sender, tm, construct);
        }
    }

    /**
     * Checks whether all <tt>constructs</tt> are in the <tt>construct</tt>'s topic map.
     * 
     * @param sender
     * @param constructs
     */
    public static void sameTopicMap(Construct sender, Collection<? extends Construct> constructs) {
        if (constructs == null) {
            return;
        }
        TopicMap tm = ((IConstruct) sender).getDocumentType().equals(IConstant.TOPIC_MAP) ? (ITopicMap) sender : sender.getTopicMap();
        for (Construct construct: constructs) {
            _sameTopicMap(sender, tm, construct);
        }
    }
    
    /**
     * Checks whether two topic maps are equal.
     * 
     * @param first
     * @param second
     */
    public static void sameTopicMap(TopicMap first, TopicMap second) {
    	if(first.getId().equals(second.getId())) _reportModelConstraintViolation(first, "Can not merge the TopicMap with itself");
    }

    private static void _sameTopicMap(Construct sender, TopicMap tm, Construct other) {
    	if(!tm.getId().equals(other.getTopicMap().getId())){
    		_reportModelConstraintViolation(sender, "All constructs must belong to the same topic map");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} iff the <tt>type</tt> is 
     * <tt>null</tt>.
     *
     * @param type The type.
     */
    public static void typeNotNull(Topic type) {
        if (type == null) {
            _reportIllegalArgument("The type must not be null");
        }
    }

    /**
     * Reports an {@link IllegalArgumentException} iff the <tt>sid</tt> is 
     * <tt>null</tt>.
     *
     * @param sid The subject identifier.
     */
    public static void subjectIdentifierNotNull(Locator sid) {
        if (sid == null) {
            _reportIllegalArgument("addSubjectIdentifier(null) is illegal");
        }
    }

    /**
     * Reports an {@link IllegalArgumentException} iff the <tt>slo</tt> is 
     * <tt>null</tt>.
     *
     * @param slo The subject locator.
     */
    public static void subjectLocatorNotNull(Locator slo) {
        if (slo == null) {
            _reportIllegalArgument("addSubjectLocator(null) is illegal");
        }
    }

    /**
     * Reports an {@link IllegalArgumentException} iff the <tt>iid</tt> is 
     * <tt>null</tt>.
     *
     * @param iid The item identifier.
     */
    public static void itemIdentifierNotNull(Locator iid) {
        if (iid == null) {
            _reportIllegalArgument("The item identifier must not be null");
        }
    }
    /**
     * Reports a {@link ModelConstraintException} iff the <tt>_reifier</tt> 
     * reifies another construct.
     * 
     * @param sender
     * @param _reifier
     */
    
    public static void reifierFree(IConstruct sender, Topic _reifier) {
    	if(_reifier != null && _reifier.getReified() != null && !(_reifier.getReified().getId().equals(sender.getId()))) {
    		throw new ModelConstraintException(sender, "The reifier reifies already another construct");
    	}
    }
    
    /**
     * Reports a {@link ModelConstraintException} iff the <tt>type</tt> is
     * <tt>null</tt>.
     * 
     * @param sender
     * @param type
     */
    public static void typeNotNull(IConstruct sender, Topic type) {
    	if(type == null) throw new ModelConstraintException(sender, "Setting the type to null is not allowed");
    }
    
    /**
     * Reports a {@link ModelConstraintException} iff the <tt>theme</tt> is
     * <tt>null</tt>.
     * 
     * @param sender
     * @param type
     */
    public static void themeNotNull(IConstruct sender, Topic theme) {
    	if(theme == null) throw new ModelConstraintException(sender, "addTheme(null) is illegal");
    }
    
    /**
     * Reports a {@link ModelConstraintException} iff the <tt>scope</tt> is
     * empty.
     * 
     * @param sender
     * @param scope
     */
    public static void scopeNotNull(IConstruct sender, Set<Object> scope) {
    	if(scope.size() == 0) throw new ModelConstraintException(sender, "Creation with an empty scope is not allowed");
    }
    
    /**
     * Reports a {@link NumberFormatException} iff the <tt>datatype</tt> is
     * not decimal.
     *  
     * @param datatype
     */
    public static void NAN(Locator datatype) {
    	String dt = datatype.getReference();
		if(dt.equals(IConstant.XSD_STRING) || dt.equals(IConstant.XSD_ANY_URI)) throw new NumberFormatException("Datatype is not decimal");
    }
    
    /**
     * Reports a {@link ModelConstraintException} iff the <tt>datatype</tt> is
     * <tt>null</tt>.
     * @param sender
     * @param datatype
     */
    public static void datatypeNotNull(IConstruct sender, Locator datatype) {
    	if(datatype == null) throw new ModelConstraintException(sender, "The datatype must not be null");
    }
    
    /**
     * Reports a {@link TopicInUseException} iff the <tt>topic</tt> is in use.
     * 
     * @param topic
     */
    public static void topicInUse(Topic topic) {
    	if(topic.getReified() != null) throw new TopicInUseException(topic, "The topic is used as reifier");
		if(topic.getRolesPlayed().size() > 0) throw new TopicInUseException(topic, "The topic is used as a player");
		TypeInstanceIndex idx = topic.getParent().getIndex(TypeInstanceIndex.class);
		if(!(idx.getTopics(topic).size() == 0)) throw new TopicInUseException(topic, "The topic is used as a topic type");
		if(!(idx.getAssociations(topic).size() == 0)) throw new TopicInUseException(topic, "The topic is used as an association type");
		if(!(idx.getNames(topic).size() == 0)) throw new TopicInUseException(topic, "The topic is used as a name type");
		if(!(idx.getOccurrences(topic).size() == 0)) throw new TopicInUseException(topic, "The topic is used as an occurrence type");
		if(!(idx.getRoles(topic).size() == 0)) throw new TopicInUseException(topic, "The topic is used as a role type");
		ScopedIndex sidx = topic.getParent().getIndex(ScopedIndex.class);
		if(!(sidx.getAssociations(topic).size() == 0)) throw new TopicInUseException(topic, "The topic is used as an association theme");
		if(!(sidx.getOccurrences(topic).size() == 0)) throw new TopicInUseException(topic, "The topic is udes as an occurrence theme");
		if(!(sidx.getNames(topic).size() == 0)) throw new TopicInUseException(topic, "The topic is used as a name theme");
		if(!(sidx.getVariants(topic).size() == 0)) throw new TopicInUseException(topic, "The topic is used as a variant theme");
		
    }
    
    /**
     * Reports a {@link modelConstraintException} if the locator is not absulute
     * 
     * @param locator
     */
    public static void locatorAbsolute(Locator locator) {
    		URI uri = null;
    		try {
    			uri = new URI(locator.toExternalForm());
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    		if(!uri.isAbsolute()) throw new ModelConstraintException(null, "The Locator must be absolute");
    }
}