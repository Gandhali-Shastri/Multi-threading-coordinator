/*	* Name:- Gandhali Girish Shastri
 * ID: 1001548562
 * Lab Assignment - 2
 
 * References:	https://www.jmarshall.com/easy/http/#http1.1c1
 * 				https://www.geeksforgeeks.org/split-string-java-examples/
 * 				https://mark.koli.ch/remember-kids-an-http-content-length-is-the-number-of-bytes-not-the-number-of-characters
 * 				https://www.youtube.com/watch?v=8lXI4YIIR9k&t=578s
 * -----------------------------------------------------
 * */

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue; 

public class Coordinator {
	
	//Declarations and Initializations
	
	//port number for connection
	int port;
	//queue for queuing threads
	static  Queue<Integer> q = new LinkedList<>();
	static ServerSocket server= null;
	static Socket client=null;
	static ExecutorService pool=null;
	
	//stores the user names to check whether username exists or not
	static ArrayList<String> users= new ArrayList<String>(3);
	//Only 3 clients can connect at a time
	private static int MAXCLIENTS = 3;
	//Creates different threads for each client
	private static clientThread[] threads = new clientThread[MAXCLIENTS];		
	
	//GUI variables
	public static JTextArea textArea;
	private JFrame frame;
	
	//main method
	public static void main(String[] args) {
		
		//run method of thread
		EventQueue.invokeLater(new Runnable() {
		public void run() {
			try {
				//window object to build the GUI
				Coordinator window = new Coordinator();
				//makes frame visible
				window.frame.setVisible(true);
			} catch (Exception e) {
				//System.out.println(e);
			}
		}
		});
		
	
		try {
			//Socket for server with port number
			//port where the clients would connect
			server=new ServerSocket(5000);	
		} catch (Exception e) {
			//System.out.println(e);
		}
		
		int i=0; //counts number of client threads
		
		while(true) {
			try {
				//creating new threads as clients join
				client=server.accept();	
				
				//Creates all the clients and stores them in array of 'threads'
				for (i = 0; i <= MAXCLIENTS; i++) {
                    if (threads[i] == null) {
                    	//starts a new thread
                        (threads[i] = new clientThread(client, threads)).start();	
                      
                        break;
                    }
                }
				
				//if a 4th client tries to connect, the foll code will be executed
                if (i == MAXCLIENTS) {
                    PrintStream os = new PrintStream(client.getOutputStream());
                    //prints on client side
                    os.println("Server too busy. Try later.");
                    //prints on server side
                    textArea.append("Maximum number of clients have already joined.");
                    os.close();
                    client.close();		//close client thread
                }
			} catch(Exception e) {
				//System.out.println(e);
			}
		}
				
	}

	//constructor
	public Coordinator() {
		initialize();	//used to build the GUI
	}
	
	//Builds the GUI (system generated code)
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		textArea = new JTextArea();
		frame.getContentPane().add(textArea, BorderLayout.CENTER);
		
		
		 JScrollPane scroll = new JScrollPane (textArea);
		 scroll.setBounds(10, 74, 259, 230);
		 scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		 frame.getContentPane().add(scroll);
	}

	
//Client thread class
public static class clientThread extends Thread {

	    private PrintStream os = null;
	    private Socket clientSocket = null;
	    private clientThread[] threads;
	    private int maxClientsCount;
	    private String username = "";
	    int time;
	    long start;
	    long end;
	  
	    //constructor.  called when a thread is started
	    public clientThread(Socket clientSocket, clientThread[] threads) {
	        this.clientSocket = clientSocket; //socket at which client is connected
	        this.threads = threads; //current thread
	        maxClientsCount = threads.length; //size of the array should be less than 4
	    }
	    
