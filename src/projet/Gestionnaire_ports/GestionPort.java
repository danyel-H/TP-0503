package projet.Gestionnaire_ports;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import projet.Back_office.ClientHttp;
import projet.Commun.Bateau;
import projet.Commun.GestionUDP;
import projet.Commun.JSONConfig;

public class GestionPort 
{		
	public static void main(String[] args) 
	{
		JSONConfig port = null;
		JSONConfig config = null;
		
		if(args.length == 0) 
        {
            // Pas d'argument : On créé un nouveau port
			String val[] = creerPort();
			if(JSONConfig.fichierExiste("data/Ports/"+val[0]+"/config.json"))
	        {
	            System.out.println("Ce port existe déjà, veuillez le fournir en argument lors du lancement du Port.");
	            System.exit(-1);
	        }
	        else
	        {
	        	//On demande à l'utilisateur de se connecter pour l'affilier à un port
	        	int id = 0;
	        	do {
	        		id = connexionUtilisateur();
	        	} while(id == 0);
	            config = creerJSON("data/Ports/"+val[0]+"/config.json", null,0);
	            port = creerJSON("data/Ports/"+val[0]+"/port.json", val, id);
	        }
        }
        else 
        {
            // Un argument : On vérifie si le port existe, sinon, on renvoie la liste des ports
            
        	if(JSONConfig.fichierExiste("data/Ports/"+args[0]))
            {
        		//On demande au propriétaire du port de se connecter et on insiste tant que l'utilisateur connecté n'est pas le gérant du port
        		boolean b = false;
        		int id, vrai_id;
    			JSONConfig verif = new JSONConfig("data/Ports/"+args[0]+"/port.json");
        		System.out.println("Veuillez vous connecter sous l'identifiant du proprietaire du port :");
        		do {
        			vrai_id = verif.getInt("proprietaire");
        			id = verifUtilisateur();
        		} while(id != vrai_id);
        		
        		//Si la connexion a réussie, on affecte les fichiers du ports
                config = new JSONConfig("data/Ports/"+args[0]+"/config.json");
                port = new JSONConfig("data/Ports/"+args[0]+"/port.json");
            }
        	else
        	{
        		//On liste les ports afin d'aider les utilisateurs
        		System.out.println("Ce port n'existe pas, afin de créer un port,veuillez ne pas fournir d'arguments.");
                System.exit(-1);
        	}
        }
        System.out.println("Bienvenue !");

        
        //On informe le Back-office que le Port est en ligne
        
        JSONObject tablo = new JSONObject();
        JSONConfig bateaux;

        if(JSONConfig.fichierExiste("data/Ports/"+port.getString("nom")+"/bateaux.json"))
        {
        	bateaux = new JSONConfig("data/Ports/"+port.getString("nom")+"/bateaux.json");
        }
        else
        {
        	bateaux = new JSONConfig("data/Ports/"+port.getString("nom")+"/bateaux.json", true);
        	bateaux.ajouterTab("bateaux", new JSONArray());
        }
        
		tablo = bateaux.getJSON();

        JSONObject envoi = new JSONObject();
        envoi.put("message", "new_port");
		Port monPort = new Port(config.getString("ip"), config.getInt("port_udp"), config.getInt("port_tcp"), port.getString("nom"), port.getJSON().getJSONArray("Places"),tablo.getJSONArray("bateaux"));
		monPort.setFichierJSON(port);
		monPort.setFichierBateaux(bateaux);
		envoi.put("Port", monPort.toJSON());
		
		//On ouvre un thread afin d'écouter constamment en TCP
		ThreadEcouteTCP t = new ThreadEcouteTCP(config.getInt("port_tcp"), monPort);
        t.start();
		
        GestionUDP.sendUDP(envoi, config.getString("bo_ip"), config.getInt("bo_port"));
        
        GestionUDP udp = new GestionUDP(config.getInt("port_udp"));
        //On écoute par défaut constamment en UDP
        while(true)
        {
        	JSONObject reponse = udp.receiveUDP();
			react(reponse, config.getJSON(), monPort);
        }
	}
	
