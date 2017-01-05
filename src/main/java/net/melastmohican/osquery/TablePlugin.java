/**
 * OSQuery
 */
package net.melastmohican.osquery;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import osquery.extensions.ExtensionResponse;
import osquery.extensions.ExtensionStatus;

/**
 * Table plugin.
 * 
 * @author Mariusz S. Jurgielewicz
 */
public abstract class TablePlugin extends BasePlugin {
	public class TableColumn extends SimpleEntry<String, String> {
		/**
		 * Default constructor
		 * 
		 * @param key
		 * @param value
		 */
		public TableColumn(final String key, final String value) {
			super(key, value);
		}
	}

	/**
	 * Registry name.
	 * 
	 * @see net.melastmohican.osquery.BasePlugin#registryName()
	 */
	@Override
	public String registryName() {
		return "table";
	}

	/**
	 * Internal routing for this plugin type
	 * 
	 * @see net.melastmohican.osquery.BasePlugin#call(java.util.Map)
	 */
	@Override
	public final ExtensionResponse call(Map<String, String> request) {
		if (!request.containsKey("action")) {
			return new ExtensionResponse(new ExtensionStatus(1, "Table plugins must include a request action", 0L),
					Collections.<Map<String, String>>emptyList());
		}

		if (request.get("action").equals("generate")) {
			return new ExtensionResponse(new ExtensionStatus(0, "OK", 0L), generate());
		} else if (request.get("action").equals("columns")) {
			return new ExtensionResponse(new ExtensionStatus(0, "OK", 0L), routes());
		}

		return new ExtensionResponse(new ExtensionStatus(1, "Table plugin request action undefined", 0L),
				Collections.<Map<String, String>>emptyList());
	}

	/**
	 * Plugin routes.
	 * 
	 * @see net.melastmohican.osquery.BasePlugin#routes()
	 */
	@Override
	public List<Map<String, String>> routes() {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (final TableColumn column : columns()) {
			result.add(new HashMap<String, String>() {
				{
					put("id", "column");
					put("name", (String) column.getKey());
					put("type", (String) column.getValue());
					put("op", "0");
				}
			});
		}
		return result;
	}

	/**
	 * Table column definitions.
	 * 
	 * @return
	 */
	public abstract List<TableColumn> columns();

	/**
	 * Implementation of table plugin.
	 * 
	 * @return Returns a list of dictionaries, such that each dictionary has a
	 *         key corresponding to each of table's columns.
	 */
	public abstract List<Map<String, String>> generate();

}
