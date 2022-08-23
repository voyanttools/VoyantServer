/**
 * 
 */
package org.voyanttools.server.ui.awt;

import java.awt.Font;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.aw20.jettydesktop.rte.JettyRunTime;
import org.aw20.jettydesktop.ui.ConfigActionInterface;
import org.aw20.jettydesktop.ui.Executor;
import org.aw20.jettydesktop.ui.ExecutorInterface;
import org.aw20.jettydesktop.ui.ServerConfigMap;
import org.aw20.util.DateUtil;
import org.voyanttools.server.ui.ServerConfig;
import org.voyanttools.server.ui.VoyantServerStart;

/**
 * @author sgs
 *
 */
public class VoyantServerTab extends JPanel implements ExecutorInterface{
	private static final long serialVersionUID = 1L;

	private static final String stopServer = "Stop Server";
	private static final String startServer = "Start Server";
	
	private ServerConfigMap serverConfigMap;
	private final ConfigActionInterface configActionI;
	private JButton gotoButton;
	
	private Executor	executor = null;
	private JTextArea textArea;
	private JLabel		labelStatus;
//	private JButton btnClose;
	private JPanel panel;
	private JLabel RemoteMemoryLabel;
	
	
    private javax.swing.JButton startStopButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel memoryUnitLabel;
    private javax.swing.JLabel portLabel;
    private javax.swing.JLabel memoryLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField portTextField;
    private javax.swing.JTextField memoryTextField;
    
	public VoyantServerTab(ConfigActionInterface _configActionI, ServerConfigMap serverConfigMap) throws IOException {
		this.configActionI		= _configActionI;
		this.serverConfigMap = serverConfigMap;
		
		initComponents();
		startServer();
	}
	
	private void initComponents() {
        logoLabel = new javax.swing.JLabel();
        startStopButton = new javax.swing.JButton();
        portTextField = new javax.swing.JTextField();
        memoryTextField = new javax.swing.JTextField();
        memoryUnitLabel = new javax.swing.JLabel();
        portLabel = new javax.swing.JLabel();
        memoryLabel = new javax.swing.JLabel();
        gotoButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        labelStatus = new javax.swing.JLabel();
        RemoteMemoryLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        
		textArea.setTabSize(2);
		textArea.setFont(new Font("Courier New", Font.PLAIN, 11));
		textArea.setEditable(false);

		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        setBackground(new java.awt.Color(255, 255, 255));

        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/voyanttools/resources/voyant_small.png"))); // NOI18N
        logoLabel.setText("<html><body><h2>Voyant<br />Server</h2></body></html>");

        startStopButton.setText(stopServer);
        startStopButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        startStopButton.setMaximumSize(new java.awt.Dimension(150, 29));
        startStopButton.setMinimumSize(new java.awt.Dimension(150, 29));
        startStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	startStopButton.setEnabled(false);
            	if (evt.getActionCommand().equals(stopServer)) {
            		stopServer();
            	}
            	else {
            		try {
						startServer();
					} catch (IOException e) {
						onConsole(e.toString());
					}
            	}
            }
        });
        startStopButton.setEnabled(false);

        portTextField.setText(serverConfigMap.getPort());
        portTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        memoryTextField.setText(serverConfigMap.getMemoryJVM());
        memoryTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        memoryUnitLabel.setText("MB");

        portLabel.setText("Port");

        memoryLabel.setText("Memory");

        gotoButton.setText("Open Web");
        gotoButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gotoButton.setMaximumSize(new java.awt.Dimension(88, 29));
        gotoButton.setMinimumSize(new java.awt.Dimension(88, 29));
        gotoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	openWeb();
            }
        });

        labelStatus.setText("Console Last Updated…");

