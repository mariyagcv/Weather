# Weather API application

This is a weather API application that provides forecast information for particular locations, using the 7timer API.
Users can perform CRUD operations on a location and get information about the minimum and maximum temperatures for a location within a given start and date range.
The application also provides frequently refreshed data on historical forecasts from the time a location was created.

### Built with
<img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot" /> <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" /> 
<img src="https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white" />
<img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white" />
<img src="https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white"/> 


## Getting Started

### Prerequisites
The following is required to run the application:
  - Java JDK 11 or Docker

### Installation

1. Clone the repo
```sh
git clone git@github.com:mariyagcv/Weather.git
```
### (Optional) Run app with Docker
Build image locally:

    ./gradlew bootBuildImage
    docker run -it -p8080:8080 weather:0.0.1-SNAPSHOT

Or access directly from Dockerhub:

    docker run -p8080:8080 mariyagcv/weather:latest
    
### Run the app
    ./gradlew bootRun

### Run the tests
    ./gradlew test

**Note:** When you run the tests, a test coverage report is generated under build/reports/jacoco/index.html

### Test coverage
<img src="https://i.imgur.com/Pbxp8UB.png" />

# REST API

### Access dynamic API documentation
You can access the API documentation at: 

    http://localhost:8080/swagger-ui.html

## Create a new Location

### Request

`POST /locations/`

    curl --location --request POST 'http://localhost:8080/locations/' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "slug": "new-slug",
    "latitude": 23.1,
    "longitude": 113.2
    }'

### Response

    HTTP/1.1 201 Created
    
## Get list of Locations when not empty

### Request

`GET /locations/`

    curl --location --request GET 'http://localhost:8080/locations/'

### Response

    HTTP/1.1 200 OK
    [ { "slug": "new-slug", "latitude": 23.1, "longitude": 113.2 } ]

## Get list of Locations when empty

### Request

`GET /locations/`

    curl --location --request GET 'http://localhost:8080/locations/'

### Response

    HTTP/1.1 200 OK
    [ ]


## Get an existing Location

### Request

`GET /location/slug`

    curl --location --request GET 'http://localhost:8080/locations/new-slug'

### Response

    HTTP/1.1 200 OK

    {"slug":"new-slug","latitude":23.1,"longitude":113.2}

## Get forecast for Location within start and end date range

### Request

`GET /location/slug`

    curl --location --request GET 'http://localhost:8080/locations/new-slug/forecast?start_date=2021-08-26&end_date=2021-08-28'

### Response

    HTTP/1.1 200 OK

    [
    {
        "date": "2021-08-26",
        "min-forecasted": 29.0,
        "max-forecasted": 33.0
    },
    {
        "date": "2021-08-27",
        "min-forecasted": 28.0,
        "max-forecasted": 35.0
    },
    {
        "date": "2021-08-28",
        "min-forecasted": 28.0,
        "max-forecasted": 36.0
    }
    ]

## Get forecast for Location when start date is before the creation date of a location

### Request

`GET /location/slug`

    curl --location --request GET 'http://localhost:8080/locations/new-slug/forecast?start_date=2021-08-20&end_date=2021-08-28'

### Response

    HTTP/1.1 400 Bad Request

    {
    "timestamp": "2021-08-26T11:34:19.648+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Start date should not be earlier than creation date of the location",
    "path": "/locations/new-slug/forecast"
    }



## Get a non-existent Location

### Request

`GET /locations/slug`

    curl --location --request GET 'http://localhost:8080/locations/non-existent'

### Response
    
    HTTP/1.1 404 Not Found

    {
    "timestamp": "2021-08-26T11:09:45.514+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "404 NOT_FOUND",
    "path": "/locations/slug"
    }

## Update a Location

### Request

`PUT /locations/slug`

    curl --location --request PUT 'http://localhost:8080/locations/new-slug/' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "slug": "new-slug",
    "latitude": 23.1,
    "longitude": 1
    }'

### Response

    HTTP/1.1 200 OK

## Update a Location with invalid parameters

### Request

`PUT /thing/:id`

    curl --location --request PUT 'http://localhost:8080/locations/new-slug/' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "slug": "new-slug",
    "latitude": invalid,
    "longitude": 1
    }'

### Response

    HTTP/1.1 400 Bad Request


## Delete a Location

### Request

`DELETE /locations/slug`

    curl --location --request DELETE 'http://localhost:8080/locations/new-slug/'

### Response

    HTTP/1.1 204 No Content


## Testing historical forecast for locations 

The h2 debugging console can be used to experiment with some queries to test the synchronisation and getting information about 
locations created in the past.

To do so, head to the following link and use the JDBC URL in application.properties if it's not specified by default
    http://localhost:8080/h2-console/


Some useful queries:

    /* After doing a POST request to create a location, check the contents of the location and forecast */
    SELECT * FROM Location;
    SELECT * FROM Forecast;

    /* Shift the created_date of a location to the past, shift forecast dates, and update with some example data, 
    so you can test a historical GET request */
    UPDATE Location
    SET created_date = created_date - 3;
    
    UPDATE Forecast 
    SET date = date - 3, max_forecasted = 5, min_forecasted = 1;

    /* Select once again to confirm that the changes were successful */ 
    SELECT * FROM Location;
    SELECT * FROM Forecast;

    /* After 1 minute, check the forecast table to see how the synchronisation has updated the values with recent data.
    You should expect to see only the values from this day onwards to change, since the external API doesn't provide past data */
    SELECT * FROM Forecast;

