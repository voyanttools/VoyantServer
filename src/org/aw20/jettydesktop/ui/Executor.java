/* 
 *  JettyDesktop is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  Free Software Foundation,version 3.
 *  
 *  JettyDesktop is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  If not, see http://www.gnu.org/licenses/
 *  
 *  Additional permission under GNU GPL version 3 section 7
 *  
 *  If you modify this Program, or any covered work, by linking or combining 
 *  it with any of the JARS listed in the README.txt (or a modified version of 
 *  (that library), containing parts covered by the terms of that JAR, the 
 *  licensors of this Program grant you additional permission to convey the 
 *  resulting work. 
 *  
 *  https://github.com/aw20/jettydesktop
 *  
 *  May 2013
 */
package org.aw20.jettydesktop.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.aw20.jettydesktop.rte.JettyRunTime;
import org.aw20.util.SocketUtil;

public class Executor extends Object {

	/**
	 * 
	 * "C:\Program Files (x86)\Java\jre6\bin\javaw.exe" -agentlib:jdwp=transport=dt_socket,suspend=y,address=localhost:55560 -Dfile.encoding=Cp1252 -classpath C:\github\jettydesktop\bin org.aw20.jettydesktop.ui.Executor
	 */

	private Process process;
	private ExecutorInterface executorI;
	private boolean bRun = true;
	private Thread ioconsumers[];
	private adminPortWatcher	AdminPortWatcher = null;
	private int	adminPort;

	public Executor(ServerConfigMap options, ExecutorInterface executorI) throws IOException {
		this.executorI = executorI;

		// Check to see if this server is already running
		if (SocketUtil.isRemotePortAlive(options.getIP(), Integer.parseInt(options.getPort()))) {
			throw new IOException("Port#" + options.getPort() + " appears to be in use already");
		}

		findFreePort();
				
		// Start up the server
		String USR_HOME = System.getProperty("user.dir") + File.separator;

		String JDK_HOME;
		if (options.getCurrentJVM() == null)
			JDK_HOME = options.getCustomJVM() + File.separator + "bin" + File.separator;
		else
			JDK_HOME = System.getProperty("java.home") + File.separator + "bin" + File.separator;

		if (new File(JDK_HOME, "javaw.exe").exists() )
			JDK_HOME += "javaw.exe";
		else if (new File(JDK_HOME, "javaw").exists() )
			JDK_HOME += "javaw";
		else
			JDK_HOME += "java";

		List<String> programArgs = new ArrayList<String>();
		programArgs.add(JDK_HOME);

		// force UTF-8
		programArgs.add("-Dfile.encoding=UTF-8");
		
		if (options.getAllowInput()==false) {
			programArgs.add("-Dorg.voyanttools.server.allowinput=false");
		}
		if (options.getAllowDownload()==false) {
			programArgs.add("-Dorg.voyanttools.server.allowdownload=false");
		}
		// default is false in Voyant
		if (options.getAllowPrivateIP()==true) {
			programArgs.add("-Dorg.voyanttools.server.allowprivateip=true");
		}

		if (options.containsKey("open_menu")) {
			String openMenu = options.get("open_menu");
			if (openMenu!=null && openMenu.isEmpty()==false) {
				programArgs.add("-Dorg.voyanttools.voyant.openmenu=\""+URLEncoder.encode(openMenu, "UTF-8")+"\"");
			}
		}
		
		if (options.getMemoryJVM() != null)
			programArgs.add("-Xmx" + options.getMemoryJVM() + "m");

		if ( options.getDefaultJVMArgs() != null )
			programArgs.add( options.getDefaultJVMArgs() );

		programArgs.add("-classpath");
		programArgs.add(getClasspath(USR_HOME));

		programArgs.add(JettyRunTime.class.getName());

		if (options.getIP().length() > 0)
			programArgs.add(options.getIP());

		programArgs.add(options.getPort());
		programArgs.add(options.getContextPath());
		programArgs.add(options.getWebFolder());
		programArgs.add( String.valueOf(adminPort) );

		ProcessBuilder pb = new ProcessBuilder(programArgs);
		
		System.out.print("Command: ");
		for (String arg : programArgs) {
			System.out.print(arg+" ");
		}
		System.err.println("");

		// Start the process
		process = pb.start();

		if (executorI != null) {
			executorI.onServerStart();
		}

		ioconsumers = new Thread[2];
		ioconsumers[1] = new ioConsumer(process.getInputStream());
		if (options.getLogsFile()) {
			File dir = new File(options.getDataFolder(), "logs");
			if (dir.exists()==false) {
				if (!dir.mkdir()) {
					System.err.println("Can't create logs directory: "+dir);
					throw new IOException("Can't create logs directory: "+dir);
				}
			}
			File out = new File(dir, "jetty.log");
			if (executorI!=null) {
				executorI.onConsole("Logging to file: "+out.getAbsolutePath());
			}
			FileWriter outWriter = new FileWriter(out, true);
			ioconsumers[0] = new ioFileConsumer(process.getErrorStream(), outWriter);
		}
		else {
			ioconsumers[0] = new ioConsumer(process.getErrorStream());
		}
		
		if ( adminPort > 0 ){
			try{
				AdminPortWatcher	= new adminPortWatcher();
			}catch(IOException ioe){
				AdminPortWatcher = null;
			}
		}
	}

	
	private void findFreePort(){
		try{
			adminPort	= 34000;
			
			for ( int x=0; x < 1000; x++ ){
				adminPort += x;

				Socket s = new Socket();
				s.connect( new InetSocketAddress( "127.0.0.1", adminPort ), 1000 );
				s.close();
			}

			adminPort = -1;
			
		}catch(Exception e){
			executorI.onConsole("Using Free AdminPort=" + adminPort );
			return;
		}
	}
	
	
	class adminPortWatcher extends Thread {
		
