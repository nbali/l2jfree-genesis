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
package com.l2jfree.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Noctarius
 */
public final class L2Properties extends Properties
{
	private static final long serialVersionUID = -4599023842346938325L;
	
	private boolean _warn = true;
	
	public L2Properties()
	{
	}
	
	public L2Properties setLog(boolean warn)
	{
		_warn = warn;
		
		return this;
	}
	
	// ===================================================================================
	
	public L2Properties(String name) throws IOException
	{
		load(new FileInputStream(name));
	}
	
	public L2Properties(File file) throws IOException
	{
		load(new FileInputStream(file));
	}
	
	public L2Properties(InputStream inStream) throws IOException
	{
		load(inStream);
	}
	
	public L2Properties(Reader reader) throws IOException
	{
		load(reader);
	}
	
	public L2Properties(Node node)
	{
		loadAttributes(node);
	}
	
	// ===================================================================================
	
	public void load(String name) throws IOException
	{
		load(new FileInputStream(name));
	}
	
	public void load(File file) throws IOException
	{
		load(new FileInputStream(file));
	}
	
	@Override
	public void load(InputStream inStream) throws IOException
	{
		try
		{
			super.load(inStream);
		}
		finally
		{
			IOUtils.closeQuietly(inStream);
		}
	}
	
	@Override
	public void load(Reader reader) throws IOException
	{
		try
		{
			super.load(reader);
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}
	}
	
	public void loadAttributes(Node node)
	{
		final NamedNodeMap attrs = node.getAttributes();
		
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node attr = attrs.item(i);
			
			setProperty(attr.getNodeName(), attr.getNodeValue());
		}
	}
	
	// ===================================================================================
	
	@Override
	public String getProperty(String key)
	{
		String property = super.getProperty(key);
		
		if (property == null)
		{
			if (_warn)
				System.err.println("L2Properties: Missing property for key - " + key);
			
			return null;
		}
		
		return property.trim();
	}
	
	public String getProperty(Object key)
	{
		return getProperty(String.valueOf(key));
	}
	
	@Override
	public String getProperty(String key, String defaultValue)
	{
		String property = super.getProperty(key, defaultValue);
		
		if (property == null)
		{
			if (_warn)
				System.err.println("L2Properties: Missing defaultValue for key - " + key);
			
			return null;
		}
		
		return property.trim();
	}
	
	public String getProperty(Object key, String defaultValue)
	{
		return getProperty(String.valueOf(key), defaultValue);
	}
	
	// ===================================================================================
	
	public synchronized Object setProperty(Object key, Object value)
	{
		return setProperty(String.valueOf(key), String.valueOf(value));
	}
	
	// ===================================================================================
	
	public boolean getBoolean(Object name)
	{
		final String value = getProperty(name);
		
		return L2Parser.getBoolean(value);
	}
	
	public boolean getBoolean(Object name, String defaultValue)
	{
		final String value = getProperty(name, defaultValue);
		
		return L2Parser.getBoolean(value);
	}
	
	public boolean getBoolean(Object name, boolean defaultValue)
	{
		final String value = getProperty(name);
		if (value == null)
			return defaultValue;
		
		return L2Parser.getBoolean(value);
	}
	
	public byte getByte(Object name)
	{
		final String value = getProperty(name);
		
		return L2Parser.getByte(value);
	}
	
	public byte getByte(Object name, String defaultValue)
	{
		final String value = getProperty(name, defaultValue);
		
		return L2Parser.getByte(value);
	}
	
	public byte getByte(Object name, byte defaultValue)
	{
		final String value = getProperty(name);
		if (value == null)
			return defaultValue;
		
		return L2Parser.getByte(value);
	}
	
	public short getShort(Object name)
	{
		final String value = getProperty(name);
		
		return L2Parser.getShort(value);
	}
	
	public short getShort(Object name, String defaultValue)
	{
		final String value = getProperty(name, defaultValue);
		
		return L2Parser.getShort(value);
	}
	
	public short getShort(Object name, short defaultValue)
	{
		final String value = getProperty(name);
		if (value == null)
			return defaultValue;
		
		return L2Parser.getShort(value);
	}
	
	public int getInteger(Object name)
	{
		final String value = getProperty(name);
		
		return L2Parser.getInteger(value);
	}
	
	public int getInteger(Object name, String defaultValue)
	{
		final String value = getProperty(name, defaultValue);
		
		return L2Parser.getInteger(value);
	}
	
	public int getInteger(Object name, int defaultValue)
	{
		final String value = getProperty(name);
		if (value == null)
			return defaultValue;
		
		return L2Parser.getInteger(value);
	}
	
	public long getLong(Object name)
	{
		final String value = getProperty(name);
		
		return L2Parser.getLong(value);
	}
	
	public long getLong(Object name, String defaultValue)
	{
		final String value = getProperty(name, defaultValue);
		
		return L2Parser.getLong(value);
	}
	
	public long getLong(Object name, long defaultValue)
	{
		final String value = getProperty(name);
		if (value == null)
			return defaultValue;
		
		return L2Parser.getLong(value);
	}
	
	public float getFloat(Object name)
	{
		final String value = getProperty(name);
		
		return L2Parser.getFloat(value);
	}
	
	public float getFloat(Object name, String defaultValue)
	{
		final String value = getProperty(name, defaultValue);
		
		return L2Parser.getFloat(value);
	}
	
	public float getFloat(Object name, float defaultValue)
	{
		final String value = getProperty(name);
		if (value == null)
			return defaultValue;
		
		return L2Parser.getFloat(value);
	}
	
	public double getDouble(Object name)
	{
		final String value = getProperty(name);
		
		return L2Parser.getDouble(value);
	}
	
	public double getDouble(Object name, String defaultValue)
	{
		final String value = getProperty(name, defaultValue);
		
		return L2Parser.getDouble(value);
	}
	
	public double getDouble(Object name, double defaultValue)
	{
		final String value = getProperty(name);
		if (value == null)
			return defaultValue;
		
		return L2Parser.getDouble(value);
	}
	
	public String getString(Object name)
	{
		final String value = getProperty(name);
		
		return L2Parser.getString(value);
	}
	
	public String getString(Object name, String defaultValue)
	{
		final String value = getProperty(name, defaultValue);
		
		return L2Parser.getString(value);
	}
	
	public <T extends Enum<T>> T getEnum(Object name, Class<T> enumClass)
	{
		final String value = getProperty(name);
		
		return L2Parser.getEnum(enumClass, value);
	}
	
	public <T extends Enum<T>> T getEnum(Class<T> enumClass, Object name, String defaultValue)
	{
		final String value = getProperty(name, defaultValue);
		
		return L2Parser.getEnum(enumClass, value);
	}
	
	public <T extends Enum<T>> T getEnum(Class<T> enumClass, Object name, T defaultValue)
	{
		final String value = getProperty(name);
		if (value == null)
			return defaultValue;
		
		return L2Parser.getEnum(enumClass, value);
	}
}
