# Utilisez une image Maven officielle avec JDK 17 sur Alpine Linux
DEPUIS maven : 3.8.4-openjdk-17-slim AS build

# Définissez le répertoire de travail sur /app
RÉPERT TRAVAIL /app

# Copiez le code source
COPIE . .

# Construire l'application
EXÉCUTER mvn clean install -DskipTests

# Créez une nouvelle image avec le fichier JAR
DE openjdk:17-jdk-alpine

# Définissez le répertoire de travail sur /app
RÉPERT TRAVAIL /app

# Copiez le fichier JAR depuis l'image de build
COPIER --from=build /app/target/BeeOranized-0.0.1-SNAPSHOT.jar /app/BeeOranized-0.0.1-SNAPSHOT.jar

# Exposer le port 9999
EXPOSER 9999

# Définir la variable d'environnement
ENV JAVA_OPTS=""

# Exécutez l'application Java
POINT D'ENTRÉE ["sh", "-c", "java $JAVA_OPTS -jar /app/BeeOranized-0.0.1-SNAPSHOT.jar"]
