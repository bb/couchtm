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

package de.topicmapslab.couchtm.index;

import java.util.Collection;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;
import org.tmapi.index.LiteralIndex;
import java.lang.IllegalArgumentException;

import de.topicmapslab.couchtm.internal.utils.Converter;
import de.topicmapslab.couchtm.internal.utils.TopicMapObjectManager;
import de.topicmapslab.couchtm.internal.api.IConstant;
import de.topicmapslab.couchtm.core.LocatorImpl;

/**
 * {@link org.tmapi.index.LiteralIndex} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class LiteralIndexImpl extends IndexImpl implements LiteralIndex {

	public LiteralIndexImpl(TopicMapObjectManager tmom) {
		super(tmom);
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getNames(java.lang.String)
     */
	@Override
	public Collection<Name> getNames(String value) {
		if(value == null) throw new IllegalArgumentException("getNames((String)null) is illegal");
		return Converter.setINameToName(tmom.getNamesByValue(value));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getOccurrences(java.lang.String)
     */
	@Override
	public Collection<Occurrence> getOccurrences(String value) {
		if(value == null) throw new IllegalArgumentException("getOccurrences((String)null) is illegal");
		return Converter.setIOccurrenceToOccurrence(tmom.getOccurrencesByValue(value, new LocatorImpl(IConstant.XSD_STRING)));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getOccurrences(org.tmapi.core.Locator)
     */
	@Override
	public Collection<Occurrence> getOccurrences(Locator value) {
		if(value == null) throw new IllegalArgumentException("getOccurrences((Locator)null) is illegal");
		return Converter.setIOccurrenceToOccurrence(tmom.getOccurrencesByValue(value, new LocatorImpl(IConstant.XSD_ANY_URI)));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getOccurrences(java.lang.String, org.tmapi.core.Locator)
     */
	@Override
	public Collection<Occurrence> getOccurrences(String value, Locator datatype) {
		if(datatype == null) throw new IllegalArgumentException("getOccurrences(\"value\", null) is illegal");
		return Converter.setIOccurrenceToOccurrence(tmom.getOccurrencesByValue(value, datatype));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getVariants(java.lang.String)
     */
	@Override
	public Collection<Variant> getVariants(String value) {
		if(value == null) throw new IllegalArgumentException("getVariants((String)null) is illegal");
		return Converter.setIVariantToVariant(tmom.getVariantsByValue(value, new LocatorImpl(IConstant.XSD_STRING)));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getVariants(org.tmapi.core.Locator)
     */
	@Override
	public Collection<Variant> getVariants(Locator value) {
		if(value == null) throw new IllegalArgumentException("getVariants((Locator)null) is illegal");
		return Converter.setIVariantToVariant(tmom.getVariantsByValue(value, new LocatorImpl(IConstant.XSD_ANY_URI)));
	}

	/* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getVariants(java.lang.String, org.tmapi.core.Locator)
     */
	@Override
	public Collection<Variant> getVariants(String value, Locator datatype) {
		if(datatype == null) throw new IllegalArgumentException("getVariants(\"value\", null) is illegal");
		return Converter.setIVariantToVariant(tmom.getVariantsByValue(value, datatype));
	}

}
