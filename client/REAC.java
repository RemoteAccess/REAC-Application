import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

class REAC extends JFrame{
    private SocketMain mainSocket, executorTransmitter;


    private JTextField ip,command;
    private JButton connect,execute;
    private JTextArea output;
    
        public REAC(final String name)          //throws IOException
        {
        super(name);
        JPanel top = new JPanel();
        JPanel mid = new JPanel();
        
        JPanel bottom = new JPanel();

        setLayout(new FlowLayout());
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        mid.setLayout(new BoxLayout(mid, BoxLayout.X_AXIS));
        
        connect = new JButton("CONNECT");
        execute = new JButton("EXECUTE");
        ip = new JTextField(50);
        command = new JTextField(50);
       
        output=new JTextArea(20,41);
        output.setFont(new Font("Verdana",Font.PLAIN,14));
        output.setBackground(Color.BLACK);
        output.setForeground(Color.WHITE);
        output.setLineWrap(true);

        connect.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event)
            {
                
                connectNow();          
            }
        });

        execute.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event)
            {
               executorTransmitter.sendMsg(command.getText());
            }
        });       

        top.add(ip);
        top.add(connect);
        
        bottom.add(output);
        mid.add(command);
        mid.add(execute);       
        add(top);
        add(bottom);
        add(mid);


        }

    void appendMsg(String s) {
        output.setText(output.getText()+s);
    }

    void connectNow() {
        System.out.println("Connect Now!");
        System.out.println(     mainSocket = new SocketMain(8080,ip.getText()) );
        System.out.println(     mainSocket.connect() );
        mainSocket.startShell();
        SocketReceiver receiver = new SocketReceiver(this, 8082);
        receiver.start();

        try
        {
            Thread.sleep(5);
        }catch(Exception e){}
        executorTransmitter = new SocketMain(8081, ip.getText());
        executorTransmitter.connect();
    }



    public static void main(String[] args)      //throws IOException
    {
        String n="REAC";
        REAC reac=new REAC(n);
        //reac.open.doClick();f++;
        reac.setSize(800,640);
        reac.setVisible(true);
    }

}