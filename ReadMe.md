1. Create Mysql database

create database weather_forcast_db


2. Change mysql username and password as per your installation.

+ open `src/main/resources/application.properties`

+ change `spring.datasource.username` and `spring.datasource.password` as per your mysql installation

3. Build and run the app using maven


The app will start running at <http://localhost:8080>.

## Rest APIs

    GET /weather/api/fetchAll
    
    GET /weather/api/forecast/{cityName}
    
    DELETE /weather/api/{weatherLogId}

You can test them using postman or any other rest client.

