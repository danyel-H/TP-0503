package projet.Commun;

import org.json.JSONObject;

/*
 * Symbolise un bateau muni de toutes ses configurations
 *@author Danyel Hocquigny
 *@date 29/11/2018 
 */

public class Bateau
{
	private String nom;
	private int id;
	private String proprietaire;
	private String type;
	private String taille;
	private String cdm = "";
	
	
	public Bateau(int id,String nom, String proprietaire, String type, String taille)
	{
		this.id = id;
		this.nom = nom;
		this.proprietaire = proprietaire;
		this.type = type;
		this.taille = taille;
	}
	
	/*
	 * Retourne un Bateau à partir d'un JSON
	 */
	public static Bateau fromJSON(JSONObject json)
	{
		return new Bateau(json.getInt("id"), json.get("nom").toString(), json.get("proprietaire").toString(), json.get("type").toString(), json.get("taille").toString());
	}
	
	/*
	 * Retourne un objet JSON du bateau instancié
	 */
	public JSONObject toJSON()
	{
		JSONObject json = new JSONObject();
		json.put("id", String.valueOf(this.id));
		json.put("nom", this.nom);
		json.put("proprietaire", this.proprietaire);
		json.put("type", this.type);
		json.put("taille", this.taille);
		json.put("cdm", this.cdm);
		
		return json;
	}

	/**
	 * @return le nom du bateau
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Change le nom du bateau
	 * @param nom Le nouveau nom
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/*
	 * Retourne l'id du bateau
	 */
	public int getId() {
		return id;
	}

	/**
	 * Change l'id du bateau
	 * @param id le nouvel id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/*
	 * Retourne l'id du propriétaire
	 */
	public String getProprietaire() {
		return proprietaire;
	}
	
	/**
	 * Change l'id du propriétaire
	 * @param proprietaire le nouvel id
	 */
	public void setProprietaire(String proprietaire) {
		this.proprietaire = proprietaire;
	}

	/*
	 * Reoturne le type de bateau
	 */
	public String getType() {
		return type;
	}

	/**
	 * Change le type du bateau
	 * @param type le nouveau type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/*
	 * Retourne la taille du bateau
	 */
	public String getTaille() {
		return taille;
	}

	/**
	 * Change la taille du bateau
	 * @param taille la nouvelle talle en cm
	 */
	public void setTaille(String taille) {
		this.taille = taille;
	}
	
	
	
}
