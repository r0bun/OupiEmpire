
# 🏰 Oupi Empire

**Oupi Empire** est un jeu de stratégie développé en Java avec une interface web complémentaire. Le client web permet aux utilisateurs de consulter des statistiques ou des données liées au jeu, en se connectant à un serveur local via XAMPP.

## 📁 Structure du projet

```
/
├── .git
├── .gitignore
├── .metadata
├── oupi_empire         # Projet Java (jeu)
└── client_web          # Client web compatible avec XAMPP
```

## 🚀 Lancer le projet

### 1. Partie Java (Jeu)

1. Ouvre le projet `oupi_empire` dans Eclipse (ou tout autre IDE Java).
2. Assure-toi que le JDK est bien configuré dans les paramètres du projet.
3. Lance la classe principale pour démarrer le jeu.

### 2. Partie Web (client_web)

Le dossier `client_web` contient les fichiers web à héberger via un serveur Apache (XAMPP).

#### 🔧 Configuration de XAMPP

1. Installe [XAMPP](https://www.apachefriends.org/fr/index.html) si ce n’est pas déjà fait.
2. Ouvre le fichier de configuration `httpd.conf`. Il se trouve généralement ici :
   ```
   C:\xampp\apache\conf\httpd.conf
   ```

3. Repère cette ligne :
   ```apache
   DocumentRoot "C:/xampp/htdocs"
   ```
   Et remplace-la par :
   ```apache
   DocumentRoot "C:/chemin/vers/le/projet/client_web"
   ```

4. Juste en dessous, modifie aussi ce bloc :
   ```apache
   <Directory "C:/xampp/htdocs">
   ```
   en :
   ```apache
   <Directory "C:/chemin/vers/le/projet/client_web">
   ```

5. Redémarre Apache via le panneau de contrôle XAMPP.

#### 🌐 Accéder au client web

Ouvre ton navigateur à l’adresse suivante :

```
http://localhost/
```

Tu devrais voir l’interface du client web.

---

## ❗ Remarques

- Assure-toi que les ports 80 (Apache) et 3306 (MySQL) ne sont pas utilisés par d'autres services.
- Pour toute modification du côté serveur, relance Apache pour appliquer les changements.

## 🛠 Technologies utilisées

- Java (JDK 11+ recommandé)
- HTML/CSS/JS (client web)
- XAMPP (Apache + MySQL)