	/*
	 * Réagis en fonction du JSON passé, par convention, une clé "utilité" est utilisée afin de dire l'utilité du message
	 * @param json JSONObject récupéré à partir d'une réponse UDP
	 * @param configPort JSONObject représentant la config du port 
	 * @param infosPort JSONObject représentant les infos du port
	 */
	public static void react(JSONObject reponse, JSONObject configPort, Port infosPort)
	{
		JSONObject refresh = new JSONObject();
		refresh.put("port", infosPort.getNom());

		//Cas où quelqu'un a créé un nouveau bateau
		if(reponse.getString("message").equals("create"))
		{
			Random rand = new Random();
			Bateau b = new Bateau(rand.nextInt(5000), reponse.getString("nom"), reponse.getString("proprietaire"), reponse.getString("type"), reponse.getString("taille"));
			
			infosPort.addBateau(b);
			infosPort.PrendrePlace(reponse.getInt("place"), String.valueOf(b.getId()));
			
			refresh.put("message", "createBoat");
			refresh.put("bateau", b.toJSON());
			refresh.put("place", reponse.getInt("place"));
			
			//On envoie au Back Office pour qu'il actualise sa liste
	        GestionUDP.sendUDP(refresh, configPort.getString("bo_ip"), configPort.getInt("bo_port"));
		}
		else if(reponse.getString("message").equals("changer"))//L'utilisateur a changé les infos de son bateau
		{
			Bateau b = new Bateau(reponse.getInt("id"), reponse.getString("nom"), reponse.getString("proprietaire"), reponse.getString("type"), reponse.getString("taille"));
			infosPort.setBateau(b.getId(), b);
			
			refresh.put("message", "changerInfos");
			refresh.put("bateau", b.toJSON());
			
			//On envoie au Back Office pour qu'il actualise sa liste
	        GestionUDP.sendUDP(refresh, configPort.getString("bo_ip"), configPort.getInt("bo_port"));
		}
		else if(reponse.getString("message").equals("supprimer"))//L'utilisateur a supprimé son bateau
		{
			infosPort.libererPlace(reponse.getString("id"));
			infosPort.removeBateau(reponse.getInt("id"));

			refresh.put("message", "supprimerBateau");
			refresh.put("bateau", reponse.get("id"));
			
			//On envoie au Back Office pour qu'il actualise sa liste
	        GestionUDP.sendUDP(refresh, configPort.getString("bo_ip"), configPort.getInt("bo_port"));
		}
		else if(reponse.getString("message").equals("transfert"))//L'utilisateur veut transférer un bateau
		{
			Bateau b = infosPort.getBateau(reponse.getInt("id"));
			JSONObject tcp = new JSONObject();
			tcp.put("bateau", b.toJSON());
			tcp.put("message", "transfert");
			tcp.put("new_place", reponse.getString("new_place"));
			tcp.put("code", "10");
			envoiTCP(tcp, reponse.getInt("port_tcp"), reponse.getString("ip"), infosPort);
			refresh.put("message", "transfertBateau");
			refresh.put("bateau", reponse.get("id"));
			refresh.put("old_port", reponse.getString("old_port"));
			refresh.put("new_port", reponse.getString("new_port"));
			refresh.put("new_place", reponse.getString("new_place"));
	        GestionUDP.sendUDP(refresh, configPort.getString("bo_ip"), configPort.getInt("bo_port"));
		}
	}
	
	
	/*
	 * Méthode qui va créér le JSON de Configuration ou le JSON définissant le port
	 * @param nomFichier le chemin du fichier
	 * @param valeurs Les différentes valeurs du JSON du port
	 * @param prop l'identifiant du proprietaire
	 */
	public static JSONConfig creerJSON(String nomFichier, String[] valeurs, int prop) 
    {
        JSONConfig config = new JSONConfig(nomFichier, true);

        //S'il n'y a pas de valeurs, on créé le fichier de configuration
        if(valeurs == null)
        {
        	Random rand = new Random();
        	
        	//Ports aléatoires
        	String tcp = ""+(rand.nextInt(1000)+2000);
        	String udp = ""+(rand.nextInt(1000)+3000);

        	config.ajouterValeur("ip", "127.0.0.1");
        	config.ajouterValeur("port_udp", udp);
        	config.ajouterValeur("port_tcp", tcp);
        	config.ajouterValeur("autorite_ip", "127.0.0.1");
        	config.ajouterValeur("autorite_port", "9000");
        	config.ajouterValeur("bo_ip", "127.0.0.1");
        	config.ajouterValeur("bo_port", "9001");
        }
        //Si oui, on créé le fichier de port
        else
        {
        	config.ajouterValeur("nom", valeurs[0]);
        	config.ajouterValeur("proprietaire", prop);
        	JSONArray a = new JSONArray();
        	int taille = Integer.parseInt(valeurs[1]);
        	for(int i=0; i< taille; i++)
        	{
        		JSONObject temp = new JSONObject();
        		temp.put("longueur", valeurs[2]);
        		temp.put("Prise", "false");
        		a.put(temp);
        	}
        	config.ajouterTab("Places", a);
        }
        
        config.sauvegarder();
        return config;
    }
	
