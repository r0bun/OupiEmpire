document.addEventListener("DOMContentLoaded", function() {
    loadProfile(); // Charge les données utilisateur au chargement
    
// Fonction pour charger les données utilisateur
async function loadProfile() {
    let resultat = await fetch("http://127.0.0.1/ClientWeb/api/v1/api.php/loadUnits", {
        method: "POST",
        credentials: "include", 
        headers: {
            "Content-Type": "application/json"
        }
    });

    let resultatJson = await resultat.json();
    console.log(resultatJson);

    if (resultatJson.success) {
        displayUnits(resultatJson.units); // Affiche les unités
    } else {
        console.error("Erreur de chargement :", resultatJson.error);
    }
}

function getUnitImage(unitName) {
    switch (unitName) {
        case 'Soldat':
            return 'assets/images/Lobotomisateur.jpg';
        case 'Archer':
            return 'assets/images/oupi goupi.jpg';
        // Add more cases for other unit names
        default:
            return 'assets/images/default.jpg'; 
    }
}

function displayUnits(units) {
    let unitList = document.querySelector('.unit-list');
    unitList.innerHTML = ''; 

    units.forEach(unit => {
        let unitContainer = document.createElement('div');
        unitContainer.className = 'unit-container';

        let unitImage = document.createElement('img');
        unitImage.className = 'unit-image';
        unitImage.src = getUnitImage(unit.nom);
        unitImage.alt = unit.nom;

        let unitDetails = document.createElement('div');
        unitDetails.className = 'unit-details';

        let unitTitle = document.createElement('h2');
        unitTitle.className = 'unit-title';
        unitTitle.textContent = unit.nom;

        let unitDescription = document.createElement('p');
        unitDescription.className = 'unit-description';
        unitDescription.textContent = unit.description || 'Description non disponible.';

        let unitStats = document.createElement('p');
        unitStats.className = 'unit-stats';
        unitStats.textContent = `Statistiques : Attaque ${unit.attaque}, Défense ${unit.defense}, PV ${unit.hp}`;

        unitDetails.appendChild(unitTitle);
        unitDetails.appendChild(unitDescription);
        unitDetails.appendChild(unitStats);

        unitContainer.appendChild(unitImage);
        unitContainer.appendChild(unitDetails);

        unitList.appendChild(unitContainer);
    });
}
});