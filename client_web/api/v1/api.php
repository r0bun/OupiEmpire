<?php

    // Handle CORS preflight request (OPTIONS)
    if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
        header("Access-Control-Allow-Origin: http://127.0.0.1:3000");
        header("Access-Control-Allow-Credentials: true");
        header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
        header("Access-Control-Allow-Headers: Content-Type, Authorization");
        http_response_code(200);
    exit();
    }

    header("Access-Control-Allow-Origin: http://127.0.0.1:3000"); // Spécifier l'origine
    header("Access-Control-Allow-Credentials: true"); // Autoriser les cookies de session
    header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
    header("Access-Control-Allow-Headers: Content-Type, Authorization");

    require_once 'Router.php';

    $router = new Router();

    session_start();

    //************************************************************************************************************
    //Login 
    //************************************************************************************************************
    $router->post('/api.php/login', function() {


        error_log("Cookies reçus : " . print_r($_COOKIE, true));
        error_log("Session active : " . session_id());
        error_log("Données de session : " . print_r($_SESSION, true));
        error_log("Session save path: " . session_save_path());


        // Récupérer les données de la requête
        $data = json_decode(file_get_contents('php://input'), true);
        $id = $data['identifiant'];
        $mdp = $data['motDePasse'];

        $config = require_once '../../config.php';

        // Gestion des erreurs
        try {
            // options de connexion
            $options = [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ];
            // Instancier la connexion
            $pdo = new PDO(
                "mysql:host={$config['host']};dbname={$config['dbname']};charset=utf8",
                $config['username'],
                $config['password']
            );

        } catch (PDOException $e) {
            http_response_code(500);
            die("La connexion a échoué: " . $e->getMessage());
        }

        $query = $pdo->prepare("SELECT mdp FROM usagers WHERE identifiant = :id");
        $query->execute(['id' => $id]);
        $utilisateur = $query->fetch();

        if ($utilisateur && password_verify($mdp, $utilisateur['mdp'])) {
            $_SESSION['user_id'] = $id;
            $_SESSION['mdp'] = $mdp;
            error_log("Session set: " . print_r($_SESSION, true)); // Debugging line to check session data
            echo json_encode(['success' => true]);
        } else {
            http_response_code(401);
            echo json_encode(['success' => false]);
        }
        
    });

    //************************************************************************************************************
    //Inscription
    //************************************************************************************************************
    $router->post('/api.php/register', function() {

        // Récupérer les données de la requête
        $data = json_decode(file_get_contents('php://input'), true);
        $id = $data['identifiant'];
        $mdp = $data['motDePasse'];
        $email = $data['email'];

        $config = require_once '../../config.php';

        // Gestion des erreurs
        try {
            // options de connexion
            $options = [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ];
            // Instancier la connexion
            $pdo = new PDO(
                "mysql:host={$config['host']};dbname={$config['dbname']};charset=utf8",
                $config['username'],
                $config['password']
            );

        } catch (PDOException $e) {
            http_response_code(500);
            die("La connexion a échoué: " . $e->getMessage());
        }

        // Hash mdp 
        $mdpHache = password_hash($mdp, PASSWORD_BCRYPT);
        
        // Créer utilisateur
        $query = $pdo->prepare("INSERT INTO usagers (identifiant, email, mdp) VALUES (:id, :email, :mdp)");
        $query->execute(['id' => $id, 'mdp' => $mdpHache, 'email' => $email]);

        http_response_code(201);
        echo json_encode(['success' => true]);
    
    });

    //************************************************************************************************************
    //Vérification de l'existence d'un utilisateur 
    //************************************************************************************************************
    $router->post('/api.php/userCheck/{id}', function($id) {

        $config = require_once '../../config.php';

        // Gestion des erreurs
        try {
            // options de connexion
            $options = [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ];
            // Instancier la connexion
            $pdo = new PDO(
                "mysql:host={$config['host']};dbname={$config['dbname']};charset=utf8",
                $config['username'],
                $config['password']
            );

        } catch (PDOException $e) {
            http_response_code(500);
            die("La connexion a échoué: " . $e->getMessage());
        }

        try {
            $query = $pdo->prepare("SELECT identifiant FROM usagers WHERE identifiant = :id");
            $query->execute(['id' => $id]);
            $user = $query->fetch();
    
            if ($user === false) {
                echo json_encode(['success' => true, 'message' => 'L\'utilisateur n\'existe pas']);
            } else {
                http_response_code(409);
                echo json_encode(['success' => false, 'message' => 'L\'utilisateur existe déjà']);
            }
    
        } catch (PDOException $e) {
            http_response_code(500);
            echo json_encode(['success' => false, 'error' => 'Erreur lors de l\'exécution de la requête: ' . $e->getMessage()]);
        }
    
    });

    //************************************************************************************************************
    //Charger les données d'un utilisateur 
    //************************************************************************************************************
    $router->post('/api.php/loadUser', function(){

        error_log("Session save path: " . session_save_path());
        error_log("Session ID: " . session_id());

        // Debugging line to check session data
        error_log("Session data on loadUser: " . print_r($_SESSION, true));

        if (!isset($_SESSION['user_id'])) {
            error_log("Session user_id not set on loadUser");
            http_response_code(401);
            echo json_encode(["error" => "Utilisateur non authentifié"]);
            return;
        }

        $user_id = $_SESSION['user_id'];

        $config = require_once '../../config.php';

        // Gestion des erreurs
        try {
            // options de connexion
            $options = [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ];
            // Instancier la connexion
            $pdo = new PDO(
                "mysql:host={$config['host']};dbname={$config['dbname']};charset=utf8",
                $config['username'],
                $config['password']
            );

        } catch (PDOException $e) {
            http_response_code(500);
            die("La connexion a échoué: " . $e->getMessage());
        }

        try {
            $query = $pdo->prepare("SELECT identifiant, mdp, email FROM usagers WHERE identifiant = :id");
            $query->execute(['id' => $user_id]);
            $user = $query->fetch(PDO::FETCH_ASSOC);

            if ($user) {
                echo json_encode(["username" => $user['identifiant'], "mdp" => $user['mdp'], "email" => $user['email'], "success" => true]);
            } else {
                http_response_code(404);
                echo json_encode(["error" => "Utilisateur introuvable", "success" => false]);
            }
        } catch (PDOException $e) {
            http_response_code(500);
            echo json_encode(["error" => $e->getMessage()]);
        }
    });


    //************************************************************************************************************
    //Update profil
    //************************************************************************************************************
    $router->post('/api.php/updateUser', function() {

        // Récupérer les données de la requête
        $data = json_decode(file_get_contents('php://input'), true);
        $id = $data['identifiant'];
        $mdp = $data['motDePasse'];

        $config = require_once '../../config.php';

        // Gestion des erreurs
        try {
            // options de connexion
            $options = [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ];
            // Instancier la connexion
            $pdo = new PDO(
                "mysql:host={$config['host']};dbname={$config['dbname']};charset=utf8",
                $config['username'],
                $config['password']
            );

        } catch (PDOException $e) {
            http_response_code(500);
            die("La connexion a échoué: " . $e->getMessage());
        }

        // Hash mdp 
        $mdpHache = password_hash($mdp, PASSWORD_DEFAULT);
        
        // Update utilisateur

        error_log("Session data avant update: " . print_r($_SESSION, true));
        error_log("infos du formulaire: " . print_r($data, true));

        $query = $pdo->prepare("UPDATE usagers SET identifiant = :id, mdp = :mdp WHERE identifiant = :oldId");
        $query->execute(['id' => $id, 'mdp' => $mdpHache, 'oldId' => $_SESSION['user_id']]);

        $_SESSION['user_id'] = $id;
        $_SESSION['mdp'] = $mdpHache;
        error_log("Session data apres update: " . print_r($_SESSION, true));

        http_response_code(201);
        echo json_encode(['success' => true]);
    
    });  

    //************************************************************************************************************
    //Vérification du mdp de l'utilisateur 
    //************************************************************************************************************
    $router->post('/api.php/mdpCheck', function() {

        $data = json_decode(file_get_contents('php://input'), true);
        $mdp = $data['motDePasse'];

        $config = require_once '../../config.php';

        // Gestion des erreurs
        try {
            // options de connexion
            $options = [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ];
            // Instancier la connexion
            $pdo = new PDO(
                "mysql:host={$config['host']};dbname={$config['dbname']};charset=utf8",
                $config['username'],
                $config['password']
            );

        } catch (PDOException $e) {
            http_response_code(500);
            die("La connexion a échoué: " . $e->getMessage());
        }

        try {
            $query = $pdo->prepare("SELECT mdp FROM usagers WHERE identifiant = :id");
            $query->execute(['id' => $_SESSION['user_id']]);
            $user = $query->fetch();

            if ($user && password_verify($mdp, $user['mdp'])) {
                error_log("Session set: " . print_r($_SESSION, true)); // Debugging line to check session data
                echo json_encode(['success' => true]);
            } else {
                http_response_code(401);
                echo json_encode(['success' => false]);
            }
    
        } catch (PDOException $e) {
            http_response_code(500);
            echo json_encode(['success' => false, 'error' => 'Erreur lors de l\'exécution de la requête: ' . $e->getMessage()]);
        }
    
    });

    //************************************************************************************************************
    //Charger les unités de la casèrne 
    //************************************************************************************************************
    $router->post('/api.php/loadUnits', function(){

        error_log("Session save path: " . session_save_path());
        error_log("Session ID: " . session_id());

        // Debugging line to check session data
        error_log("Session data on loadUser: " . print_r($_SESSION, true));

        /*if (!isset($_SESSION['user_id'])) {
            error_log("Session user_id not set on loadUser");
            http_response_code(401);
            echo json_encode(["error" => "Utilisateur non authentifié"]);
            return;
        }*/

        //$user_id = $_SESSION['user_id'];

        $config = require_once '../../config.php';

        // Gestion des erreurs
        try {
            // options de connexion
            $options = [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ];
            // Instancier la connexion
            $pdo = new PDO(
                "mysql:host={$config['host']};dbname={$config['dbname']};charset=utf8",
                $config['username'],
                $config['password']
            );

        } catch (PDOException $e) {
            http_response_code(500);
            die("La connexion a échoué: " . $e->getMessage());
        }

        try {
            $query = $pdo->prepare("SELECT nom, hp, attaque, defense, description FROM units WHERE identifiant = :id");
            $query->execute(['id' => 1]);
            $units = $query->fetchAll(PDO::FETCH_ASSOC);

            if ($units) {
                echo json_encode(["units" => $units, "success" => true]);
            } else {
                http_response_code(404);
                echo json_encode(["error" => "Utilisateur introuvable", "success" => false]);
            }
        } catch (PDOException $e) {
            http_response_code(500);
            echo json_encode(["error" => $e->getMessage()]);
        }
    });

    $router->dispatch($_SERVER['REQUEST_URI'], $_SERVER['REQUEST_METHOD']);
?>

