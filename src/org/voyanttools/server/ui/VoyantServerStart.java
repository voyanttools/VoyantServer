/**
 * 
 */
package org.voyanttools.server.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.aw20.jettydesktop.ui.ServerConfigMap;
import org.aw20.jettydesktop.ui.Start;
import org.voyanttools.server.ui.awt.VoyantServerTab;

/**
 * @author sgs
 *
 */
public class VoyantServerStart extends Start {

	public static final String VERSION = "2.6.4";
	
	private JFrame frame;
	
	private JTabbedPane tabbedPane;
	
	protected List<ServerConfigMap>	serverConfigList;


	/**
	 * @throws IOException 
	 * 
	 */
	public VoyantServerStart(ServerConfigMap serverConfigMap) throws IOException {
		initialize(serverConfigMap);
	}


	public static void main(String[] args) {
		File file = new File(System.getProperty("user.dir"), "server-settings.txt");
		for (String arg : args) {
			if (arg.startsWith("settings=") && arg.length()>9) {
				file = new File(arg.substring(9));
				if (file.exists()==false) {
					file = new File(System.getProperty("user.dir"), "server-settings.txt");
				}
				if (file.exists()==false) {
					throw new IllegalArgumentException("Could not find specified settings file: "+file);
				}
			}
		}
		final ServerConfigMap serverConfigMap;
		try {
			serverConfigMap = ServerConfig.getStoredServerConfig(file);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}

		for (String string : args) {
			if (string.contains("headless=true")) {
				try {
					VoyantServerStartHeadless voyantServerStartHeadless = new VoyantServerStartHeadless(serverConfigMap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return; // don't create gui
			}
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
					
					// only seems to work for Java 1.6<
					// take the menu bar off the jframe
					System.setProperty("apple.laf.useScreenMenuBar", "true");
					// set the name of the application menu item
					System.setProperty("com.apple.mrj.application.apple.menu.about.name", "VoyantServer");
					
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					final VoyantServerStart window = new VoyantServerStart(serverConfigMap);
					window.frame.setVisible(true);
					
					if (System.getProperty("os.name").equals("Mac OS X"))
				    {
				        Runtime.getRuntime().addShutdownHook(new Thread()
				        {
				            @Override
				            public void run()
				            {
				            	window.stopServers();
				            }
				        });
				    }

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void initialize(ServerConfigMap serverConfigMap) throws IOException {
		frame = new JFrame();
		frame.setTitle("Voyant Server");
		frame.setBounds(100, 100, 610, 414);
		frame.setMinimumSize( new Dimension(610, 414) );
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/org/voyanttools/resources/voyant_small.png")));
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				terminateApp();
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				terminateApp();
			}
		});
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmSupportSite = new JMenuItem("Visit Voyant Server Help Page");
		mntmSupportSite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://voyant-tools.org/docs/#!/guide/server"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		mnHelp.add(mntmSupportSite);
		
		// We need to add a new tab
		VoyantServerTab	sT	= new VoyantServerTab(this, serverConfigMap);
		
		frame.getContentPane().add(sT, BorderLayout.CENTER);
	}
	
	private void stopServers() {
		VoyantServerTab sT	= (VoyantServerTab)frame.getContentPane().getComponent(0);
		if ( sT.isServerRunning() ) {
			sT.stopServer();
		}
	}
	protected boolean terminateApp() {
		stopServers();
		System.exit(1);
		return true;
	}
}
