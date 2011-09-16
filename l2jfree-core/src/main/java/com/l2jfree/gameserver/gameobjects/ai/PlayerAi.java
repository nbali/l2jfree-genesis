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
package com.l2jfree.gameserver.gameobjects.ai;

import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.gameobjects.components.interfaces.IPlayerAi;

/**
 * @author hex1r0
 */
public class PlayerAi extends CharacterAi implements IPlayerAi
{
	public PlayerAi(L2Player activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public L2Player getActiveChar()
	{
		return getActiveChar();
	}
	
	@Override
	protected void onIntentionIdle()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onIntentionActive()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onIntentionMoveTo(int x, int y, int z)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onEvtArrived()
	{
		// TODO Auto-generated method stub
		
	}
	
	// TODO
	
}
