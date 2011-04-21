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

import org.tmapi.core.Topic;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;
import org.tmapi.core.Role;
import org.tmapi.core.Association;

import de.topicmapslab.couchtm.internal.api.ITopic;
import de.topicmapslab.couchtm.internal.api.IName;
import de.topicmapslab.couchtm.internal.api.IOccurrence;
import de.topicmapslab.couchtm.internal.api.IVariant;
import de.topicmapslab.couchtm.internal.api.IRole;
import de.topicmapslab.couchtm.internal.api.IAssociation;

import de.topicmapslab.couchtm.internal.utils.CollectionFactory;

import java.util.Set;
import java.util.Collection;

/**
 * This class contains several static methods which convert the input
 * in a desired output of another form.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 *
 */
public class Converter {
	
	private static String intToHex(int s) {
		String str = Integer.toHexString(s);
		switch(str.length()) {
		case 1: str = "000" + str; break;
		case	2: str = "00"  + str; break;
		case	3: str = "0"   + str; break;
		default: break;
		}
		return str;
	}
	
	/**
	 * Converts <tt>str</tt> to a dbName string.
	 * 
	 * @param str String
	 * @return str String
	 */
	public static String stringToDBName(String str) {
		StringBuilder sb = new StringBuilder("ctm-");
		for(int i = 0; i < str.length(); i++) {
			sb.append(intToHex(str.codePointAt(i)));
		}
		return sb.toString();
	}
	
	private static String hexToCharacter(String hex) {
		int[] array = new int[1];
		array[0] = Integer.valueOf(hex,16).intValue();
		String str = new String(array, 0, 1);
		return  str;
	}
	
	/**
	 * Converts <tt>name</tt> as used in the database to a name
	 * used in CouchTM.
	 * 
	 * @param name database name
	 * @return string CouchTM name
	 */
	public static String dbNameToString(String name) {
		StringBuilder sb = new StringBuilder();
		for(int i = 4; i < name.length(); i+=4) {
			sb.append(hexToCharacter(name.substring(i, i+4)));
		}
		return sb.toString();
	}
	
	/**
	 * Converts a collection of <tt>ITopic</tt> to a set of <tt>Topic</tt>.
	 * 
	 * @param set Collection of ITopics
	 * @return set Set of Topics
	 */
	public static Set<Topic> setITopicToTopic(Collection<ITopic> set) {
		Set<Topic> topics = CollectionFactory.createSet();
		for(ITopic topic : set) {
			topics.add((Topic) topic);
		}
		return topics;
	}
	
	/**
	 * Converts a collection of <tt>Topic</tt> to a set of <tt>ITopic</tt>.
	 * 
	 * @param set Collection of Topics
	 * @return topics Set of ITopics
	 */
	public static Set<ITopic> setTopicToITopic(Collection<Topic> set) {
		Set<ITopic> topics = CollectionFactory.createSet();
		for(Topic topic : set) {
			topics.add((ITopic) topic);
		}
		return topics;
	}
	
	/**
	 * Converts a collection of <tt>IName</tt> to a set of <tt>Name</tt>.
	 * 
	 * @param set Collection of INames
	 * @return names Set of Names
	 */
	public static Set<Name> setINameToName(Collection<IName> set) {
		Set<Name> names = CollectionFactory.createSet();
		for(IName name : set) {
			names.add((Name) name);
		}
		return names;
	}
	
	/**
	 * Converts a collection of <tt>Name</tt> to a set of <tt>IName</tt>.
	 * 
	 * @param set Collection of Names
	 * @return  names Set of INames
	 */
	public static Set<IName> setNameToIName(Collection<Name> set) {
		Set<IName> names = CollectionFactory.createSet();
		for(Name name : set) {
			names.add((IName) name);
		}
		return names;
	}
	
	/**
	 * Converts a collection of <tt>IRole</tt> to a set of <tt>Role</tt>.
	 * 
	 * @param set Collection of IRoles
	 * @return roles set of Roles
	 */
	public static Set<Role> setIRoleToRole(Collection<IRole> set) {
		Set<Role> roles = CollectionFactory.createSet();
		for(IRole role : set) {
			roles.add((Role) role);
		}
		return roles;
	}
	
	/**
	 * Converts a collection of <tt>Association</tt> to a set of <tt>IAssociation</tt>.
	 * 
	 * @param set Collection of Association
	 * @return associations Set of IAssociations
	 */
	public static Set<IAssociation> setAssociationToIAssociation(Collection<Association> set) {
		Set<IAssociation> associations = CollectionFactory.createSet();
		for(Association assoc : set) {
			associations.add((IAssociation) assoc);
		}
		return associations;
	}
	
	/**
	 * Converts a collection of <tt>IAssociation</tt> to a set of <tt>Association</tt>.
	 * 
	 * @param set Collection of IAssociations
	 * @return associaitons Set of Associations
	 */
	public static Set<Association> setIAssociationToAssociation(Collection<IAssociation> set) {
		Set<Association> associations = CollectionFactory.createSet();
		for(IAssociation assoc : set) {
			associations.add((Association) assoc);
		}
		return associations;
	}
	
	/**
	 * Converts a collection of <tt>Role</tt> to a set of <tt>IRole</tt>.
	 * 
	 * @param set Collection of Roles
	 * @return roles Set of IRoles
	 */
	public static Set<IRole> setRoleToIRole(Collection<Role> set) {
		Set<IRole> roles = CollectionFactory.createSet();
		for(Role role : set) {
			roles.add((IRole) role);
		}
		return roles;
	}
	
	/**
	 * Converts a collection of <tt>IVariant</tt> to a set of <tt>Variant</tt>.
	 * 
	 * @param set Collection of IVariants
	 * @return variants set of Variants
	 */
	public static Set<Variant> setIVariantToVariant(Collection<IVariant> set) {
		Set<Variant> variants = CollectionFactory.createSet();
		for(IVariant variant : set) {
			variants.add((Variant) variant);
		}
		return variants;
	}
	
	/**
	 * Converts a collection of <tt>IVariant</tt> to a set of <tt>Variant</tt>.
	 * 
	 * @param set Collection of IVariants
	 * @return variants set of Variants
	 */
	public static Set<IVariant> setVariantToIVariant(Collection<Variant> set) {
		Set<IVariant> variants = CollectionFactory.createSet();
		for(Variant variant : set) {
			variants.add((IVariant) variant);
		}
		return variants;
	}
	
	/**
	 * Converts a Collection of <tt>IOccurrence</tt> to a set of <tt>Occurrence</tt>.
	 * 
	 * @param set Collection of IOccurrences
	 * @return occurrences Set of Occurrences
	 */
	public static Set<Occurrence> setIOccurrenceToOccurrence(Collection<IOccurrence> set) {
		Set<Occurrence> occurrences = CollectionFactory.createSet();
		for(Occurrence occurrence : set) {
			occurrences.add((Occurrence) occurrence);
		}
		return occurrences;
	}
	
	/**
	 * Converts a Collection of <tt>Occurrence</tt> to a set of <tt>IOccurrence</tt>.
	 * 
	 * @param set Collection of Occurrences
	 * @return occurrences Set of IOccurrences
	 */
	public static Set<IOccurrence> setOccurrenceToIOccurrence(Collection<Occurrence> set) {
		Set<IOccurrence> occurrences = CollectionFactory.createSet();
		for(Occurrence occurrence : set) {
			occurrences.add((IOccurrence) occurrence);
		}
		return occurrences;
	}

}
