<?php
include_once('Includer.php');
$page = new Page();
echo $page->createHead('Votre profil');
 ?>
    <div id="bloc_prim">
        <?php
        echo 'Votre identifiant : '.$_SESSION['login'];
        echo '<br/>Vous êtes un ';
        if($_SESSION['grade'] == '2')
        {
            echo 'Propriétaire de bateau';
        }
        else
        {
            echo 'Propriétaire de port';
        }
        ?>
    </div>
</html>
