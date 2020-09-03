package projet.Commun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Classe permettant de créer/gérer un fichier json
 * @author Danyel Hocquigny
 * @date 29/11/2018
 */
public class JSONConfig 
{

    private String nomFichier;      // Nom du fichier json
    private JSONObject f_json;      // L'objet json
    
    /**
     * Ouverture d'un fichier json
     * @param nomFichier le nom du fichier de configuration
     */
    public JSONConfig(String nomFichier) 
    {
        this.nomFichier = nomFichier;
        charger();
    }

    /**
     * Ouverture/création d'un fichier json
     * @param nomFichier le nom du fichier json
     * @param creation si 'true', crée un nouveau fichier vide
     */
    public JSONConfig(String nomFichier, boolean creation) 
    {
        if(!creation) 
        {
            this.nomFichier = nomFichier;
            charger();
        }
        else 
        {
            this.nomFichier = nomFichier;
            f_json = new JSONObject();
        }
    }
    
    /**
     * Indique si un fichier existe.
     * @param nomFichier le nom du fichier
     * @return 'true' s'il existe
     */
    public static boolean fichierExiste(String nomFichier) 
    {
        File f = new File(nomFichier);

        return f.exists();       
    }
    
    /**
     * Retourne la valeur associée à une clef.
     * @param clef le nom de la clef
     * @return la valeur de la clef
     */
    public String getString(String clef) 
    {
        /**
         * #TODO#
         * Récupère la donnée dont la clef est spécifiée dans l'objet
         * JSON (attribut 'config').
         */      
    	String temp;
    	temp = f_json.getString(clef);
    	return temp;
    }
    
    /**
     * Retourne la valeur associée à une clef.
     * @param clef le nom de la clef
     * @return la valeur de la clef
     */
    public int getInt(String clef) 
    {

    	int temp;
    	temp = f_json.getInt(clef);
    	return temp;
    }
    
    /**
     * Ajoute une valeur dans le json.
     * @param clef le nom de la clef
     * @param valeur la valeur de la clef
     */
    public void ajouterValeur(String clef, int valeur) 
    {
    	try {
    		f_json.put(clef, valeur);
    	} catch (JSONException e) {
    		System.err.println("Erreur ajouterValeur 1" + e);
    	}
    }

    /**
     * Ajoute une valeur dans le json.
     * @param clef le nom de la clef
     * @param valeur la valeur de la clef
     */
    public void ajouterValeur(String clef, String valeur) 
    {
    	try {
    		f_json.put(clef, valeur);
    	} catch (JSONException e) {
    		System.err.println("Erreur ajouterValeur 2" + e);
    	}
    }
    
    /**
     * Ajoute un JSONObject dans le json, utilisé essentiellement pour Bateaux et Utilisateurs.
     * @param clef le nom de la clef
     * @param valeur la valeur de la clef
     */
    public void ajouterJSON(String clef, JSONObject valeur) 
    {
    	try {
    		JSONArray a = f_json.getJSONArray(clef);
    		a.put(valeur);
    	} catch (JSONException e) {
    		System.err.println("Erreur ajouterValeur JSONObject" + e);
    	}
    }
    
    /*
     * Cherche l'id Max dans un JSONArray
     * @return l'id Max +1
     */
    public static String MaxId(JSONArray json)
    {
    	int id = 1;
		if(json.length() != 0)
		{
			for(int i =0; i < json.length(); i++)
			{
				JSONObject temp = new JSONObject(json.get(i).toString());
				if(temp.getInt("id") > id)
				{
					id = temp.getInt("id");
				}
			}
		}
		return String.valueOf(id+1);
    }
    
    
    /**
     * Ajoute un JSONArray dans le json.
     * @param clef le nom de la clef
     * @param valeur la valeur de la clef
     */
    public void ajouterTab(String clef, JSONArray valeur)
    {
    	try {
    		f_json.put(clef, valeur);
    	} catch (JSONException e) {
    		System.err.println("Erreur ajouterTab" + e);
    	}
    }
    
    /*
     * Supprime un des élements d'un tableau
     * @param nomTab s'il y'a, le nom de la clé du tableau
     * @param index l'index de l'élément à supprimer
     */
    public void supprimerDansTab(String nomTab, int index)
    {
    	f_json.getJSONArray(nomTab).remove(index);
    }
    
    /*
     * Remplace un des éléments d'un tableau JSON
     * @param nomTab s'il y'a, le nom de la clé du tableau
     * @param JSON le JSON qui doit remplacer l'ancien
     * @param index l'index de l'élément à remplacer
     */
    public void remplacerDansTab(String nomTab,JSONObject json, int index)
    {
    	f_json.getJSONArray(nomTab).put(index, json);
    }
    
    
    /*
     * Retourne le JSON du fichier
     */
    public JSONObject getJSON()
    {
    	return this.f_json;
    }
    
    /**
     * Charge un fichier json en mémoire.
     */
    private void charger() 
    {

    	// Ouverture du fichier
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(nomFichier);
        } catch(FileNotFoundException e) {
            System.err.println("Fichier '" + nomFichier + "' introuvable");
            System.exit(-1);
        }
         
        // Récupération de la chaîne JSON depuis le fichier
        String json = new String();
        Scanner scanner = new Scanner(fs);
        while(scanner.hasNext())
        {
            json += scanner.nextLine();
        }
        scanner.close();
        json = json.replaceAll("[\t ]", "");
 
        // Fermeture du fichier
        try {
            fs.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de la fermeture du fichier.");
            System.err.println(e);
            System.exit(-1);
        } 
        
        f_json = new JSONObject(json);
    }
    
    /**
     * Sauvegarde le json dans le fichier.
     */
    public void sauvegarder() 
    {
        // Création du fichier de sortie
        
        /**
         * #TODO#
         * Sauvegarder le fichier JSON dans le fichier dont le nom
         * correspond à l'attribut 'nomFichier'.
         */
    	
    	//Si le dossier dans lequel va être créé le fichier n'existe pas, on le créé
		String temp[] = nomFichier.split("/");
		String dossier = "";
		for(int i=0; i< temp.length-1; i++)
		{
			dossier += temp[i]+"/";
		}
		
    	if(!fichierExiste(dossier))
    	{
    		new File(dossier).mkdirs();
    	}
    	
    	 FileWriter fs = null;
         try {
             fs = new FileWriter(nomFichier);
         } catch(IOException e) {
             System.err.println("Erreur lors de l'ouverture du fichier '" + nomFichier + "'.");
             System.err.println(e);
             System.exit(-1);
         }
  
         // Sauvegarde dans le fichier
         try {
             f_json.write(fs, 3, 0); //Arguments 3 et 0 : Indentation
             fs.flush();
         } catch(IOException e) {
             System.err.println("Erreur lors de l'écriture dans le fichier.");
             System.err.println(e);
             System.exit(-1);
         }
  
         // Fermeture du fichier
         try {
             fs.close();
         } catch(IOException e) {
             System.err.println("Erreur lors de la fermeture du fichier.");
             System.err.println(e);
             System.exit(-1);
         }
    }
    
}
