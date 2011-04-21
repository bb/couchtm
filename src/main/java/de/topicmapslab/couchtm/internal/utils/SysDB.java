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


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.net.URI;

import de.topicmapslab.couchtm.internal.api.ITopicMap;
import de.topicmapslab.couchtm.internal.api.ITopicMapSystem;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.HttpVersion;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * This class provides access to CouchDB.
 * 
 * @author Hans-Henning Koch (phi04bib[at]studserv.uni-leipzig.de)
 *
 */
public class SysDB {
	
	protected String url;
	protected int port;
	protected String dbName;
	protected HttpClient client;
	protected HttpGet get;
	protected HttpDelete delete;
	protected HttpPut put;
	protected HttpPost post;
	protected ResponseHandler<String> responseHandler;
	protected TopicMapDB topicMaps;
	protected boolean flag = true;
	
	public SysDB(String url, int port, String dbName, ITopicMapSystem sys) throws TMAPIException {
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		client = new DefaultHttpClient(params);
		responseHandler = new BasicResponseHandler();
		if(!dbUp()) throw new TMAPIException("Database not reachable");
		topicMaps = null;
		if(sys == null )updateTopicMapDB();
	}
	
	public SysDB(String url, int port) throws TMAPIException {
		this(url, port, null, null);
	}
	
