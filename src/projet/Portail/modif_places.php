<?php
include_once('includer.php');
$port = $_GET['port'];
$gestionPort = new GestionnairePort();
$current_port = $gestionPort->getPort($port);
$placesDispo = $current_port->getPlacesDispo();
$html = '';

$html .= 'Nouvelle place : <select name="place">';
        foreach ($placesDispo as $value)
        {
            $html .= '<option value='.$value['id_place'].'>Numéro '.$value['id_place'].' de longueur '.$value['longueur'].' : '.$value['prix'].' €</option>';
        }
$html .= '</select>';

echo $html;

?>
