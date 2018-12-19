/*	* Name:- Gandhali Girish Shastri
 * ID: 1001548562
 * Lab Assignment - 2
 * -----------------------------------------------------
 * 
 * References:	https://www.jmarshall.com/easy/http/#http1.1c1
 * 				https://www.geeksforgeeks.org/split-string-java-examples/
 * 				https://mark.koli.ch/remember-kids-an-http-content-length-is-the-number-of-bytes-not-the-number-of-characters
 * 				https://www.youtube.com/watch?v=8lXI4YIIR9k&t=578s
 * 
 * */

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.Socket;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Client implements ActionListener, Runnable {
	//Declarations
	
	Thread clientThread;
	Socket skt;
	BufferedReader sin;
	PrintStream sout;
	BufferedReader stdin;
	
	//GUI declarations
	private JFrame frame;
	public  JTextField txtEnterUsernameHere ;
	private  JTextArea textArea;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	
	//constructor to initialize
	public Client() {
		//Builds GUI
		initialize();
		
		try{
			//connects to server at port 5000
			skt= new Socket("127.0.0.1",5000);	
			
			sin= new BufferedReader(new InputStreamReader(skt.getInputStream())); //input
			sout= new PrintStream(skt.getOutputStream()); //output
			
			stdin= new BufferedReader(new InputStreamReader(System.in)); //input
			
			clientThread=new Thread(this);		//start a new thread
			clientThread.start();
			
		}catch(Exception e) {
			
		}
	}

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//window obj to create GUI
					Client window = new Client();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	//Builds GUI (system generated)
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 578, 520);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		txtEnterUsernameHere = new JTextField();
		txtEnterUsernameHere.setText("Enter username here");
		txtEnterUsernameHere.setBounds(10, 22, 252, 20);
		frame.getContentPane().add(txtEnterUsernameHere);
		txtEnterUsernameHere.setColumns(10);
		
		textArea = new JTextArea();
		textArea.setBounds(23, 75, 239, 175);
		
		 JScrollPane scroll = new JScrollPane (textArea);
		 scroll.setBounds(10, 74, 525, 312);
		 scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		 frame.getContentPane().add(scroll);
		
		 
		//send button
		btnNewButton = new JButton("Send");
		btnNewButton.setBounds(316, 21, 89, 23);
		frame.getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(this);
		//quit button
		btnNewButton_1 = new JButton("Quit");
		btnNewButton_1.setBounds(433, 21, 89, 23);
		frame.getContentPane().add(btnNewButton_1);
		btnNewButton_1.addActionListener(this);
		
	}

	//Func for buttons: SEND and QUIT
	public void actionPerformed(ActionEvent ae) {
			//send button
		if(ae.getSource()== btnNewButton) {
			sout.println(txtEnterUsernameHere .getText());
			txtEnterUsernameHere .setText("");
		}
		//quit button
		if(ae.getSource()== btnNewButton_1) {
			sout.println("quit");
			textArea.append("Disconnecting");
			System.out.println("Client disconnected");
			System.exit(0);
		}
	}
	
public void run() {
		
		String s=null;
		
		textArea.append("You need to register yourself.\n");
		textArea.append("Enter username.\n");
		
		try {
			
			s=sin.readLine();	//accept username
			
			//If username exists, user will re-enter username
			while(s.equals("re-enter")){
				
				textArea.append("Username exists.\nTry registering with a new username.\n");
				s=sin.readLine();
			}
		} catch (IOException e) {}
		
		//splits the http message to print it in normal text format
		String[] response= s.split("#",10);
				
		for (String a : response) {
		    textArea.append(a + "\n");
		}
		
		//generate random integers
		while(true) {
						
			int min=3, max=10;
			Random rand = new Random();		//generate random int

			int  n = rand.nextInt((max-min)+1) + min;	//int are generated in a given range

			int time=(n * 1000);	//conv to ms
			sout.println(time); //send to server

			try {
				//accept response from server "will wait"
				s=sin.readLine();
				response = s.split("#",5);
				for(String a: response) {
					//if(i==5)
						textArea.append(a + "\n");
				}
				
				//accept response from server "waited for" 
				s=sin.readLine();
				response = s.split("#");
				for(String a: response) {
					//if(i==5)
						textArea.append(a + "\n");
				}
				//accept response from server "total time spent waiting"
				s=sin.readLine();
				response = s.split("#");
				for(String a: response) {
					//if(i==5)
						textArea.append(a + "\n");
				}
				
			} catch (IOException e) {
				
			}
			

		}	
	}
}
