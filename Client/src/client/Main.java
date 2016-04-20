/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scopeinfinity
 */
public class Main extends javax.swing.JFrame {
  public SocketMain mainSocket, executorTransmitter;
  private String filename,tempFilename=".temp";
  public boolean editFile = false;
   
    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();
        disableExecutionUI(true);
        ta_EditFile.setEnabled(false);
        ta_EditFile.setText("No File is Opened!");
        
        setTitle("REAC-Client");
    }
    
    
    void connectNow() {
        mainSocket = new SocketMain(8080,tf_IP.getText());
        if(!mainSocket.connect()) {
            appendMsg("Unable to Connect!!!\n");
            return;
            
        }
        
        String check = mainSocket.getNextTag();
        if(check==null || !check.equals("[[REAC:RemoteAccess]]")) {
            appendMsg("REAC Server not Running on Given IP\n");
            try {
                mainSocket.connection.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        
        
        mainSocket.sendMsg(tf_pwd.getText()+"\r\n");         
        String checkPassword = mainSocket.getNextTag();
       if(checkPassword==null || !checkPassword.equals("Status : Allowed")) {
            appendMsg("Invalid Password\n");
            try {
                mainSocket.connection.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        
        
        mainSocket.startShell();
        String checkShell = mainSocket.getNextTag();
       if(checkShell==null || !checkShell.equals("Status : 1")) {
            appendMsg("Unable to open Shell\n");
            try {
                mainSocket.connection.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        
        SocketReceiver receiver = new SocketReceiver(this, 8082);
        receiver.start();

        try
        {
            Thread.sleep(1000);
             executorTransmitter = new SocketMain(8081, tf_IP.getText());
            executorTransmitter.connect();
            Thread.sleep(50);
            
            //Assuming Connected!!
            disableExecutionUI(false);
            appendMsg("Connected!\n\nWelcome to REAC-Shell\n"
                    +               "======================\n\n");
            
            //executorTransmitter.sendMsg("pwd"+"\r\n");
        }catch(Exception e){
            System.err.println("Cant Wait!!!!!!");
        }
       
    }

    void editFileContent(String content) {
        ta_EditFile.setText(content);
        ta_EditFile.setEnabled(true);
        ta_EditFile.setVisible(true);
        
        SCREEN.setVisible(false);
    }



    void editFile(String s)
    {
        editFile=true;
        String[] filen=s.split(" ");
        filename=filen[1];
        executorTransmitter.sendMsg("cat "+filename+"\r\n");
        
        tf_commandline.setEnabled(false);
        
        ta_EditFile.setText("No File is Opened!");
        Send.setText("Save");
    }
    
    void saveFile() {
        
        try{
              
            //System.out.println(content);
            Runtime runtime = Runtime.getRuntime();
            Process p = runtime.exec("base64 -w 0"  );

            //File file = new File(tempFilename);
            //FileInputStream fin = new FileInputStream(file);



            BufferedReader stdInput = new BufferedReader(new
                     InputStreamReader(p.getInputStream()));

            OutputStream out = p.getOutputStream();
            out.write(ta_EditFile.getText().getBytes());

            //int c;
           //  while ((c = fin.read()) != -1) {
           //     out.write(c);
            // }
            // fin.close();
             out.close();

             String content=stdInput.readLine();
           // Thread.sleep(100);

                executorTransmitter.sendMsg("echo \""+content+"\" | base64 -d > "+filename+"\r\n");
                editFile=false;
                 tf_commandline.setEnabled(true);
                Send.setText("Send");
                
                ta_EditFile.setEnabled(false);
                ta_EditFile.setVisible(false);

                SCREEN.setVisible(true);
        }
        catch(Exception e)
        {
            e.printStackTrace();

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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tf_IP = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tf_pwd = new javax.swing.JPasswordField();
        b_Connect = new javax.swing.JButton();
        tf_commandline = new javax.swing.JTextField();
        Send = new javax.swing.JButton();
        jTabbedPane = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        SCREEN = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        ta_EditFile = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(640, 480));

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(700, 480));

        jLabel1.setText("Other's IP :");

        tf_IP.setText("127.0.0.1");

        jLabel2.setText("Password :");

        tf_pwd.setText("123456");

        b_Connect.setText("Connect");
        b_Connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_ConnectActionPerformed(evt);
            }
        });

        tf_commandline.setText("echo \"Test\";");
        tf_commandline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_commandlineActionPerformed(evt);
            }
        });

        Send.setText("Send");
        Send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendActionPerformed(evt);
            }
        });

        SCREEN.setEditable(false);
        SCREEN.setBackground(new java.awt.Color(27, 27, 27));
        SCREEN.setForeground(new java.awt.Color(39, 176, 1));
        SCREEN.setOpaque(false);
        jScrollPane1.setViewportView(SCREEN);

        jTabbedPane.addTab("Terminal", jScrollPane1);

        ta_EditFile.setColumns(20);
        ta_EditFile.setRows(5);
        jScrollPane2.setViewportView(ta_EditFile);

        jTabbedPane.addTab("TextEditor", jScrollPane2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tf_commandline, javax.swing.GroupLayout.PREFERRED_SIZE, 523, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Send, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tf_IP, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(jLabel2)
                                .addGap(3, 3, 3)
                                .addComponent(tf_pwd, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(b_Connect)))
                        .addGap(0, 11, Short.MAX_VALUE))
                    .addComponent(jTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tf_IP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tf_pwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(b_Connect))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_commandline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Send))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Connection", jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 448, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Info", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tf_commandlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_commandlineActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_commandlineActionPerformed

    private void SendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendActionPerformed
        if(editFile) {
            saveFile();
            
            return;
        }       
        
        String s=tf_commandline.getText().trim();
               if(s.length()>=4 && s.substring(0,4).equals("edit"))
               {
                editFile(s);
               }
               else{
               appendMsg("user@REAC:$"+tf_commandline.getText().trim()+"\r\n");
               executorTransmitter.sendMsg(tf_commandline.getText().trim()+"\r\n");
               System.out.println("Sending : "+tf_commandline.getText().trim()+"\r\n");
               tf_commandline.setText("");
               }        // TODO add your handling code here:
    }//GEN-LAST:event_SendActionPerformed

    private void b_ConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_ConnectActionPerformed
                connectNow(); 
                // TODO add your handling code here:
    }//GEN-LAST:event_b_ConnectActionPerformed

    
    private void disableExecutionUI(boolean type) {
        type=!type;
        b_Connect.setEnabled(true^type);
        tf_IP.setEnabled(true^type);
        tf_pwd.setEnabled(true^type);
        tf_commandline.setEnabled(false^type);
        Send.setEnabled(false^type);
    }
  
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
    
    public void appendMsg(String c) {
        SCREEN.setText(SCREEN.getText()+c);
        try{
        SCREEN.setCaretPosition(SCREEN.getDocument().getLength());
        }catch(Exception e) {
            
        }
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane SCREEN;
    private javax.swing.JButton Send;
    private javax.swing.JButton b_Connect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea ta_EditFile;
    private javax.swing.JTextField tf_IP;
    private javax.swing.JTextField tf_commandline;
    private javax.swing.JPasswordField tf_pwd;
    // End of variables declaration//GEN-END:variables
}
