package net.melastmohican.osquery;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import osquery.extensions.ExtensionResponse;
import osquery.extensions.ExtensionStatus;

public class BasePluginTest {
	
	private BasePlugin plugin;
	
	public static class SimplePlugin extends BasePlugin {
		
		private final String name;
		
		public SimplePlugin() {
			this.name = "pass";
		}
		
		public SimplePlugin(String name) {
			this.name = name;
		}

		@Override
		public String name() {
			return this.name;
		}

		@Override
		public String registryName() {
			return "config";
		}

		@Override
		public ExtensionResponse call(Map<String, String> request) {
			return new ExtensionResponse (new ExtensionStatus(0, "OK", 0), routes());
		}

		@Override
		public List<Map<String, String>> routes() {
			return Collections.<Map<String, String>>emptyList();
		}
		
	}

	@Before
	public void setUp() throws Exception {
		plugin = new SimplePlugin();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRoutes() {	
		List<Map<String, String>> routes = plugin.routes();
		assertThat(routes, is(empty()));
	}
	
	@Test
	public void testCall() {
		ExtensionResponse resp = plugin.call(Collections.<String, String>emptyMap());
		assertThat(resp.status.message, is("OK"));
	}

}
