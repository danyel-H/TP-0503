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

public class RegisterHandler implements HttpHandler
{

	  public void handle(HttpExchange t) 
	  {
	        JSONObject reponse = null;
	        
	        // R�cup�ration des donn�es en GET
	        URI requestedUri = t.getRequestURI();
	        String query = requestedUri.getRawQuery();

	        // Utilisation d'un flux pour lire les donn�es du message Http
	        BufferedReader br = null;
	        try {
	            br = new BufferedReader(new InputStreamReader(t.getRequestBody(),"utf-8"));
	        } catch(UnsupportedEncodingException e) {
	            System.err.println("Erreur lors de la r�cup�ration du flux " + e);
	            System.exit(-1);
	        }
	 
	        // R�cup�ration des donn�es en POST
	        try {
	            query = br.readLine();
	        } catch(IOException e) {
	            System.err.println("Erreur lors de la lecture d'une ligne " + e);
	            System.exit(-1);
	        }
	 
	        
	        JSONObject request = new JSONObject();
	        if(query != null)
	        {
	        	//Envoi des donn�es au Gestionnaire afin de pouvoir les traiter
	        	request = ClientHttp.sendData(query, "inscription");
	        }
	        	
	        // Envoi de l'en-t�te Http
	        try {
	            Headers h = t.getResponseHeaders();
	            h.set("Content-Type", "text/html; charset=utf-8");
	            t.sendResponseHeaders(200, request.toString().getBytes().length);
	        } catch(IOException e) {
	            System.err.println("Erreur lors de l'envoi de l'en-t�te : " + e);
	            System.exit(-1);
	        }
	 
	        // Envoi du corps (donn�es HTML)
	        try {
	            OutputStream os = t.getResponseBody();
	            os.write(request.toString().getBytes());
	            os.close();
	        } catch(IOException e) {
	            System.err.println("Erreur lors de l'envoi du corps : " + e);
	        }
	  }
}
