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


##### Dec 14, 2024
In this release edition, I have covered 
* _Service Discovery_ with __Netflix Eureka__, 
* use of _Edge Server_ using __Spring Cloud Gateway__ to hide the microservices behind and configured routing rules,
* API security with custom OIDC-compliant _authorization server_ with __Spring Authorization Server__ in the internal landscape of microservices, then concluded with __Auth0__ as the authorization server outside of the landscape.


Focusing on the latter approach to API security, you head to Auth0 and create an account. Then create an Auth0 Management API. You get a client ID and Secret. Create a test user as well.

In the file __env.bash__ in ```auth0``` directory, map the details from Auth0, then execute the file __setup-tenant.bash__ in terminal. The details you get will be applicable in the parameters for getting the token in below sections.

* Fetch token in either ways:
    - __Client Credentials Grant__
```cmd
export TENANT=unrevealed-tenant-create-yours-at.us.auth0.com
export WRITER_CLIENT_ID=xg6jwriter9client9id9ja41jCr
export WRITER_CLIENT_SECRET=PKmE2writer9client9secret9UzIUCQMN3OImUMJ
curl -X POST https://$TENANT/oauth/token \
-d grant_type=client_credentials \
-d audience=https://localhost:8443/product-composite \
-d scope=product:read+product:write \
-d client_id=$WRITER_CLIENT_ID \
-d client_secret=$WRITER_CLIENT_SECRET
```

    
    - __Authorization Code Grant__
    First, get the code by entering the following in the browser:
    
    [https://dev-ww47b0brrhicfeqm.us.auth0.com/authorize?audience=https://localhost:8443/product-composite&scope=openid email product:read product:write&response_type=code&client_id=xg6jwriter9client9id9ja41jCr&redirect_uri=https://my.redirect.uri&state=845361](https://dev-ww47b0brrhicfeqm.us.auth0.com/authorize?audience=https://localhost:8443/product-composite&scope=openid email product:read product:write&response_type=code&client_id=xg6jwriter9client9id9ja41jCr&redirect_uri=https://my.redirect.uri&state=845361)
    
    
    You will get an authorization code in the browser like this:
    [https://my.redirect.uri/?code=770Rpv2drFBdstCv5Em-FVac1-BVfqXdl95P342syBq4p&state=845361](https://my.redirect.uri/?code=770Rpv2drFBdstCv5Em-FVac1-BVfqXdl95P342syBq4p&state=845361)
    
    This will be the code: ```770Rpv2drFBdstCv5Em-FVac1-BVfqXdl95P342syBq4p```
```cmd
curl --location 'https://dev-ww47b0brrhicfeqm.us.auth0.com/oauth/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=authorization_code' \
--data-urlencode 'client_id=xg6jwriter9client9id9ja41jCr' \
--data-urlencode 'client_secret=PKmE2writer9client9secret9UzIUCQMN3OImUMJ' \
--data-urlencode 'code=770Rpv2drFBdstCv5Em-FVac1-BVfqXdl95P342syBq4p' \
--data-urlencode 'redirect_uri=https://my.redirect.uri'
```


* Now you can make the request to product microservices with the token received:
```cmd
curl --location 'https://localhost:8443/product-composite' \
--header 'Authorization: Bearer eyJhb-pass-your-jwt=bearer-token-here-as-received-from-above-gwqggwqcA' \
--header 'Content-Type: application/json' \
--data '{
    "productId": 4,
    "name": "product Exo",
    "weight": 20,
    "recommendations": [
        {
            "recommendationId": 1,
            "author": "author Arthur",
            "rate": 3,
            "content": "content long desc"
        },
        {
            "recommendationId": 2,
            "author": "author Betani",
            "rate": 2,
            "content": "content short description"
        },
        {
            "recommendationId": 3,
            "author": "author Calamine",
            "rate": 3,
            "content": "content brief desc"
        }
    ],
    "reviews": [
        {
            "reviewId": 1,
            "author": "author 1",
            "subject": "subject 1",
            "content": "content 1"
        },
        {
            "reviewId": 2,
            "author": "author 2",
            "subject": "subject 2",
            "content": "content 2"
        },
        {
            "reviewId": 3,
            "author": "author 3",
            "subject": "subject 3",
            "content": "content 3"
        }
    ]
}'
```


___
Moving to next subject: centralized configuration. That will likely fall into another branch. See you soon

___

#### Centralized Configuration
> Dec 22, 2024

In this sprint, a __config server__ has been added to the landscape, all application configs moved to a central repo.


The __config server__ uses the _config-repo_ directory as its repository. To view configs returned for each microservice, use the following example curl request:
```cmd
curl --location 'http://user:pwd@localhost:8888/gateway/default'
```


To protect sensitive information (like passwords) at rest, use the following example request to the config server for encrypting the values:
```cmd
curl --location 'http://user:pwd@localhost:8888/encrypt' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'pwd='
```


