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
package com.l2jfree;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.l2jfree.config.L2Properties;
import com.l2jfree.config.model.ConfigClassInfo;
import com.l2jfree.io.BufferedRedirectingOutputStream;
import com.l2jfree.lang.L2Math;
import com.l2jfree.lang.L2System;
import com.l2jfree.lang.L2TextBuilder;
import com.l2jfree.lang.management.DeadlockDetector;
import com.l2jfree.sql.DataSourceInitializer;
import com.l2jfree.sql.L2Database;
import com.l2jfree.sql.L2DatabaseInstaller;
import com.l2jfree.util.HandlerRegistry;
import com.l2jfree.util.concurrent.L2ThreadPool;
import com.l2jfree.util.concurrent.ThreadPoolInitializer;
import com.l2jfree.util.jar.ClassFinder;
import com.l2jfree.util.logging.L2Logger;

// LOW fill /doc folder
/**
 */
public abstract class L2Config
{
	/**
	 * Returns application lifetime in an user-friendly string.
	 * 
	 * @return time since launch
	 */
	public static String getUptime()
	{
		final long uptimeInSec = (long)Math.ceil(ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0);
		
		final long s = uptimeInSec / 1 % 60;
		final long m = uptimeInSec / 60 % 60;
		final long h = uptimeInSec / 3600 % 24;
		final long d = uptimeInSec / 86400;
		
		final L2TextBuilder tb = new L2TextBuilder();
		
		if (d > 0)
			tb.append(d + " day(s), ");
		
		if (h > 0 || tb.length() != 0)
			tb.append(h + " hour(s), ");
		
		if (m > 0 || tb.length() != 0)
			tb.append(m + " minute(s), ");
		
		if (s > 0 || tb.length() != 0)
			tb.append(s + " second(s)");
		
		return tb.moveToString();
	}
	
	/**
	 * Returns application lifetime in a short string.
	 * 
	 * @return time since launch
	 */
	public static String getShortUptime()
	{
		final long uptimeInSec = (long)Math.ceil(ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0);
		
		final long s = uptimeInSec / 1 % 60;
		final long m = uptimeInSec / 60 % 60;
		final long h = uptimeInSec / 3600 % 24;
		final long d = uptimeInSec / 86400;
		
		final L2TextBuilder tb = new L2TextBuilder();
		
		if (d > 0)
			tb.append(d + "d");
		
		if (h > 0 || tb.length() != 0)
			tb.append(h + "h");
		
		if (m > 0 || tb.length() != 0)
			tb.append(m + "m");
		
		if (s > 0 || tb.length() != 0)
			tb.append(s + "s");
		
		return tb.moveToString();
	}
	
	/** Logging configuration file */
	public static final String LOG_FILE = "./config/logging.properties";
	
	/**
	 * Defines the type of log entries that should be followed by a complete stack trace, regardless
	 * if an exception is attached.
	 */
	public static Level EXTENDED_LOG_LEVEL = Level.OFF;
	
	protected static final L2Logger _log;
	
	/** A stream where normal log messages are printed. */
	public static final PrintStream out = System.out;
	/** A stream where error messages are printed. */
	public static final PrintStream err = System.err;
	
	// TODO temporarily fixed, proper detection required
	public static boolean LAUNCHED_FROM_IDE = true;
	
