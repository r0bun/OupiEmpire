document.addEventListener('DOMContentLoaded', function() {

    let formulaire = document.getElementById('formulaire');

    //Bouton se connecter
    formulaire.addEventListener('submit', async function(event) {

        event.preventDefault();

        let identifiant = document.getElementById('identifiant').value;
        let motDePasse = document.getElementById('mot_de_passe').value;

        let resultat = await fetch("http://127.0.0.1/ClientWeb/api/v1/api.php/login", {
            method: 'POST',
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                identifiant: identifiant,
                motDePasse: motDePasse
            })
        });
        let resultatJson = await resultat.json();

        if (resultatJson.success){
            sessionStorage.setItem('identifiant', identifiant);
            sessionStorage.setItem('motDePasse', motDePasse);
            window.location.assign("connected.html");
        } else {
            let p = document.createElement('p');
                let erreursMdp = document.getElementById('erreurs-mdp');
                p.textContent = 'Mot de passe incorrect !';
                p.className = 'msg-erreur-mdp msg-erreur';
                erreursMdp.appendChild(p);
        }
    });

    //Bouton s'inscrire
    let btnInscription = document.getElementById('inscription');

    btnInscription.addEventListener("click", function(){
        console.log("Inscription button clicked");
        window.location.assign("inscription.html");
    });

});