	    //execution of a particular thread starts here
		public void run() {
	    	
	    	int maxClientsCount = this.maxClientsCount; //current client count
	        clientThread[] threads = this.threads; //all active clients

	        try {
	        	
	        	BufferedReader is = new BufferedReader(new InputStreamReader(client.getInputStream()));
	            os = new PrintStream(clientSocket.getOutputStream());

	            Date today = new Date();	//Func to get the date
	    		int length=0;	//length of response
	    		Date current_time = new Date();		//Func to get the time
	    		//changes the time format
                String time_to_string = new SimpleDateFormat("k:m:s").format(current_time);            
                
                byte[] responseBytes = null;	//this is to calculate the content-length
	    		String response=null;
	    		
	    		//the HTTP response
	    		String httpResponse1 = "GET HTTP / 1.1 200 OK#" + today + time_to_string
	                    + "#Connection Host : " + clientSocket.getLocalAddress() + ":" + clientSocket.getLocalPort()
	                    + "#Content-Type: text/html" 		
	                    + "#User-Agent : User-Agent: Mozilla/4.0";
	    		
	    		String httpResponse = "GET HTTP / 1.1 200 OK\n" + today + time_to_string
	                    + "\nConnection Host : " + clientSocket.getLocalAddress() + ":" + clientSocket.getLocalPort()
	                    + "\nContent-Type: text/html" 		
	                    + "\nUser-Agent : User-Agent: Mozilla/4.0";
	    		
	    	
	    			this.username = is.readLine();	//accept username from client
	    		
	            // this block of code is for accepting username only if it is unique
				while(true) {
						//Checks if username exists
						if(users.contains(this.username)) {
							os.println("re-enter");		//client side handles accepting many usernames until its unique
						}
						else {
								response= "Registeration successfull.\n" + this.username + " has been connected.\n";
								
							
							try {
				    			responseBytes=response.getBytes();		//Content-length
				    		}catch(Exception e) {}
			                
							length=responseBytes.length;
							
							//prints on server side
							textArea.append(httpResponse + "Content-Length : " + length + "\n" + response );
							textArea.append("\n -------------Response without HTTP.---------------\n\n");
							textArea.append(response+"\n");
							
							//sends to client
							os.println(response);
	          				//os.println(httpResponse1 + "#Content-Length : " + length +  response);
						
							users.add(this.username);	//stores usernames
							textArea.append("\n Online user: \n");
							for(int i=0;i<users.size();i++) {
								textArea.append(users.get(i) + "\n");
							}
							
							break;
						}
						
					
						this.username=is.readLine();		//accepts username after it is flagged as unique
				}
				
				
				//this block of code is for putting threads to sleep
				while(true) {
					//stores the random integer of current thread
					
					this.time = Integer.parseInt(is.readLine());	
					//starts calculating the time in ms of current thread
					start = System.currentTimeMillis();
					//adds an the time sent by the thread to the queue
					q.add(time);
					
					
					//notifies how long the server will wait
					response= "Server will wait for " + (time/1000) + " seconds\n";
					length=responseBytes.length;
					
					textArea.append(httpResponse + "Content-Length : " + length + "\r\n" + response);
					textArea.append("\n \n------------------Response without HTTP.----------\n\n");
					textArea.append(response+"\n");
      			//	os.println(httpResponse + "Content-Length : " + length + "\r\n" + response);
      				os.println(response + "\n");
					
					//synchronized block of threads on object queue
					synchronized (q) {
						//makes the threads wait other than current thread until the current threads sleep time is not over
						
						while(time != q.element()) {
							q.wait(); //other threads starts waiting
						}
						
						//puts current thread on sleep by popping the element in queue
						this.sleep(q.remove());
						
						//notifies other threads that they can wakeup
						q.notify();
						//end time of current thread
						end = System.currentTimeMillis();
						
						//notifies how long it waited
						
						response= "Server waited " + (time/1000) + " seconds for client - " + username +"\n";
						length=responseBytes.length;
						
						textArea.append(httpResponse + "Content-Length : " + length + "\r\n" + response);
	      				//os.println(httpResponse + "Content-Length : " + length + "\r\n" + response);
						textArea.append("\n \n------------------Response without HTTP.----------\n\n");
						textArea.append(response+"\n");
						os.println(response);
					
						//prints the total time a thread waited
						System.out.println("Total time spent waiting for the server by " + username + " " + (end-start)/1000);
						textArea.append("Total time spent waiting for the server by " + username +" is " + ((end-start)/1000) +"\n");
						os.println("Total time spent waiting for the server by " + username +" is " + ((end-start)/1000)+"\n");
					}
				
				}

			} 
		    catch(IOException ex){
		    	System.out.println("Error client disconn: "+ex);
		    	textArea.append("\n ****************client: " +username + "disconnected. ************** \n");
		    } catch (InterruptedException e) {
				// TODO Auto-generated catch block
		    	System.out.println("Error client disconn: "+e);
		    	textArea.append("\n ****************client: " +username + "disconnected. ************** \n");
			}
	        catch(NoSuchElementException ex) {
	        	System.out.println("Error client disconn: "+ex);
		    	textArea.append("\n ****************client: " +username + "disconnected. ************** \n");
	        }
		 
		}
	}
}