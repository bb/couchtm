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

package de.topicmapslab.couchtm.internal.api;

import org.tmapi.core.TMAPIException;

/**
 * Event handler that is able to handle Topic Maps events.
 * 
 * @author hans
 */
public interface IEventHandler {

	/**
	 * Handles events.
	 * 
	 * @param evt event
	 * @param sender construct that caused the event
	 * @param oldValue old value
	 * @param newValue new value
	 */
	public void handleEvent(Event evt, IConstruct sender, Object oldValue, Object newValue) throws TMAPIException;
	
}
