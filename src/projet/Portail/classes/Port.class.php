<?php

/**
 *Représente les données d'un port
 */
class Port implements JsonSerializable
{
    private $nom;
	private $ip;
	private $places;
	private $bateaux;
    private $nbPlaces;

    function __construct(String $nom, String $ip, array $places, array $bateaux)
    {
        $this->nom = $nom;
        $this->ip = $ip;
        $this->bateaux = $bateaux;
        $this->nbPlaces = count($places);

        //On donne des informations aux places nécessaires aux traitements du portail
        $temp = array();
        for($i = 0; $i < $this->nbPlaces; $i++)
        {
            $temp[$i]['id_place'] = $i+1;
            $temp[$i]['longueur'] = $places[$i]['longueur'];
            $temp[$i]['Prise'] = $places[$i]['Prise'];
            $temp[$i]['prix'] = $places[$i]['longueur']*1.5;
        }

        $this->places = $temp;
    }

    /*
    *Génère un nouveau port à partir d'un JSON
    *@param $json JSON du port à générer
    */
    public static function fromJSON(String $json)
    {
        $tab = json_decode($json,true);
        return new Port($tab['nom'],$tab['ip'],$tab['Places'],$tab['bateaux']);
    }

    /*
    *Génère le JSON du port courant
    */
    public function jsonSerialize()
    {
        $json = array('nom' => $this->nom,'ip' => $this->ip, 'Places' => $this->places, 'bateaux' => $this->bateaux);
        return $json;
    }

    /*
    *Récupère la place du bateau selon son identifiant
    *@param $bateau l'id du bateau
    */
    function getPlaceBateau(String $bateau)
    {
        $place = null;
        foreach ($this->getPlaces() as $value)
        {
            if($value['Prise'] == $bateau)
            {
                $place = $value;
            }
        }
        return $place;
    }

    /*
    *Récupère les bateaux d'un utilisateur du port
    *@param $u l'id de l'utilisateur
    @return la liste des bateaux
    */
    function getMesBateaux(String $u)
    {
        $bateaux = array();
        foreach($this->bateaux as $value)
        {
            $new_value = Bateau::fromJSON(json_encode($value,true));
            if($new_value->getProprietaire() == $u)
            {
                array_push($bateaux,$new_value);
            }
        }
        return $bateaux;
    }

    /*
    *Retoure un bateau selon son id
    *@param $id l'id du bateau nécessaire
    *@return le bateau en question, s'il est trouvé
    */
    function getBateau(int $id)
    {
        $bateau = null;
        foreach($this->getMesBateaux($_SESSION['id']) as $value)
        {
            if($value->getId() == $id)
            {
                $bateau = $value;
            }
        }
        return $bateau;
    }

    //Tous les getters et Setters
    public function getNom()
    {
        return $this->nom;
    }

    public function setNom(String $nom)
    {
        $this->nom = $nom;
    }

    public function getIp()
    {
        return $this->ip;
    }

    public function setIp(String $ip)
    {
        $this->ip = $ip;
    }

    public function getPlaces()
    {
        return $this->places;
    }

    public function setPlaces(array $places)
    {
        $this->places = $places;
    }

    public function getBateaux()
    {
        return $this->bateaux;
    }

    public function setBateaux(array $bateaux)
    {
        $this->bateaux = $bateaux;
    }

    /*
    *Retourne les places dont l'attribut 'Prise' est à false
    */
    public function getPlacesDispo()
    {
        $places = $this->getPlaces();
        $temp = array();
        foreach($places as $value)
        {
            if($value['Prise'] == 'false')
            {
                array_push($temp, $value);
            }
        }

        return $temp;
    }

}


?>
