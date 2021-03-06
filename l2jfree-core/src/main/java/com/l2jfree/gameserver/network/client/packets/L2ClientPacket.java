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
package com.l2jfree.gameserver.network.client.packets;

import com.l2jfree.gameserver.network.client.L2Client;
import com.l2jfree.gameserver.network.client.packets.sendable.CreatureSay.Chat;
import com.l2jfree.gameserver.network.client.packets.sendable.CreatureSay.ChatMessage;
import com.l2jfree.network.mmocore.ReceivablePacket;

/**
 * Just for convenience.
 * 
 * @author savormix
 */
public abstract class L2ClientPacket extends ReceivablePacket<L2Client, L2ClientPacket, L2ServerPacket>
{
	public void sendCMessage(Chat chatType, String sender, String message)
	{
		ChatMessage cm = new ChatMessage(chatType, sender, message);
		sendPacket(cm);
	}
	
	// it's high time we tested when AF is really needed:
	// after every Action packet, regardless of result (so send in packet handler, not in NPC or elsewhere)
	// ...?
	// ...?
}
