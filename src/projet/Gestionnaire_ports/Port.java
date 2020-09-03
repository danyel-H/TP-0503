package projet.Gestionnaire_ports;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import projet.Commun.Bateau;
import projet.Commun.JSONConfig;


/*
 * Créée par HOCQUIGNY Danyel
 * Classe représentant un port
 */
public class Port 
{
	private String nom;
	private String ip;
	private int port_udp;
	private int port_tcp;
	private JSONArray places;
	private Vector<Bateau> bateaux;
	private JSONConfig jsonPort = null;
	private JSONConfig jsonBateaux = null;
	
	public Port(String ip, int port_udp, int port_tcp, String nom, JSONArray places, JSONArray bateaux) 
	{
		this.nom = nom;
		this.ip = ip;
		this.places = places;
		this.port_udp = port_udp;
		this.port_tcp = port_tcp;
		
		Vector<Bateau> temp = new Vector<Bateau>();
		for(int i = 0; i < bateaux.length(); i++)
		{
			temp.add(Bateau.fromJSON(bateaux.getJSONObject(i)));
		}
		this.bateaux = temp;
		
	}
	
	public Port(String ip, int port_udp, int port_tcp, String nom, JSONArray places, Vector<Bateau> bateaux) 
	{
		this.nom = nom;
		this.places = places;
		setBateaux(bateaux);
		this.port_tcp = port_tcp;
		this.port_udp = port_udp;
		this.ip = ip;
	} 

	/**
	 * Génère un port à partir d'un JSON
	 * @param json le JSON du Port
	 * @return un Port
	 */
	public static Port fromJSON(JSONObject json)
	{
		Vector<Bateau> temp = new Vector<Bateau>();
		
		JSONArray bato = json.getJSONArray("bateaux");
		for(int i = 0; i < bato.length(); i++)
		{
			temp.add(Bateau.fromJSON(bato.getJSONObject(i)));
		}
		
		return new Port(json.getString("ip"),json.getInt("port_udp"), json.getInt("port_tcp"), json.getString("nom"), json.getJSONArray("Places"), temp);
	}
	
	/**
	 * Transforme le Port courant en JSON
	 * @return le JSON du port
	 */
	public JSONObject toJSON()
	{
		JSONObject json = new JSONObject();
		JSONArray bato = new JSONArray();
		
		for(Bateau b : bateaux)
		{
			bato.put(b.toJSON());
		}
		
		json.put("ip", this.ip);
		json.put("nom", getNom());
		json.put("port_udp", getPort_udp());
		json.put("port_tcp", getPort_tcp());
		json.put("bateaux", bato);
		json.put("Places", getPlaces());
				
		return json;
	}
	
	/*
	 * Retourne le nom du port
	 */
	public String getNom() 
	{
		return nom;
	}

	/*
	 * Méthode permettant de renseigner un fichier de configurationafin de modifier le port en temps réel
	 */
	public void setFichierJSON(JSONConfig json)
	{
		this.jsonPort = json;
	}
	
	/*
	 * Méthode servant à lier le fichier des bateaux avec l'objet afin d'avoir toutes les sauvegardes en temps réel
	 */
	public void setFichierBateaux(JSONConfig json)
	{
		this.jsonBateaux = json;
	}
	
	/**
	 * Change le nom du port
	 * @param nom le nouveau nom
	 */
	public void setNom(String nom) 
	{
		this.nom = nom;
	}
	
	/*
	 * Renvoie l'adresse ip du port
	 */
	public String getIp() {
		return ip;
	}

	/*
	 * Retourne le port sur lequel le Port écoute en UDP
	 */
	public int getPort_udp() {
		return port_udp;
	}

	/*
	 * Retourne le port sur lequel le port écoute en TCP
	 */
	public int getPort_tcp() {
		return port_tcp;
	}

	/*
	 * Retourne les places du Port
	 */
	public JSONArray getPlaces() 
	{
		return places;
	}

