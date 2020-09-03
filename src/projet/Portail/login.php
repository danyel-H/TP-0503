<?php
include_once('Includer.php');

$page = new Page();
$user = new Utilisateur();
echo $page->createHead('Se connecter');

if (isset($_POST['login']))
{
    $sender = new Sender("login");
    $posts = array(
        'login' => $_POST['login'],
        'mdp' => $_POST['mdp']);

    $verif = $user->verifInscription($posts);
    if($verif)
    {
        $sender->setPost($posts);
        $result = json_decode($sender->request());
        if($result->state == "true")
        {
            $_SESSION['connect'] = true;
            $_SESSION['id'] = $result->id;
            $_SESSION['login'] = $result->login;
            $_SESSION['grade'] = $result->grade;
            header('Location: index.php');
        }
        else {
            header('Location: login.php?login=0');
        }
    }
    else
        header('Location: inscription.php?login=1');
}

?>
    <div id="bloc_prim">
        Connectez-vous
        <form method="post" action="#">
            <label for="login">Identifiant</label><t>
            <input type="text" name="login" id="login"/><br/>
            <label for="login">Mot de passe</label>
            <input type="password" name="mdp" id="mdp"/><br/><br/>
            <input type="submit" value="Se connecter"/>
        </form><br/>
        <?php
        if(isset($_GET['register']))
        {
            if($_GET['register'] == '1')
                echo 'Une erreur a eu lieu, attention au format de vos informations';
            else
                echo 'Une erreur a eu lieu, vos informations sont fausses';
        }
        ?>
    </div>
</html>
