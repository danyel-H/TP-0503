package projet.Gestionnaire_ports;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadEcouteTCP extends Thread
{
	private int portEcoute;
	private Port port;

	public ThreadEcouteTCP(int p, Port port)
	{
		this.portEcoute = p;
		this.port = port;
	}

	
	@Override
	public void run()
	{
		// Création de la socket serveur
        ServerSocket socketServeur = null;
        try {    
            socketServeur = new ServerSocket(portEcoute);
        } catch(IOException e) {
            System.err.println("Création de la socket impossible : " + e);
            System.exit(-1);
        }
 
        // Attente d'une connexion d'un client
        try {
	        while(true)
	        {
        		Socket socketClient;
	            socketClient = socketServeur.accept();
	            ThreadConnexion t = new ThreadConnexion(socketClient, this.port);
	            t.start();
	        }
        } catch(IOException e) {
            System.err.println("Erreur lors de l'attente d'une connexion : " + e);
            System.exit(-1);
        }
	
		try {
			socketServeur.close();
		} catch(IOException e) {
			System.err.println("Erreur lors de la fermeture : "+e);
			System.exit(-1);
		}
	}
	
}
