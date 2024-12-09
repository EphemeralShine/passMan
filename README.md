# PROJECT SUMMARY
Project goal was to try to implement security functionality, following best practices and try to prevent most widespread and destructive attack vectors. Below you can find a sections describing implemented functionality.  
## Authentication and session management
The project uses Spring Security to implement secure session management. Sessions are created and managed server-side, providing a robust mechanism to identify and maintain user authentication across multiple requests.

### **Key Features**:

-   **Session Tracking**:
    
    -   Each user session is identified using a JSESSIONID cookie.
    -   The cookie is marked as HttpOnly to prevent access by client-side scripts and reduce the risk of XSS attacks.
    -   The cookie is configured with SameSite=Strict, helping to prevent it from being sent with cross-origin requests.
-   **Secure Cookie Configuration**:
    
    -   The `Secure` flag is enabled, ensuring the cookie is transmitted only over HTTPS connections.
-   **Session Timeout**:
    
    -   Sessions automatically expire after a predefined period of inactivity(30min), reducing risks from abandoned sessions.
-   **Concurrent Session Control** :
    
    -   Limits the number of active sessions per user, providing additional protection against session hijacking.

This session management configuration in Spring Security adheres to best practices, ensuring robust security and user session integrity.

## Stored Data Security

Authentication passwords are stored as salted hashes using bcrypt, providing enhanced security in the event of a data breach. Vault passwords are also securely stored with salting and hashing. However, vault entries cannot be stored as hashes since they need to be retrievable by the user. These entries are instead encrypted using AES-GCM, with the encryption key derived from the user's master password and a salt using PBKDF2. When the user enters their master password, the encryption key is derived and temporarily stored in the server session, allowing it to be used for the encryption and decryption of vault password entries.

## CSRF
CSRF attacks are mitigated by using http-only session cookies to store session identifiers, preventing client-side access and reducing the risk of session hijacking via malicious scripts. Additionally, CSRF tokens are embedded in user input forms, which are unique for each session and request. These tokens are sent with each form submission and validated on the server side to ensure that the request is legitimate and originated from the authenticated user. 
## XSS
User input is HTML-escaped through Thymeleaf templating engine. Server-side user input sanitization is the next upgrade this application should receive to enhance security even further. 
## Database operations
The project uses Spring Data JPA with Hibernate ORM for database interactions, which simplifies CRUD operations and entity management. By using prepared statements, Hibernate automatically protects against SQL injection attacks, ensuring that user inputs are handled safely as data and not executable code. This approach, combined with Spring Data JPA's repository abstraction, helps prevent common vulnerabilities while providing a streamlined way to interact with the database.
## Communication encryption
All communications happen over HTTPS to provide security and encryption.
## Security Headers
To enhance application security, HTTP security headers were implemented, including X-XSS-Protection and Content-Security-Policy (CSP). The X-XSS-Protection header is enabled to block potential XSS attacks by instructing the browser to block responses containing detected malicious scripts. Additionally, the CSP header restricts the loading and execution of resources to trusted origins, reducing the risk of content injection attacks such as XSS and clickjacking.



# LOCAL SETUP
## Requirements
### JDK and build tools
JDK 19 and latest Apache Maven build tool are required for project setup.
### HTTPS
A signed certificate is needed for application to run over https as it does not support http. For development and testing self-signed certificate was used. It can be generated with following command.
```
keytool -genkeypair -alias passman -keyalg RSA -keysize 3072 -storetype PKCS12 -keystore keystore.p12 -validity 365
```
However sample certificate was provided with the project (keystore.p12), for ease of setup and testing.
### .env 
.env file is required in the project root(/passMan/) in order to run the application.
Here is a sample:
```
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb  
SPRING_DATASOURCE_USERNAME=username  
SPRING_DATASOURCE_PASSWORD=password  
SSL_KEYSTORE_PATH=classpath:keystore.p12  
SSL_KEYSTORE_PASSWORD=123456
```
TLS variables in this sample config will work with provided example certificate for ease of setup and testing. 

## Running the application 
No server setup is required as the application uses Spring boot default Tomcat embedded webserver.
Simply run:
``` 
mvn spring-boot:run
```
form project root and application will start on port **8443**.
