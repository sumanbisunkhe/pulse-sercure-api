


# Pulse Application

Pulse is a backend web application focused on user authentication and management, featuring secure login and user data handling.

## Introduction

Pulse offers a secure platform for managing user accounts. Built using Java and Spring technologies, it efficiently handles user authentication and CRUD operations through RESTful APIs. The application leverages JWT (JSON Web Token) for secure authentication, ensuring user sessions are protected.

### Key Features

- **User Registration**: Allows new users to create accounts with validated input.
- **User Login**: Implements secure login via username or email with JWT authentication.
- **User Management**: Supports retrieving, updating, and deleting user accounts.
- **Profile Management**: Enables users to upload and manage profile pictures.
- **Validation**: Utilizes Bean Validation (JSR 380) to ensure data integrity during user registration and updates.
- **Error Handling**: Provides detailed error messages for validation issues and resource not found exceptions.

### Technologies Used

- **Backend**: Java, Spring Boot
- **Authentication**: JWT (JSON Web Token)
- **Validation**: Bean Validation (JSR 380)
- **User Interface**: Thymeleaf for rendering dynamic web pages
- **Security**: Spring Security for managing authentication and authorization
- **Data Persistence**: JPA/Hibernate for database interactions
- **Database**: PostgreSQL as the database management system
- **Testing Frameworks**: JUnit and Mockito for unit testing and mocking dependencies


## Project Structure
```
pulse-api-secure/
│
├── .idea/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── pulse/
│   │   │           └── api/
│   │   │               ├── controller/
│   │   │               │   ├── AuthController.java
│   │   │               │   └── UserController.java
│   │   │               │
│   │   │               ├── dto/
│   │   │               │   └── UserDto.java
│   │   │               │
│   │   │               ├── enums/
│   │   │               │   ├── Gender.java
│   │   │               │   ├── RelationshipStatus.java
│   │   │               │   └── RoleName.java
│   │   │               │
│   │   │               ├── exceptions/
│   │   │               │   ├── EmailAlreadyExistsException.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── PhoneAlreadyExistsException.java
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   └── UsernameAlreadyExistsException.java
│   │   │               │
│   │   │               ├── model/
│   │   │               │   ├── Role.java
│   │   │               │   └── User.java
│   │   │               │
│   │   │               ├── repo/
│   │   │               │   ├── RoleRepo.java
│   │   │               │   └── UserRepo.java
│   │   │               │
│   │   │               ├── security/
│   │   │               │   └── SecurityConfig.java
│   │   │               │
│   │   │               ├── service/
│   │   │               │   ├── impl/
│   │   │               │   │   └── UserServiceImpl.java
│   │   │               │   └── UserService.java
│   │   │               │
│   │   │               └── utils/
│   │   │               │   ├── CustomeCustomerDetailsService.java
│   │   │               │   ├── DatabaseInitializer.java
│   │   │               │   └── RoleInitializer.java
│   │   │               │
│   │   │               └── PulseApiSecureApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │       
│   │
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── pulse/
│       │           └── api/
│       │               ├── controller/
│       │               │   └── UserControllerTest.java
│       │               ├── service/
│       │               │   └── UserServiceImplTest.java
│       │               └── PulseApiSecureApplicationTests.java
│
├── .gitignore
├── README.md
├── pom.xml
└── mvnw
```

## APIs

### Authentication APIs

#### User Login

- **Endpoint**: `POST /api/auth/login`
- **Description**: Authenticates a user and returns a JWT for accessing protected APIs.
- **Request Body**: `AuthenticationRequest`
- **Response**: Returns a JWT token for authenticated user sessions along with a success message.

### User Management APIs

#### Create User

- **Endpoint**: `POST /api/users/create`
- **Description**: Registers a new user with validated input data.
- **Request Body**: `UserDto`
- **Response**: Returns a success message and created user data.

#### Update User

- **Endpoint**: `PUT /api/users/update/{id}`
- **Description**: Updates an existing user identified by `{id}`.
- **Request Body**: `UserDto`
- **Response**: Returns a success message and updated user data.

#### Get User by ID

- **Endpoint**: `GET /api/users/id/{id}`
- **Description**: Retrieves a user by their unique identifier `{id}`.
- **Response**: Returns user details if found, or a "Not Found" message.

#### Get All Users

- **Endpoint**: `GET /api/users/all`
- **Description**: Retrieves a list of all registered users.
- **Response**: Returns a list of user data.

#### Delete User by ID

- **Endpoint**: `DELETE /api/users/delete/{id}`
- **Description**: Deletes a user identified by `{id}`.
- **Response**: Returns a success message upon successful deletion.

#### Upload Profile Picture

- **Endpoint**: `POST /api/users/upload-profile-picture/{id}`
- **Description**: Allows users to upload a profile picture.
- **Request Parameter**: `file` (MultipartFile)
- **Response**: Returns a success message upon successful upload.

### Error Handling

The application includes comprehensive error handling for various scenarios, providing informative responses for validation errors, resource not found exceptions, and other issues, enhancing the user experience.

### Testing

The application includes comprehensive unit tests for the service and controller layers using JUnit and Mockito to ensure robust functionality. Tests cover:

- User creation and validation error scenarios
- User authentication and profile management
- Exception handling for various cases

### Usage

1. Utilize tools like Postman to interact with the APIs.
2. Include proper authentication headers (JWT) for accessing protected APIs.

## Getting Started

To run the application locally, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/sumanbisunkhe/pulse-sercure-api.git
   ```

2. Navigate to the project directory:
   ```bash
   cd pulse-sercure-api
   ```

3. Set up your PostgreSQL database and configure the connection details in `application.properties`.

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

5. Access the application through your browser or API client at
   ```bash
   http://localhost:8080
   ```

### Contributors

- [Suman Bisunkhe](https://github.com/sumanbisunkhe) - Developer

### License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---







    



