## Product Microservices in Spring Boot 3.3.0 / Gradle 8.8

Featuring microservices architecture, the walk aims to build an end-to-end Product Service supported by modern stacks: Spring Boot, Gradle, Docker, Kubernetes, monitoring and logging tools which I'll list below.
This repo contains codebase for the Product Service comprising:

* `api`  the library that defines interfaces and plain old Java objects that the Product Service implements in the concrete services
* `util` this library defines protocol information such as Services Addresses and error handling classes
* `product-service` this is the core microservice features the Product itself
* `recommendation-service` entails recommendations related to the Product
* `review-service` serves reviews given by authors related to the Product
* `product-composite-service` aggregates the three microservices above such that they sit behind this service. The composite makes the actual communication to the microservices and delivers an aggregate response to the requesting client

Created Aug 9, 2024

##### Aug 14, 2024
* Updated ServiceUtil to give the correct hostname instead of IP address of microservice instance
* Created Dockerfile for each microservice that will build image in layers that are cacheable. Here, we explode uber jar and organize the running of the jar file
`org.springframework.boot.loader.launch.JarLauncher`
* Created Docker Compose yml script to start and tear down the microservice landscape
* Updated the test script to start the landscape for testing and tear down the landscape after tests
`./test-em-all.bash start stop`

##### Aug 19, 2024
* Added API Documentation
Build and start the landscape:


`./gradlew build && docker compose build && docker compose up -d`


The doc will be accessible via [http://localhost:8080/openapi/swagger-ui.html](http://localhost:8080/openapi/swagger-ui.html)


##### Continuation:
This documentation is continued in [the README](https://github.com/david-matu/Microservices-Diary/blob/main/README.md) file of the __Microservices-Diary__ documentation project.

##### Nov 3, 2024
* Added message processors (consumers) for Recommendation and Review microservices. 
    
    Messages can be seen in the RabbitMQ at:
    [http://localhost:15672](http://localhost:15672/)

    Username and password: ``guest``/``guest``
    
* Added Health API in the composite
    * Added as a bean that calls the core services health endpoints:
    * Access via [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
    
    * For the core services:
        - Product: [http://localhost:7001/actuator/health](http://localhost:7001/actuator/health)
        - Recommendation: [http://localhost:7002/actuator/health](http://localhost:7002/actuator/health)
        - Review: [http://localhost:7003/actuator/health](http://localhost:7003/actuator/health)



