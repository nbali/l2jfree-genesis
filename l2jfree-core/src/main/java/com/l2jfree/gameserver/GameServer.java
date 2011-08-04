/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfree.gameserver;

import com.l2jfree.L2Config;
import com.l2jfree.Shutdown;
import com.l2jfree.TerminationStatus;
import com.l2jfree.Util;
import com.l2jfree.gameserver.config.DatabaseConfig;
import com.l2jfree.gameserver.config.NetworkConfig;
import com.l2jfree.gameserver.network.client.L2ClientConnections;
import com.l2jfree.gameserver.network.client.L2ClientSecurity;
import com.l2jfree.sql.L2Database;

/**
 * This class contains the application entry point.
 * 
 * @author NB4L1
 * @author savormix
 */
public final class GameServer extends Config
{
	/**
	 * Launches the game server.
	 * 
	 * @param args ignored
	 */
	public static void main(String[] args)
	{
		if (DatabaseConfig.DB_OPTIMIZE)
			L2Database.optimize();
		
		L2ClientSecurity.getInstance();
		
		try
		{
			L2ClientConnections.getInstance().openServerSocket(NetworkConfig.NET_LISTEN_IP,
					NetworkConfig.NET_LISTEN_PORT);
			L2ClientConnections.getInstance().start();
		}
		catch (Throwable e)
		{
			_log.fatal("Could not start login server!", e);
			Shutdown.exit(TerminationStatus.RUNTIME_INITIALIZATION_FAILURE);
			return;
		}
		
		// TODO
		
		L2Config.onStartup();
		
		Util.printSection("l2jfree-core");
		for (String line : CoreInfo.getFullVersionInfo())
			_log.info(line);
		_log.info("Operating System: " + Util.getOSName() + " " + Util.getOSVersion() + " " + Util.getOSArch());
		_log.info("Available CPUs: " + Util.getAvailableProcessors());
		
		Util.printSection("Memory");
		System.gc();
		System.runFinalization();
		
		for (String line : Util.getMemUsage())
			_log.info(line);
		
		_log.info("Server loaded in " + Util.formatNumber(System.currentTimeMillis() - L2Config.SERVER_STARTED)
				+ " milliseconds.");
		
		Shutdown.addShutdownHook(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					L2ClientConnections.getInstance().shutdown();
				}
				catch (Throwable t)
				{
					_log.warn("Orderly shutdown sequence interrupted", t);
				}
			}
		});
	}
}
