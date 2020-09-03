<?php
/**
*Se charge d'envoyer à un serveur HTTP
 *@author Danyel Hocquigny
 *@date 29/11/2018
 */

class Sender
{
    private $cible;
    private $vars;

    /*
    *@param $c la cible (l'adresse du contexte qui heberge le handler)
    */
    function __construct(String $c)
    {
        $this->cible = $c;
    }

    /*
    *Fixe les variables à envoyer
    *@param $post les variables sous form de tableau
    */
    function setPost($post)
    {
        $this->vars = $post;
    }

    /*
    *Permet d'envoyer la requête à la cible
    */
    function request()
    {
    $post = http_build_query(
        $this->vars
    );

    $opts = array('http' =>
        array(
            'method'  => "POST",
            'header'  => "Content-Type: text/html; charset=utf-8",
            'content' => $post
        )
    );

    $context = stream_context_create($opts);

    $response = file_get_contents('http://localhost:8082/'.$this->cible.'.html', false, $context);

    return $response;
    }
}


?>
