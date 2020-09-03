package projet.Commun;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class KeyManager 
{	
	private PrivateKey clePrivee;
	private PublicKey clePublique;
	
	public KeyManager()
	{
		// Générateur de clés
		KeyPairGenerator generateurCles = null;
		SecureRandom random = null;
		try {
			generateurCles = KeyPairGenerator.getInstance("RSA");
        	random = SecureRandom.getInstance("SHA1PRNG");
        	generateurCles.initialize(1024, random);
        	} catch( NoSuchAlgorithmException e) {
        		System.err.println("Erreur d'algorithme" + e);
                System.err.println(e);
                System.exit(-1);
        	}
		KeyPair paireCles = generateurCles.generateKeyPair();
    	this.clePrivee = paireCles.getPrivate();
    	this.clePublique = paireCles.getPublic();
	}
	
	
	 /**
     * Lecture d'une clé privée depuis un fichier.
     * @param nomFichier le nom du fichier contenant la clé privée
     * @return la clé privée
     */
    public PrivateKey lectureClePrivee(String nomFichier) {
        BigInteger modulo = null, exposant = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(nomFichier)));        
            modulo = (BigInteger) ois.readObject();
            exposant = (BigInteger) ois.readObject();
        } catch(IOException e) {
            System.err.println("Erreur lors de la lecture de la clé : " + e);
            System.exit(-1);
        } catch(ClassNotFoundException e) {
            System.err.println("Fichier de cle incorrect : " + e);
            System.exit(-1);
        }
 
        PrivateKey clePrivee = null;
        try {
            RSAPrivateKeySpec specification = new RSAPrivateKeySpec(modulo, exposant);
            KeyFactory usine = KeyFactory.getInstance("RSA");
            clePrivee = usine.generatePrivate(specification);
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Algorithme RSA inconnu : " + e);
            System.exit(-1);
        } catch(InvalidKeySpecException e) {
            System.err.println("Spécification incorrecte : " + e);
            System.exit(-1);
        }
        return clePrivee;
    }
 
    /**
     * Lecture d'une clé publique depuis un fichier.
     * @param nomFichier le nom du fichier contenant la clé publique
     * @return la clé publique
     */
    public PublicKey lectureClePublique(String nomFichier) {
        BigInteger modulo = null, exposant = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(nomFichier)));        
            modulo = (BigInteger) ois.readObject();
            exposant = (BigInteger) ois.readObject();
        } catch(IOException e) {
            System.err.println("Erreur lors de la lecture de la clé : " + e);
            System.exit(-1);
        } catch(ClassNotFoundException e) {
            System.err.println("Fichier de clé incorrect : " + e);
            System.exit(-1);
        }
 
        PublicKey clePublique = null;
        try {
            RSAPublicKeySpec specification = new RSAPublicKeySpec(modulo, exposant);
            KeyFactory usine = KeyFactory.getInstance("RSA");
            clePublique = usine.generatePublic(specification);
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Algorithme RSA inconnu : " + e);
            System.exit(-1);
        } catch(InvalidKeySpecException e) {
            System.err.println("Spécification incorrecte : " + e);
            System.exit(-1);
        }
                
        return clePublique;
    }
    
    /**
     * Demande le modulo et l'exposant d'un certificat
     * @param nomFichier le chemin du certificat
     * @return Un tableau d'octets (La clé)
     */
    public byte[] getOctets(String nomFichier)
    {
        BigInteger modulo = null, exposant = null;
        try {
    	ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(nomFichier)));        
        modulo = (BigInteger) ois.readObject();
        exposant = (BigInteger) ois.readObject();
        } catch(IOException e) {
        	System.err.println("Erreur lors de la sérialisation en objet : " + e);
            System.exit(-1);
        } catch(ClassNotFoundException e) {
        	System.err.println("Erreur lors de la lecture des objets : " + e);
            System.exit(-1);
        }
     
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(modulo);
            oos.writeObject(exposant);
        } catch(IOException e) {
            System.err.println("Erreur lors de la sérialisation : " + e);
            System.exit(-1);
        }
        
        byte[] donnees = baos.toByteArray();
        return donnees;
    }
    
    /**
     * Retourne le modulo de la clé
     * @param nomFichier le chemin du certificat
     * @return Le modulo sous forme de BigInteger
     */
    public BigInteger getModulo(String nomFichier)
    {
    	 BigInteger modulo = null, exposant = null;
         try {
     	ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(nomFichier)));        
         modulo = (BigInteger) ois.readObject();
         } catch(IOException e) {
         	System.err.println("Erreur lors de la sérialisation en objet : " + e);
             System.exit(-1);
         } catch(ClassNotFoundException e) {
         	System.err.println("Erreur lors de la lecture des objets : " + e);
             System.exit(-1);
         }
         
         return modulo;
    }
    
    /**
     * Retourne l'exposant de la clé
     * @param nomFichier le chemin du certificat
     * @return L'exposant sous forme de BigInteger
     */
    public BigInteger getExposant(String nomFichier)
    {
    	 BigInteger modulo = null, exposant = null;
         try {
     	ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(nomFichier)));        
         modulo = (BigInteger) ois.readObject();
         exposant = (BigInteger) ois.readObject();
         } catch(IOException e) {
         	System.err.println("Erreur lors de la sérialisation en objet : " + e);
             System.exit(-1);
         } catch(ClassNotFoundException e) {
         	System.err.println("Erreur lors de la lecture des objets : " + e);
             System.exit(-1);
         }
         
         return exposant;
    }

    /**
     * Permet de chiffrer un texte
     * @param s le texte à chiffrer
     * @param pb La clé publique
     * @return le texte chiffré (en octets)
     */
    private static byte[] chiffre(String s, PublicKey pb)
	{
		byte[] bytes = null;
		Cipher chiffreur =null;
		try {
		chiffreur = Cipher.getInstance("RSA");
		chiffreur.init(Cipher.ENCRYPT_MODE, pb);
		bytes = chiffreur.doFinal(s.getBytes());
		} catch (Exception e) {
			System.err.println("Erreur chiffrage "+ e);
		}
		return bytes;
	}
	
    /**
     * Déchiffre un texte 
     * @param b Le texte chiffré
     * @param clepr La clé pour déchiffrer
     * @return La chaine déchiffrée
     */
	private static String dechiffre(byte[] b, PrivateKey clepr)
	{
		byte[] bytes = null;
		Cipher chiffreur =null;
		try {
		chiffreur = Cipher.getInstance("RSA");
		chiffreur.init(Cipher.DECRYPT_MODE, clepr);
		bytes = chiffreur.doFinal(b);
		} catch (Exception e) {
			System.err.println("Erreur dechiffrage "+ e);
		}
		return new String(bytes);
	}
	
    
}
