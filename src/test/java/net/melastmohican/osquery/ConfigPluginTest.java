package net.melastmohican.osquery;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import osquery.extensions.ExtensionResponse;

public class ConfigPluginTest {
	final PluginManager pm = PluginManager.getInstance();
	private BasePlugin plugin;

	public static class SimpleConfigPlugin extends ConfigPlugin {

		@Override
		public String name() {
			return "simpleconfig";
		}

		@Override
		public List<Map<String, String>> content() {
			List<Map<String, String>> result = Arrays.<Map<String, String>>asList(new HashMap<String, String>() {
				{
					put("source1", "{\n\t\"schedule\": {\n\t\t\"time_1\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 1,\n\t\t},\n\t},\n} ");
					put("source2", "{\n\t\"schedule\": {\n\t\t\"time_2\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 2,\n\t\t},\n\t},\n}");
				}
			});
			return result;
		}

	}

	@Before
	public void setUp() throws Exception {
		plugin = new SimpleConfigPlugin();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddPlugin() {
		pm.addPlugin(plugin);
		Map<String, Map<String, List<Map<String, String>>>> registry = pm.registry();
		assertThat(registry, hasKey("config"));
		assertThat(registry.get("config"), hasKey("simpleconfig"));
	}

	@SuppressWarnings({ "unchecked", "serial" })
	@Test
	public void testCallActionGenConfig() {
		List<Map<String, String>> expected = Arrays.<Map<String, String>>asList(new HashMap<String, String>() {
			{
				put("source1", "{\n\t\"schedule\": {\n\t\t\"time_1\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 1,\n\t\t},\n\t},\n} ");
				put("source2", "{\n\t\"schedule\": {\n\t\t\"time_2\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 2,\n\t\t},\n\t},\n}");
			}
		});
		final Map<String, String> request = new HashMap<String, String>() {
			{
				put("action", "genConfig");
			}
		};
		ExtensionResponse result = plugin.call(request);
		assertThat(result.response, is(expected));
	}

	@SuppressWarnings({ "unchecked", "serial" })
	@Test
	public void testPluginManagerCallActionGenConfig() throws TException {
		List<Map<String, String>> expected = Arrays.<Map<String, String>>asList(new HashMap<String, String>() {
			{
				put("source1", "{\n\t\"schedule\": {\n\t\t\"time_1\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 1,\n\t\t},\n\t},\n} ");
				put("source2", "{\n\t\"schedule\": {\n\t\t\"time_2\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 2,\n\t\t},\n\t},\n}");
			}
		});
		final Map<String, String> request = new HashMap<String, String>() {
			{
				put("action", "genConfig");
			}
		};
		pm.addPlugin(plugin);
		ExtensionResponse result = pm.call("config", "simpleconfig", request);
		assertThat(result.response, is(expected));
	}

	}
