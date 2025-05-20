# Système de Réservation de Billets - Jeux Olympiques 2024

## 1. Introduction

Ce projet est un système de réservation complet permettant aux utilisateurs d’acheter des billets pour les événements des Jeux Olympiques 2024. 
Il assure une gestion sécurisée des réservations, des paiements et des billets électroniques, tout en offrant un espace d'administration pour la gestion des événements et des utilisateurs.

## 2. Technologies Utilisées

* **Backend :** Java 17, Spring Boot, Spring Data JPA, Spring Security, JWT (JSON Web Tokens), PostgreSQL 
* **Frontend :** React, JavaScript
* **Sécurité :** Spring Security, JWT, BCrypt (pour le hachage des mots de passe) 
* **Gestion de Projet :** Trello (méthode Kanban) 
* **Outils de Développement :** Maven, Git/GitHub, Postman
* **Déploiement :** Heroku (pour le backend, le frontend et la base de données)

## 3. Installation et Démarrage

### 3.1 Prérequis

Assurez-vous d'avoir les éléments suivants installés sur votre machine :

* Java Development Kit (JDK) 17 ou supérieur
* Node.js et npm (Node Package Manager)
* Un serveur PostgreSQL en cours d'exécution (ou un accès à une instance PostgreSQL distante).
* Git (vérification avec `git --version`)

### 3.2 Cloner le Projet

bash
git clone [https://github.com/Alakan/systeme-reservation-jo.git](https://github.com/Alakan/systeme-reservation-jo.git)
cd systeme-reservation-jo

3.3 Démarrer le Backend (API Spring Boot)

Naviguez dans le répertoire du backend et exécutez les commandes suivantes :

cd backend
mvn clean install
mvn spring-boot:run

L'API Spring Boot sera accessible localement à l'adresse suivante : http://localhost:8080.

### 3.4 Démarrer le Frontend (Application React)

Naviguez dans le répertoire du frontend et exécutez les commandes suivantes :

cd frontend
npm install
npm start

L'application frontend sera accessible localement à l'adresse suivante : http://localhost:3000 (par défaut pour React).

### 4. Fonctionnalités Principales

Authentification et Gestion de Compte : Création de compte, connexion sécurisée avec JWT, gestion des rôles (Utilisateur/Administrateur).

Gestion des Événements : Consultation, recherche, filtrage  et détails des événements sportifs. Les administrateurs peuvent ajouter, modifier et supprimer des événements.

Réservation et Paiement : Sélection et réservation de billets par type et quantité, processus de paiement en ligne sécurisé  (via mock pour l'examen, pré-intégration Stripe/PayPal).

Gestion des Billets Électroniques : Téléchargement, impression  et réception des billets au format PDF/QR code.

Espace Administrateur : Supervision des réservations et paiements, gestion complète des utilisateurs, des billets  et des événements.

Notifications et Support : Notifications par email/SMS pour les changements importants, accès à un support client.

### 5. Déploiement en Ligne

L'application est déployée publiquement pour faciliter l'accès et les tests :

Lien Application Déployée (Frontend) : [URL FRONTEND A INSÉRER]
Lien API Backend Déployée : [URL FRONTEND A INSÉRER] 
Base de Données : PostgreSQL hébergée sur Heroku (intégrée au déploiement du backend).

### 6. Documentation

Retrouvez la documentation complète du projet dans le dossier docs/ et via les liens externes :

Tableau Kanban (Gestion de Projet) : https://trello.com/b/sRr3NmYA/systeme-de-reservation-jo-2024 
Manuel Utilisateur : docs/manuel_utilisateur.pdf
Modèle Conceptuel des Données (MCD) : docs/MCD.pdf
Documentation API et Endpoints : docs/API.pdf (Ce document détaillera les routes de l'API REST, les requêtes et réponses attendues).
Documentation Technique (Sécurité et Évolutions Futures) : docs/documentation_technique.pdf (Ce document contient les informations de sécurité et les évolutions futures prévues pour l'application).

### 7. Tests et Validation

### 7.1 Tests Backend

Les tests unitaires et d’intégration sont réalisés avec JUnit et Mockito pour garantir la fiabilité et la robustesse de l'API.

Pour exécuter les tests et générer le rapport de couverture de code (via JaCoCo):

cd backend
mvn clean install

Le rapport de couverture de code est ensuite généré et disponible localement à l'adresse suivante : backend/target/site/jacoco/index.html.
(Note : Nous visons une couverture de code significative pour les composants critiques du backend).

### 8. Contact et Support

Pour toute question, suggestion d'amélioration ou problème rencontré, n'hésitez pas à soumettre une issue sur ce dépôt GitHub ou à me contacter directement :

Anthony Bastide
Email : [Votre.Email@example.com]


Ce projet a été développé dans le cadre de ma formation Studi.
