package net.melastmohican.osquery;

import java.io.IOException;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import osquery.extensions.ExtensionManager;
import osquery.extensions.ExtensionResponse;

/**
 * Unit test class for {@link ClientManager}
 * @author Mariusz S. Jurgielewicz
 *
 */
public class ClientManagerTest {
	/**
	 * Interfaces with osqueryctl.
	 */
	private ProcessBuilder osqueryctl = new ProcessBuilder("/usr/local/bin/osqueryctl", "start");

	/**
	 * Test setup.
	 * @throws Exception if an I/O error occurs
	 */
	@Before
	public final void setUp() throws Exception {
		System.out.println("Starting service...");
		osqueryctl.start();		 
	}

	/**
	 * 
	 * @throws Exception if an I/O error occurs
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Testing client query.
	 * @throws TException if Thrift problem occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Test
	public final void testGetClient() throws TException, IOException {
		ClientManager cm =  new ClientManager();
		cm.open();	
		ExtensionManager.Client client = cm.getClient();
		System.out.println("select timestamp from time");
		ExtensionResponse res = client.query("select timestamp from time");
		System.out.println(res.response);
	}

}
