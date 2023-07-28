mvn clean install -DskipTests

sudo docker-compose -f docker-compose-prod.yml up -d --build

sudo docker-compose -f docker-compose-prod.yml up