	/**
	 * Query the server, not a specific database.
	 *  
	 * @param query
	 * @return result
	 */
	protected String getTopLevel(String query) {
		String responseBody = "{}";
		try {
			URI uri = URIUtils.createURI("http", url, port, query, null, null);
			get = new HttpGet(uri);
			responseBody = client.execute(get, responseHandler);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return responseBody;
	}
	
	/**
	 * Check whether the database server is running.
	 * 
	 * @return boolean
	 */
	protected boolean dbUp() {
		String result = getTopLevel("");
		if(JSONToObject.dbUp(result)) return true;
		else return false;
	}
	
	/**
	 * Method used to retrieve something from the database.
	 * 
	 * @param query
	 * @param key
	 * @return result
	 */
	protected String getMethod(String query, String key) {
		return getMethod(query, key, dbName);
	}
	
	/**
	 * Sends a GET request to the database.
	 * 
	 * @param query
	 * @param key
	 * @param db database name
	 * @return result string
	 */
	protected String getMethod(String query, String key, String db) {
		String responseBody = "{}";
		URI uri = null;
		if(key != null) {
			List<NameValuePair> pair = new ArrayList<NameValuePair>();
			pair.add(new BasicNameValuePair("key", key));
			key = URLEncodedUtils.format(pair, "UTF-8");
		}
		try {
			uri = URIUtils.createURI("http", url, port, db+"/"+query, key, null);
			//System.out.println("get: "+uri.toString());
			get = new HttpGet(uri);
			responseBody = client.execute(get, responseHandler);
		} catch(HttpResponseException e) {
			//System.out.println("status Code: "+e.getStatusCode());
			//System.out.println("URI: "+uri.toString());
			//System.out.println("query: "+query+" key: "+key+" db: "+db);
		} catch (Exception e) {
			//System.err.println(uri.toString());
			e.printStackTrace();
		} 
		return responseBody;
	}
	
	/**
	 * Insertion of a topic map construct in the database.
	 * 
	 * @param query Query to be executed
	 * @param entity topic map construct to be put in the database
	 * @return str Resultstring
	 */
	protected String putMethod(String query, String entity) {
		String responseBody = "{}";
		try {
			put = new HttpPut("http://"+url+":"+port+"/"+query);
			if(entity != null && entity.length() > 0) {
				StringEntity stringEntity = new StringEntity(entity, "UTF-8");
				stringEntity.setContentEncoding("application/json");
				put.setEntity(stringEntity);
			}
			//System.out.println("query: "+query);
			//System.out.println("entity "+entity);
			responseBody = client.execute(put, responseHandler);
		} catch(Exception e) {
			//System.out.println(put.getRequestLine());
			e.printStackTrace();
		}
		return responseBody;
	}
	
	/**
	 * Sends a POST request to the database.
	 * 
	 * @param query
	 * @param entity
	 * @returnresult string
	 */
	protected String postMethod(String query, String entity) {
		String responseBody = "";
		post = new HttpPost("http://"+url+":"+port+"/"+query);
		post.addHeader("Content-Type","application/json");
		try {
			if(entity != null && entity.length() > 0) {
				StringEntity stringEntity = new StringEntity(entity, "UTF-8");
				post.setEntity(stringEntity);
			}
			responseBody = client.execute(post, responseHandler);
		} catch(Exception e) {
			System.err.println(post.getRequestLine());
			e.printStackTrace();
		}
		return responseBody;
	}
	
	/**
	 * Method used to delete something in the database.
	 * 
	 * @param query
	 * @param key
	 * @return result
	 */
	protected int deleteMethod(String query, String key) {
		int statusCode = 0;
		URI uri = null;
		if(key != null) {
			List<NameValuePair> pair = new ArrayList<NameValuePair>();
			pair.add(new BasicNameValuePair("rev", key));
			key = URLEncodedUtils.format(pair, "UTF-8");
		}
		try {
			uri = URIUtils.createURI("http", url, port, (dbName == null ? "" : dbName+"/")+query, key, null);
			delete = new HttpDelete(uri);
			client.execute(delete, responseHandler);
		} catch(Exception e) {
			System.err.println(uri.toString());
			e.printStackTrace();
		}
		return statusCode;
	}
	
	/**
	 * Returns the locators of all topic maps.
	 * 
	 * @return locators
	 */
	public Set<Locator> getTopicMapLocators() {
		return topicMaps.keySet();
	}
	
	/**
	 * Returns the <tt>ITopicMap</tT> with the given <tt>Locator</tt>.
	 * 
	 * @param locator locator
	 * @param sys topic ma system
	 * @return tm topic map
	 */
	public ITopicMap getTopicMap(Locator locator, ITopicMapSystem sys) {
		String result = getMethod(topicMaps.get(locator),null, topicMaps.get(locator));
		ITopicMap tm = JSONToObject.JSONToTopicMap(result, sys, 0);
		return tm;
	}
	
	/**
	 * Deletes a database.
	 * 
	 * @param db database name
	 */
	public void removeDb(Locator loc) {
		String id = removeTopicMapEntry(loc);
		if(deleteMethod(id+"/", null) == 500) removeDb(loc);
	}
	
	/**
	 * Removes an entry from the id to locator database.
	 * 
	 * @param loc topic map locator
	 */
	public String removeTopicMapEntry(Locator loc) {
		String id = topicMaps.get(loc);
		topicMaps.remove(loc);
		updateTopicMapDB();
		return id;
	}
	
	/**
	 * Adds a new entry in the id to locator database.
	 * 
	 * @param tm new topic map
	 */
	public void newTopicMapEntry(ITopicMap tm) {
		topicMaps.put(tm.getLocator(), tm.getId());
		updateTopicMapDB();
	}

	/**
	 * Updates the locator to topic map id database
	 */
	public void updateTopicMapDB() {
		if(topicMaps == null) {
			String result = "{}";
			try {
				result = getMethod("topicmaps", null, "ctm-topicmaps");
			} catch(Exception e) {
				//404
			}
			topicMaps = JSONToObject.JSONToTopicMapDB(result);
			if(topicMaps == null) {
				createTopicMapDB();
			}
		} else {
			String result = putMethod("ctm-topicmaps/"+"topicmaps", topicMaps.toJSON());
			topicMaps.setRev(JSONToObject.getRev(result));
		}
	}
	
	/**
	 * Creates a new locator to topic map id database
	 */
	private void createTopicMapDB() {
		putMethod("ctm-topicmaps", "");
		topicMaps = new TopicMapDB();
		String result = putMethod("ctm-topicmaps/"+"topicmaps", topicMaps.toJSON());
		topicMaps.setRev(JSONToObject.getRev(result));
	}
	
	/**
	 * Releases the conncetion to the database.
	 */
	public void releaseConnection() {
		client.getConnectionManager().shutdown();
	}
}
