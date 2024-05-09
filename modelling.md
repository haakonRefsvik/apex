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

