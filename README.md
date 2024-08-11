# How to start?
1. Clone the repository and build it with Maven
2. Start PG-Vector as Docker container: `sudo docker run -d --name vector_db --restart always -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=vector_store -e PGPASSWORD=postgres --log-opt max-size=10m --log-opt max-file=3 -p 5433:5432 ankane/pgvector:v0.5.1`
3. Configure Azure endpoint and api-key with permission to use Azure OpenAI in `application.properties` file.
4. Start the Spring Boot application. If needed set proxy as VM option in the IDE: `-Dhttp.proxyHost=proxy -Dhttp.proxyPort=8080`
