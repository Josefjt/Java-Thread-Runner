import java.util.*;              // Contains collection framework, collection classes, classes related to date and time, event model, internationalization, and miscellaneous utility classes.
import java.io.*;                // Used for use of  input/output stream.
import javax.swing.*;            // Import for java swing used to create window-based applications, provides classes for java swing API such as JButton, JTextField, JTextArea
import java.awt.*;
import java.awt.event.*;
import javax.swing.JComponent;
/**
   Races main constructor, extends JFrame. Contains constructors and methods.
*/ 
public class Races extends JFrame{
   
   final String raceImg = "races.gif";          // Creating a final String for the image name.
   Icon raceIcon = new ImageIcon(raceImg);      // Creating new ImageIcon and passing file name.
   Object oos = new Object();                   // Creating an Object for synchronization 
   boolean runLoop=true;                        // Declaring Boolean variable for run method while loop
   
   /**
      Races constructor which passes an interger for number of threads to be executed.
   */
   public Races(int racersNum){
      try{                                            // Tests block of code for errors
         for(int i=0; i<racersNum;i++){               // For loop to create Threads
            innerRacer threadRunner;                  // Declaring innerRacer attribute
            threadRunner = new innerRacer(""+(i+1));  // Making new thread instance and assigning numerical value
            add(threadRunner);                        // Adding to JFrame.
            Thread thread = new Thread(threadRunner); // Creating anew Thread
            thread.start();                           // Starting Thread.
         }
      }
      catch(Exception e){                            // catches and handles exceptions.
         e.printStackTrace();
      }
      
      setLayout(new BorderLayout());   // Creating new boarder layout
      
      JMenuBar jmb = new JMenuBar();   // Creating a JMenuBar to hold actions
      
      // Create Options JMenu
      JMenu jmOptions = new JMenu("Options");
      jmOptions.setMnemonic('o');
      jmb.add(jmOptions);                                // Adding to JMenu.
      
      JMenuItem jmiRestart = new JMenuItem("Restart");   // Creating JMenuItem to Restart program.
      jmiRestart.setMnemonic('r');
      jmOptions.add(jmiRestart);
   
      JMenuItem jmiExit = new JMenuItem("Exit");
      jmiExit.setMnemonic('e');
      jmOptions.addSeparator();
      jmOptions.add(jmiExit);
      setJMenuBar(jmb);
      
   
      // Action Listener for Exit JMenuItem
      jmiExit.addActionListener(
         new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){       
               System.exit(0);   // Exits and ends current running operation. 
            }
         });
         
      jmiRestart.addActionListener(
         new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
               dispose();  // Gets rif of current instance
               new Races(racersNum);   // Creats a new instance and restarts race.
            }
         });
   
      // Adding to JFrame and setting parameters.
      setTitle("Off to the Races - by Josef Arieatas");        // Title for JPanel.
      setPreferredSize(new Dimension(raceIcon.getIconWidth()*20, raceIcon.getIconHeight()*2*racersNum));         // Setting size of JFrame.
      setLocationRelativeTo(null);                     // Sets the location of the window to the center of the screen
      pack();                                            // Method which sizes the frame
      setDefaultCloseOperation(EXIT_ON_CLOSE);     // exits program on close
      setLayout(new GridLayout(racersNum, 1));     // sets gridlayout based on number of threads 
      setVisible(true);                            // makes GUI visible.
      setResizable(false);                         // Disables window resizing.
   
   
   }
   /**
      innerRacer constructor which extends JPanel and implements Runnable
   */
   class innerRacer extends JPanel implements Runnable{
      boolean winner = false;          // Creating boolean for determining winner.
      private String name="";          // Creating blank name
      int raceCounter=0;               // initalzing counter 
      
      public innerRacer(String name){  // inner class 
         this.name = name;
      }
      
      // override function
      @Override
      public void run(){   // run method.
      
         try{ // tests block of code for errors.
            Thread.sleep(1000);  // Pauses images for one second before starting.
            
         // executes block of code while condition is true.
            while(runLoop){
                
               int randomNum = (int)Math.round(Math.random()*15);  // creating a random number generator incliuding trype casting
               Thread.sleep(randomNum);                           // adding delay to Thread.
               raceCounter++;                                     // incarmenting counter 
               repaint();                                         // updates position of Threads.
               
               if(raceCounter== raceIcon.getIconWidth()*18){      // setting finish line based on width of image
                  synchronized(oos){                              // synchronizing though an object 
                     runLoop = false;                             // stops all threads at once 
                     winner = true;                               // changes winner boolean to true.
                  } 
               }
            }                                  
         }
         catch(Exception ex){                                  // catch exceptions thrown
            System.out.println(ex);
         }  
      }
      
      /**
         // Paint method which calls super paintComponent
      */
      public void paint(Graphics g)                               
      {
         super.paintComponent(g);
         raceIcon.paintIcon(this, g, raceCounter, 0);                                  // adds image to Jframe 
         g.drawLine(raceIcon.getIconWidth()*18, 0, raceIcon.getIconWidth()*18, 10000); // Draws a finish line based on image width.
         if(winner==true){                                                             // enters when winner is set to true.
            g.drawString("Winner is: #" + name, 10, 10);                               // prints winner to JFrame.
         }
      }
   } // End of innerRacer extends JPanel which implements Runnable
   
   /**
      Main Method, allows print to screen.
   */
   public static void main(String[] args){
      int racersDefault = 5;  // sets default number of threads to be exectured.
      if(args.length>0){   // checks for Run Arguements 
         racersDefault = Integer.parseInt(args[0]);   // converts Run Arguemnts String to interger.
      }
      new Races(racersDefault);  // runs constructor and passes parameter.
            
   }  // End of Main Method
}  // End of Races extends JFrame