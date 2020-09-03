package projet.Back_office;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import projet.Commun.GestionUDP;
import projet.Gestionnaire_ports.Port;

public class BateauHandler implements HttpHandler
{

	 public void handle(HttpExchange t) 
	  {
	        JSONObject reponse = null;

	        // Récupération des données en GET
	        URI requestedUri = t.getRequestURI();
	        String query = requestedUri.getRawQuery();

	        // Utilisation d'un flux pour lire les données du message Http
	        BufferedReader br = null;
	        try {
	            br = new BufferedReader(new InputStreamReader(t.getRequestBody(),"utf-8"));
	        } catch(UnsupportedEncodingException e) {
	            System.err.println("Erreur lors de la récupération du flux " + e);
	            System.exit(-1);
	        }
	 
	        // Récupération des données en POST
	        try {
	            query = br.readLine();
	        } catch(IOException e) {
	            System.err.println("Erreur lors de la lecture d'une ligne " + e);
	            System.exit(-1);
	        }
	 
	        
	        JSONObject request = new JSONObject();
	        request.put("state", "false");
	        if(query != null)
	        {
	        	//On s'adapte selon la query
		        String[] traitement = query.split("&");
		        String[] message = traitement[0].split("=");
		        
		        if(message[1].equals("create"))//Le portail veut créer un bateau
		        {
		        	JSONObject data = new JSONObject();
		        	for(int i = 0; i < traitement.length; i++)
		        	{
		        		String[] temp = traitement[i].split("=");
		        		data.put(temp[0], temp[1]);
		        	}
		        	Port port = ServeurBO.getPortObject(data.getString("port"));
		        	GestionUDP.sendUDP(data, port.getIp(), port.getPort_udp());
		        	request.put("state", "true");
		        }
		        else if(message[1].equals("changer"))//Le portail veut changer les informations d'un bateau
		        {
		        	JSONObject data = new JSONObject();
		        	for(int i = 0; i < traitement.length; i++)
		        	{
		        		String[] temp = traitement[i].split("=");
		        		data.put(temp[0], temp[1]);
		        	}
		        	Port port = ServeurBO.getPortObject(data.getString("port"));
		        	GestionUDP.sendUDP(data, port.getIp(), port.getPort_udp());

		        	request.put("state", "true");
		        }	
		        else if(message[1].equals("supprimer"))//Le portail veut supprimer un bateau
		        {
		        	JSONObject data = new JSONObject();
		        	for(int i = 0; i < traitement.length; i++)
		        	{
		        		String[] temp = traitement[i].split("=");
		        		data.put(temp[0], temp[1]);
		        	}
		        	Port port = ServeurBO.getPortObject(data.getString("port"));
		        	GestionUDP.sendUDP(data, port.getIp(), port.getPort_udp());
		        	
		        	request.put("state", "true");
		        }
		        else if(message[1].equals("transfert"))//Le portail veut transférer un bateau
		        {
		        	JSONObject data = new JSONObject();
		        	for(int i = 0; i < traitement.length; i++)
		        	{
		        		String[] temp = traitement[i].split("=");
		        		data.put(temp[0], temp[1]);
		        	}
		        	Port port = ServeurBO.getPortObject(data.getString("old_port"));
		        	Port port2 = ServeurBO.getPortObject(data.getString("new_port"));
		        	
		        	data.put("port_tcp", port2.getPort_tcp());
		        	data.put("ip", port2.getIp());
		        	
		        	GestionUDP.sendUDP(data, port.getIp(), port.getPort_udp());
		        	request.put("state", "true");

		        }
	        }
	        	
	        

	        // Envoi de l'en-tête Http
	        try {
	            Headers h = t.getResponseHeaders();
	            h.set("Content-Type", "text/html; charset=utf-8");
	            t.sendResponseHeaders(200, request.toString().getBytes().length);
	        } catch(IOException e) {
	            System.err.println("Erreur lors de l'envoi de l'en-tête : " + e);
	            System.exit(-1);
	        }
	 
	        // Envoi du corps (données HTML)
	        try {
	            OutputStream os = t.getResponseBody();
	            os.write(request.toString().getBytes());
	            os.close();
	        } catch(IOException e) {
	            System.err.println("Erreur lors de l'envoi du corps : " + e);
	        }
	    }

}


