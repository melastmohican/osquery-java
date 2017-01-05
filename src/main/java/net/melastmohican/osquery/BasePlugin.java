/**
 * OSQuery
 */
package net.melastmohican.osquery;

import java.util.List;
import java.util.Map;

import osquery.extensions.ExtensionResponse;

/**
 * Base plugin
 * @author Mariusz S. Jurgielewicz
 *
 */
public abstract class BasePlugin {	
	/**
	 * Name of plugin.
	 * @return String name of plugin
	 */
	public abstract String name();
	
	/**
	 * Name of the registry type for plugins.
	 */
	public abstract String registryName();
 	/**
	 * Routing a thrift request to the appropriate class method.
	 * @param request thrift request
	 * @return ExtensionResponse
	 */
	public abstract ExtensionResponse call(Map<String, String> request);
	/**
	 * Routes that should be broadcasted by plugin
	 * @return List<Map<String, String>>
	 */
	public abstract List<Map<String, String>> routes();

}
