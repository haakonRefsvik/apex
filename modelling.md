# Modelling

\
\
Klassestruktur for lagring av værdata

```mermaid

classDiagram

    WeatherData <|-- WeatherAtPos
    WeatherData <|-- WeatherFavorites
    WeatherData: -weatherList list(WeatherAtPosHour)
    WeatherAtPosHour "Many" <|-- "1" WeatherData: Contains

    class WeatherAtPosHour{
        -String date
        -Int hour
        -Double lat
        -Double lon
        -Series series
        -VerticalProfile verticalProfile
        -Int soilMoisture
        -HashMap~String, Double~ valuesToLimitMap
        -Double closeToLimitScore
        -Boolean favorite
    }


```
\
\
Klassestruktur for VerticalProfile
```mermaid

classDiagram
    ShearWind "many" <|-- "1" VerticalProfile: Contains
    LevelData "many" <|-- "1" VerticalProfile: Contain
    LevelData "2" <|-- "2" ShearWind: Contains

    class VerticalProfile{
        -Int heightLimitMeters
        -Double lat
        -Double lon
        -HashMap~Double, LevelData~ verticalProfileMap
        -String time
        +getAllSheerWinds() List~ShearWind~
        +getAllLevels() List~Double~
        +getAllLevelDatas() List~LevelData~
        +findLevel(Double level) LevelData
        +addGroundInfo(Series series)
        +getMaxSheerWind() ShearWind
    }

    class LevelData{
        -Double pressurePascal
        -Double groundLevelTempKelvin
        -Double tempValueKelvin
        -Double uComponentValue
        -Double vComponentValue
        -Double seaPressurePa
        +convertKelvinToCelsius(Double kelvin) Double
        +getLevelHeightInMeters() Double
        +getWindSpeed(uComp Double vComp Double) Double
        +getWindDirection(uComp Double vComp Double) Double
        +getTemperatureCelsius() Double
        +addValue(Int parameterNumber, Double value)
    }

        class ShearWind{
            -Double altitude
            -Double direction
            -Double windSpeed
            -LevelData upperLayer
            -LevelData lowerLayer
            +getShearWind(upp LevelData, low) Double
            +getShearDirection(upp LevelData, low) Double
    }

```
\
\
Sekvensdiagram for at bruker trykker "Get Weatherdata"
```mermaid
sequenceDiagram
    actor User
    User->>HomeScreen: Hent værdata(lat, lon)
    HomeScreen->>HomeViewModel: getWeatherByCord(lat, lon)
    HomeViewModel->>+WeatherRepository: loadWeather(lat, lon)
    WeatherRepository-)+GribDataSource: getGrib()
    GribDataSource--)-WeatherRepository: grib-data
    WeatherRepository-)+ForecastDataSource: getForecast(lat, lon)
    ForecastDataSource--)-WeatherRepository: location-forecast
    WeatherRepository-)+SoilForecast: getSoil(lat, lon)
    SoilForecast--)-WeatherRepository: soil-forecast

    WeatherRepository->>-WeatherRepository: Make  weatherAtPos-objects

    WeatherRepository-->>HomeViewModel: WeatherAtPos
    HomeViewModel-->>HomeScreen: Show cards with weatherdata

```

\
\
Sekvensdiagram for at bruker velger et værkort
```mermaid

sequenceDiagram
    actor User
    participant HomeScreen
    participant NavController
    participant DetailScreen
    participant DetailsViewModel
    participant HomeViewModel

    User->>HomeScreen: Hent værdata(lat, lon)
    HomeScreen->>HomeViewModel: getWeatherByCord(lat, lon)
    HomeViewModel->>+WeatherRepository: loadWeather(lat, lon)
    WeatherRepository->>-WeatherRepository: Make  weatherAtPos-objects
    WeatherRepository-->>HomeViewModel: WeatherAtPos
    HomeViewModel-->>HomeScreen: Show cards with weatherdata
    User->>HomeScreen: choose weather-card
    HomeScreen->>NavController: navigate(DetailScreen)
    NavController->>NavController: Change screen
    HomeScreen->>+DetailsViewModel: showCard(date)
    DetailsViewModel->>+HomeViewModel: getWeatherData
    HomeViewModel-->>-DetailsViewModel: WeatherData
    DetailsViewModel-->>DetailScreen: WeatherData
    DetailsViewModel-->>-DetailScreen: Date
    DetailScreen->>DetailScreen: showCard(date)

```
\
\
Flytdiagram

```mermaid

flowchart TB
    start([Start])
    c1[Choose card]
    c2[Save as favorite]
    start -->c1
    c1-->c2
    c2--> d[Check if position\nexits in favorite-location\n database]
    d -->e[(Location-database)]
    e --> |No| c5{Do you \nwant to add location\n to favorites?}
    e --> |Yes| c6[Add card to database]
    c6 --> e2[(Card-database)]
    c5 --> |Yes| c7[Give location a name]
    c7 --> c8[Save location to database]
    c8 --> e
    c8 --> c6
    c5 --> |No| c6
    c6 --> c9[End]
```
\
\
Sekvensdiagram
```mermaid
sequenceDiagram
    actor User
    participant DetailScreen
    participant HomeViewModel
    participant FavoriteCardViewModel
    participant FavoriteRepository
    participant FavoriteDatabase
    participant LocationDatabase

    User->>DetailScreen: Add card to favorites
    par Location is in database
        DetailScreen->>HomeViewModel: checkForLocation(pos)
        HomeViewModel->>LocationDatabase: checkForLocation(pos)
        LocationDatabase-->>HomeViewModel: location is in database
    end
    par Location is not in database
        DetailScreen->>HomeViewModel: checkForLocation(pos)
        HomeViewModel->>LocationDatabase: checkForLocation(pos)
        LocationDatabase-->>HomeViewModel: location is not in database
        HomeViewModel-->>DetailScreen: location is not in database
        DetailScreen-->>User: do you want to add location?
        par User wants to add location
            User-->>DetailScreen: addLocation(name)
            DetailScreen->>HomeViewModel: addLocation(name)
            HomeViewModel->>LocationDatabase: addLocationToDatabase(lat, lon, name)

        end
    end
    DetailScreen->>FavoriteCardViewModel: addFavorite(lat, lon, date)
    FavoriteCardViewModel->>FavoriteRepository: addFavorite(lat, lon, date)
    FavoriteRepository->>FavoriteDatabase: insertCard(lat, lon, date)
    FavoriteRepository->>FavoriteDatabase: getFavoriteCards()
    FavoriteDatabase-->>+FavoriteRepository: favorite-cards
    FavoriteRepository->>-FavoriteRepository: update states

```

