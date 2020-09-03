<?php
include_once('Includer.php');
$page = new Page();
echo $page->createHead('Bienvenue sur le portail !');
if(isset($_GET['deconnexion']))
{
    $user = new Utilisateur();
    $user->deconnect();
}
 ?>
    <div id="bloc_prim">
        <?php
        $html = '';
        if(!isset($_SESSION['connect']))
        {
            $html .= '
            Que voulez vous faire ?<br/> <br/>
            <a href="login.php">Se connecter</a><br />
            <a href="inscription.php">S\'inscrire</a>
            </div>';
        }
        else
        {
            $html .= 'Bienvenue '.$_SESSION['login'].' !';
            $html .= 'Que voulez vous faire ?<br/> <br/>';
            $html .= '<a href="profil.php"><div class="bloc_sec">Voir mon profil</div></a>';
            $html .= '<a href=".?deconnexion=1"><div class="bloc_sec">Se déconnecter</div></a>';
            $html .= '<a href="gestion.php">';
            if($_SESSION['grade'] == 1)
            {
                $html .= '<div class="bloc_sec">Voir mon port</div>';
            }
            else {
                $html .= '<div class="bloc_sec">Voir mes bateau</div></a>';
            }
            $html .= '</a>';
        }
        echo $html;

        ?>
    </div>
</html>