//        RemoteMemoryLabel.setText("Memory Usage…");

        textArea.setColumns(20);
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);
        
        JLabel creditsLabel = new JLabel();
        creditsLabel.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        creditsLabel.setText("Stéfan Sinclair & Geoffrey Rockwell (v"+VoyantServerStart.VERSION+")");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(labelStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(RemoteMemoryLabel))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(creditsLabel)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelStatus)
                    .addComponent(RemoteMemoryLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addComponent(creditsLabel))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(startStopButton, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(gotoButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(portLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(memoryLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(memoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(memoryUnitLabel)))
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(portLabel)
                            .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(startStopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(memoryUnitLabel)
                            .addComponent(memoryLabel)
                            .addComponent(memoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(gotoButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
      }
	

	public boolean isServerRunning(){
		return (executor != null);
	}
	
	public void stopServer(){
//		startstopButton.setEnabled(false);
//		startstopButton.setText( "Stopping..." );
		executor.exit();
	}
	
	private void startServer() throws IOException{
		if (ServerConfig.isModified(serverConfigMap)) {
			serverConfigMap = ServerConfig.getStoredServerConfig();
		}
		serverConfigMap.setPort(portTextField.getText());
		serverConfigMap.setMemoryJVM(memoryTextField.getText());
		onConsole("*** Starting Voyant Server – Web page will open automatically when ready ***\n");
//		startstopButton.setEnabled(false);
//		startstopButton.setText( "Starting..." );
		try {
			executor	= new Executor(serverConfigMap, this );
		} catch (IOException e) {
			onConsole( e.getMessage() );
//			startstopButton.setEnabled(true);
//			startstopButton.setText( "Start Server" );
		}
	}
	
	public ServerConfigMap getConfig(){
		return serverConfigMap;
	}
	
	public void setConfig(ServerConfigMap scm){
		serverConfigMap.clear();
		serverConfigMap.putAll( scm );
	}

	public void onMemory( String m ){
//		RemoteMemoryLabel.setText(m);
	}
	
	@Override
	public synchronized void onConsole(String message) {
		textArea.append(message);
		textArea.append("\r\n");
		labelStatus.setText( "Console updated: " + DateUtil.getHttpDate(System.currentTimeMillis()) );
		
		if ( message.indexOf( JettyRunTime.JETTYSTARTED ) != -1 ) {
			startStopButton.setEnabled(true);
			startStopButton.setText("Stop Server");
			openWeb();
		}
		
		else if (message.indexOf( "Server terminated") != -1) {
			startStopButton.setEnabled(true);
			startStopButton.setText(startServer);
		}
//			gotoButton.setEnabled(true);
	}

	@Override
	public void onServerStart() {
		onConsole( "Server Starting" );
//		new WebOpener().run();
//		gotoButton.setEnabled(false);
//		editConfigButton.setEnabled(false);
//		deleteButton.setEnabled(false);
//		btnClose.setEnabled(false);
//		startstopButton.setEnabled(true);
//		startstopButton.setText("Stop Server");
	}

	@Override
	public synchronized void onServerExit() {
		if ( executor == null )
			return;
		
		onConsole("Server terminated.");
//		gotoButton.setEnabled(false);
//		editConfigButton.setEnabled(true);
//		deleteButton.setEnabled(true);
//		startstopButton.setEnabled(true);
//		btnClose.setEnabled(true);
//		startstopButton.setText("Start Server");
		
		executor	= null;
	}

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void openWeb() {                                   
		if ( executor!=null && !executor.isWebAppRunning() )
			return;
		
		try {
			URI uri = getUri();
			java.awt.Desktop.getDesktop().browse(uri);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

    }
    
    private URI getUri() {
		String host	= serverConfigMap.getIP();
		if ( host == null || host.length() == 0 ) host = "127.0.0.1";
		
		String contextPath = serverConfigMap.getContextPath();
		if (contextPath == null) contextPath = "/";
		
		String queryParams = serverConfigMap.getQueryParameters();
		if (queryParams == null) queryParams = "";
		
		return java.net.URI.create("http://" + host + ":" + serverConfigMap.getPort() + contextPath + queryParams );
   	
    }
    
    private class WebOpener extends TimerTask {

		@Override
		public void run() {
			boolean isReady = false;
			
			if (executor==null || !executor.isWebAppRunning()) {isReady=false;}
			else {
				URI uri = getUri();
				try {
					URLConnection con = uri.toURL().openConnection();
					System.err.println(con.getContent());
					isReady = true;
				}  catch (Exception e) {
//					e.printStackTrace();
				}
			}
			if (isReady) {
				openWeb();
			}
			else {
				Timer timer = new Timer();
				timer.schedule(new WebOpener(), 100);
			}
		}
    	
    }

}
