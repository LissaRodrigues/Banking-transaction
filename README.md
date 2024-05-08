**Important: Don't forget to update the [Candidate README](#candidate-readme) section**

Real-time Transaction Challenge
===============================
## Overview


Today, you will be building a small but critical component of Current's core banking enging: real-time balance calculation through [event-sourcing](https://martinfowler.com/eaaDev/EventSourcing.html).

## Schema
The [included service.yml](service.yml) is the OpenAPI 3.0 schema to a service we would like you to create and host. 

## Details
The service accepts two types of transactions:
1) Loads: Add money to a user (credit)

2) Authorizations: Conditionally remove money from a user (debit)

Every load or authorization PUT should return the updated balance following the transaction. Authorization declines should be saved, even if they do not impact balance calculation.

You may use any technologies to support the service. We do not expect you to use a persistent store (you can you in-memory object), but you can if you want. We should be able to bootstrap your project locally to test.

## Bootstrap instructions
To run this transaction service locally, follow these steps:

-Clone this repository to your local machine.
-Navigate to the project directory.
-Make sure you have Java and Maven installed on your machine.
-Build the project by running 'mvn clean install'.
-Start the server by running 'mvn spring-boot:run'.
-The server will be accessible at http://localhost:8080.

Test APIs:
Ping API:
Endpoint: GET (http://localhost:8080/ping)
Description: Tests the availability of the service and returns the current server time.


Authorize Transaction API:
Endpoint: PUT (http://localhost:8080/authorization/{messageId})
Description: Removes funds from a user's account if sufficient funds are available.
Authorization Request Body: json
{
    "userId": "user123",
    "messageId": "message456",
    "transactionAmount": {
        "amount": "100.00",
        "currency": "USD",
        "debitOrCredit": "DEBIT"
    }
}

Load Transaction API:
Endpoint: PUT (http://localhost:8080/load/{messageId})
Description: Adds funds to a user's account.
Load Request Body: json
{
    "userId": "user789",
    "messageId": "message987",
    "transactionAmount": {
        "amount": "50.00",
        "currency": "USD",
        "debitOrCredit": "CREDIT"
    }
}


## Design considerations

For the transaction service project, we chose to use Spring Boot for the following reasons:
Ease of development: Spring Boot provides a powerful and streamlined framework for building Java-based web applications, reducing boilerplate code and configuration.
Dependency Injection: Spring Boot's dependency injection capabilities make it easy to manage and wire components, promoting modular and testable code.
Integration with Spring ecosystem: Spring Boot seamlessly integrates with other Spring projects and libraries, such as Spring Data and Spring Security, providing a comprehensive solution for building enterprise-grade applications.
Annotation-based configuration: Spring Boot's annotation-based configuration simplifies the setup and configuration of the application, improving productivity and maintainability.


## Bonus: Deployment considerations

Cloud platform: Deploy the application to a cloud platform like AWS, Azure, or Google Cloud Platform for scalability, reliability, and ease of management.
Containerization: Containerize the application using Docker to ensure consistency between development, testing, and production environments and facilitate deployment to container orchestration platforms like Kubernetes.
Continuous Integration/Continuous Deployment (CI/CD): Implement a CI/CD pipeline using tools like Jenkins, GitLab CI/CD, or GitHub Actions to automate the build, test, and deployment process, ensuring faster time-to-market and improved code quality.
Monitoring and logging: Use monitoring and logging tools like Prometheus, Grafana, ELK stack, or Splunk to monitor the application's performance, identify and troubleshoot issues, and gain insights into application behavior in production environments.

## ASCII art
*Optional but suggested, replace this:*
```
                                                                                
  _   _   _   _   _   _   _   _  
 / \ / \ / \ / \ / \ / \ / \ / \ 
( I | n | n | o | v | a | t | e )
 \_/ \_/ \_/ \_/ \_/ \_/ \_/ \_/ 
           
```
