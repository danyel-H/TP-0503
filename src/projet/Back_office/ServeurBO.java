package projet.Back_office;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpServer;

import projet.Commun.Bateau;
import projet.Commun.GestionUDP;
import projet.Commun.JSONConfig;
import projet.Gestionnaire_ports.Port;

public class ServeurBO 
{
	private static Vector<Port> LesPorts = new Vector<Port>();
	private static JSONConfig config;
	private static GestionUDP udp;

	
	public static void main(String[] args) 
	{
	        HttpServer serveur = null;
	        try {
	            serveur = HttpServer.create(new InetSocketAddress(8082), 0);
	        } catch(IOException e) {
	            System.err.println("Erreur lors de la création du serveur " + e);
	            System.exit(-1);
	        }
	 
	        udp = new GestionUDP(9001);
	        
	       	//Création des contextes
	        serveur.createContext("/inscription.html", new RegisterHandler());
	        serveur.createContext("/login.html", new LoginHandler());
	        serveur.createContext("/port.html", new PortHandler());
	        serveur.createContext("/bateau.html", new BateauHandler());
	        serveur.setExecutor(null);
	        serveur.start();
	 
	        System.out.println("Serveur démarré. Pressez CRTL+C pour arrêter.");
	        while(true)
	        {
	        	JSONObject reponse = udp.receiveUDP();
				react(reponse);
	        }
	}
	
	/*
	 * Réagis en fonction du JSON passé, par convention, une clé "message" est utilisée afin de dire l'utilité du message
	 * @param json JSONObject récupéré à partir d'une réponse UDP 
	 */
	public static void react(JSONObject json)
	{
		String but = json.get("message").toString();
		JSONObject ack = new JSONObject();
		if(but.equals("new_port"))
		{
			//Ajout d'un port dans la liste des ports connectés et réponse "OK"
			JSONObject port = json.getJSONObject("Port");
			LesPorts.add(Port.fromJSON(port));
			ack.put("message", "OK");
			GestionUDP.sendUDP(ack, port.getString("ip"), port.getInt("port_udp"));
		}
		else if(but.equals("createBoat"))//Cas où le Back office doit créer un bateau
		{
			Port port = getPortObject(json.getString("port"));
			addBoat(json, json.getString("port"));
			ack.put("message", "OK");
			GestionUDP.sendUDP(ack, port.getIp(), port.getPort_udp());
		}
		else if(but.equals("changerInfos"))//Cas où le Back Office doit modifier les infos d'un bateau
		{
			Port port = getPortObject(json.getString("port"));
			RefreshBoat(json.getJSONObject("bateau"), json.getString("port"));
			ack.put("message", "OK");
			GestionUDP.sendUDP(ack, port.getIp(), port.getPort_udp());
		}
		else if(but.equals("supprimerBateau"))//Cas où un bateau a été supprimé
		{
			Port port = getPortObject(json.getString("port"));
			RemoveBoat(json.getInt("bateau"), json.getString("port"));
			ack.put("message", "OK");
			GestionUDP.sendUDP(ack, port.getIp(), port.getPort_udp());
		}
		else if(but.equals("transfertBateau"))//Cas où un bateau a été déplacé
		{
			Port port = getPortObject(json.getString("old_port"));
			bougerBateau(json.getInt("bateau"), json.getInt("new_place"), json.getString("old_port"), json.getString("new_port"));
			ack.put("message", "OK");
			GestionUDP.sendUDP(ack, port.getIp(), port.getPort_udp());
		}
	}
	
	/*
	 * Méthode permettant de retourner un JSON représentant tous les ports
	 * @return l'ensemble des ports
	 */
	public static JSONObject getPortsDispo()
	{
		JSONObject json = new JSONObject();
		JSONArray a = new JSONArray();
		for(Port temp : LesPorts)
		{
			a.put(temp.toJSON());
		}
		json.put("ports", a);
		return json;
	}
	
	/*
	 * Retourne un port sous la forme d'un JSON
	 * @param port le nom du port cherché
	 */
	public static JSONObject getPort(String port)
	{
		JSONObject retour = new JSONObject();
		retour.put("state", "false");
		JSONArray json = getPortsDispo().getJSONArray("ports");
		for(int i =0; i < json.length(); i++)
		{
			JSONObject temp = json.getJSONObject(i);
			if(temp.get("nom").toString().equals(port));
			{
				retour  = new JSONObject(temp.toString());
				retour.put("state", "true");
			}
		}
		
		return retour;
	}
	
	/*
	 * Méthode permettant de déplacer un bateau d'un port à un autre
	 */
	public static void bougerBateau(int id, int place, String oldport, String newport)
	{
		Port p1 = getPortObject(oldport);
		Port p2 = getPortObject(newport);
		Bateau b = p1.getBateau(id);
		
		p1.removeBateau(id);
		p1.libererPlace(String.valueOf(id));
		
		p2.addBateau(b);
		p2.PrendrePlace(place, String.valueOf(b.getId()));
	}
	
	/*
	 * Retourne un port sous la forme d'un objet Bateau
	 * @param port le nom du port cherché
	 */
	public static Port getPortObject(String port)
	{
		Port temp = null;
		
		for(Port p : LesPorts)
		{
			if(p.getNom().equals(port))
			{
				temp = p;
			}
		}
		
		return temp;
	}
	
	/*
	 * Méthode permettant de changer les infos d'un bateau donné à un port donné
	 * @param json le JSON du bateau
	 * @param port le nom du port
	 */
	public static void RefreshBoat(JSONObject bateau, String port)
	{
		Bateau b = Bateau.fromJSON(bateau);
		getPortObject(port).setBateau(b.getId(), b);
	}
	
	/*
	 * Méthode permettant d'enlever un bateau du vecteur des Ports
	 * @param id l'id du bateau
	 * @param port Le nom du port
	 */
	public static void RemoveBoat(int id, String port)
	{
		getPortObject(port).removeBateau(id);
		getPortObject(port).libererPlace(String.valueOf(id));
	}
	
	/*
	 * Méthode permettant d'ajouter un bateau à un port donné
	 * @param json le JSON du bateau
	 * @param port le nom du port
	 */
	public static void addBoat(JSONObject json, String port)
	{
		JSONObject json_bato = json.getJSONObject("bateau");
		getPortObject(port).addBateau(Bateau.fromJSON(json_bato));
		getPortObject(port).PrendrePlace(json.getInt("place"), String.valueOf(json_bato.getString("id")));
	}

	/**
	 * Retourne les bateaux d'un port indiqué
	 * @param port le nom du port
	 * @return tous les bateaux de ce port
	 */
	public static JSONObject getBateaux(String port)
	{
		JSONObject temp = new JSONObject();
		temp.put("bateaux",getPortObject(port).getBateauxJSON());
		return temp;
	}
	
}
