package projet.Autorite;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class Certificat 
{
	private String nom_autorite;	
	private String adresse_autorite;
	private String nom_entite;
	private String adresse_entite;
	private String port;
	private Autorite autorite;
	/*****G�n�r� par l'autorite****/
	private String cle_publique;
	private String Signa = "Non-d�finie";
	
	public Certificat(String ne, String ae, String p, Autorite A)
	{
		this.nom_entite = ne;
		this.adresse_entite = ae;
		this.port = p;
		this.autorite = A;
		
		this.nom_autorite = A.getNom();
		this.adresse_autorite = A.getAdresse();
	}
	
	/*
	 * G�n�re un certificat � partir d'un JSON
	 * @param e le JSON
	 * @return un Certificat
	 */
	public static Certificat fromJSON(JSONObject e)
	{
		JSONObject json1 = new JSONObject(e.get("autorite"));
		Autorite temp = new Autorite(json1.getString("nom").toString(), json1.getString("adresse").toString());
		Certificat c = new Certificat(e.get("nom").toString(), e.get("adresse").toString(), e.get("port").toString(), temp);
		return c;
		
	}
	
	/**
	 * Transforme le certificat courant en JSON
	 * @return le JSON sous forme de certificat
	 */
	public JSONObject toJSON()
	{
		JSONObject json = new JSONObject();
		try {
		json.put("nom", this.nom_entite);
		json.put("adresse", this.adresse_entite);
		json.put("port", this.port);
		json.put("autorite", autorite.toJSON());
		json.put("signature", this.Signa);
		} catch (JSONException ex) {
			System.out.println("Erreur "+ex);
			System.exit(-1);
		}
		
		return json;
	}
	
	/**
	 * G�n�re la signature � partir d'une cl� Priv�e et d'un certificat
	 * @param clePrivee La cl� Priv�e utilis�e pour signer
	 * @param nomFichier le chemin du certificat
	 * @return la signature sous forme de tableau d'octets
	 */
	public byte[] genererSignature(PrivateKey clePrivee, String nomFichier)
	{
		 // Cr�ation de la signature
        Signature signature = null;
        try {
            signature = Signature.getInstance("SHA1withRSA");
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Erreur lors de l'initialisation de la signature : " + e);
            System.exit(-1);
        }
 
        // Initialisation de la signature
        try { 
            signature.initSign(clePrivee);
        } catch(InvalidKeyException e) {
            System.err.println("Cl� priv�e invalide : " + e);
            System.exit(-1);
        }
 
        // Mise-�-jour de la signature par rapport au contenu du fichier
        try {
            BufferedInputStream fichier = new BufferedInputStream(new FileInputStream(nomFichier));
            byte[] tampon = new byte[1024];
            int n;
            while (fichier.available() != 0) {
            n = fichier.read(tampon);
            signature.update(tampon, 0, n);
            }
            fichier.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de la lecture du fichier � signer : " + e);
            System.exit(-1);
        }
        catch(SignatureException e) {
            System.err.println("Erreur lors de la mise-�-jour de la signature : " + e);
            System.exit(-1);
        }
 
        byte[] retour = null;
        // Sauvegarde de la signature du fichier
        try {
            retour = signature.sign();
        } catch(SignatureException e) {
            System.err.println("Erreur lors de la r�cup�ration de la signature : " + e);
            System.exit(-1);
        } 
        
        String sign = Base64.getEncoder().encodeToString(retour);
        Signa = sign;
        
        return retour;
	}
}