	/**
	 * Permet d'envoyer un JSON en TCP, utile pour transférer un bateau
	 * @param j le JSON à envoyer
	 * @param portEcoute le port visé
	 * @param adresse l'adresse visée
	 * @param infosPort L'objet représentant le Port courant, utile pour le modifier selon le code reçu
	 */
	public static void envoiTCP(JSONObject j,int portEcoute, String adresse, Port infosPort)
	{
		// Création de la socket
        Socket socket = null;
        try {
            socket = new Socket(adresse, portEcoute);
        } catch(UnknownHostException e) {
            System.err.println("Erreur sur l'hôte : " + e);
            System.exit(-1);
        } catch(IOException e) {
            System.err.println("Création de la socket e impossible : " + e);
            System.exit(-1);
        }
 
        // Association d'un flux d'entrée et de sortie
        BufferedReader input = null;
        PrintWriter output = null;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch(IOException e) {
            System.err.println("Association des flux impossible : " + e);
            System.exit(-1);
        }
 
        int code = 0;
        String message = "";
        JSONObject json = new JSONObject();
        json = j;
        boolean b = false;
        
        // Envoi
        while(b == false)
        {
	        output.println(json.toString());
	        
	        // Lecture
	        try {
	            message = input.readLine();
	        } catch(IOException e) {
	            System.err.println("Erreur lors de la lecture : " + e);
	            System.exit(-1);
	        }
	        
	        JSONObject recu = new JSONObject(message);
        	json = new JSONObject();

	        if(recu.getString("message").equals("transfert")) // Le bateau a été transféré
	        {
	        	Bateau bato = Bateau.fromJSON(j.getJSONObject("bateau"));
	        	infosPort.libererPlace(String.valueOf(bato.getId()));
	 			infosPort.removeBateau(bato.getId());
	        }
	        
	        if(recu.getString("code").equals("10")) //Toutes les actions nécessaires ont été faites, on peut quitter
	        {
	        	b = true;
	        	json.put("message", "ack");
	        	json.put("code", "200");
	            output.println(json.toString()); // On atteste de la bonne réception à l'autre port
	        }
	        
        }

 
        // Fermeture des flux et de la socket
        try {
            input.close();
            output.close();
            socket.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de la fermeture des flux et de la socket : " + e);
            System.exit(-1);
        }
        
	}
	
