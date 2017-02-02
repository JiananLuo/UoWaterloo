import java.io.*;
import java.net.*;

class server
{
	public static void main(String args[]) throws Exception
	{
		//error inputs
		if(args.length != 1)
		{
			System.err.println("Wrong number of command line argument.");
			return;
		}

		//when inputs are fine
		String reqCode = args[0];
		int requestCode = Integer.valueOf(reqCode);
		System.out.println("SERVER_PORT=52500");

		//Create welcoming socket at port 52500
		ServerSocket welcomeSocket = new ServerSocket(52500);
		while(true)
		{
			//Wait, on welcoming socket for contact by client
			Socket connectionSocket = welcomeSocket.accept();

			//Create input stream, attached to socket
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			//Read in line from socket
			String TCPRequestMsg = inFromClient.readLine();
			int TCPRequestMsgInt = Integer.valueOf(TCPRequestMsg);

			//Create output stream, attached to socket
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			//verifying if the received reqCode is equal to the argument reqCode, if so do UDP
			if(TCPRequestMsgInt == requestCode)
			{
				//Get available socket
				ServerSocket availableSocket = new ServerSocket(0);
				int rPort = availableSocket.getLocalPort();
				//Write out line to socket
				outToClient.writeBytes(String.valueOf(rPort) + '\n');
				System.out.println("Generated rPort: " + rPort);

				//Create datagram socket at port rPort
				DatagramSocket UDPServerSocket = new DatagramSocket(rPort);
				byte[] receiveData = new byte[1024];
				byte[] sendData = new byte[1024];

				//Create space for received datagram
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				//Receive datagram
				UDPServerSocket.receive(receivePacket);

				//Generate UPD Response
				String UDPRequestString = new String(receivePacket.getData());
				String UDPResponseString = new StringBuilder(UDPRequestString).reverse().toString();
				sendData = UDPResponseString.getBytes();

				//Get IP addr port #, of sender
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				//Create datagram to send to client
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

				//Write out datagram to socket
				UDPServerSocket.send(sendPacket);
			}
			else
			{
				//if different close TCP connection
				connectionSocket.close();
			}
		}
	}
}