/**
 * Code is taken from Computer Networking: A Top-Down Approach Featuring 
 * the Internet, second edition, copyright 1996-2002 J.F Kurose and K.W. Ross, 
 * All Rights Reserved.
 **/

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*; 
public class SFTPClient {
    
    public SFTPClient(int port) throws Exception
    { 
        String sentence; 
        String modifiedSentence;
        boolean active = true;

        Socket clientSocket = new Socket("localhost", 115);
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while(active) {

            sentence = inFromUser.readLine();

            outToServer.writeBytes(sentence + '\n');

            modifiedSentence = inFromServer.readLine();

            System.out.println("FROM SERVER: " + modifiedSentence);

            if(modifiedSentence.equals("+")){
                active = false;
            }
        }
        clientSocket.close();
    } 
} 
