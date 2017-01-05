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
 * Logger Plugin
 * 
 * @author Mariusz S. Jurgielewicz
 *
 */
public abstract class LoggerPlugin extends BasePlugin {
	
	/**
	 * Registry name.
	 * 
	 * @see net.melastmohican.osquery.BasePlugin#registryName()
	 */
	@Override
	public String registryName() {
		return "logger";
	}
	
	/**
	 * Internal routing for this plugin type
	 * 
	 * @see net.melastmohican.osquery.BasePlugin#call(java.util.Map)
	 */
	@Override
	public final ExtensionResponse call(Map<String, String> request) {
		if (request.containsKey("string")) {
			return new ExtensionResponse(logString(request.get("string")), Collections.<Map<String, String>>emptyList());
		} else if (request.containsKey("snapshot")) {
			return new ExtensionResponse(logSnapshot(request.get("snapshot")), Collections.<Map<String, String>>emptyList());
		} else if (request.containsKey("health")) {
			return new ExtensionResponse(logHealth(request.get("health")), Collections.<Map<String, String>>emptyList());
		} else if (request.containsKey("init")) {
			return new ExtensionResponse(new ExtensionStatus(1, "Use Glog for status logging", 0L),
					Collections.<Map<String, String>>emptyList());
		} else if (request.containsKey("status")) {
			return new ExtensionResponse(new ExtensionStatus(1, "Use Glog for status logging", 0L),
					Collections.<Map<String, String>>emptyList());
		}
		
		return new ExtensionResponse(new ExtensionStatus(1, "Logger plugin request action undefined", 0L),
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
	 * The implementation of your logger plugin
	 * 
	 * @param value the string to log
	 * @return ExtensionStatus
	 */
	public abstract ExtensionStatus logString(String value);

	/**
	 * If you'd like the log health statistics about osquery's performance, override this method in your logger plugin.
	 * By default, this action is just hands off the string to logString.
	 * @param value
	 * @return ExtensionStatus
	 */
	public ExtensionStatus logHealth(String value) {
		return new ExtensionStatus(0, "OK", 0L);
	}
 
	/**
	 * If you'd like to log snapshot queries in a special way, override this method.
	 * @param value
	 * @return ExtensionStatus
	 */
	public ExtensionStatus logSnapshot(String value) {
		return this.logString(value);
	}

}
