<?php

/**
 *
 */
class Utilisateur
{

    /*
    *Déconnecte l'utilisateur en effaçant ses variables de sessions
    */
    function deconnect()
    {
        unset($_SESSION['connect']);
        unset($_SESSION['login']);
        unset($_SESSION['grade']);
        unset($_SESSION['id']);
    }

    /*
    *Vérifie les données de l'Utilisateur lors de son inscription
    *@param posts L'ensemble des données que l'utilisateur a envoyé pour s'inscrire
    */
    function verifInscription($posts)
    {
        $b = true;
        //Caractères bannis
        $recherche = array ('"',' ');
        $remp = array ('_');
        foreach ($posts as $key => $value)
        {
            if($key == "login" || $key == "mdp")
            {
                if(strlen($value) < 4 || strlen($value) > 30 || str_replace($recherche, $remp, $value) != $value)
                    $b = false;
            }
            else
            {
                if($value != '1' && $value != '2')
                    $b = false;
            }
        }
        return $b;
    }

}



?>