	/*
	 * Méthode permettant de demander les informations du port, seulement utile afin de gagner de la place dans le main
	 * @return le tableau des informations	
	 */
	private static String[] creerPort()
	{
		String s = new String();
        Scanner scanner = new Scanner(System.in);
        String nomPort = "";
        
		System.out.println("Le nom du port ? (CTRL+C pour arrêter la création du port)");
		while((nomPort += scanner.nextLine()).equals(""))//On empêche une entrée vide
		{
			nomPort = "";
		}
		s += nomPort + ",";

        System.out.println("Le nombre de places de parking ?");
        s+= scanner.nextInt() +",";
        
        System.out.println("La longueur par défaut des places en mètres ? (Vous pourrez modifier sur le portail)");
        s+= scanner.nextInt();
        		            
        String val[] = s.split(",");
        return val;
	}
	
	/*
	 * Méthode gérant la connexion de l'utilisateur à un nouveau port
	 * @return le nom de l'utilisateur
	 */
	private static int connexionUtilisateur()
	{
		
		System.out.println("Vous devez maintenant vous inscrire ou vous connecter afin de pouvoir gérer votre port sur le portail, que choisissez vous ?");
		System.out.println("1) Se connecter");
        System.out.println("2) S'inscrire");
    	
        //On demande une réponse absolue 
        Scanner scanner = new Scanner(System.in);
        boolean b = false;
        int id = 0;
        JSONObject reponse = new JSONObject();
        String connect = "";
        String login = "";
        String mdp ="";

        switch(scanner.nextInt())
        {
        	//A chaque fois, on constitue la requête HTTP
        	case 1:
        		System.out.println("Très bien, quel est votre identifiant ?");
        		connect += "login=";
        		while((login += scanner.nextLine()).equals("")) //On empêche une entrée vide
        		{
        			login = "";
        		}
        		connect += login;
        		connect += "&";
        		System.out.println("Et quel est votre mot de passe ?");
        		connect += "mdp=";
        		while((mdp += scanner.nextLine()).equals("")) //On empêche une entrée vide
        		{
        			mdp = "";
        		}
        		connect += mdp;
        		reponse = ClientHttp.sendData(connect, "login");
        		break;
        	case 2:
        		System.out.println("Très bien, quel est votre identifiant ?");
        		connect += "login=";
        		while((login += scanner.nextLine()).equals(""))
        		{
        			login = "";
        		}
        		connect += login;
        		connect += "&";
        		System.out.println("Et quel est votre mot de passe ?");
        		connect += "mdp=";
        		while((mdp += scanner.nextLine()).equals(""))
        		{
        			mdp = "";
        		}
        		connect += mdp;
        		connect += "&grade=1";
        		reponse = ClientHttp.sendData(connect, "inscription");
        		break;
        	default:
        		break;
        }

        
        
        if(reponse.get("state").toString().equals("true"))
        {
        	id = reponse.getInt("id");
        }
        
        return id;
	}
	
	/*
	 * Méthode permettant à un utilisateur prétendant posséder un port de vérifier son identité
	 * @return l'identifiant de l'utilisateur
	 */
	private static int verifUtilisateur()
	{
		int id = 0;
		System.out.println("Connectez-vous");
    	
        //On demande une réponse absolue 
        Scanner scanner = new Scanner(System.in);
        boolean b = false;
        JSONObject reponse = new JSONObject();
        String connect = "";
        String login = "";
        String mdp= "";

		System.out.println("Quel est votre identifiant ?");
		connect += "login=";
		while((login += scanner.nextLine()).equals(""))
		{
			login = "";
		}
		connect += login;
		connect += "&";
		System.out.println("Et quel est votre mot de passe ?");
		connect += "mdp=";
		while((mdp += scanner.nextLine()).equals(""))
		{
			mdp = "";
		}
		connect += mdp;
		reponse = ClientHttp.sendData(connect, "login");
		
        if(reponse.get("state").toString().equals("true"))
        {
        	id = reponse.getInt("id");
        }
        
        return id;
	}
	
	public static void CreerBateau()
	{
		
	}
}
