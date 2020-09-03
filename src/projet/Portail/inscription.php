<?php
include_once('Includer.php');

$page = new Page();
echo $page->createHead('S\'inscrire en tant que propriétaire de bateau');

//Si le formulaire a été envoyé, on traite les données
if (isset($_POST['login']))
{
    $sender = new Sender("inscription");
    $user = new Utilisateur();
    $posts = array(
        'login' => $_POST['login'],
        'mdp' => $_POST['mdp'],
        'grade' => "2");
    //Vérification de la conformité des données
    $verif = $user->verifInscription($posts);
    if($verif)
    {
        $sender->setPost($posts);
        $result = json_decode($sender->request());
        //L'inscription a réussie
        if($result->state == "true")
            header('Location: index.php?register=1');
        else
            header('Location: inscription.php?register=1');
    }
    else
        header('Location: inscription.php?register=0');
}

?>
    <div id="bloc_prim">
        Inscrivez vous
        <form method="post" action="#">
            <label for="login">Identifiant</label><t>
            <input type="text" name="login" id="login"/><br/>
            <label for="login">Mot de passe</label>
            <input type="password" name="mdp" id="mdp"/><br/><br/><br/>
            <input type="submit" value="Se connecter"/>
        </form><br/>
        <?php
            if(isset($_GET['register']))
            {
                if($_GET['register']== '0')
                    echo 'Une erreur a eu lieu, attention au format de vos informations';
                else
                    echo 'Une erreur a eu lieu, l\'identifiant est déjà pris';
            }
        ?>
    </div>
</html>
