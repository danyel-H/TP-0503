package projet.Back_office;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

public class ClientHttp 
{
	/*
	 * Intermediaire permettant d'envoyer des donn�es HTTP au Gestionnaire d'Utilisateurs
	 * @param query la requ�te re�ue par le Back Office
	 * @param but Objectif de la requ�te, permet de pointer la bonne URL
	 * @return JSONObject renvoit la reponse trait�e correctement
	 */
	public static JSONObject sendData(String query, String but)
	{
        // Mise en forme de l'URL
        URL url = null;
        try { 
        	if(but.equals("login"))
        		url = new URL("http://localhost:8083/login.html"); 
        	else if(but.equals("inscription"))
        		url = new URL("http://localhost:8083/inscription.html"); 
        } catch(MalformedURLException e) { 
            System.err.println("URL incorrect : " + e);
            System.exit(-1);
        }
 
        // Etablissement de la connexion
        URLConnection connexion = null; 
        try { 
            connexion = url.openConnection(); 
            connexion.setDoOutput(true);
        } catch(IOException e) { 
            System.err.println("Connexion impossible : " + e);
            System.exit(-1);
        } 
 
        // Envoi de la requ�te
        try {
            OutputStreamWriter writer = new OutputStreamWriter(connexion.getOutputStream());
            writer.write(query);
            writer.flush();
            writer.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi de la requete : " + e);
            System.exit(-1);            
        }        
 
        // R�ception des donn�es depuis le serveur
        String donnees = ""; 
        try { 
            BufferedReader reader = new BufferedReader(new InputStreamReader(connexion.getInputStream())); 
            String tmp; 
            while((tmp = reader.readLine()) != null) 
                donnees += tmp;
            
            reader.close(); 
        } catch(Exception e) { 
            System.err.println("Erreur lors de la lecture de la r�ponse : " + e);
            System.exit(-1);
        }
 
        // Affichage des donn�es re�ues
        return new JSONObject(donnees);
    }

}
