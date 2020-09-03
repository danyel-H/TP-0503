<?php

/**
 *Représente les données d'un Bateau
 */
class Bateau implements JsonSerializable
{
    private $nom;
    private $id;
    private $proprietaire;
    private $type;
    private $taille;
    private $cdm;

    function __construct(String $nom, int $id, String $proprietaire, String $type, String $taille, String $cdm)
    {
        $this->nom = $nom;
        $this->id = $id;
        $this->type = $type;
        $this->taille = $taille;
        $this->proprietaire = $proprietaire;
        $this->cdm = $cdm;
    }

    /**
    *Transforme un JSON en objet Bateau
    *@return un Objet bateau
    */
    public static function fromJSON(String $json)
    {
        $tab = json_decode($json,true);
        return new Bateau($tab['nom'],$tab['id'],$tab['proprietaire'],$tab['type'],$tab['taille'], $tab['cdm']);
    }

    /**
    *Génère un JSON à partir du Bateau courant
    *@return le JSON du bateau courant
    */
    public function jsonSerialize()
    {
        $json = array('nom' => $this->nom,'id' => $this->id, 'proprietaire' => $this->proprietaire, 'type' => $this->type, 'taille' => $this->taille, 'cdm' => $this->cdm);
        return $json;
    }

    /*
    *Retourne le nom du Bateau
    */
    public function getNom()
    {
		return $this->nom;
	}

    /*
    *Change le nom du Bateau
    *@param $nom le Nouveau nom
    */
	public function setNom(String $nom)
    {
		$this->nom = $nom;
	}

    /*
    *Retourne l'id du bateau
    */
	public function getId()
    {
		return $this->id;
	}

    /*
    *Change l'id du Bateau
    *@param $id le Nouvel id
    */
	public function setId(int $id)
    {
		$this->id = $id;
	}

    /*
    *Retourne l'id du propriétaire du bateau
    */
	public function getProprietaire()
    {
		return $this->proprietaire;
	}

    /*
    *Change le propriétaire du Bateau
    *@param $proprietaire l'id du nouveau propriétaire'
    */
	public function setProprietaire(String $proprietaire)
    {
		$this->proprietaire = $proprietaire;
	}

    /*
    *Retourne le type du bateau
    */
	public function getType()
    {
		return $this->type;
	}

    /*
    *Change le type du Bateau
    *@param $type le Nouveau type
    */
	public function setType(String $type)
    {
		$this->type = $type;
	}

    /*
    *Retourne la taille du bateau en cm
    */
	public function getTaille()
    {
		return $this->taille;
	}

    /*
    *Change la taille du Bateau
    *@param $taille la nouvelle taille en cm
    */
	public function setTaille(String $taille)
    {
		$this->taille = $taille;
	}
}


?>
