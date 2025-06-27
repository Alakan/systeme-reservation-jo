Système de Réservation de Billets – Jeux Olympiques 2024
Un projet full-stack (Spring Boot + React) qui permet aux utilisateurs de réserver et payer leurs billets pour les événements des JO 2024, avec un espace administrateur pour piloter les événements, les utilisateurs et les réservations.

Table des matières
Technologies

Installation & Démarrage 2.1. Prérequis 2.2. Cloner le dépôt & branches 2.3. Configurer la base de données 2.4. Démarrer le backend 2.5. Démarrer le frontend

Fonctionnalités

API – Endpoints clés

Tests

Déploiement

Git & Contribuer

Contacts

Technologies
Backend : Java 17, Spring Boot, Spring Data JPA, Spring Security, JWT, PostgreSQL

Frontend : React, React Router, Context API, Fetch/Axios

Sécurité : JWT (stateless auth), BCrypt pour le hash des mots de passe

Tests : JUnit, Mockito, JaCoCo (couverture)

Outils : Maven, npm, Git/GitHub, Postman

CI/CD & Déploiement : Heroku (API + Front), PostgreSQL Add-on

Installation & Démarrage
Prérequis
JDK 17+

Node.js 16+ & npm

PostgreSQL (en local ou distant)

Git (vérifier git --version)

Cloner le dépôt & branches
bash
git clone https://github.com/Alakan/systeme-reservation-jo.git
cd systeme-reservation-jo
# Travailler sur la branche de dev
git checkout dev
Si vous souhaitez fusionner dev dans main avant de déployer :

bash
# Merge "propre" avec PR ou en CLI :
git checkout main
git pull origin main
git merge --no-ff dev
git push origin main
Configurer la base de données
Créez une base postgres vide.

Dans backend/src/main/resources/application.yml, renseignez vos paramètres :

yaml
spring:
  datasource:
    url: jdbc:postgresql://<HOST>:<PORT>/<DB_NAME>
    username: <USERNAME>
    password: <PASSWORD>
  jpa:
    hibernate:
      ddl-auto: update
Démarrer le backend
bash
cd backend
mvn clean install
mvn spring-boot:run
L’API est alors joignable sur : > http://localhost:8080/

Démarrer le frontend
bash
cd frontend
npm install
npm start
L’interface React s’ouvre sur : > http://localhost:3000/

Fonctionnalités
Auth & Roles : inscription, connexion, JWT stateless, rôles Utilisateur & Administrateur

Événements : listing, filtres, détails ; CRUD pour les admins

Panier : ajout, mise à jour, suppression de billets

Réservations : création, historique “Mes réservations”

Paiement : simulé, payload JSON { "methodePaiement": "CARTE" }

Billets électroniques : téléchargement PDF/QR

Back-office : gestion complète des utilisateurs, réservations et événements

Notifications : (optionnel) email/SMS selon config

API – Endpoints clés
Route	Méthode	Payload / Query	Accès
POST /api/auth/signup	POST	{ email, password, username }	Anonyme
POST /api/auth/login	POST	{ email, password }	Anonyme
GET /api/evenements	GET	—	Public
GET /api/evenements/{id}	GET	—	Public
POST /api/reservations	POST	{ evenement: { id }, nombreBillets }	Authentifié
PUT /api/reservations/{id}/paiement	PUT	**`{ "methodePaiement": "CARTE"	"PAYPAL"	"VIREMENT" }`**	Authentifié
GET /api/reservations/utilisateur	GET	—	Utilisateur
GET /api/admin/reservations	GET	—	Administrateur
GET /api/paiements	GET	—	Admin
POST /api/paiements	POST	{ reservationId, montant, methodePaiement, statut, … }	Auth/ Admin
> Consultez docs/API.pdf pour la liste complète et les schémas de requêtes/réponses.

Tests
Backend
bash
cd backend
mvn test
Unitaires & Intégration : JUnit 5 + Mockito

Rapport de couverture :

backend/target/site/jacoco/index.html
Frontend
bash
cd frontend
npm test
Déploiement
Heroku :

App Backend + PostgreSQL Add-on

App Frontend statique

Mettre à jour main → GitHub → Heroku (via pipeline)

Variables d’environnement configurées dans le dashboard Heroku

Git & Contribuer
Fork le projet

Créez une branche feature/XXX ou bugfix/YYY à partir de dev

Développez & testez localement

Faites un Pull Request dev → main (ou utilisez l’interface GitHub)

Revue de code & merge

Pour synchroniser main avec dev en CLI :

bash
git checkout main
git pull origin main
git merge --no-ff dev
git push origin main
Contacts
Anthony Bastide Email : anthony.bastide@example.com Projet réalisé dans le cadre de la formation Studi.