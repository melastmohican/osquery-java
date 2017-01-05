package net.melastmohican.osquery;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import osquery.extensions.ExtensionResponse;

public class TablePluginTest {
	final PluginManager pm = PluginManager.getInstance();
	private BasePlugin plugin;

	public static class SimpleTablePlugin extends TablePlugin {

		@Override
		public List<TableColumn> columns() {
			return Arrays.asList(new TableColumn("foo", "TEXT"), new TableColumn("baz", "TEXT"));
		}

		@Override
		public String name() {
			return "simpletable";
		}

		@Override
		public List<Map<String, String>> generate() {
			List<Map<String, String>> result = Arrays.<Map<String, String>>asList(new HashMap<String, String>() {
				{
					put("foo", "bar");
					put("baz", "baz");
				}
			}, new HashMap<String, String>() {
				{
					put("foo", "bar");
					put("baz", "baz");
				}
			});
			return result;
		}

	}

	@Before
	public void setUp() throws Exception {
		plugin = new SimpleTablePlugin();
	}

	@After
	public void tearDown() throws Exception {
	}

	@SuppressWarnings("serial")
	@Test
	public void testRoutes() {
		final List<Map<String, String>> expected = new ArrayList<Map<String, String>>() {
			{
				add(new HashMap<String, String>() {
					{
						put("id", "column");
						put("name", "foo");
						put("type", "TEXT");
						put("op", "0");
					}

				});
				add(new HashMap<String, String>() {
					{
						put("id", "column");
						put("name", "baz");
						put("type", "TEXT");
						put("op", "0");
					}
				});
			}
		};
		List<Map<String, String>> routes = plugin.routes();
		assertThat(routes, is(expected));
	}

	@Test
	public void testAddPlugin() {
		pm.addPlugin(plugin);
		Map<String, Map<String, List<Map<String, String>>>> registry = pm.registry();
		assertThat(registry, hasKey("table"));
		assertThat(registry.get("table"), hasKey("simpletable"));
	}

	@SuppressWarnings({ "unchecked", "serial" })
	@Test
	public void testCallActionGenerate() {
		List<Map<String, String>> expected = Arrays.<Map<String, String>>asList(new HashMap<String, String>() {
			{
				put("foo", "bar");
				put("baz", "baz");
			}
		}, new HashMap<String, String>() {
			{
				put("foo", "bar");
				put("baz", "baz");
			}
		});
		final Map<String, String> request = new HashMap<String, String>() {
			{
				put("action", "generate");
			}
		};
		ExtensionResponse result = plugin.call(request);
		assertThat(result.response, is(expected));
	}

	@SuppressWarnings("serial")
	@Test
	public void testCallActionColums() {
		final List<Map<String, String>> expected = new ArrayList<Map<String, String>>() {
			{
				add(new HashMap<String, String>() {
					{
						put("id", "column");
						put("name", "foo");
						put("type", "TEXT");
						put("op", "0");
					}

				});
				add(new HashMap<String, String>() {
					{
						put("id", "column");
						put("name", "baz");
						put("type", "TEXT");
						put("op", "0");
					}
				});
			}
		};
		final Map<String, String> request = new HashMap<String, String>() {
			{
				put("action", "columns");
			}
		};
		ExtensionResponse result = plugin.call(request);
		assertThat(result.response, is(expected));
	}

	@SuppressWarnings({ "unchecked", "serial" })
	@Test
	public void testPluginManagerCallActionGenerate() throws TException {
		List<Map<String, String>> expected = Arrays.<Map<String, String>>asList(new HashMap<String, String>() {
			{
				put("foo", "bar");
				put("baz", "baz");
			}
		}, new HashMap<String, String>() {
			{
				put("foo", "bar");
				put("baz", "baz");
			}
		});
		final Map<String, String> request = new HashMap<String, String>() {
			{
				put("action", "generate");
			}
		};
		pm.addPlugin(plugin);
		ExtensionResponse result = pm.call("table", "simpletable", request);
		assertThat(result.response, is(expected));
	}

	@SuppressWarnings("serial")
	@Test
	public void testPluginManagerCallActionColums() throws TException {
		final List<Map<String, String>> expected = new ArrayList<Map<String, String>>() {
			{
				add(new HashMap<String, String>() {
					{
						put("id", "column");
						put("name", "foo");
						put("type", "TEXT");
						put("op", "0");
					}

				});
				add(new HashMap<String, String>() {
					{
						put("id", "column");
						put("name", "baz");
						put("type", "TEXT");
						put("op", "0");
					}
				});
			}
		};
		final Map<String, String> request = new HashMap<String, String>() {
			{
				put("action", "columns");
			}
		};
		pm.addPlugin(plugin);
		ExtensionResponse result = pm.call("table", "simpletable", request);
		assertThat(result.response, is(expected));
	}
}
