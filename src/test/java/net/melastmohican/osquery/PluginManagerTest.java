package net.melastmohican.osquery;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Collections;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.melastmohican.osquery.BasePluginTest.SimplePlugin;
import net.melastmohican.osquery.TablePluginTest.SimpleTablePlugin;
import osquery.extensions.ExtensionException;
import osquery.extensions.ExtensionResponse;
import osquery.extensions.ExtensionStatus;

public class PluginManagerTest {
	PluginManager pm = PluginManager.getInstance();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddPlugin() {	
		BasePlugin plugin = new SimplePlugin("simple1");
		pm.addPlugin(plugin);
	}
	
	@Test
	public void testPing() throws TException {
		ExtensionStatus status = pm.ping();
		assertEquals(status.code, 0);
	}
	
	@Test
	public void testCall() throws TException {
		BasePlugin plugin = new SimplePlugin("simple2");
		pm.addPlugin(plugin);
		ExtensionResponse resp = pm.call("config", "simple2", Collections.<String, String>emptyMap());
		assertThat(resp.status.message, is("OK"));
	}
	
	@Ignore
	@Test
	public void testStartExtension() throws ExtensionException, IOException {
		BasePlugin plugin = new SimpleTablePlugin();
		pm.addPlugin(plugin);
		pm.startExtension("SimpleTable","0.0.1","2.2.1","2.2.1");
	}
	

}
