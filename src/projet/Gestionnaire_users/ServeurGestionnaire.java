package projet.Gestionnaire_users;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class ServeurGestionnaire 
{
	public static void main(String[] args) 
	{
	        HttpServer serveur = null;
	        try {
	            serveur = HttpServer.create(new InetSocketAddress(8083), 0);
	        } catch(IOException e) {
	            System.err.println("Erreur lors de la création du serveur " + e);
	            System.exit(-1);
	        }
	 
	        serveur.createContext("/login.html", new ConnexionHandler());
	        serveur.createContext("/inscription.html", new InscriptionHandler());
	        serveur.setExecutor(null);
	        serveur.start();
	 
	        System.out.println("Serveur démarré. Pressez CRTL+C pour arrêter.");
	}
}
