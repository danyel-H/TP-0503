//Gestion d'AJAX
var requeteHTTP = new XMLHttpRequest(); // Création de l'objet
requeteHTTP.onloadend = handler;        // Spécification de l'handler à exécuter
var usage = 1                           //Usage de l'ajax

// Fonction pour récupérer le formulaire adapté
function getForm(id)
{
    usage = 1;
    if(id == '3')
    {
        usage = 3;
    }
    var URL = "modif_bateau.php?form="+id;
    requeteHTTP.open("GET", URL, true);
    requeteHTTP.send();
}

//Fonction permettant de changer les places selon le port choisi
function changePlaces()
{
    usage = 2;
    elem_port = document.getElementById("selectPort");
    port = elem_port.options[elem_port.selectedIndex].value;
    var URL = "modif_places.php?port="+port;
    requeteHTTP.open("GET", URL, true);
    requeteHTTP.send();
}

function handler()
{
    if((requeteHTTP.readyState == 4) && (requeteHTTP.status == 200))
    {
        if(usage == 1)
            document.getElementById("form_changer").innerHTML = requeteHTTP.responseText;
        else if(usage == 2)
            document.getElementById("places_dispo").innerHTML = requeteHTTP.responseText;
        else
            window.location.href = "port.php";
    }
    else
    {
        alert("Erreur lors de la requête AJaX.");
    }
}

/*
*Fonction permettant à l'utilisateur de modifier les infos d'un Port en libérant les inputs
*/
function modifierInfos()
{
    var inputs = document.getElementsByClassName('info');

    for(var i=0; i< inputs.length; i++)
    {
        inputs[i].disabled = false;
    }

    var submit = document.createElement("input");
    var retourChariot = document.createElement("br");
    submit.type = "submit";
    submit.value = "Confirmer";

    document.getElementById('form_changer').append(retourChariot);
    document.getElementById('form_changer').append(submit);

    var bouton = document.getElementById('bouton_info');
    bouton.parentNode.removeChild(bouton);
}
