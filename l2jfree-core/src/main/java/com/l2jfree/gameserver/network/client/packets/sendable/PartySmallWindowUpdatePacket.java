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
public abstract class PartySmallWindowUpdatePacket extends L2ServerPacket
{
	/**
	 * A nicer name for {@link PartySmallWindowUpdatePacket}.
	 * 
	 * @author savormix (generated)
	 * @see PartySmallWindowUpdatePacket
	 */
	public static final class PartyMemberUpdate extends PartySmallWindowUpdatePacket
	{
		/**
		 * Constructs this packet.
		 * 
		 * @see PartySmallWindowUpdatePacket#PartySmallWindowUpdatePacket()
		 */
		public PartyMemberUpdate()
		{
		}
	}

	/** Constructs this packet. */
	public PartySmallWindowUpdatePacket()
	{
	}

	@Override
	protected int getOpcode()
	{
		return 0x52;
	}

	@Override
	protected void writeImpl(L2Client client, L2Player activeChar, MMOBuffer buf) throws RuntimeException
	{
		// TODO: when implementing, consult an up-to-date packets_game_server.xml and/or savormix
		buf.writeD(0); // Member OID
		buf.writeS(""); // Name
		buf.writeD(0); // Current CP
		buf.writeD(0); // Maximum CP
		buf.writeD(0); // Current HP
		buf.writeD(0); // Maximum HP
		buf.writeD(0); // Current MP
		buf.writeD(0); // Maximum MP
		buf.writeD(0); // Level
		buf.writeD(0); // Class
	}
}