	/*
	 * Méthode permettant de prendre une place de parking
	 * @param id_p id de la place
	 * @param id du bateau
	 */
	public void PrendrePlace(int id_p, String id_b)
	{
		if(id_p >= 1)
		{
			JSONObject json = this.places.getJSONObject(id_p -1);
			json.put("Prise", id_b);
			if(this.jsonPort != null)
			{
				jsonPort.ajouterTab("Places", this.places);
				jsonPort.sauvegarder();
			}
		}
	}
	
	/*
	 * Permet de libérer une place de parking
	 * @param id l'id du bateau
	 */
	public void libererPlace(String id)
	{
		int id_place = -5;
		for(int i = 0; i < this.places.length(); i++)
		{
			JSONObject temp = this.places.getJSONObject(i);
			if(temp.getString("Prise").equals(id))
			{
				id_place = i;
			}
		}
		JSONObject json = this.places.getJSONObject(id_place);
		json.put("Prise", "false");
		if(this.jsonPort != null)
		{
			jsonPort.ajouterTab("Places", this.places);
			jsonPort.sauvegarder();
		}
	}
	
	/**
	 * Retourne les places qui n'ont pas l'attribut "Prise" en false
	 * @return un JSONArray des places disponibles
	 */
	public JSONArray getPlacesDispo()
	{
		JSONArray placesdisp = new JSONArray();
		for(int i = 0; i < getPlaces().length(); i++)
		{
			JSONObject place = getPlaces().getJSONObject(i);
			if(place.getString("Prise").equals("false"))
			{
				JSONObject temp = new JSONObject();
				temp.put("id_place", String.valueOf(i+1));
				temp.put("longueur", place.getString("longueur"));
				double prix = (1.5*place.getInt("longueur"));
				temp.put("prix", prix);
				
				placesdisp.put(temp);
			}			
		}
		return placesdisp;
	}
	
	
	/*
	 * Change le JSONArray des places
	 */
	public void setPlaces(JSONArray places) 
	{
		this.places = places;
	}

	/*
	 * Retourne le vecteur des Bateaux
	 */
	public Vector<Bateau> getBateaux() 
	{
		return bateaux;
	}

	/*
	 * Retourne le bateau selon son id
	 * @param id l'id du bateau
	 */
	public Bateau getBateau(int id)
	{
		Bateau temp = null;
		for(Bateau b : getBateaux())
		{
			if(b.getId() ==  id)
			{
				temp = b;
			}
		}
		return temp;
	}
	
	/*
	 * Retourne les bateaux sous forme de JSON
	 */
	public JSONArray getBateauxJSON()
	{
		JSONArray bato = new JSONArray();
		
		for(Bateau b : bateaux)
		{
			bato.put(b.toJSON());
		}
		
		return bato;
	}
	
	/*
	 * Change un bateau selon son index
	 * @param id l'id du bateau
	 * @param b le bateau en question
	 */
	public void setBateau(int id, Bateau b)
	{
		for(int i =0; i < this.bateaux.size(); i++)
		{
			if(this.bateaux.get(i).getId() == id)
			{
				this.bateaux.set(i, b);
			}
		}
		if(this.jsonBateaux != null)
		{
			jsonBateaux.ajouterTab("bateaux", this.getBateauxJSON());
			jsonBateaux.sauvegarder();
		}
	}
	
	/*
	 * Supprime un bateau du port
	 * @param id l'id du bateau
	 */
	public void removeBateau(int id)
	{
		for(int i =0; i < this.bateaux.size(); i++)
		{
			if(this.bateaux.get(i).getId() == id)
			{
				this.bateaux.remove(i);
			}
		}
		if(this.jsonBateaux != null)
		{
			jsonBateaux.ajouterTab("bateaux", this.getBateauxJSON());
			jsonBateaux.sauvegarder();
		}
	}
	
	/*
	 * Change les bateaux du Port
	 */
	public void setBateaux(Vector<Bateau> bateaux) 
	{
		this.bateaux = bateaux;
	}
	
	/*
	 * ajoute un Bateau dans le Port
	 */
	public void addBateau(Bateau b)
	{
		bateaux.add(b);
		if(this.jsonBateaux != null) // On modifie le fichier du Port s'il existe
		{
			jsonBateaux.ajouterTab("bateaux", this.getBateauxJSON());
			jsonBateaux.sauvegarder();
		}
	}
	
	
	
}
