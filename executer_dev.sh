mvn clean install -DskipTests

sudo docker-compose -f docker-compose-dev.yml up -d --build

sudo docker-compose -f docker-compose-dev.yml up