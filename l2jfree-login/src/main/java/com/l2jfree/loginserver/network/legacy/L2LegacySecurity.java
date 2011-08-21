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
package com.l2jfree.loginserver.network.legacy;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

import javax.crypto.Cipher;

import com.l2jfree.Shutdown;
import com.l2jfree.TerminationStatus;
import com.l2jfree.util.Rnd;
import com.l2jfree.util.logging.L2Logger;

/**
 * @author savormix
 */
public class L2LegacySecurity
{
	private static final L2Logger _log = L2Logger.getLogger(L2LegacySecurity.class);
	
	private static final int RSA_KEY_PAIR_COUNT = 10;
	
	private final KeyPair[] _keyPairs;
	
	private L2LegacySecurity()
	{
		_keyPairs = new KeyPair[RSA_KEY_PAIR_COUNT];
		
		KeyPairGenerator rsa = null;
		try
		{
			rsa = KeyPairGenerator.getInstance("RSA");
			AlgorithmParameterSpec spec = new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4);
			rsa.initialize(spec);
		}
		catch (GeneralSecurityException e)
		{
			_log.fatal("Could not generate RSA key pairs!", e);
			Shutdown.exit(TerminationStatus.ENVIRONMENT_MISSING_COMPONENT_OR_SERVICE);
			return;
		}
		
		for (int i = 0; i < getKeyPairs().length; i++)
			getKeyPairs()[i] = rsa.generateKeyPair();
		_log.info("Generated " + getKeyPairs().length + " RSA key pairs (legacy game server).");
		
		try
		{
			warmUp(getKeyPair().getPrivate());
		}
		catch (GeneralSecurityException e)
		{
			_log.fatal("Invalid generated key pair!", e);
			Shutdown.exit(TerminationStatus.ENVIRONMENT_MISSING_COMPONENT_OR_SERVICE);
		}
	}
	
	private void warmUp(PrivateKey key) throws GeneralSecurityException
	{
		// avoid worst-case execution, KenM
		Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
		rsaCipher.init(Cipher.DECRYPT_MODE, key);
	}
	
	/**
	 * Returns a RSA key pair.
	 * 
	 * @return a key pair
	 */
	public KeyPair getKeyPair()
	{
		return Rnd.get(getKeyPairs());
	}
	
	private KeyPair[] getKeyPairs()
	{
		return _keyPairs;
	}
	
	/**
	 * Returns a singleton object.
	 * 
	 * @return an instance of this class
	 */
	public static L2LegacySecurity getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		public static final L2LegacySecurity INSTANCE = new L2LegacySecurity();
	}
}
