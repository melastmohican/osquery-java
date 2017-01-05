/**
 * OSQuery
 */
package net.melastmohican.osquery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportFactory;
import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import osquery.extensions.Extension.Iface;
import osquery.extensions.Extension.Processor;
import osquery.extensions.ExtensionException;
import osquery.extensions.ExtensionManager;
import osquery.extensions.ExtensionResponse;
import osquery.extensions.ExtensionStatus;
import osquery.extensions.InternalExtensionInfo;

/**
 * Extension management.
 * 
 * @author Mariusz S. Jurgielewicz
 *
 */
public final class PluginManager implements Iface {
	public static final String EXTENSION_SOCKET = System.getProperty("extension.socket") != null
			? System.getProperty("extension.socket") : ClientManager.DEFAULT_SOCKET_PATH;
	public final List<String> registryTypes = Arrays.asList("config", "logger", "table");

	/**
	 * Registered plugins.
	 */
	private Map<String, Map<String, BasePlugin>> plugins = new HashMap<String, Map<String, BasePlugin>>();
	/**
	 * Extension registry.
	 */
	private Map<String, Map<String, List<Map<String, String>>>> registry = new HashMap<String, Map<String, List<Map<String, String>>>>();

	/**
	 * UUID
	 */
	private Long uuid = null;

	/**
	 * Default private constructor.
	 */
	private PluginManager() {
	}

	/**
	 * Lazy on demand singleton holder.
	 */
	private static class SingletonHolder {
		/**
		 * Instance of ExtensionManager
		 */
		private static final PluginManager INSTANCE = new PluginManager();
	}

	/**
	 * Get EtensionManager instance.
	 * 
	 * @return ExtensionManager
	 */
	public static PluginManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * OSQuery ping method.
	 * 
	 * @throws TException
	 * @see osquery.extensions.Extension.Iface#ping()
	 */
	public ExtensionStatus ping() throws TException {
		ExtensionStatus status = new ExtensionStatus(0, "OK", uuid != null ? uuid.longValue() : 0L);
		return status;
	}

	/**
	 * OSQuery call method.
	 * 
	 * @param registry
	 *            name of registry
	 * @param item
	 *            name of plugin
	 * @param request
	 *            osquery request
	 * @return ExtensionResponse
	 * @see osquery.extensions.Extension.Iface#call(java.lang.String,
	 *      java.lang.String, java.util.Map)
	 */
	public ExtensionResponse call(String registry, String item, Map<String, String> request) throws TException {
		if (!registryTypes.contains(registry)) {
			return new ExtensionResponse(
					new ExtensionStatus(1, "A registry of an unknown type was called: " + registry, uuid),
					new ArrayList<Map<String, String>>());
		}
		return plugins.get(registry).get(item).call(request);
	}

	/**
	 * Register a plugin with the extension manager.
	 * 
	 * @param plugin
	 *            the plugin class to register
	 */
	public void addPlugin(final BasePlugin plugin) {
		if (!registry.containsKey(plugin.registryName())) {
			registry.put(plugin.registryName(), new HashMap<String, List<Map<String, String>>>());
		}
		if (!registry.get(plugin.registryName()).containsKey(plugin.name())) {
			registry.get(plugin.registryName()).put(plugin.name(), new ArrayList<Map<String, String>>());
			registry.get(plugin.registryName()).get(plugin.name()).addAll(plugin.routes());
		}

		if (!plugins.containsKey(plugin.name())) {
			plugins.put(plugin.registryName(), new HashMap<String, BasePlugin>());
		}
		if (!plugins.get(plugin.registryName()).containsKey(plugin.name())) {
			plugins.get(plugin.registryName()).put(plugin.name(), plugin);
		}

	}

	/**
	 * osquery extension manager requested a shutdown.
	 */
	public void shutdown() {
		System.exit(0);
	}

	/**
	 * Accessor for the internal registry member variable.
	 * 
	 * @return ExtensionRegistry
	 */
	public Map<String, Map<String, List<Map<String, String>>>> registry() {
		return this.registry;
	}

	/**
	 * Start extension by communicating with osquery core and starting thrift
	 * server
	 * 
	 * @param name
	 *            name of extension
	 * @param version
	 *            version of extension
	 * @param sdkVersion
	 *            version of the osquery SDK used to build this extension
	 * @param minSdkVersion
	 *            minimum version of the osquery SDK that you can use
	 * @throws IOException
	 * @throws ExtensionException
	 */
	public void startExtension(String name, String version, String sdkVersion, String minSdkVersion)
			throws IOException, ExtensionException {
		ExtensionManager.Client client = new ClientManager(EXTENSION_SOCKET).getClient();
		InternalExtensionInfo info = new InternalExtensionInfo(name, version, sdkVersion, minSdkVersion);
		try {
			ExtensionStatus status = client.registerExtension(info, registry);
			if (status.getCode() == 0) {
				this.uuid = status.uuid;
				Processor<PluginManager> processor = new Processor<PluginManager>(this);
				String serverSocketPath = EXTENSION_SOCKET + "." + String.valueOf(uuid);
				File socketFile = new File(serverSocketPath);
				if (socketFile.exists()) {
					socketFile.delete();
				}
				AFUNIXServerSocket socket = AFUNIXServerSocket.bindOn(new AFUNIXSocketAddress(socketFile));
				socketFile.setExecutable(true, false);
				socketFile.setWritable(true, false);
				socketFile.setReadable(true, false);
				TServerSocket transport = new TServerSocket(socket);
				TTransportFactory transportFactory = new TTransportFactory();
				TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
				TServer server = new TSimpleServer(new Args(transport).processor(processor)
						.transportFactory(transportFactory).protocolFactory(protocolFactory));

				// Run it
				System.out.println("Starting the server...");
				server.serve();
			} else {
				throw new ExtensionException(1, status.getMessage(), uuid);
			}
		} catch (TException e) {
			throw new ExtensionException(1, "Could not connect to socket", uuid);
		}
	}

	/**
	 * Deregister the entire extension from the core extension manager.
	 * 
	 * @throws IOException
	 * @throws ExtensionException
	 */
	public void deregisterExtension() throws IOException, ExtensionException {
		ExtensionManager.Client client = new ClientManager().getClient();
		if (uuid == null) {
			throw new ExtensionException(1, "Extension Manager does not have a valid UUID", uuid);
		}
		try {
			ExtensionStatus status = client.deregisterExtension(uuid);
			if (status.getCode() != 0) {
				throw new ExtensionException(1, status.getMessage(), uuid);
			}
		} catch (TException e) {
			throw new ExtensionException(1, "Could not connect to socket", uuid);
		}
	}

}
