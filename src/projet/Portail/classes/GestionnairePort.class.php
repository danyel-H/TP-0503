<?php

/**
 *Classe servant à intéragir avec les différents ports
 */
class GestionnairePort
{

    function getListePorts()
    {
        $sender = new Sender("port");
        $sender->setPost(array(
            'message' => 'lister'));

        $result = json_decode($sender->request(), true);

        $temp = array();
        foreach($result['ports'] as $value)
        {
            foreach ($value as $key => $valeur)
            {
                if($key == "nom")
                {
                    $port = Port::fromJSON(json_encode($value));
                    array_push($temp, $port);
                }
            }

        }

        return $temp;
    }

    /*
    *Retorune un port
    *@p le nom du port
    */
    function getPort(String $p)
    {
        $port = null;
        foreach ($this->getListePorts() as $ports)
        {
            if($p == $ports->getNom())
            {
                $port = $ports;
            }
        }

        return $port;
    }

    /*
    *Vérifie si le port est en ligne
    *@param $p le nom du port
    */
    function isOnline(String $p)
    {
        $b = false;
        foreach ($this->getListePorts() as $ports)
        {
            if($p == $ports->getNom())
            {
                $b = true;
            }
        }

        return $b;
    }

    /*
    *Récupère les bateaux d'un utilisateur dans un certain port
    *@param $p Le port
    *@param $u l'id de l'utilisateur
    @return la liste des bateaux
    Sert potentiellement plus à rien
    */
    /*function getBateaux(String $p, String $u)
    {
        $sender = new Sender("port");
        $sender->setPost(array(
            'message' => 'bateaux',
            'port' => $p));

        $result = json_decode($sender->request(), true);

        $temp = array();
        if(isset($result['bateaux']))
        {
            foreach($result['bateaux'] as $value)
            {
                foreach ($value as $key => $valeur)
                {
                    if($key == "proprietaire" && $valeur == $u)
                    {
                        $bateau = Bateau::fromJSON($value);
                        array_push($temp, $bateau);
                    }
                }
            }
        }
        return $temp;
    }*/

    /*
    *Retourne la liste des places du port
    *@param $p le nom du port
    *@param $bool détermine s'il faut lister les places disponibles ou non
    */
    function getPlaces(String $p)
    {
        $sender = new Sender("port");
        $sender->setPost(array(
            'message' => 'places',
            'port' => $p));

        $result = json_decode($sender->request(), true);

        $temp = array();
        foreach($result['places'] as $value)
        {
            array_push($temp, $value);
        }

        return $temp;
    }

    /*
    *Retourn un booléen true si la place est disponible
    *@param $p le nom du port
    *@param $id l'id de la place cherchée
    */
    function PlaceDispo(String $p, String $id)
    {
        $b = false;
        foreach($getPlaces($p) as $value)
        {
            if($value['id_place'] == $id)
                $b = true;
        }
        return $b;
    }

    /*
    *Vérifie les données envoyées lors de la création d'un bateau
    *@param posts L'ensemble des données que l'utilisateur a envoyé pour créer le bateau
    */
    function verifSiToutEstOk($posts)
    {
        $b = true;
        var_dump($b);

        //Caractères bannis
        $recherche = array ('"');
        $remp = array ('_');
        foreach ($posts as $key => $value)
        {
            if(!$value)
            {
                $b = false;
            }
            else
            {
                if($key == "nom")
                {
                    if(strlen($value) < 4 || strlen($value) > 30 || str_replace($recherche, $remp, $value) != $value)
                        $b = false;
                }
                else
                {
                    if($key == "Type" && ($key != "Voilier" || $key != "Titanic"))
                    {
                        $b = false;
                    }
                }
            }
        }
        return $b;
    }

}



?>
