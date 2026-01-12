#!/bin/bash

# 1. Liste des dossiers (vérifiés d'après ton 'ls')
SERVICES=("configServer" "EurekaServer" "user-microservice" "book-microservice" "emprunttService" "gateWayService")

echo "--- 🛠️  COMPILATION DES PROJETS JAVA ---"
for service in "${SERVICES[@]}"
do
    echo "📦 Packaging $service..."
    cd $service
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "❌ Échec de la compilation pour $service. Arrêt du script."
        exit 1
    fi
    cd ..
done

echo "--- 🐳 DÉPLOYEMENT DOCKER ---"
# On arrête les anciens conteneurs s'ils tournent
docker-compose down

# On lance le build des images et le démarrage des conteneurs en arrière-plan
docker-compose up --build -d

echo "--- ✅ TOUT EST LANCÉ ! ---"
echo "Attends 30 secondes que les services s'enregistrent..."