	static
	{
		Locale.setDefault(Locale.ENGLISH);
		
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e)
			{
				System.err.print("Exception in thread \"" + t.getName() + "\" ");
				
				e.printStackTrace(System.err);
				
				// restart automatically
				if (e instanceof Error && !(e instanceof StackOverflowError))
					Shutdown.halt(TerminationStatus.RUNTIME_UNCAUGHT_ERROR);
			}
		});
		
		for (Thread t : Thread.getAllStackTraces().keySet())
			t.setUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());
		
		if (System.getProperty("user.name").equals("root") && System.getProperty("user.home").equals("/root"))
		{
			System.out.print("L2jFree servers should not run under root-account ... exited.");
			Shutdown.exit(TerminationStatus.ENVIRONMENT_SUPERUSER);
		}
		
		final Map<String, List<String>> libs = new FastMap<String, List<String>>();
		
		final Set<File> files = new FastSet<File>();
		
		for (String classPath : System.getProperty("java.class.path").split(File.pathSeparator))
		{
			final File classPathFile = new File(classPath);
			
			if (classPathFile.isDirectory())
			{
				for (File f : classPathFile.listFiles())
					files.add(f);
			}
			else
				files.add(classPathFile);
		}
		
		boolean shouldExit = false;
		
		for (File f : files)
		{
			if (!f.getName().endsWith("jar"))
				continue;
			
			final L2TextBuilder sb = new L2TextBuilder();
			
			final StringTokenizer st = new StringTokenizer(f.getName(), "-");
			
			tokenizer: while (st.hasMoreTokens())
			{
				final String token = st.nextToken();
				
				boolean numberOnly = true;
				
				for (int i = 0; i < token.length(); i++)
				{
					char c = token.charAt(i);
					
					if (numberOnly && c == '.')
						break tokenizer;
					
					numberOnly &= Character.isDigit(c);
				}
				
				if (sb.length() != 0)
					sb.append("-");
				
				sb.append(token);
			}
			
			String str = sb.moveToString();
			List<String> list = libs.get(str);
			
			if (list == null)
				libs.put(str, list = new ArrayList<String>());
			else
				shouldExit = true;
			
			list.add(f.getName());
		}
		
		if (shouldExit)
		{
			System.out.println("Server should not run with classpath conflicts! "
					+ "(rename/remove possible conflicting classpath entries)");
			
			for (Map.Entry<String, List<String>> entry : libs.entrySet())
				if (entry.getValue().size() > 1)
					for (String name : entry.getValue())
						System.out.println("\t'" + name + "'");
			
			Shutdown.exit(TerminationStatus.ENVIRONMENT_CP_CONFLICT);
		}
		
		System.setProperty("line.separator", "\r\n");
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("java.util.logging.manager", "com.l2jfree.util.logging.L2LogManager");
		
		try
		{
			// empty logging parameters
			final Properties props = new Properties();
			
			InputStream is = null;
			BufferedInputStream bis = null;
			try
			{
				// load default config from resources
				is = ClassLoader.getSystemResourceAsStream("logging.properties");
				
				props.load(is);
				
				// load optional project specific config (overwrites identical parameters)
				if (new File(LOG_FILE).exists())
				{
					bis = new BufferedInputStream(new FileInputStream(LOG_FILE));
					
					props.load(bis);
				}
			}
			catch (Exception e) // IOException | RuntimeException
			{
				e.printStackTrace();
			}
			finally
			{
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(bis);
			}
			
			if (props.isEmpty())
			{
				LogManager.getLogManager().readConfiguration();
			}
			else
			{
				final StringWriter sw = new StringWriter();
				
				try
				{
					props.store(sw, null);
				}
				catch (Exception e) // IOException | RuntimeException
				{
					e.printStackTrace();
				}
				
				LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(sw.toString().getBytes("UTF-8")));
			}
		}
		catch (Exception e) // IOException | RuntimeException
		{
			throw new Error(e);
		}
		
		_log = L2Logger.getLogger(L2Config.class);
		_log.info("logging initialized");
		
		System.setOut(new PrintStream(new BufferedRedirectingOutputStream() {
			@Override
			protected void handleLine(String line)
			{
				_log.logp(Level.INFO, getCaller(), line);
			}
		}));
		
		System.setErr(new PrintStream(new BufferedRedirectingOutputStream() {
			@Override
			protected void handleLine(String line)
			{
				_log.logp(Level.WARNING, getCaller(), line);
			}
		}));
	}
	
	private static StackTraceElement getCaller()
	{
		StackTraceElement[] stack = new Throwable().getStackTrace();
		
		for (int i = stack.length - 1; i >= 0; i--)
		{
			if (stack[i].getClassName().startsWith("java.io.") || stack[i].getMethodName().equals("printStackTrace"))
				return stack[L2Math.limit(0, i + 1, stack.length - 1)];
			
			//if (stack[i].getMethodName().equals("dispatchUncaughtException"))
			//	break;
		}
		
		return null;
	}
	
	/** Flushes all pending log entries. */
	// FIXME MMOLogger.flush()
	public static void flush()
	{
		// those are redirected to loggers, so flush them first
		System.out.flush();
		System.err.flush();
		
		// then flush the loggers themselves
		final LogManager logManager = LogManager.getLogManager();
		
		for (String loggerName : Collections.list(logManager.getLoggerNames()))
		{
			if (loggerName == null)
				continue;
			
			final Logger logger = logManager.getLogger(loggerName);
			
			if (logger == null)
				continue;
			
			for (Handler handler : logger.getHandlers())
				if (handler != null)
					handler.flush();
		}
		
		// and finally the real console streams
		L2Config.out.flush();
		L2Config.err.flush();
	}
	
	// TODO make sure to fit new config scheme, and move to config related classes
	private static final HandlerRegistry<String, ConfigLoader> _loaders = new HandlerRegistry<String, ConfigLoader>() {
		@Override
		public String standardizeKey(String key)
		{
			return key.trim().toLowerCase();
		}
	};
	
	protected static void registerConfig(ConfigLoader loader)
	{
		_loaders.register(loader.getName(), loader);
	}
	
	protected static void registerConfigClasses(String configPackageName) throws Exception
	{
		for (Class<?> clazz : ClassFinder.findClasses(configPackageName))
		{
			if (ConfigPropertiesLoader.class.isAssignableFrom(clazz))
			{
				ConfigPropertiesLoader config = (ConfigPropertiesLoader)clazz.newInstance();
				
				L2Config.registerConfig(config);
			}
		}
	}
	
	/**
	 * Load all available configuration files.
	 * 
	 * @throws Exception if any config failed to load
	 */
	public static void loadConfigs() throws Exception
	{
		for (ConfigLoader loader : _loaders.getHandlers(true).values())
			loader.load();
	}
	
	/**
	 * Save all available configuration files.
	 */
	public static void storeConfigs()
	{
		for (ConfigLoader loader : _loaders.getHandlers(true).values())
		{
			final ConfigClassInfo info = loader.getConfigClassInfo();
			
			if (info != null)
				info.store();
		}
	}
	
	/**
	 * Load the specified configuration file.
	 * 
	 * @param name Configuration name
	 * @return the outcome of this call in a string
	 * @throws Exception if the specified config could not be loaded
	 */
	public static String loadConfig(String name) throws Exception
	{
		final ConfigLoader loader = _loaders.get(name);
		
		if (loader == null)
			throw new Exception();
		
		try
		{
			loader.load();
			return "'" + loader.getName() + "' reloaded!";
		}
		catch (Exception e)
		{
			return e.getMessage();
		}
	}
	
	/**
	 * @return all available configuration names
	 */
	public static String getLoaderNames()
	{
		return StringUtils.join(_loaders.getHandlers(true).keySet().iterator(), "|");
	}
	
	protected static abstract class ConfigLoader
	{
		protected abstract String getName();
		
		@SuppressWarnings("static-method")
		protected ConfigClassInfo getConfigClassInfo()
		{
			return null;
		}
		
		protected abstract void load() throws Exception;
		
		@Override
		public final int hashCode()
		{
			return getClass().hashCode();
		}
		
		@Override
		public final boolean equals(Object obj)
		{
			return getClass().equals(obj.getClass());
		}
	}
	
	protected static abstract class ConfigFileLoader extends ConfigLoader
	{
		protected abstract File getFile();
		
		@Override
		protected final void load() throws Exception
		{
			_log.info("loading '" + getFile() + "'");
			
			BufferedReader br = null;
			try
			{
				br = new BufferedReader(new FileReader(getFile()));
				
				loadReader(br);
			}
			catch (Exception e)
			{
				_log.fatal("Failed to load '" + getFile() + "'!", e);
				
				throw new Exception("Failed to load '" + getFile() + "'!");
			}
			finally
			{
				IOUtils.closeQuietly(br);
			}
		}
		
		protected abstract void loadReader(BufferedReader reader) throws Exception;
	}
	
	/** This class defines how to load configuration properties from a file. */
	public static abstract class ConfigPropertiesLoader extends ConfigFileLoader
	{
		protected ConfigPropertiesLoader()
		{
			getConfigClassInfo();
		}
		
		@Override
		protected final String getName()
		{
			return getConfigClassInfo().getConfigClass().fileName();
		}
		
		@Override
		protected final File getFile()
		{
			return getConfigClassInfo().getConfigFile();
		}
		
		@Override
		protected final ConfigClassInfo getConfigClassInfo()
		{
			try
			{
				return ConfigClassInfo.valueOf(getClass());
			}
			catch (Exception e)
			{
				throw new Error(e);
			}
		}
		
		@Override
		protected final void loadReader(BufferedReader reader) throws Exception
		{
			final L2Properties properties = new L2Properties(reader);
			
			getConfigClassInfo().load(properties);
			
			loadImpl(properties);
			
			getConfigClassInfo().store();
		}
		
		/**
		 * @param properties
		 */
		protected void loadImpl(L2Properties properties)
		{
			// load without annotations
		}
	}
	
	protected static void initApplication(Package configPackage,
			Class<? extends ThreadPoolInitializer> threadPoolInitializerClass)
	{
		initApplication(configPackage.getName(), threadPoolInitializerClass, null);
	}
	
	protected static void initApplication(String configPackageName,
			Class<? extends ThreadPoolInitializer> threadPoolInitializerClass)
	{
		initApplication(configPackageName, threadPoolInitializerClass, null);
	}
	
	protected static void initApplication(Package configPackage,
			Class<? extends ThreadPoolInitializer> threadPoolInitializerClass,
			Class<? extends DataSourceInitializer> dataSourceInitializerClass)
	{
		initApplication(configPackage.getName(), threadPoolInitializerClass, dataSourceInitializerClass);
	}
	
	protected static void initApplication(String configPackageName,
			Class<? extends ThreadPoolInitializer> threadPoolInitializerClass,
			Class<? extends DataSourceInitializer> dataSourceInitializerClass)
	{
		Util.printSection("Config");
		
		try
		{
			L2Config.registerConfigClasses(configPackageName);
		}
		catch (Exception e)
		{
			_log.fatal("Could not load configuration classes!", e);
			Shutdown.exit(TerminationStatus.RUNTIME_INVALID_CONFIGURATION);
		}
		
		try
		{
			L2Config.loadConfigs();
		}
		catch (Exception e)
		{
			_log.fatal("Could not load configuration files!", e);
			Shutdown.exit(TerminationStatus.RUNTIME_INVALID_CONFIGURATION);
		}
		
		Util.printSection("Thread Pool");
		
		try
		{
			L2ThreadPool.initThreadPools(threadPoolInitializerClass.newInstance());
		}
		catch (Exception e)
		{
			_log.fatal("Could not initialize thread pools!", e);
			Shutdown.exit(TerminationStatus.RUNTIME_INITIALIZATION_FAILURE);
		}
		
		if (dataSourceInitializerClass != null)
		{
			Util.printSection("Database");
			
			try
			{
				L2Database.setDataSource("default", dataSourceInitializerClass.newInstance());
			}
			catch (Exception e)
			{
				_log.fatal("Could not initialize DB connections!", e);
				Shutdown.exit(TerminationStatus.RUNTIME_INITIALIZATION_FAILURE);
			}
			
			Util.printSection("Database Installer");
			
			try
			{
				L2DatabaseInstaller.check();
			}
			catch (Exception e)
			{
				_log.fatal("Could not initialize DB tables!", e);
				Shutdown.exit(TerminationStatus.RUNTIME_INITIALIZATION_FAILURE);
			}
		}
		
		Util.printSection("Utility");
		
		Shutdown.initShutdownHook();
		
		DeadlockDetector.getInstance();
	}
	
	/**
	 * To load full static utility classes.
	 * 
	 * @param clazz
	 */
	protected static void load(Class<?> clazz)
	{
		try
		{
			Class.forName(clazz.getName());
		}
		catch (ClassNotFoundException e)
		{
			// should never happen
			throw new Error(e);
		}
	}
	
	protected static void applicationLoaded(String appName, String[] versionInfo, boolean dumpHeap)
	{
		Util.printSection("Summary");
		for (String line : versionInfo)
			_log.info(line);
		_log.info("Operating System: " + Util.getOSName() + " " + Util.getOSVersion() + " " + Util.getOSArch());
		_log.info("Available CPUs: " + Util.getAvailableProcessors());
		
		System.gc();
		System.runFinalization();
		
		for (String line : Util.getMemUsage())
			_log.info(line);
		
		_log.info("Server loaded in " + Util.formatNumber(ManagementFactory.getRuntimeMXBean().getUptime())
				+ " milliseconds.");
		
		if (dumpHeap && !L2Config.LAUNCHED_FROM_IDE)
			L2System.dumpHeap(true);
		
		Util.printSection(appName);
		
		Startup.onStartup();
	}
	
	protected static void shutdownApplication()
	{
		try
		{
			L2Database.shutdown();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		
		try
		{
			L2ThreadPool.shutdown();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		
		try
		{
			L2Config.storeConfigs();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}
