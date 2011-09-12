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
package com.l2jfree.gameserver.gameobjects;

import java.util.Collection;
import java.util.Map;

import com.l2jfree.util.LazyMap;

/**
 * @author NB4L1
 */
public class ObjectKnownList implements IObjectKnownList
{
	private final L2Object _activeChar;
	
	private final Map<Integer, L2Object> _knownObjects = new LazyMap<Integer, L2Object>().setShared();
	private final Map<Integer, L2Player> _knownPlayers = new LazyMap<Integer, L2Player>().setShared();
	
	private final Map<Integer, L2Object> _knowingObjects = new LazyMap<Integer, L2Object>().setShared();
	
	public ObjectKnownList(L2Object activeChar)
	{
		_activeChar = activeChar;
	}
	
	public L2Object getActiveChar()
	{
		return _activeChar;
	}
	
	@Override
	public final Collection<L2Object> getKnownObjects()
	{
		return _knownObjects.values();
	}
	
	@Override
	public final Collection<L2Player> getKnownPlayers()
	{
		return _knownPlayers.values();
	}
	
	@Override
	public final Collection<L2Object> getKnowingObjects()
	{
		return _knowingObjects.values();
	}
	
	public final boolean knowsObject(L2Object object)
	{
		return object != null && (_activeChar == object || _knownObjects.containsKey(object.getObjectId()));
	}
	
	protected boolean addObject(L2Object obj)
	{
		if (obj == null || obj == _activeChar)
			return false;
		
		if (_knownObjects.put(obj.getObjectId(), obj) != null)
			return false;
		
		obj.getKnownList().addKnowingObject(_activeChar);
		
		if (obj instanceof L2Player)
			_knownPlayers.put(obj.getObjectId(), (L2Player)obj);
		
		return true;
	}
	
	@Override
	public boolean removeObject(L2Object obj)
	{
		if (obj == null || obj == _activeChar)
			return false;
		
		if (_knownObjects.remove(obj.getObjectId()) == null)
			return false;
		
		obj.getKnownList().removeKnowingObject(_activeChar);
		
		if (obj instanceof L2Player)
			_knownPlayers.remove(obj.getObjectId());
		
		return true;
	}
	
	@Override
	public final void addKnowingObject(L2Object obj)
	{
		_knowingObjects.put(obj.getObjectId(), obj);
	}
	
	@Override
	public final void removeKnowingObject(L2Object obj)
	{
		_knowingObjects.remove(obj.getObjectId());
	}
	
	@SuppressWarnings("static-method")
	protected int getDistanceToAddObject(@SuppressWarnings("unused") L2Object obj)
	{
		return 0;
	}
	
	@SuppressWarnings("static-method")
	protected int getDistanceToRemoveObject(@SuppressWarnings("unused") L2Object obj)
	{
		return 0;
	}
	
	protected final boolean shouldKnowObject(L2Object obj)
	{
		if (!_activeChar.getPosition().isVisible())
			return false;
		if (!obj.getPosition().isVisible())
			return false;
		
		return false;
	}
	
	@Override
	public final void update(L2Object obj)
	{
		final boolean knows = knowsObject(obj);
		final boolean shouldKnow = shouldKnowObject(obj);
		final long distance = 0; // TODO
		
		if (knows)
		{
			if (!shouldKnow && getDistanceToRemoveObject(obj) > distance)
				removeObject(obj);
		}
		else
		{
			if (shouldKnow && getDistanceToAddObject(obj) > distance)
				addObject(obj);
		}
	}
	
	@Override
	public final void update(L2Object[][] surroundingObjects)
	{
		for (L2Object[] regionObjects : surroundingObjects)
		{
			for (L2Object obj : regionObjects)
			{
				update(obj);
				obj.getKnownList().update(_activeChar);
			}
		}
	}
	
	@Override
	public void removeAllKnownObjects()
	{
		for (L2Object obj : _knownObjects.values())
			removeObject(obj);
		
		for (L2Object obj : _knowingObjects.values())
			obj.getKnownList().removeObject(_activeChar);
		
		_knownObjects.clear();
		_knownPlayers.clear();
		_knowingObjects.clear();
	}
	
	private long _lastUpdate;
	
	public synchronized final void updateKnownObjects(boolean force)
	{
		if (System.currentTimeMillis() - _lastUpdate < 100)
			return;
		
		for (L2Object obj : _knownObjects.values())
			update(obj);
		
		for (L2Object obj : _knowingObjects.values())
			obj.getKnownList().update(_activeChar);
		
		_activeChar.getPosition().getWorldRegion().updateKnownList(_activeChar, force);
		
		_lastUpdate = System.currentTimeMillis();
	}
}
