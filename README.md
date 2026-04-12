# Projet 2 - Commandes utiles

## Pré-requis

- JDK 21
- Maven 3.9.3+
- Docker & Docker Desktop
- Node.js + npm
- Angular CLI (`npm install -g @angular/cli`)

---

## Back-end (Spring Boot - port 8080)

Se placer dans le dossier `Back-end---Testez-et-am-liorez-une-application-existante/`

### Lancer le back-end

> Docker Desktop doit être démarré au préalable.

```bash
mvn spring-boot:run
```

### Lancer le back-end avec traces (aide au debug)

```bash
mvn spring-boot:run -e
```

### Lancer le back-end suite à un changement de config ou package

```bash
mvn clean compile spring-boot:run
```



### Lancer les tests du back-end

> Docker Desktop doit être démarré au préalable (les tests d'intégration utilisent Testcontainers).

```bash
mvn clean test
```

---

## Front-end (Angular - port 4200)

Se placer dans le dossier `Front-end---Testez-et-am-liorez-une-application-existante/`

### Installer les dépendances

```bash
npm install
```

### Lancer le front-end

```bash
npm start
```

ou

```bash
ng serve
```

L'application est accessible sur `http://localhost:4200`

### Lancer les tests du front-end

```bash
npm test
```

ou

```bash
jest
```

### Lancer les tests en mode watch

```bash
npm run test:watch
```
