# Meal Mojo Backend API

The <b>Spring Boot</b> backend API for the 'Meal Mojo' demo food delivery website.<br> 
The <b>frontend</b> portion of this app was built in Angular 13. Check out the [frontend repository here](https://github.com/qelery/Meal-Mojo).

## Requirements
* Maven 3.6.3+
* Java 13+
* A Postgresql database

## Usage
* Clone and download the frontend repository
* Start the frontend Angular server
* Clone and download this repository
* Adjust db configs in application-dev.properties as needed
* Run `mvn clean install` in the terminal
* Run `mvn spring-boot:run` in the terminal
* The web application is accessible via localhost:9092

## Features
* Endpoints for customer and merchant sign-up and login
* Endpoints for adding restaurants and menu items
* Endpoints for querying nearby restaurants and placing orders
* High test coverage using JUnit5, Mockito, and MockMVC
