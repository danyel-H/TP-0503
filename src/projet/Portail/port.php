<?php
include_once('Includer.php');
$page = new Page();
$getPort = '';
if(isset($_GET['port']))
    $getPort = $_GET['port'];
else
{
    if(isset($_SESSION['portchoisi']))
    {
        $temp = Port::fromJSON($_SESSION['portchoisi']);
        $getPort = $temp->getNom();
    }
}

echo $page->createHead('Vos bateaux dans le port de '.$getPort);

 ?>
    <div id="bloc_prim">
        <?php

        if($_SESSION['grade'] == 2)
        {
            $port = new GestionnairePort();
            if(!$port->isOnline($getPort))
            {
                echo 'Ce port n\'est pas / plus en ligne';
            }
            else
            {
                $_SESSION['portchoisi'] = json_encode($port->getPort($getPort));
                $current_port = Port::fromJSON($_SESSION['portchoisi']);

                echo '<button onclick="window.location.href=\'bateau.php?choix=1\'">Créer un bateau dans ce port</button><br/>';
                echo '<b>Vos bateaux dans ce port :</b><br/>(Cliquez sur le nom du bateau afin de le gérer)<br/>Rafraichissez si votre bateau n\'est pas encore disponible<br/><br/>';
                foreach($current_port->getMesBateaux($_SESSION['id']) as $bateau)
                {
                    echo '<a href="bateau.php?choix=2&bateau='.$bateau->getId().'">'.urldecode($bateau->getNom()).'</a> qui est un bateau de type '.$bateau->getType();
                    echo '<br/>';
                }

            }
        }
        else if($_SESSION['grade'] == 1)
        {
            echo 'Vous êtes un propriétaire de bateau';
        }
        else
        {
            header('Location: .');
        }

        ?>
    </div>
</html>
