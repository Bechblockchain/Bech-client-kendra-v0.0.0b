package JsonRPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;


import connect.Network;

public class Server {

	  /*  public static void main(String[] args)
	    {
	        ServerSocket server = null;
	  
	        try {
	  
	            // server is listening on port 1234
	            server = new ServerSocket(Network.port3);
	            server.setReuseAddress(true);
	  
	            // running infinite loop for getting
	            // client request
	            while (true) {
	  
	                Socket client = server.accept();
	  
	                ClientHandler clientSock = new ClientHandler(client);
	                new Thread(clientSock).start();
	            }
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	        finally {
	            if (server != null) {
	                try {
	                    server.close();
	                }
	                catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	  
	    // ClientHandler class
	    private static class ClientHandler implements Runnable {
	        private final Socket clientSocket;
	  
	        // Constructor
	        public ClientHandler(Socket socket)
	        {
	            this.clientSocket = socket;
	        }
	  
	        public void run()
	        {
	           
	        	ObjectInputStream inFromClient = null;
	        	JSONRPC2Request reqIn = null;
	            try {
	               
	            	   new ObjectInputStream(clientSocket.getInputStream());
	                
	                 String jsonString = (String) inFromClient.readObject();
	                 reqIn = JSONRPC2Request.parse(jsonString);

	                 // How to extract the request data
	                 System.out.println("Parsed request with properties :");
	                 System.out.println("\tmethod     : " + reqIn.getMethod());
	                 System.out.println("\tparameters : " + reqIn.getNamedParams());
	                 System.out.println("\tid         : " + reqIn.getID() + "\n\n");

	                 Map<String,Object> params = reqIn.getNamedParams();
	                 String method = reqIn.getMethod();
	                 Object id = reqIn.getID();

	                 processResponse(method,params,id);
	             
	            }
	            catch (IOException | ClassNotFoundException | JSONRPC2ParseException e) {
	                e.printStackTrace();
	            }
	            finally {
	                try {
	                    
	                    if (inFromClient != null) {
	                    	inFromClient.close();
	                        clientSocket.close();
	                    }
	                }
	                catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }*/

		public static void processResponse(String method, Map<String, Object> params, Object id) {
			// TODO Auto-generated method stub
			if(method.equals(Specs.getPublicKey)) {
				//Wallet.getPublicKey(method,params,id);
				
			}else if(method.equals(Specs.getMintAddress)) {
			//	Wallet.getMintAddress(method,params,id);
				
			}else if(method.equals(Specs.getAccountCoinNonce)) {
			//	Wallet.getAccountCoinNonce(method,params,id);
				
			}else if(method.equals(Specs.getAccountPackNonce)) {
			//	Wallet.getAccountPackNonce(method,params,id);
				
			}else if(method.equals(Specs.getCoinBalance)) {
			//	Wallet.getCoinBalance(method,params,id);
				
			}else if(method.equals(Specs.CreateCoinTransaction)) {
				//Wallet.CreateCoinTransaction(method,params,id);
				
			}else if(method.equals(Specs.getCoinBlockHash)) {
				//blockchain_Info.getCoinBlockHash(method,params,id);
				
			}else if(method.equals(Specs.getCoinBlockNumber)) {
				//blockchain_Info.getCoinBlockNumber(method,params,id);
				
			}else if(method.equals(Specs.getCoinBlockInfo)) {
				//blockchain_Info.getCoinBlockInfo(method,params,id);
				
			}else if(method.equals(Specs.getPrevCoinBlockInfo)) {
			//	blockchain_Info.getPrevCoinBlockInfo(method,params,id);
				
			}else if(method.equals(Specs.getChainHeight)) {
			//	blockchain_Info.getChainHeight(method,params,id);
				
			}else if(method.equals(Specs.getLatestCoinBlock)) {
			//	blockchain_Info.getLatestCoinBlock(method,params,id);
				
			}else if(method.equals(Specs.getAllCoinBlockTransactions)) {
			//	blockchain_Info.getAllCoinBlockTransactions(method,params,id);
				
			}else if(method.equals(Specs.getCoinTransaction)) {
			//	blockchain_Info.getCoinTransaction(method,params,id);
			}
		}
	
	
}
