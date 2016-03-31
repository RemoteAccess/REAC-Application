import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.Border;

class REAC extends JFrame{
    private SocketMain mainSocket, executorTransmitter;


    private JTextField ip,command;
    private JButton connect,execute;
    private JTextArea output;
    private JLabel ipstr,outstr,cmdstr;
    private JScrollPane scroll;
    private JMenuBar menuBar;
        public REAC(final String name)          //throws IOException
        {
        super(name);
        JPanel top = new JPanel();
        JPanel mid = new JPanel();
        
        JPanel bottom = new JPanel();

        setLayout(new FlowLayout());
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        //mid.setLayout(new BoxLayout(mid, BoxLayout.X_AXIS));
        //mid.setLayout(new FlowLayout());
        //menubar styling
        menuBar = new JMenuBar();
        menuBar.add(Box.createRigidArea(new Dimension(20,10)));

        //JLABELS
        ipstr=new JLabel("IP Address: ");
        outstr=new JLabel("Terminal");
        
        cmdstr=new JLabel("Command :  ");
        connect = new RoundButton("CONNECT");
        execute = new RoundButton("EXECUTE");
        // textfields
        ip = new RoundJTextField(50);
        command = new RoundJTextField(50);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        ip.setBorder(border);
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
               executorTransmitter.sendMsg(command.getText()+"\r\n");
               System.out.println("Sending : "+command.getText()+"\r\n");
            }
        });       

        //menuBar.add(outstr);
        top.add(ipstr);
        top.add(Box.createHorizontalStrut(10));
        top.add(ip);
        top.add(Box.createHorizontalStrut(10));
        top.add(connect);
        top.add(Box.createVerticalStrut(40));
        //mid.add(menuBar);
        mid.add(scroll);
        //mid.add(Box.createVerticalStrut(40));
        bottom.add(cmdstr);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(command);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(execute);
        bottom.add(Box.createVerticalStrut(40));       
        add(top);
        add(mid);
        add(bottom);
        


        }

    void appendMsg(String s) {
        output.setText(output.getText()+s);
    }

    void connectNow() {
        System.out.println("Connect Now!");
        System.out.println(     mainSocket = new SocketMain(8080,ip.getText()) );
        System.out.println(     mainSocket.connect() );
        mainSocket.sendMsg("hello\r\n");
        mainSocket.startShell();
               
        SocketReceiver receiver = new SocketReceiver(this, 8082);
        receiver.start();

        try
        {
            Thread.sleep(1000);
             executorTransmitter = new SocketMain(8081, ip.getText());
            executorTransmitter.connect();
        }catch(Exception e){
            System.err.println("Cant Wait!!!!!!");
        }
       
    }



    public static void main(String[] args)      //throws IOException
    {
        String n="REAC";
        REAC reac=new REAC(n);
        //reac.open.doClick();f++;
        reac.setSize(900,550);
        reac.setVisible(true);
    }

    }