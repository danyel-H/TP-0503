<?php

/**
 * Classe servant à générer différents élémnents d'une page
 */
class Page
{

    /*
    *Fonction créant le head et le header de la page
    *@param $t Le texte affiché dans le header
    */
    function createHead(String $t)
    {
        $html ='';
        $html .= '<!DOCTYPE html><html>';
        $html .= '<head>';
        $html .= '<title>Application pour bateaux</title>';
		$html .= '<meta http-equiv="content-type" content="text/html; charset=UTF-8">';
		$html .= '<link href="css/main.css" rel="stylesheet" type="text/css">';
        $html .= '<script src="js/bateau.js"></script>';
        $html .= '</head>';
        $html .= '<header><a href="."><div style="float:left;">Home</div></a>';
        $html .= '<span style="float:left; margin-left:15px;" onclick="history.back()">Précédent</span><br/>'.$t.'</header>';

        return $html;
    }

    /*
    *Fonction permettant de générer un Footer
    */
    function createFooter()
    {
        $html = '';
        $html .= '<footer></footer>';

        session_destroy();
        return $html;
    }
}


?>
