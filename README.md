# Spring Web Quiz Engine

This project provides a multi-user web service for creating and solving quizzes over the HTTP protocol which can be
accessed via through several endpoints. The service supports the basic HTTP authentication.

It's a part of the [JetBrains Academy](https://hyperskill.org/projects/91) project.

## Requirements

- [JDK 17](https://www.openjdk.java.net/projects/jdk/17/)
- [Gradle 7.2](https://gradle.org/install/)

## Usage

1. Clone the repository
    ```shell
    git clone https://github.com/dan-koller/Spring-Web-Quiz-Engine
    ```

2. Setup the environment
    ```shell
    cd Spring-Web-Quiz-Engine
    cp .env.example .env
    ```
   Edit the `.env` file to your needs. Since the project uses H2 as a database, you just need to
   the `H2_DATABASE_URL`, `H2_DATABASE_USERNAME` and `H2_DATABASE_PASSWORD` variables. The default values are:
    ```shell
    H2_DATABASE_URL=h2:file:../quizdb
    H2_DATABASE_USERNAME=sa
    H2_DATABASE_PASSWORD=
    ```

3. Build and run the project
    ```shell
    ./gradlew build
    ./gradlew bootRun
    ```

The endpoints can be accessed using a browser or a tool that allows you to send HTTP requests
like [Postman](https://www.getpostman.com/). There are several endpoints that you can use to interact with the system.
Request the according endpoint in a format shown in the examples below.

### Processes

- [Registration](#registration)
- [Post a quiz](#post-a-quiz)
- [Solve a quiz](#solve-a-quiz)
- [Get all quizzes](#get-all-quizzes)
- [Get a quiz by id](#get-a-quiz-by-id)
- [Get all completed quizzes](#get-all-completed-quizzes)
- [Delete a quiz](#delete-a-quiz)
- [Patch a quiz](#patch-a-quiz)

## API Endpoints

| Endpoint                     | Anonymous | User |
|------------------------------|-----------|------|
| POST /api/register           | +         | +    |
| POST /api/quizzes            | -         | +    |
| POST /api/quizzes/{id}/solve | -         | +    |
| GET /api/quizzes             | -         | +    |
| GET /api/quizzes/{id}        | -         | +    |
| GET /api/quizzes/completed   | -         | +    |
| DELETE /api/quizzes/{id}     | -         | +    |
| PATCH /api/quizzes/{id}      | -         | +    |

_'+' means the user with the role above can access that endpoint. '-' means the user with the role above does not have
access to that endpoint._

### Examples

#### Registration

```shell
POST /api/register
{
  "email": "johndoe@<domain>.com",
  "password": "password"
}
```

_The password must be at least 5 characters long._

#### Post a quiz

```shell
POST /api/quizzes
{
  "title": "The Java Logo",
  "text": "What is depicted on the Java logo?",
  "options": [
    "Robot",
    "Tea leaf",
    "Cup of coffee",
    "Bug"
  ],
  "answer": [2]
}
```

_The `answer` field is an **array** (starting with "0") of numbers that represent the correct options._

#### Solve a quiz

```shell
POST /api/quizzes/1/solve
{
  "answer": [2]
}
```

Response:

```shell
{
  "success": true,
  "feedback": "Congratulations, you're right!"
}
```

#### Get all quizzes

```shell
GET /api/quizzes
```

Response:

```shell
[
  {
    "id": 1,
    "title": "The Java Logo",
    "text": "What is depicted on the Java logo?",
    "options": [
      "Robot",
      "Tea leaf",
      "Cup of coffee",
      "Bug"
    ]
  },
  {
    "id": 2,
    "title": "The Ultimate Question of Life, the Universe, and Everything",
    "text": "According to The Hitchhiker's Guide to the Galaxy by Douglas Adams, what is the answer to the Ultimate Question of Life, the Universe, and Everything?",
    "options": [
      "Forty-two",
      "Love",
      "Pi",
      "Chocolate"
    ]
  }
]
```

#### Get a quiz by id

```shell
GET /api/quizzes/1
```

Response:

```shell
{
  "id": 1,
  "title": "The Java Logo",
  "text": "What is depicted on the Java logo?",
  "options": [
    "Robot",
    "Tea leaf",
    "Cup of coffee",
    "Bug"
  ]
}
```

#### Get all completed quizzes

```shell
GET /api/quizzes/completed
```

Response (for the currently authenticated user):

```shell
[
  {
    "id": 1,
    "completedAt": "2021-09-01T12:00:00"
  },
  {
    "id": 2,
    "completedAt": "2021-09-01T12:00:00"
  }
]
```

#### Delete a quiz

```shell
DELETE /api/quizzes/1
```

#### Patch a quiz

```shell
PATCH /api/quizzes/1
{
  "title": "The Java Logo",
  "text": "What is depicted on the Java logo?",
  "options": [
    "Robot",
    "Tea leaf",
    "Cup of coffee",
    "Bug"
  ],
  "answer": [2]
}
```

## Architecture

The system is built on a [Spring Framework](https://spring.io/) application context. The application itself follows the
model-view-controller pattern. The application consists of the following components:

- **Authentication**: The Authentication component is responsible for managing user authentication. It is responsible
  for validating user credentials and creating a session for the user.
- **Controller**: The Controller component is responsible for handling requests from the user.
- **Entity**: The Entity components are responsible for managing the different data models.
- **Repository**: The repository components are responsible for managing the data storage.
- **Service**: The service layer manages the main business logic.
- **Security**: The security layer is responsible for managing the access control and the authorization.

## Stack

- Java 17
- Gradle 7.5
- Spring Boot 2.7.2
- H2 Database (Version 1.4.200)

## Dependencies

- [Spring Boot 2.7.0](https://spring.io/projects/spring-boot)
- [Spring Boot Web Starter 2.7.0](https://spring.io/projects/spring-boot-web)
- [Spring Boot Actuator 2.7.0](https://spring.io/projects/spring-boot-actuator)
- [Spring Boot Data JPA 2.7.0](https://spring.io/projects/spring-boot-data-jpa)
- [Spring Boot Security 2.7.0](https://spring.io/projects/spring-boot-security)
- [Hibernate Validator 6.1.0.Final](https://hibernate.org/validator/)
- [H2 Database 1.4.200](https://www.h2database.com/)
- [Jackson Annotations 2.13.0](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations)
- [Lombok 1.18.24](https://projectlombok.org/)
- [Spring Boot Test 2.7.0](https://spring.io/projects/spring-boot-test)
- [Spring Security Test 5.6.0](https://spring.io/projects/spring-security-test)
- [JUnit 5.8.1](https://junit.org/junit5/)
- [Spring dotenv 2.3.0](https://github.com/paulschwarz/spring-dotenv)

## Testing

The application is tested using [JUnit 5](https://junit.org/junit5/). The tests are located in the `src/test/java`
folder. The unit tests are run using the Spring Boot Test framework. The code coverage is 86% for classes and 89% for
lines.

Run the tests using the following command:

```shell
./gradlew test
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
