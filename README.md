# ALKEMY JAVA TECHNICAL CHALLENGE - WALLET

#### Description: A Java API to manage a digital wallet.

## Development team

- Leandro Deferrari - <a href="https://github.com/leandrodeferrari" target="blank">GitHub</a>
- Agatha Stanchi - <a href="https://github.com/stanagatha" target="blank">GitHub</a>
- Franco Maximiliano Miranda - <a href="https://github.com/maximiranda" target="blank">GitHub</a>
- Natalia Gamarra - <a href="https://github.com/NatGamarra" target="blank">GitHub</a>
- Alejandro Corvalán - <a href="https://github.com/no7sag" target="blank">GitHub</a>
- Julián Garzón - <a href="https://github.com/julianDGS" target="blank">GitHub</a>
- Santiago Fonseca - <a href="https://github.com/Wiliamfm" target="blank">GitHub</a>
- Ignacio Avila - <a href="https://github.com/ignacioavilamdp" target="blank">GitHub</a>

## Our mentor

- Yamil Amado Selene - <a href="https://github.com/turcoamado" target="blank">GitHub</a>

## Technologies used

- Main language: Java 17
- Framework: SpringBoot
- Dependency manager: Maven
- ORM: Hibernate - JPA
- Security manager: Spring security
- API documentation: Swagger
- Automated testing: JUnit and Mockito
- Manual testing: Postman
- Data Base engine: MySQL
- Version Control System: GIT
- Project management: JIRA
- Team communication: Discord

## Build and Setup information

### Build and run instructions
- Clone the repo.
- On the root folder run: ```mvn clean install```
- Then run: ```mvn spring-boot:run```

### Consume the API
- Navigate to http://localhost:8080/api/docs to use the swagger testing UI.

### Configuration
The applications.properties file located in the resources directory sets some configuration 
properties that you may need to modify to run the application properly: 

- The default port is 8080. If you already have that port in used consider to modify it.
- Set the proper username and password for your database engine.
- By default, at initialization time, a database is created with empty tables based on the entities
of the application. 
- To perform some immediately testing you may want to easily populate the database. We have provided
two sql scripts located in the resources directory to alleviate the task: schema.sql and data.sql. Set the
"spring.sql.init.mode" property to "always" so the database is created and populated based on those
scripts.

### Pre-populated users:

If you decide to run the application using the schema.sql and data.sql scripts expect to get
the following preloaded users:

#### Admin users:

| email | password |
| ----- | -------- |
| nataliag@alkemy<span></span>.com | 12345678 |
| maximiliano@alkemy<span></span>.com | 12345678 |
| ignacio@alkemy<span></span>.com | 12345678 |
| agatha@alkemy<span></span>.com | 12345678 |
| alejandro@alkemy<span></span>.com | 12345678 |
| julian@alkemy<span></span>.com | 12345678 |
| leandro@alkemy<span></span>.com | 12345678 |
| santiago@alkemy<span></span>.com | 12345678 |
| pedro@alkemy<span></span>.com | 12345678 |
| mariano@alkemy<span></span>.com | 12345678 |


#### Standard users:

| email | password |
| ----- | -------- |
| sergio@alkemy<span></span>.com | 12345678 |
| diego@alkemy<span></span>.com | 12345678 |
| eliana@alkemy<span></span>.com | 12345678 |
| fernando@alkemy<span></span>.com | 12345678 |
| oscar@alkemy<span></span>.com | 12345678 |
| florencia@alkemy<span></span>.com | 12345678 |
| alexis@alkemy<span></span>.com | 12345678 |
| brian@alkemy<span></span>.com | 12345678 |
| gonzalo@alkemy<span></span>.com | 12345678 |
| lucas@alkemy<span></span>.com | 12345678 |
