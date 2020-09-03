package projet.Gestionnaire_users;

import org.json.JSONArray;
import org.json.JSONObject;

import projet.Commun.JSONConfig;

public class GestionnaireUtilisateurs 
{
	JSONObject users;
	JSONConfig interactions;
	
	public GestionnaireUtilisateurs(String nomFichier)
	{
		if(JSONConfig.fichierExiste(nomFichier))
            this.interactions = new JSONConfig(nomFichier);
		else
		{
            this.interactions = new JSONConfig(nomFichier, true);
            this.interactions.ajouterTab("utilisateurs", new JSONArray());
		}

        this.users = interactions.getJSON();
        
	}
	
	/*
	 * Retourne les utilisateurs du fichier
	 * @return le JSONObject des utilisateurs
	 */
	public JSONObject getUsers()
	{
		return this.users;
	}
	
	/**
	 * Retourne un utilisateur en particulier selon son login
	 * @param login le login de l'utilisateur
	 * @param mdp le mdp de l'utilisateur
	 * @return le JSONObject de l'utilisateur
	 */
	public JSONObject getUser(String login)
	{
		JSONObject json = null;

		JSONArray users = getUsers().getJSONArray("utilisateurs");

		for(int i =0; i < users.length(); i++)
		{
			JSONObject temp = new JSONObject(users.get(i).toString());
			if(temp.get("login").toString().equals(login))
			{
				json = temp;
			}
		}
		
		return json;
	}
	
	/**
	 * Retourne l'id d'un utilisateur
	 * @param login le login de l'utilisateur
	 * @return Son id (String)
	 */
	public String getId(String login)
	{
		JSONObject json = getUser(login);
		String temp = json.getString("id");
		return temp;
	}
	
	/**
	 * Retourne le grade d'un utilisateur
	 * @param login le login de l'utilisateur
	 * @return
	 */
	public String getGrade(String login)
	{
		JSONObject json = getUser(login);
		String temp = json.getString("grade");
		return temp;
	}
	
	/*
	 * Méthode renvoyant le plus petit id disponible dans le fichier utilisateur
	 * @return le plus grand id+1, sous forme de String
	 */
	public String getMaxId()
	{
		int id = 1;
		if(this.users.length() != 0)
		{
			JSONArray users = getUsers().getJSONArray("utilisateurs");
			for(int i =0; i < users.length(); i++)
			{
				JSONObject temp = new JSONObject(users.get(i).toString());
				if(temp.getInt("id") > id)
				{
					id = temp.getInt("id");
				}
			}
		}
		return String.valueOf(id+1);
	}
	
	/**
	 * Vérifie la bonne authentification de l'utilisateur
	 * @param login le login qu'il a entré
	 * @param mdp le mot de passe qu'il a entré
	 * @return "vrai" si les informations sont correctes
	 */
	public boolean verifUser(String login, String mdp)
	{

		boolean b = false;
		JSONObject json = getUser(login);

		if(json != null)
		{
			if(json.get("mdp").toString().equals(mdp))
				b = true;
		}
		
		return b;
	}
	
	/**
	 * Permet d'inscrire l'utilisateur dans le fichier des utilisateurs
	 * @param login son login
	 * @param mdp son mot de passe
	 * @param grade son grade (Propriétaire de port ou de bateau)
	 * @return "true" si l'inscription s'est bien passée
	 */
	public boolean InscrireUser(String login, String mdp, String grade)
	{
		boolean b = false;
		//On vérifie une dernière fois afin de ne pas corrompre le JSON
		String new_login = login.replace('"', ' '), new_mdp = mdp.replace('"', ' ');
		JSONObject user = getUser(login);
		if(new_login.equals(login) && new_mdp.equals(mdp) && user == null)
		{
			b = true;
			JSONObject json = new JSONObject();
			json.put("id", getMaxId());
			json.put("login", login);
			json.put("mdp", mdp);
			json.put("grade", grade);
			this.interactions.ajouterJSON("utilisateurs", json);
			this.interactions.sauvegarder();
			//On actualise le JSON de nos utilisateurs
			this.users = this.interactions.getJSON();
		}
		
		return b;
	}
}
