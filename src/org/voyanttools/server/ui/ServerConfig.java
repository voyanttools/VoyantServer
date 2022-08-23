package org.voyanttools.server.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.aw20.jettydesktop.ui.ServerConfigMap;

public class ServerConfig {

	public static ServerConfigMap getStoredServerConfig() throws IOException {
		return getStoredServerConfig(getFile());
	}
	public static ServerConfigMap getStoredServerConfig(File file) throws IOException {
		ServerConfigMap serverConfigMap =  new ServerConfigMap(){{
			setName("Voyant");
			setIP("");
			setPort("8888");
			setWebFolder(System.getProperty("user.dir") + File.separator + "_app");
			setCurrentJVM();
			setMemoryJVM("1024");
			setLogsFile(false);
			setAllowInput(true);
		}};
		Properties properties = new Properties();
		serverConfigMap.put("lastModified", String.valueOf(file.lastModified()));
		FileInputStream fis = new FileInputStream(file);
		properties.load(fis);
		fis.close();
		for (String name : properties.stringPropertyNames()) {
			String val = properties.getProperty(name).trim();
			String prop = System.getProperty("org.voyanttools.server."+name); // allow items to be overwritten on the command line
			if (val.isEmpty() && prop==null) { continue;}
			if (name.equals("port")) {
				serverConfigMap.setPort(prop==null ? val : prop);
			}
			else if (name.equals("memory")) {
				serverConfigMap.setMemoryJVM(prop==null ? val : prop);
			}
			else if (name.equals("data_directory")) {
				serverConfigMap.setDataFolder(prop==null ? val : prop);
			}
			else if (name.equals("uri_path")) {
				serverConfigMap.setDefaultWebUri(prop==null ? val : prop);
			}
			else if (name.equals("host")) {
				serverConfigMap.setIP(prop==null ? val : prop);
			}
			else if (name.equals("context_path")) {
				serverConfigMap.setContextPath(prop==null ? val : prop);
			}
			else if (name.equals("query_parameters")) {
				serverConfigMap.setQueryParameters(prop==null ? val : prop);
			}
//			else if (name.equals("logs_file")) {
//				serverConfigMap.setLogsFile(prop==null ? val.equals("true") : prop.equals("true"));
//			}
			else {
				serverConfigMap.put(name, properties.getProperty(name));
			}
		}
		
		String dataFolder = serverConfigMap.getDataFolder();
		if (dataFolder==null || dataFolder.isEmpty()) {
			File dataFile = new File("data");
			if (dataFile.exists()) {
				serverConfigMap.setDataFolder(dataFile.getAbsolutePath());
			}
			else {
				File voyantFile = new File(System.getProperty("java.io.tmpdir"), "VoyantServer");
				if (!voyantFile.exists()) {
					if (!voyantFile.mkdir()) {
						throw new IOException("Can't create temporary directory: "+voyantFile);
					}
				}
				serverConfigMap.setDataFolder(voyantFile.getAbsolutePath());
			}
		}
		
		File dataFile = new File(serverConfigMap.getDataFolder());
		if (!dataFile.exists()) {
			throw new IOException("Data folder must already exist, VoyantServer won't create it: "+dataFile.getAbsolutePath());
		}
		
		// set java.io.tmpdir for voyant data
		serverConfigMap.setDefaultJVMArgs((serverConfigMap.getDefaultJVMArgs()!=null ? serverConfigMap.getDefaultJVMArgs()+" " : "") +"-Djava.io.tmpdir="+new File(serverConfigMap.getDataFolder()).getAbsolutePath() );
		
		String prop = System.getProperty("org.voyanttools.server.logs_file"); // allow items to be overwritten on the command line
		if (prop!=null || properties.getProperty("logs_file", "").equals("true")) {
			serverConfigMap.setLogsFile(prop==null ? true : prop.equals("true"));
		}
		
		return serverConfigMap;
	}
	
	public static boolean isModified(ServerConfigMap serverConfigMap) {
		File file = getFile();
		return file.lastModified() > Long.valueOf(serverConfigMap.get("lastModified"));
	}
	
	private static File getFile() {
		return new File("server-settings.txt");
	}
	
	public static void main(String[] args) throws IOException {
		ServerConfig.getStoredServerConfig();
	}
}
