package servergui;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import java.net.*;
import java.util.*;
import java.io.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author scopeinfinity
 */
public class Home extends javax.swing.JFrame {
    public static final String ROOT_URL = "http://127.0.0.1/";
    
    private boolean isRunningServer = false;
    private Runtime rt;
    private Process proc;
    
    private BufferedReader stdIn,stdErr;
    private Display display1, display2;
    
    private static final String PASS_NORMAL = ".pass_n";
    private static final String PASS_ROOT = ".pass_r";
    private static final String PASS = ".pass";
    
    
    private class Display implements Runnable {

        boolean isRunning = true;
        BufferedReader br;
        Thread t;
        
        
        public Display(BufferedReader br) {
            t = new Thread(this);
            t.start();
            this.br = br;
        
        }

        @Override
        public void run() {
            String s;
            while(isRunning) {
                
                try {
                    t.sleep(200);
                    while((s = br.readLine())!=null) {
                        OutputScreen.setText(OutputScreen.getText()+"\n"+s+"\n");
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                    isRunning = false;
                } catch (IOException ex) {
                    Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                    isRunning = false;
                }
            }
        }
        
        
    }
    
    private void refreshIP() {
       /* try {
            String ips = "";
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()) {
                NetworkInterface net = interfaces.nextElement();
                Enumeration<InetAddress> intf = net.getInetAddresses();
                
                while(intf.hasMoreElements()) {
                    String newone = intf.nextElement().getHostAddress();
                    
                    if(newone.matches("([0-9]{1,3}\\.){3}([0-9 ]{3})"))
                    {
                        ips+=newone+" ";
                    }
                    
                }
            }
            IPField.setText(ips);
            
        } catch (SocketException ex) {
            IPField.setText("127.0.0.1");
        }*/
        try{
            String ips = "";
        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec("ifconfig ");//| grep \"inet addr\"");
        Thread.sleep(100);
       BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
       StringBuffer stdout=new StringBuffer();
       String t = null;
        while ((t = stdInput.readLine()) != null) {
        
        stdout.append(t);
        }
        String s=stdout.toString();
            System.out.println(s);
        String[] a=s.split(" ");
        for(int i=0;i<a.length;i++)
        {
            if(a[i].length()>=6 && a[i].substring(0,5).equals("addr:"))
                if(!(a[i].substring(5,a[i].length()).equals("127.0.0.1")))
                {                     
                    ips+=a[i].substring(5)+"\t";   
                    if(!a[i].substring(5).trim().isEmpty())
                        update_ip(a[i].substring(5).trim());
                    
                }
        }
        IPField.setText(ips);
        
        }
        catch(Exception e)
        {
            e.printStackTrace();
         
        }
    }
    
    	public void update_ip(String s)
	{
		String content = null;
		URLConnection connection = null;
		try {
  				connection =  new URL(ROOT_URL+"sendip?"+s).openConnection();
  				Scanner scanner = new Scanner(connection.getInputStream());
 				scanner.useDelimiter("\\Z");
  				content = scanner.next();
			}
		catch ( Exception ex ) {
    			ex.printStackTrace();
			}
	}		
       
