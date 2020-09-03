package projet.Gestionnaire_users;

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

public class InscriptionHandler implements HttpHandler
{
	public void handle(HttpExchange t) 
	  {

			JSONObject reponse = new JSONObject();
	    	reponse.put("message", "inscription");
	
	        
	        // Récupération des données
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
	 
	        if(query != null)
	        {
	        	//Division des parties de la requête afin de la traiter
	        	String info[] = query.split("&");
	        	String login[] = info[0].split("=");
	        	String mdp[] = info[1].split("=");
	        	String grade[] = info[2].split("=");
	        	JSONObject json = new JSONObject();
	        	json.put("message", "inscription");
	        	json.put("login", login[1]);
	        	json.put("mdp", mdp[1]);
	        	json.put("grade", grade[1]);
	
	        	//JSONObject json = new JSONObject(query);

	        	GestionnaireUtilisateurs g = new GestionnaireUtilisateurs("data/GestionnaireUtilisateur/users.json");
	        	if(g.InscrireUser(json.get("login").toString(), json.get("mdp").toString(), json.get("grade").toString()))
	        	{
	        		reponse.put("state", "true");
	        		reponse.put("login", json.getString("login"));
	        		reponse.put("id", g.getId(json.getString("login")));
	        		reponse.put("grade", g.getGrade(json.getString("login")));
	        		System.out.println("Inscription faite");
	        	}
	        	else
	        		reponse.put("state", "false");
	        }
	        	
	        // Envoi de l'en-tête Http
	        try {
	            Headers h = t.getResponseHeaders();
	            h.set("Content-Type", "text/html; charset=utf-8");
	            t.sendResponseHeaders(200, reponse.toString().getBytes().length);
	        } catch(IOException e) {
	            System.err.println("Erreur lors de l'envoi de l'en-tête : " + e);
	            System.exit(-1);
	        }
	 
	        // Envoi du corps (données HTML)
	        try {
	            OutputStream os = t.getResponseBody();
	            os.write(reponse.toString().getBytes());
	            os.close();
	        } catch(IOException e) {
	            System.err.println("Erreur lors de l'envoi du corps : " + e);
	        }
	    }
}
