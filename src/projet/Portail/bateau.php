<?php
include_once('Includer.php');
$page = new Page();
$port = new GestionnairePort();
$choix = '';
if(isset($_GET['choix']))
    $choix = $_GET['choix'];

$current_port = Port::fromJSON($_SESSION['portchoisi']);

//Si le formulaire a été envoyé, on traite les données
if(isset($_POST['purpose']) && $port->isOnline($current_port->getNom()))
{
    var_dump($_POST);
    $sender = new Sender("bateau");

    if($_POST['purpose'] == 'creation')//Cas de la création de bateau (choix=1)
    {
    $posts = array(
        'message' => 'create',
        'port' => $current_port->getNom(),
        'nom' => $_POST['nom'],
        'proprietaire' => $_SESSION['id'],
        'place' => $_POST['place'],
        'taille' => $_POST['taille'],
        'type' => $_POST['Type']);
    }
    else if($_POST['purpose'] == 'changer')//Cas de la modif d'infos (choix=2)
    {
        $posts = array(
            'message' => 'changer',
            'id' => $_POST['id'],
            'port' => $current_port->getNom(),
            'nom' => $_POST['nom'],
            'proprietaire' => $_SESSION['id'],
            'taille' => $_POST['taille'],
            'type' => $_POST['Type']);
    }
    else if($_POST['purpose'] == 'transfert')//Cas du transfert de position
    {
        $bateau_choisi = Bateau::fromJSON($_SESSION['bateau_choisi']);
        $place_bateau = $current_port->getPlaceBateau($bateau_choisi->getId());
        $posts = array(
            'message' => 'transfert',
            'id' => $bateau_choisi->getId(),
            'old_port' => $current_port->getNom(),
            'new_port' => $_POST['port'],
            'old_place' => $place_bateau['id_place'],
            'new_place' => $_POST['place']);
    }

    //Vérification de la conformité des données
    $verif = $port->verifSiToutEstOk($posts);
    if($verif)
    {
        $sender->setPost($posts);
        $result = json_decode($sender->request());
        //L'inscription a réussie
        if($result->state == "true")
            header('Location: port.php?port='.$current_port->getNom());
        else
            header('Location: index.php?erreur=1');
    }
    else
        header('Location: index.php?erreur=2');
}

echo $page->createHead('Gérer un bateau');


 ?>
    <div id="bloc_prim">
        <?php
        if($_SESSION['grade'] == 2)
        {
            if(!$port->isOnline($current_port->getNom()))
            {
                echo 'Ce port n\'est pas / plus en ligne';
            }
            else
            {
                if($choix)
                {
                    //Cas où l'utilisateur créé son bateau
                    if($choix == '1')
                    {
                        $placesDispo = $current_port->getPlacesDispo();
                        if(count($placesDispo) != 0 ) {?>
                        <form method="post" action="#">
                            <label for="nom">Nom du bateau :</label>
                            <input type="text" name="nom" id="nom"/><br/>
                            <input type="hidden" name="purpose" value="creation"/>
                            <label for="type">Type du bateau :</label>
                            <select name='Type'>
                                <option value='Voilier'>Voilier</option>
                                <option value='Titanic'>Titanic</option>
                             </select><br/>
                             <label for="place">Choisissez votre place :</label>
                             <select name="place">
                                 <?php
                                     foreach ($placesDispo as $value)
                                     {
                                         echo '<option value='.$value['id_place'].'>Numéro '.$value['id_place'].' de longueur '.$value['longueur'].' : '.$value['prix'].' €</option>';
                                     }
                                 ?>
                             </select><br/>
                            <label for="type">Taille du bateau en cm:</label>
                            <input type="number" name="taille" /><br/><br/>

                            <input type="submit" value="Créer le bateau"/>
                        </form>
                        <?php }
                        else
                        {
                                echo 'Plus de places disponibles dans ce port.';
                        }
                    }
                    else //Choix où l'utilisateur a choisi de gérer un bateau existant
                    {
                        $bateau_choisi = $current_port->getBateau($_GET['bateau']);
                        // On stocke dans un cookie afin de pouvoir y accéder à partir à partir de la page appelée via Ajax
                        $_SESSION['bateau_choisi'] = json_encode($bateau_choisi);

                        echo 'Que faire à propos du bateau nommé '.urldecode($bateau_choisi->getNom()).'?';
                        echo '<br/><br/><button onclick="getForm(1)">Voir les détails</button>';
                        echo '<button onclick="getForm(2)">Changer de port/place</button>';
                        echo '<button onclick="getForm(3)">Supprimer le bateau</button><br/><br/>';
                        echo '<form method="post" id="form_changer">';
                        echo '</form>';

                    }
                }
                else
                {
                    echo 'Vous n\'avez pas accédé à cette page de façon conventionnelle';
                }

            }
        }
        else
        {
            echo 'Vous êtes un propriétaire de port, vous n\'avez rien à faire là voyons';
        }
        ?>
    </div>
</html>
