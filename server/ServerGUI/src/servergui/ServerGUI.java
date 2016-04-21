/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servergui;


/**
 *
 * @author scopeinfinity
 */
public class ServerGUI {

    public static final String rootPath = "../";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Home home = new Home();
        home.setVisible(true);
        home.setTitle("Remote Access [Server Side]");
        
    }
    
    public static final String INFO = "REAC [Remote-Access]\n==========================\n\nTabs\n----------\n"
            +"\tStart\n\t  Start/Stop Server\n"
            +"\tConfiguration\n\t  Change Password of Server(Used by Client)\n"
            +"\t  Add/Pick Allowed Commands\n"
            +"\tInfo\n\t  Help\n\n"
            +"Instructions\n--------------------\n"
            +"\tBefore Executing Server Check 'Root Mode' Status\n"
            +"\t[Warning!] If 'Root Mode' is enabled it will give almost all access to client\n"
            +"\nFAQ\n------------\n"
            +"\nHow to Enable Run Mode?\n\t From terminal instead of executing './runServer'\n"
            +" Perform : su root \"./runServer\", and then continue by entering root password\n"
            +"\nCan't run server in normal mode?\n\tYou must be running program from root mode.\n"
            +"\tPerform : su <yourusername> \"./runServer\" \n";
            ;
            
    
            
    
    
    
    
}