		BufferedReader br;
		Socket s;
		
		public adminPortWatcher() throws IOException{
			s = new Socket();
			s.connect( new InetSocketAddress( "127.0.0.1", adminPort ), 1000 );
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			start();
		}
		
		public void run(){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				return;
			}

			while (bRun) {
				String line;
				try {
					while ((line = br.readLine()) != null) {
						if (executorI != null)
							executorI.onMemory(line);
					}
				} catch (IOException e) {
					break;
				}
			}
			
			try {
				br.close();
				s.close();
			} catch (IOException e) {}

		}
	}
	
	
	class ioConsumer extends Thread {

		BufferedReader br;

		public ioConsumer(InputStream io) {
			br = new BufferedReader(new InputStreamReader(io));
			start();
		}

		public void run() {
			
			while (bRun) {
				String line;
				try {
					while ((line = br.readLine()) != null) {
						if (executorI != null)
							executorI.onConsole(line);
					}
				} catch (IOException e) {
					break;
				}
			}
			
			try {
				br.close();
			} catch (IOException e) {}

			if (executorI != null) {
				executorI.onServerExit();
			}
		}
	}
	
	class ioFileConsumer extends Thread {
		BufferedReader br;
		PrintWriter wr;

		public ioFileConsumer(InputStream io, Writer wr) {
			br = new BufferedReader(new InputStreamReader(io));
			this.wr = new PrintWriter(wr);
			start();
		}

		public void run() {
			
			while (bRun) {
				String line;
				try {
					while ((line = br.readLine()) != null) {
						if (executorI != null) {
							wr.println(line);
							wr.flush();
						}
					}
				} catch (IOException e) {
					break;
				}
			}
			
			try {
				br.close();
				wr.close();
			} catch (IOException e) {}
			

			if (executorI != null) {
				executorI.onServerExit();
			}
		}		
	}

	public boolean isWebAppRunning() {
		return true;
	}

	public void exit() {
		process.destroy();
		bRun = false;
		ioconsumers[0].interrupt();
		ioconsumers[1].interrupt();
		
		if ( AdminPortWatcher != null )
			AdminPortWatcher.interrupt();
	}

	private String getClasspath(String usrdir) {
		StringBuilder sb = new StringBuilder(64);

//		if (new File(usrdir, "jettydesktop.jar").isFile()) {
//			sb.append(usrdir + "jettydesktop.jar");
		if (new File(usrdir, "VoyantServer.jar").isFile()) {
			sb.append(usrdir + "VoyantServer.jar");
		} else {
			sb.append(usrdir + "bin").append(File.pathSeparator + usrdir + "lib/*"); /* + File.separator + "jetty-all-9.3.9.M1-uber.jar")
				.append(File.pathSeparator + usrdir + "lib" + File.separator + "servlet-api-3.1.0.jar")
				.append(File.pathSeparator + usrdir + "lib" + File.separator + "jasper-6.0.45.jar")
				.append(File.pathSeparator + usrdir + "lib" + File.separator + "tomcat-juli-9.0.0.M4.jar")
				.append(File.pathSeparator + usrdir + "lib" + File.separator + "jsp-api-2.0.jar");*/
		}

		return sb.toString();
	}

}
