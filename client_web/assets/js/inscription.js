document.addEventListener('DOMContentLoaded', function() {

    let formulaire = document.getElementById('formulaire');

    //Bouton s'inscrire
    formulaire.addEventListener('submit', async function(event) {

        event.preventDefault();

        let identifiant = document.getElementById('id').value;
        let motDePasse = document.getElementById('mdp').value;
        let email = document.getElementById('email').value;

        function validerMotDePasse(motDePasse) {
            let messages = [];
            let mdpOk = 1;
        
            // Vérifier la présence d'au moins une majuscule
            if (!/[A-Z]/.test(motDePasse)) {
                messages.push("Le mot de passe doit contenir au moins une majuscule.");
                mdpOk = 0;
            }
        
            // Vérifier la présence d'au moins une minuscule
            if (!/[a-z]/.test(motDePasse)) {
                messages.push("Le mot de passe doit contenir au moins une minuscule.");
                mdpOk = 0;
            }
        
            // Vérifier la présence d'au moins un chiffre
            if (!/\d/.test(motDePasse)) {
                messages.push("Le mot de passe doit contenir au moins un chiffre.");
                mdpOk = 0;
            }
        
            // Vérifier la longueur du mot de passe
            if (motDePasse.length < 8 || motDePasse.length > 32) {
                messages.push("Le mot de passe doit contenir entre 8 et 32 caractères.");
                mdpOk = 0;
            }
        
            // Retourner un objet avec les résultats de la validation
            return {
                mdpOk: mdpOk,
                messages: messages
            };
        }

        let mdpValide = validerMotDePasse(motDePasse);

        if (mdpValide.mdpOk === 0){
            
            $('.msg-erreur-mdp').remove();

            // Afficher les messages d'erreur dans le div
            mdpValide.messages.forEach(function(message) {
                let p = document.createElement('p');
                let erreursMdp = document.getElementById('erreurs-mdp');
                p.textContent = message;
                p.className = 'msg-erreur-mdp msg-erreur';
                erreursMdp.appendChild(p);
            });
            return;
        }
        
        let requete = await fetch("http://127.0.0.1/ClientWeb/api/v1/api.php/userCheck/" + identifiant, {
            method: 'POST',
            credentials: "include"
        });
        let requeteJson = await requete.json();

        if (requeteJson.success == false){
            let p = document.createElement('p');
            let erreursMdp = document.getElementById('erreurs-mdp');
            p.textContent = "Nom d'utilisateur indisponible !";
            p.className = 'msg-erreur-mdp msg-erreur';
            erreursMdp.appendChild(p);
            return;

        } else if (requeteJson.success == true){
            try {
                let resultat = await fetch("http://127.0.0.1/ClientWeb/api/v1/api.php/register", {
                    method: 'POST',
                    credentials: "include",
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        identifiant: identifiant,
                        motDePasse: motDePasse,
                        email: email
                    })
                });

                if (!resultat.ok) {
                    throw new Error('Erreur lors de l\'inscription');
                }

                window.location.assign("index.html");
            } catch (error) {
                let messageErreur = document.getElementById('message-erreur');
                messageErreur.style.display = 'block';
                messageErreur.innerHTML = "Erreur lors de l'inscription. Veuillez réessayer.";
            }
        }
    });

});