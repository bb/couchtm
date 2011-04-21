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

import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TMAPIRuntimeException;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

/**
 * {@link Locator} implementation.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 */
public class LocatorImpl implements Locator{
	
	private static final String _EMPTY = "";
	private final URI uri;
	private final String reference;
	
	public LocatorImpl(String reference) {
        if (_EMPTY.equals(reference) || reference.charAt(0) == '#') {
            throw new MalformedIRIException("Illegal absolute IRI: '" + reference + "'");
        }
        try {
            this.reference = URLDecoder.decode(reference, "utf-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new TMAPIRuntimeException(ex);
        }
        uri = URI.create(reference.replace(" ", "%20").replace("+", "%20"));
    }

    public LocatorImpl(URI uri) {
        try {
            reference = URLDecoder.decode(uri.toString(), "utf-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new TMAPIRuntimeException(ex);
        }
        this.uri = uri;
    }
	
    /*
     * (non-Javadoc)
     * @see org.tmapi.core.Locator#getReference()
     */
	@Override
	public String getReference() {
		return reference;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Locator#resolve(java.lang.String)
	 */
	@Override
	public Locator resolve(String reference) {
		Locator loc = null;
		try{
			loc = new LocatorImpl(uri.resolve(reference.replaceAll("[ ]","%20")));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return loc;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.tmapi.core.Locator#toExternalForm()
	 */
	@Override
	public String toExternalForm() {
		return uri.toASCIIString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
    public int hashCode() {
        return reference.hashCode();
    }
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
    public String toString() {
        return uri.toString();
    }
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof LocatorImpl && reference.equals(((LocatorImpl) obj).reference));
    }

}
