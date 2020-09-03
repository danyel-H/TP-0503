package projet.Autorite;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.json.JSONException;
import org.json.JSONObject;

import projet.Commun.KeyManager;

public class Autorite 
{
	private String nom;
	private String adresse;
	private static String port = "5002";
	private static String private_key;
	private static PublicKey cle_publique;
	private static PrivateKey cle_privee;
	
	
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

	public Autorite(String nom, String adresse)
	{
		this.nom = nom;
		this.adresse = adresse;
		KeyManager clés = new KeyManager();
		cle_publique = clés.lectureClePublique("cle_pb_autorite.json");
		cle_privee = clés.lectureClePrivee("cle_pv_autorite.json");
	}
	
	/*
	 * Génère son propre certificat
	 * @return le certificat de l'autorité
	 */
	public Certificat PropreCertificat()
	{
		Certificat c = new Certificat(this.nom, this.adresse, this.port, this);
		
		return c;
	}
	
	/*
	 * Retourne le nom de l'autorité
	 */
	public String getNom()
	{
		return this.nom;
	}
	
	/*
	 * Retourne l'adresse de l'autorité
	 */
	public String getAdresse()
	{
		return this.adresse;
	}
	
	/*
	 * Retourne le port(udp) de l'autorité
	 */
	public String getPort()
	{
		return this.port;
	}
	
	/*
	 * Transforme l'autorité en JSON
	 */
	public JSONObject toJSON()
	{
		JSONObject json = new JSONObject();
		try {
		json.put("nom", this.nom);
		json.put("adresse", this.adresse);
		} catch (JSONException ex) {
			System.out.println("Erreur "+ex);
			System.exit(-1);
		}
		
		return json;
	}
	
}
