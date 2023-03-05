# Post Service

-- Requirements

1) Java 17
2) Docker

-- How to run:

1) docker-compose up -> run Kafka
2) java -jar service-discovery-{version}.jar -> run Service Discovery (port: 8761)
3) java -jar api-gateway-{version}.jar -> run API Gateway (port: 3500)
4) java -jar api-gateway-{version}.jar -> run Post Service multiple instance (port: random)

-- Usage REST API:

- Host: localhost
- Port: 3500 (API Gateway port)
- Example requests are added as Postman collection.
- H2 database console is enabled. -> localhost:{ port }/h2
- Swagger is enabled. -> localhost:{ port }/swagger-ui/index.html