    private boolean setPassword(boolean asRoot) throws IOException {
        BufferedWriter bw = null;
        try {
            // TODO add your handling code here:
            File fileIn;
            if(!asRoot)
                fileIn = new File(ServerGUI.rootPath+PASS_NORMAL);
            else
                fileIn = new File(ServerGUI.rootPath+PASS_ROOT);
            BufferedReader br = new BufferedReader(new FileReader(fileIn));
            String pwd = br.readLine();
            br.close();
            
            File file = new File(ServerGUI.rootPath+PASS);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            try {
                bw.write(pwd);
                return true;

            } catch (IOException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,"Error in Loading Password!");
        
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Error in Loading Password!");
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,"Error in Loading Password!");
            }
        }
        return false;
    }
    
    
    private void startServer(boolean asRoot) {
        try {
            setPassword(asRoot);
        } catch (IOException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        String[] commands = {ServerGUI.rootPath+"server"};
        //if(asRoot)
        //    commands[0] = "gksu -u root \""+ServerGUI.rootPath+"server\"";
            
        try {
            proc = rt.exec(commands);
            
            ToggleServer.setText("Stop Server");
            isRunningServer = true;
            stdIn = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            stdErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            
            display1 = new Display(stdIn);
            display2 = new Display(stdIn);
            
            updatePassword.setEnabled(false);
            updatePasswordSudo.setEnabled(false);
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            OutputScreen.setText(OutputScreen.getText().concat("\nError in Starting Server!\n Details\n"+ex.toString()+"\n\n"));
            
        }
    } 
    
    private void stopServer() {
        
        ToggleServer.setText("Start Server");
        proc.destroy();
        updatePassword.setEnabled(true);
        updatePasswordSudo.setEnabled(true);
            
        isRunningServer = false;
        if(display1!=null)
        {
            display1.isRunning = false;
            display1 = null;
        }
        if(display2!=null)
        {
            display2.isRunning = false;
            display2 = null;
        }
        
            
    } 
    
    
    /**
     * Creates new form Home
     */
    public Home() {
        rt = Runtime.getRuntime();
        initComponents();
        String user = System.getProperty("user.name");
        System.out.println("Welcome "+user+"!");
        
        if(user.trim().equals("root"))
        {
            rootMode.setSelected(true);
            rootMode.setText("Root Mode : ON");
        }
        else     {
            rootMode.setSelected(false);
            
            rootMode.setText("Root Mode : OFF");
        }
        
        
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if(isRunningServer)
                    stopServer();
                System.exit(0);
            }
        });
        
        refreshIP();
        
        updateAllowedCommands();
    }
    
    private ArrayList<JCheckBox> checkBoxCmds;
    private void updateAllowedCommands() {
        checkBoxCmds = new ArrayList<>();
        ArrayList<Pair<Integer,String>> cmds = new ArrayList<>();
        File file = new File(ServerGUI.rootPath+"executor/AllCmds");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while((line = br.readLine())!=null) {
                {
                    if(line.length()>2)
                    cmds.add(new Pair<Integer,String>(line.charAt(0)-'0',line.substring(1)));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IOException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        checkBoxCmds.clear();
        for(Pair<Integer,String> cmd:cmds){
            JCheckBox b = new JCheckBox(cmd.getValue());
            checkBoxCmds.add(b);
            b.setSelected(cmd.getKey()!=0);
            if(cmd.getKey()==2)
                b.setEnabled(false);
            
            b.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    saveAllowedCommands(null);
                }
            });
           jPanelAllowedCommands.setLayout(new GridLayout(0,1));
           jPanelAllowedCommands.add(b);
        }
    }
    
    private void saveAllowedCommands(String newCommand) {
        if(isRunningServer) {
            JOptionPane.showMessageDialog(null,"Please Turn Off Server First!");
        } else {
            String out = "[";
            String outAll = "";
            for(JCheckBox c : checkBoxCmds)
            {
                if(c.isEnabled())
                    outAll+=(c.isSelected()?"1":"0")+c.getText()+"\n";
                else
                    outAll+=(c.isSelected()?"2":"0")+c.getText()+"\n";
                
                if(c.isSelected())
                    out+="'"+c.getText()+"',";
            }
            if(newCommand!=null)
            outAll+="0"+newCommand+"\n";
            
            int l=-1;
            if((l=out.lastIndexOf(","))>=0)
                out = out.substring(0,l );
            out+="]";
            
            File file = new File(".",ServerGUI.rootPath+"executor/cmds");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(out.getBytes());
                fos.close();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                 JOptionPane.showMessageDialog(null,"Can't Save Commands");
        
            } catch (IOException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                 JOptionPane.showMessageDialog(null,"Can't Save Commands");
        
            }
            
            File fileAll = new File(".",ServerGUI.rootPath+"executor/AllCmds");
            try {
                FileOutputStream fos = new FileOutputStream(fileAll);
                fos.write(outAll.getBytes());
                fos.close();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                 JOptionPane.showMessageDialog(null,"Can't Save Commands");
        
            } catch (IOException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                 JOptionPane.showMessageDialog(null,"Can't Save Commands");
        
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        ToggleServer = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        IPField = new javax.swing.JTextField();
        RefreshIP = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        OutputScreen = new javax.swing.JTextPane();
        rootMode = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        updatePassword = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        updatePasswordSudo = new javax.swing.JButton();
        passwordFieldSudo = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        addCommand = new javax.swing.JButton();
        newCmd = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanelAllowedCommands = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ToggleServer.setText("Start Server");
        ToggleServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToggleServerActionPerformed(evt);
            }
        });

        jLabel1.setText("My IP : ");

        IPField.setEditable(false);
        IPField.setText("127.0.0.1");
        IPField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IPFieldActionPerformed(evt);
            }
        });

        RefreshIP.setText("Refresh");
        RefreshIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshIPActionPerformed(evt);
            }
        });

        OutputScreen.setEditable(false);
        OutputScreen.setText("Hello User!\n\nYou can change configuration from above tab.\nPress 'Start Server' to continue...\n\n");
        jScrollPane1.setViewportView(OutputScreen);

        rootMode.setText("Root Mode");
        rootMode.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(IPField, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RefreshIP)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(213, 213, 213)
                .addComponent(ToggleServer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rootMode)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(IPField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RefreshIP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ToggleServer)
                    .addComponent(rootMode))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Start", jPanel1);

        jLabel2.setText("Password");

        passwordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordFieldActionPerformed(evt);
            }
        });

        updatePassword.setText("Update Password");
        updatePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatePasswordActionPerformed(evt);
            }
        });

        jLabel3.setText("Allowed Commands");

        updatePasswordSudo.setText("Update Sudo Password");
        updatePasswordSudo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatePasswordSudoActionPerformed(evt);
            }
        });

        passwordFieldSudo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordFieldSudoActionPerformed(evt);
            }
        });

        jLabel4.setText("Sudo Password");

        addCommand.setText(" + ");
        addCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCommandActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelAllowedCommandsLayout = new javax.swing.GroupLayout(jPanelAllowedCommands);
        jPanelAllowedCommands.setLayout(jPanelAllowedCommandsLayout);
        jPanelAllowedCommandsLayout.setHorizontalGroup(
            jPanelAllowedCommandsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 602, Short.MAX_VALUE)
        );
        jPanelAllowedCommandsLayout.setVerticalGroup(
            jPanelAllowedCommandsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 293, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(jPanelAllowedCommands);

        jScrollPane3.setViewportView(jScrollPane2);

        jLabel5.setText("New Command");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(updatePassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(passwordFieldSudo, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(updatePasswordSudo)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addGap(3, 3, 3)
                        .addComponent(newCmd, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addCommand))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updatePassword))
                        .addGap(29, 29, 29))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(passwordFieldSudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(updatePasswordSudo)))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(addCommand)
                    .addComponent(newCmd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Configuration", jPanel2);

        jTextPane1.setEditable(false);
        jTextPane1.setText(ServerGUI.INFO);
        jScrollPane4.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Info", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void RefreshIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshIPActionPerformed
        refreshIP();
    }//GEN-LAST:event_RefreshIPActionPerformed

    private void IPFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IPFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IPFieldActionPerformed

    private void ToggleServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ToggleServerActionPerformed
        // TODO add your handling code here:
        if(!isRunningServer) {
            startServer(rootMode.isSelected());
        } else {
            stopServer();
            
        }
    }//GEN-LAST:event_ToggleServerActionPerformed

    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordFieldActionPerformed

    private void passwordFieldSudoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldSudoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordFieldSudoActionPerformed

    private void updatePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatePasswordActionPerformed
        BufferedWriter bw = null;
        try {
            // TODO add your handling code here:
            File file = new File(ServerGUI.rootPath+PASS_NORMAL);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            try {
                bw.write(passwordField.getPassword());
                
            } catch (IOException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,"Error in Saving Password!");
        
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Error in Saving Password!");
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,"Error in Saving Password!");
            }
        }
        
        
    }//GEN-LAST:event_updatePasswordActionPerformed

    private void updatePasswordSudoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatePasswordSudoActionPerformed

         BufferedWriter bw = null;
        try {
            // TODO add your handling code here:
            File file = new File(ServerGUI.rootPath+PASS_ROOT);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            try {
                bw.write(passwordFieldSudo.getPassword());
                
            } catch (IOException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,"Error in Saving Password!");
        
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Error in Saving Password!");
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,"Error in Saving Password!");
            }
        }
        
    }//GEN-LAST:event_updatePasswordSudoActionPerformed

    private void addCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCommandActionPerformed
        // TODO add your handling code here:
        if(isRunningServer) {
           JOptionPane.showMessageDialog(null,"Please Stop the Server First!!");
            return;
        }
        String cmd = newCmd.getText();
        saveAllowedCommands(cmd);
        updateAllowedCommands();
        newCmd.setText("");
        
    }//GEN-LAST:event_addCommandActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IPField;
    private javax.swing.JTextPane OutputScreen;
    private javax.swing.JButton RefreshIP;
    private javax.swing.JButton ToggleServer;
    private javax.swing.JButton addCommand;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelAllowedCommands;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextField newCmd;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JPasswordField passwordFieldSudo;
    private javax.swing.JToggleButton rootMode;
    private javax.swing.JButton updatePassword;
    private javax.swing.JButton updatePasswordSudo;
    // End of variables declaration//GEN-END:variables
}
