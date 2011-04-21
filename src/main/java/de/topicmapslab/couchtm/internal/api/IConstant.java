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
 * Copied from tinyTIM and extended.
 */
package de.topicmapslab.couchtm.internal.api;

/**
 * Provides constants.
 * <p>
 * This interface is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public interface IConstant {
    /**
     * Initial size of the item identifier set.
     */
    public static final int CONSTRUCT_IID_SIZE = 4;
    /**
     * Initial size of the subject identifier set.
     */
    public static final int TOPIC_SID_SIZE = 4;
    /**
     * Initial size of the subject locator set.
     */
    public static final int TOPIC_SLO_SIZE = 2;
    /**
     * Initial size of the topic types set.
     */
    public static final int TOPIC_TYPE_SIZE = 2;
    /**
     * Initial size of the name set.
     */
    public static final int TOPIC_NAME_SIZE = 2;
    /**
     * Initial size of the occurrence set.
     */
    public static final int TOPIC_OCCURRENCE_SIZE = 2;
    /**
     * Initial size of the topic roles-played set.
     */
    public static final int TOPIC_ROLE_SIZE = 2;
    /**
     * Initial size of the association roles set.
     */
    public static final int ASSOC_ROLE_SIZE = 2;
    /**
     * Initial size of the name variants set.
     */
    public static final int NAME_VARIANT_SIZE = 2;
    /**
     * Documenttype string for topic maps in CouchDB.
     */
    public static final String TOPIC_MAP = "TOPIC_MAP";
    /**
     * Documenttype string for topics in CouchDB.
     */
    public static final String TOPIC = "TOPIC";
    /**
     * Documenttype string for names in CouchDB.
     */
    public static final String NAME = "NAME";
    /**
     * Document type string for variants in CouchDB.
     */
    public static final String VARIANT = "VARIANT";
    /**
     * Documenttype string for occurrences in CouchDB.
     */
    public static final String OCCURRENCE = "OCCURRENCE";
    /**
     * Documenttype string for roles in CouchDB.
     */
    public static final String ROLE = "ROLE";
    /**
     * Documenttype string for associations in CouchDB.
     */
    public static final String ASSOCIATION = "ASSOCIATION";
    /**
     * Value base.
     */
    public static final String XSD = "http://www.w3.org/2001/XMLSchema#";
    /**
     * Value: string.
     */
    public static final String XSD_STRING = XSD + "string";
    /**
     * Value: integer.
     */
    public static final String XSD_INTEGER = XSD + "integer";
    /**
     * Value: int.
     */
    public static final String XSD_INT = XSD + "int";
    /**
     * Value: float.
     */
    public static final String XSD_FLOAT = XSD + "float";
    /**
     * Value: decimal.
     */
    public static final String XSD_DECIMAL = XSD + "decimal";
    /**
     * Value: long.
     */
    public static final String XSD_LONG = XSD + "long";
    /**
     * Value: anyURI.
     */
    public static final String XSD_ANY_URI = XSD + "anyURI";
}
