/**
 * 
 */
package net.melastmohican.osquery;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import osquery.extensions.ExtensionResponse;
import osquery.extensions.ExtensionStatus;

/**
 * Config Plugin
 * 
 * @author Mariusz S. Jurgielewicz
 *
 */
public abstract class ConfigPlugin extends BasePlugin {
	
	/**
	 * Registry name.
	 * 
	 * @see net.melastmohican.osquery.BasePlugin#registryName()
	 */
	@Override
	public String registryName() {
		return "config";
	}
	
	/**
	 * Internal routing for this plugin type
	 * 
	 * @see net.melastmohican.osquery.BasePlugin#call(java.util.Map)
	 */
	@Override
	public final ExtensionResponse call(Map<String, String> request) {
		if (!request.containsKey("action")) {
			return new ExtensionResponse(new ExtensionStatus(1, "Config plugins must include a request action", 0L),
					Collections.<Map<String, String>>emptyList());
		}

		if (request.get("action").equals("genConfig")) {
			return new ExtensionResponse(new ExtensionStatus(0, "OK", 0L), content());
		}

		return new ExtensionResponse(new ExtensionStatus(1, "Config plugin request action undefined", 0L),
				Collections.<Map<String, String>>emptyList());
	}
	
	/**
	 * Routes that should be broadcasted by plugin
	 * @return List<Map<String, String>>
	 */
	@Override
	public final List<Map<String, String>> routes() {
		return Collections.<Map<String, String>>emptyList();
	}
	
	/**
	 * Implementation of config plugin.
	 * 
	 * @return Returns a list of dictionaries, such that each dictionary has a
	 *         key corresponding to each of table's columns.
	 */
	public abstract List<Map<String, String>> content();

}
