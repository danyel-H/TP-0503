<?php
include_once('includer.php');
$form = $_GET['form'];
$html = '';
$port = new GestionnairePort();
$current_port = Port::fromJSON($_SESSION['portchoisi']);
$bateau_choisi = Bateau::fromJSON($_SESSION['bateau_choisi']);
$place_bateau = $current_port->getPlaceBateau($bateau_choisi->getId());

if($form == '1')//Cas du détail du bateau
{
    $types = ['Voilier', 'Titanic'];

    $html.= 'Voici les détails du bateau nommé <input type="text" name="nom" class="info" disabled value="'.urldecode($bateau_choisi->getNom()).'"/>';
    $html.= '<input type="hidden" name="purpose" value="changer"/>';
    $html.= '<input type="hidden" name="id" value="'.$bateau_choisi->getId().'"/>';
    $html .= '<br/><br/>Type : <select class="info" disabled name="Type">';
    foreach ($types as $value)
    {
        if($value == $bateau_choisi->getType())
        {
            $html .= '<option selected value="'.$value.'">'.$value.'</option>';
        }
        else
        {
            $html .= '<option value="'.$value.'">'.$value.'</option>';
        }
    }
    $html .= '</select>';
    $html.= '<br/>Taille : <input type="number" name="taille" disabled class="info" value="'.$bateau_choisi->getTaille().'"/>';
    $html.= '<br/>Son port : '.$current_port->getNom();
    $html.= '<br/>Propriétaire : '.$_SESSION['login'];
    $html.= '<br/>Numéro de place : '.$place_bateau['id_place'];
    $html.= '<br/>Prix mensuel : '.$place_bateau['prix'];
    $html.= '<br/>Sa longueur : '.$place_bateau['longueur'];

    $html.= '<br/><br/><button id="bouton_info" onclick="modifierInfos()">Changer une information</button>';
}
else if($form == '2')//Cas où l'utilisateur veut changer la position du bateau
{
    $html .= 'Choisissez le Port et la Place de Parking';
    $html .= '<input type="hidden" value="transfert" name="purpose"/>';
    $html .= '<br/><br/>Port : <select onchange="changePlaces()" id="selectPort" name="port">';
    $html .= '<option value="" disabled selected>Selectionnez le port</option>';
    foreach ($port->getListePorts() as $value)
    {
        if($value->getNom() != $current_port->getNom())
            $html .= '<option value="'.$value->getNom().'">'.$value->getNom().'</option>';
    }
    $html .= '</select>';
    $html .= '<br/><br/><span id="places_dispo"></span>';
    $html .= '<br/><br/><input type="submit" value="Valider"/>';
}
else if($form == '3')//Cas de la suppression
{
    $sender = new Sender("bateau");

    $posts = array(
        'message' => 'supprimer',
        'port' => $current_port->getNom(),
        'id' => $bateau_choisi->getId());

    $sender->setPost($posts);
    $result = json_decode($sender->request());
    $html.= 'BONSOIR';
}

echo $html;

?>
