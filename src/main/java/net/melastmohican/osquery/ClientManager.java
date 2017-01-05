/**
 * OSQuery
 */

package net.melastmohican.osquery;

import java.io.File;
import java.io.IOException;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import osquery.extensions.ExtensionManager;

/**
 * Client Manager
 * @author Mariusz S. Jurgielewicz
 */
public final class ClientManager {
	/**
	 * Default Unix domain socket.
	 */
	public static final String DEFAULT_SOCKET_PATH = "/var/osquery/osquery.em";
	public static final String SHELL_SOCKET_PATH = System.getProperty("user.home") + "/.osquery/shell.em";
	
	private String socketPath = null;
	private TTransport transport;
	private TProtocol protocol;
	
	/**
	 * Default  constructor.
	 * @throws IOException 
	 */
	public ClientManager() throws IOException {
		this(ClientManager.DEFAULT_SOCKET_PATH);
	}
	
	/**
	 * Constructor with given socket path
	 * @param socketPath
	 * @throws IOException
	 */
	public ClientManager(String socketPath) throws IOException  {
		this.socketPath = socketPath;
		AFUNIXSocket socket = AFUNIXSocket.connectTo(new AFUNIXSocketAddress(new File(socketPath)));
		this.transport = new TIOStreamTransport(socket.getInputStream(), socket.getOutputStream());
		this.protocol = new TBinaryProtocol(transport);
	}
	
	/**
	 * Opens transpot for reading and writing.
	 * @throws TTransportException
	 */
	public void open() throws TTransportException {
		this.transport.open();
	}
	
	/**
	 * Closes transport.
	 */
	public void close() {
		if(this.transport != null) {
			this.transport.close();
		}
	}

	/**
	 * Client factory method.
	 * @return ExtensionManager.Client
	 * @throws IOException if I/O exception occurs
	 */
	public ExtensionManager.Client getClient() throws IOException {
		return new ExtensionManager.Client(protocol);
	}

}
