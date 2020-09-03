<?php
include_once('Includer.php');
$page = new Page();
echo $page->createHead('Gérer vos possessions');

 ?>
    <div id="bloc_prim">
        <?php

        if($_SESSION['grade'] == 2)
        {
            echo '<h2> Regarder les bateaux dans un port connecté en particulier </h2>';
            $port = new GestionnairePort();
            foreach ($port->getListePorts() as $ports)
            {
                echo '<a href="port.php?port='.$ports->getNom().'">';
                echo $ports->getNom();
                echo '</a>';
                echo '<br/>';
            }
        }
        else
        {
            echo 'Vous êtes un propriétaire de port';
        }
        ?>
    </div>
</html>
