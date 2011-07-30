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
package com.l2jfree.loginserver;

import com.l2jfree.sql.DataSourceInitializer;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author savormix
 */
public final class L2LoginDataSource implements DataSourceInitializer
{
	private static final int DB_MIN_CONNECTIONS = 2;
	
	@Override
	public ComboPooledDataSource initDataSource() throws Exception
	{
		if (Config.DB_MAX_CONNECTIONS < DB_MIN_CONNECTIONS)
			throw new IllegalArgumentException("At least " + DB_MIN_CONNECTIONS + " required in pool.");
		
		ComboPooledDataSource source = new ComboPooledDataSource();
		source.setAutoCommitOnClose(true);
		
		source.setInitialPoolSize(DB_MIN_CONNECTIONS);
		source.setMinPoolSize(DB_MIN_CONNECTIONS);
		source.setMaxPoolSize(Config.DB_MAX_CONNECTIONS);
		
		source.setAcquireRetryAttempts(0);
		source.setAcquireRetryDelay(500);
		source.setCheckoutTimeout(0);
		
		source.setAcquireIncrement(5);
		
		source.setAutomaticTestTable("_connection_test_table");
		source.setTestConnectionOnCheckin(false);
		
		source.setIdleConnectionTestPeriod(3600);
		source.setMaxIdleTime(1800);
		
		source.setMaxStatementsPerConnection(100);
		
		source.setBreakAfterAcquireFailure(false);
		
		source.setDriverClass(Config.DB_DRIVER);
		source.setJdbcUrl(Config.DB_URL);
		source.setUser(Config.DB_USER);
		source.setPassword(Config.DB_PASSWORD);
		
		return source;
	}
}
