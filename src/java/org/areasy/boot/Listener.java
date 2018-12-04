package org.areasy.boot;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
 *
 * This library, AREasy Runtime and API for BMC Remedy AR System, is free software ("Licensed Software");
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * including but not limited to, the implied warranty of MERCHANTABILITY, NONINFRINGEMENT,
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

class Listener extends Thread
{
	private ServerSocket socket = null;

	private boolean enabled = false;
	private int port = 0;

	private Listener(Properties configuration)
	{
		this.enabled = new Boolean(configuration.getProperty("boot.monitor.enable")).booleanValue();
		this.port = Integer.parseInt(configuration.getProperty("boot.monitor.port", "0"));
	}

	public static void startup(Properties configuration)
	{
		Listener listener = new Listener(configuration);

		listener.runServer();
		listener.start();
	}

	public static void shutdown(Properties configuration)
	{
		Listener listener = new Listener(configuration);

		try
		{
			Socket socket = new Socket(InetAddress.getLocalHost(), listener.port);
			if(Main.verbose) System.out.println("\nSending listener command to address: " + InetAddress.getLocalHost() + ":" + listener.port);

			OutputStream out = socket.getOutputStream();
			out.write((InetAddress.getLocalHost() + "\n").getBytes());
			out.flush();

			socket.shutdownOutput();
			socket.close();

			if(Main.verbose) System.out.println("Command executed.");
		}
		catch (Exception e)
		{
			System.err.println("Error executing shutdown command: " + e.getMessage());
		}
	}

	public void run()
	{
		while (enabled && this.socket != null &&!this.socket.isClosed())
		{
			Socket socket = null;

			try
			{
				socket = this.socket.accept();
				LineNumberReader line = new LineNumberReader(new InputStreamReader(socket.getInputStream()));

				String cmd = line.readLine();
				String host = InetAddress.getLocalHost().toString();

				if(cmd.equals(host))
				{
					if (socket != null)
					{
						try
						{
							socket.close();
						}
						catch (Exception e) { }
					}

					if (this.socket != null)
					{
						try
						{
							this.socket.close();
						}
						catch (Exception e) { }
					}

					System.out.println("Shutdown listener.");
					System.exit(0);
				}
			}
			catch (Exception e)
			{
				System.err.println("Error running listener: " + e.getMessage());
			}
			finally
			{
				if (socket != null)
				{
					try
					{
						socket.close();
					}
					catch (Exception e) { }
				}
			}
		}
	}

	protected void runServer()
	{
		if(socket == null && enabled)
		{
			try
			{
				if(Main.verbose) System.out.println("Starting listener using address: " + InetAddress.getLocalHost() + ":" + port);

				if (port < 0)
				{
					System.err.println("Could not listen on port: " + port);
					System.exit(0);
				}

				//open server socket
				socket = new ServerSocket(port, 1, InetAddress.getLocalHost());

				//set thread
				setDaemon(true);
				setPriority(Thread.MIN_PRIORITY);
			}
			catch (Exception e)
			{
				System.err.println("Error starting listener: " + e.getMessage());
				System.exit(1);
			}
		}
		else if(Main.verbose) System.err.println("Listener is not enabled or other instance is started or is trying to start listener server!");
	}
}
