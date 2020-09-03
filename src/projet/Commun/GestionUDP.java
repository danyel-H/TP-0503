package projet.Commun;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONObject;

public class GestionUDP 
{
	private int portEcoute;
	private DatagramSocket socket;
	
	public GestionUDP(int port)
	{
		this.portEcoute = port;
		// Cr�ation de la socket
        socket = null;
        try {        
            socket = new DatagramSocket(portEcoute);
        } catch(SocketException e) {
            System.err.println("Erreur lors de la création de la socket : " + e);
            System.exit(-1);
        }
	}
	
	/**
	 * Permet de se mettre en �coute sur un port UDP fix� dans le constructeur
	 * @return le JSON re�u
	 */
	public JSONObject receiveUDP()
	{		
        JSONObject json = new JSONObject();

        // Création du message
        byte[] tampon = new byte[8192];
        DatagramPacket msg = new DatagramPacket(tampon, tampon.length);

        // Lecture du message du client
        try {
            socket.receive(msg);

            String texte = new String(msg.getData(), 0, msg.getLength());
            json = new JSONObject(texte);
        } catch(IOException e) {
            System.err.println("Erreur lors de la réception du message : " + e);
            System.exit(-1);
        }

        // Fermeture de la socket
        return json;
	}
	
	/**
	 * Envoie un JSON en UDP
	 * @param json le JSON � envoyer
	 * @param ip l'adresse vis�e
	 * @param port Le port vis�
	 */
	public static void sendUDP(JSONObject json, String ip, int port)
	{
		DatagramSocket socket = null;
        
        // Création de la socket
        try {
            socket = new DatagramSocket();
        } catch(SocketException e) {
            System.err.println("Erreur lors de la création de la socket : " + e);
            System.exit(-1);
        }
        
        // Création du message
        DatagramPacket msg = null;
        try {
            InetAddress adresse = InetAddress.getByName(ip);
            byte[] tampon = json.toString().getBytes();            
            msg = new DatagramPacket(tampon, tampon.length, adresse, port);
        } catch(UnknownHostException e) {
            System.err.println("Erreur lors de la création du message : " + e);
            System.exit(-1);
        }

        // Envoi du message
        try {
            socket.send(msg);
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi du message : " + e);
            System.exit(-1);
        }
        


        // Fermeture de la socket
        socket.close();

	}
	
	public void closeUDP()
	{
		socket.close();
	}

}
