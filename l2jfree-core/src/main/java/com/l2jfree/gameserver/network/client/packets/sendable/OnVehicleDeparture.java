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
package com.l2jfree.gameserver.network.client.packets.sendable;

import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.network.client.L2Client;
import com.l2jfree.gameserver.network.client.packets.L2ServerPacket;
import com.l2jfree.network.mmocore.MMOBuffer;

/**
 * @author savormix (generated)
 */
public abstract class OnVehicleDeparture extends L2ServerPacket
{
	/**
	 * A nicer name for {@link OnVehicleDeparture}.
	 * 
	 * @author savormix (generated)
	 * @see OnVehicleDeparture
	 */
	public static final class VehicleInfo extends OnVehicleDeparture
	{
		/**
		 * Constructs this packet.
		 * 
		 * @see OnVehicleDeparture#OnVehicleDeparture()
		 */
		public VehicleInfo()
		{
		}
	}

	/** Constructs this packet. */
	public OnVehicleDeparture()
	{
	}

	@Override
	protected int getOpcode()
	{
		return 0x6c;
	}

	@Override
	protected void writeImpl(L2Client client, L2Player activeChar, MMOBuffer buf) throws RuntimeException
	{
		// TODO: when implementing, consult an up-to-date packets_game_server.xml and/or savormix
		buf.writeD(0); // Vehicle OID
		buf.writeD(0); // Location X
		buf.writeD(0); // Location Y
		buf.writeD(0); // Location Z
		buf.writeD(0); // Movement speed
		buf.writeD(0); // Rotation speed
	}
}
