# Quotation-task
Quotation task is a simple quotation storage with options to load all quotations, create/update a quotation or delete a quotation. You can use REST to communicate with application.

# Tech/frameworks used
<img src="https://spring.io/images/spring-logo-9146a4d3298760c2e7e49595184e1975.svg" width="200"><img src="https://hibernate.org/images/hibernate-logo.svg" width="200"><img src="https://upload.wikimedia.org/wikipedia/commons/thumb/2/29/Postgresql_elephant.svg/1200px-Postgresql_elephant.svg.png" width="100"><img src="https://junit.org/junit4/images/junit5-banner.png" width="200"><img src="https://raw.githubusercontent.com/mockito/mockito/main/src/javadoc/org/mockito/logo.png" width="200"><img src="https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Apache_Maven_logo.svg/2560px-Apache_Maven_logo.svg.png" width="200"><img src="https://www.javanibble.com/assets/images/feature-images/feature-image-lombok.png" width="100">

# Installation

* JDK 11
* Apache Maven 3.x

# Build and Run

```
mvn clean package
mvn exec:java
```

# Database setup

Perstistance layer of the application is PostgreSQL database. In order to successfully connect application with a persistance layer, configure it locally (create DB and grant priviliges to selected user with usage og PgAdmin or another tool). Then, replace the following values in your [application.yaml](https://github.com/Radek87o/Quotation-task/blob/master/src/main/resources/application.yaml)

```
spring.datasource.url=<yourDatabaseUrl>
spring.datasource.username=<yourUsername>
spring.datasource.password=<yourPassword>
```

# API
Application is available on localhost:8080. You can use the api with POSTMAN or another http client. The application exposes 4 endpoints to the client:

1. <b>Find all quotations</b> - returns paginated quotations
  ```
  Endpoint: GET http://localhost:8080/api/quotations/
  Produces: application/json
  Params:
  - page - number of result's page which you want to retrieve. Default value is 0;
  - size - number of results you want to retrieve per a single page. Default value is 25, max value is 1000
  ```
2. <b>Save quotation</b> - saves new quotation to a database and returns the newly saved quotation.
  ```
  Endpoint: POST http://localhost:8080/api/quotations/
  Accept: application/json
  Produces: application/json
  Example of request body:
    {
      "content": "<Quotation content>",
      "author": {
          "firstName": "<Quotation author firstName>",
          "lastName": "<Quotation author lastName>"
      }
    }
   Fields validation rules:
   - content - cannot be blank and its lenght cannot be greater than 1000 characters
   - author.firstName - cannot be blank
   - author.lastName - cannot be blank
   - author - cannot be null
   - combination of content, author's first name and author's last name is unique
  ```
3. <b>Update quotation</b>
  ```
  Endpoint: PUT http://localhost:8080/api/quotations/{quotationId}
  Accept: application/json
  Produces: application/json
  Example of request body:
    {
      "content": "<Quotation content>",
      "author": {
          "firstName": "<Quotation author firstName>",
          "lastName": "<Quotation author lastName>"
      }
    }
   Fields validation rules:
   - content - cannot be blank and its lenght cannot be greater than 1000 characters
   - author.firstName - cannot be blank
   - author.lastName - cannot be blank
   - author - cannot be null
  ```
4. <b>Delete quotation</b>
  ```
  Endpoint: DELETE http://localhost:8080/api/quotations/{quotationId}
  Produces: application/json
  ```

