package projet.Gestionnaire_ports;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

import projet.Commun.Bateau;

public class ThreadConnexion extends Thread
{
	private Socket socketClient;
	private BufferedReader input;
	private PrintWriter output;
	private Port port;
	
	private static String ipClient;
	private static int portClient;

	public ThreadConnexion(Socket sc, Port p)
	{
	
		this.socketClient = sc;
		this.port = p;
		
		// Association d'un flux d'entrée et de sortie
        try {
            input = new BufferedReader(new InputStreamReader(this.socketClient.getInputStream()));
            output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.socketClient.getOutputStream())), true);
        } catch(IOException e) {
            System.err.println("Association des flux impossible : " + e);
            System.exit(-1);
        }
	}
	
	@Override
	public void run()
	{
        String message = "";
        JSONObject json;
        
  
        boolean b = true;
        
        while(b == true)
        {
	        // Lecture
	        try {
	            message = input.readLine();
	        } catch(IOException e) {
	            System.err.println("Erreur lors de la lecture : " + e);
	            System.exit(-1);
	        }
	        portClient = socketClient.getPort();
	        ipClient = socketClient.getInetAddress().getHostAddress();
	 
	        json = new JSONObject(message);
	        int code = json.getInt("code");
	        
	        JSONObject retour = new JSONObject();

	        if(json.getString("message").equals("transfert"))
	        {
	        	Bateau bato = Bateau.fromJSON(json.getJSONObject("bateau"));
	        	this.port.PrendrePlace(json.getInt("new_place"), String.valueOf(bato.getId()));
	        	this.port.addBateau(bato);
		        retour.put("code", "10"); //10 : Bateau bien transféré
		        retour.put("message", "transfert");
	        }
	        
        	if(code == 200)
        	{
        		retour.put("code", "200");
        		b = false;
        	}
        
	        output.println(retour.toString());
        }
        
 
 
        // Fermeture des flux et des sockets
        try {
            input.close();
            output.close();
            socketClient.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de la fermeture des flux et des sockets : " + e);
            System.exit(-1);
        }
	}

}
