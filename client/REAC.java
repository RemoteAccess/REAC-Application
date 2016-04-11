import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.*;
import java.io.*;


class REAC extends JFrame{
    public SocketMain mainSocket, executorTransmitter;
    private Scanner in=new Scanner(System.in);

    private JTextField ip,command,passwdtext;
    private JButton connect,execute,passbutton,save;
    private JTextArea output;
    private JLabel ipstr,outstr,cmdstr,pass;
    private JScrollPane scroll;
    private JMenuBar menuBar;
    private String filename,temp="temp.txt";
    boolean edit_file=false;
        public REAC(final String name)          //throws IOException
        {
        super(name);
        JPanel top = new JPanel();
        JPanel top2= new JPanel();
        JPanel mid = new JPanel();
        
        JPanel bottom = new JPanel();
        JPanel bottom2 = new JPanel();

        setLayout(new FlowLayout());
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top2.setLayout(new BoxLayout(top2, BoxLayout.X_AXIS));
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        //mid.setLayout(new BoxLayout(mid, BoxLayout.X_AXIS));
        //mid.setLayout(new FlowLayout());
        //menubar styling
        menuBar = new JMenuBar();
        menuBar.add(Box.createRigidArea(new Dimension(20,10)));

        //JLABELS
        ipstr=new JLabel("IP Address: ");
        pass=new JLabel("Enter Password: ");
        outstr=new JLabel("Terminal");
        cmdstr=new JLabel("Command :  ");

        connect = new RoundButton("CONNECT");
        //passbutton=new RoundButton("VALIDATE");
        execute = new RoundButton("EXECUTE");
        save =new RoundButton("SAVE");
        // textfields
        ip = new RoundJTextField(50);
        passwdtext=new RoundJTextField(50);
        command = new RoundJTextField(50);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        ip.setBorder(border);
        passwdtext.setBorder(border);
        command.setBorder(border);
        

        // adding scroll to JTEXTAREA
        output=new JTextArea(21,50);
        scroll = new JScrollPane(output);
        //scroll.setBounds(0, 0, 800, 600);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //scroll.setValue( scroll.getMaximum() );
        scroll.getViewport().setViewPosition(new Point(0,output.getDocument().getLength()));

        getContentPane().add(scroll);

        output.setFont(new Font("Verdana",Font.PLAIN,14));
        output.setBackground(Color.BLACK);
        output.setForeground(Color.GREEN);
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord (false);
        output.setCaretPosition(output.getDocument().getLength());

        connect.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event)
            {
                 
                connectNow(); 
            }
        });

        execute.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event)
            {
              String s=command.getText().trim();
               if(s.length()>=4 && s.substring(0,4).equals("edit"))
               {
                editFile(s);
               }
               else{
               executorTransmitter.sendMsg(command.getText()+"\r\n");
               System.out.println("Sending : "+command.getText()+"\r\n");
               executorTransmitter.sendMsg("pwd"+"\r\n");
                }
            }
        });  

        save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event)
            {
                try{
              
        //System.out.println(content);
        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec("base64 -w 0"  );
        
        File file = new File("temp.txt");
        FileInputStream fin = new FileInputStream(file);



        BufferedReader stdInput = new BufferedReader(new
                 InputStreamReader(p.getInputStream()));
    
        OutputStream out = p.getOutputStream();

        int c;
         while ((c = fin.read()) != -1) {
            out.write(c);
         }
         fin.close();
         out.close();

         String content=stdInput.readLine();
        Thread.sleep(100);

            executorTransmitter.sendMsg("echo \""+content+"\" | base64 -d > "+filename+"\r\n");
            edit_file=false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
         
                }
        }
            
        });  
    
        

        //menuBar.add(outstr);
        top.add(ipstr);
        top.add(Box.createHorizontalStrut(10));
        top.add(ip);
        top.add(Box.createHorizontalStrut(10));
        top.add(connect);
        top.add(Box.createVerticalStrut(40));
        // adding password field
        top2.add(pass);
        top2.add(Box.createHorizontalStrut(10));
        top2.add(passwdtext);
        top2.add(Box.createHorizontalStrut(10));
        //top2.add(passbutton);
        top2.add(Box.createVerticalStrut(40));

        //mid.add(menuBar);
        mid.add(scroll);
        //mid.add(Box.createVerticalStrut(40));
        bottom.add(cmdstr);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(command);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(execute);
        bottom.add(Box.createVerticalStrut(40)); 
        bottom2.add(save);      
        add(top);
        add(top2);
        add(mid);
        add(bottom);
        add(bottom2);
        


        }

    void appendMsg(String s) {
        output.setText(output.getText()+s);
    }

    void connectNow() {
        System.out.println("Connect Now!");
        System.out.println(     mainSocket = new SocketMain(8080,ip.getText()) );
        System.out.println(     mainSocket.connect() );
        mainSocket.sendMsg(passwdtext.getText()+"\r\n");         
        mainSocket.startShell();
               
        SocketReceiver receiver = new SocketReceiver(this, 8082);
        receiver.start();

        try
        {
            Thread.sleep(1000);
             executorTransmitter = new SocketMain(8081, ip.getText());
            executorTransmitter.connect();
            Thread.sleep(50);
            //executorTransmitter.sendMsg("pwd"+"\r\n");
        }catch(Exception e){
            System.err.println("Cant Wait!!!!!!");
        }
       
    }




    void editFile(String s)
    {
        edit_file=true;
        String[] filen=s.split(" ");
        filename=filen[1];
        executorTransmitter.sendMsg("cat "+filename+"\r\n");
       

    }

    public static void main(String[] args)      //throws IOException
    {
        String n="REAC";
        REAC reac=new REAC(n);
        //reac.open.doClick();f++;
        reac.setSize(900,650);
        reac.setVisible(true);
    }

    }