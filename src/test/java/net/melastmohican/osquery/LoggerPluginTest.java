package net.melastmohican.osquery;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
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
import osquery.extensions.ExtensionStatus;

public class LoggerPluginTest {
	final PluginManager pm = PluginManager.getInstance();
	private SimpleLoggerPlugin plugin;

	public static class SimpleLoggerPlugin extends LoggerPlugin {
		
		public List<String> logs = new ArrayList<String>();

		@Override
		public String name() {
			return "simplelogger";
		}

		@Override
		public ExtensionStatus logString(String value) {
			logs.add(value);
			return new ExtensionStatus(0, "OK", 0L);
		}

	}

	@Before
	public void setUp() throws Exception {
		plugin = new SimpleLoggerPlugin();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddPlugin() {
		pm.addPlugin(plugin);
		Map<String, Map<String, List<Map<String, String>>>> registry = pm.registry();
		assertThat(registry, hasKey("logger"));
		assertThat(registry.get("logger"), hasKey("simplelogger"));
	}

	@SuppressWarnings({ "unchecked", "serial" })
	@Test
	public void testCallLogString() {
		final Map<String, String> request = new HashMap<String, String>() {
			{
				put("string", "test");
			}
		};
		ExtensionResponse result = plugin.call(request);
		assertThat(plugin.logs, contains("test"));
	}

	@SuppressWarnings({ "unchecked", "serial" })
	@Test
	public void testPluginManagerCallLogString() throws TException {
		final Map<String, String> request = new HashMap<String, String>() {
			{
				put("string", "test");
			}
		};
		pm.addPlugin(plugin);
		ExtensionResponse result = pm.call("logger", "simplelogger", request);
		assertThat(plugin.logs, contains("test"));
	}

	}
