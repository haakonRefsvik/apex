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
\
Sekvensdiagram: Appen skal kunne regne ballistisk bane på et gitt tidspunkt
\
Pre betingelse: bruker har allerde valgt et tidspunkt for en bestemt lat og lon.

```mermaid
sequenceDiagram
actor User
participant HomeScreen
participant DetailScreen
participant Map
participant MapViewModel
participant Trajectory
participant SettingsViewModel
participant SettingsRepository
participant RocketSpecsDatabase


User->>DetailScreen: Clicked "Calculate Ballistic trajectory"
DetailScreen-)+MapViewModel: deleteTrajectory()
MapViewModel->>MapViewModel:makeTrajectory = false
MapViewModel->>MapViewModel:trajectory = listOf()
DetailScreen->>MapViewModel: makeTrajectory
MapViewModel-->>Map: makeTrajectory = true 
MapViewModel-->>Map: makeTrajectory
Map-)+Map: Make3dtrajectory()
Map-)+SettingsViewModel: getRocketSpec()
SettingsViewModel-)+SettingsRepository: getRocketSpecValue()
SettingsRepository-)+RocketSpecsDatabase: getRocketSpecValues()
RocketSpecsDatabase--)-SettingsRepository: RocketSpecValues
SettingsRepository--)-SettingsViewModel: RocketSpecValue
SettingsViewModel--)-Map: RocketSpecs


Map-)+MapViewModel: loadTrajectory(allLevels, rocketSpecs)
MapViewModel->>Trajectory: simulateTrajectory(Rocketspecs)
Trajectory-->>MapViewModel: trajectoryList
MapViewModel--)-Map: trajectory
Map-->>HomeScreen: Shows trajectory

```



\
\
Sekvensdiagram:
Legg til favoritt

```mermaid
sequenceDiagram
    actor User
    participant HomeScreen
    participant HomeScreenViewModel
    participant FavoriteRepository
    participant FavoriteDao
 
   
    User->>HomeScreen: Add card to favorites
      alt name in usee

    HomeScreen ->> HomeScreenViewModel: isNameAlreadyUsed
    HomeScreenViewModel ->> FavoriteRepository: getFavoriteLocations()
    FavoriteRepository-)+ FavoriteDao: getFavoriteLocation()

   
    FavoriteDao --)- FavoriteRepository: favorite locations
    FavoriteRepository->>HomeScreenViewModel: getFavoriteLocations()

    HomeScreenViewModel -->> HomeScreen: Name is already in use

    end

    alt location in use

    HomeScreen ->> HomeScreenViewModel: checkForLocation(pos)
        HomeScreenViewModel ->> FavoriteRepository: getFavoriteLocations()
    FavoriteRepository-)+ FavoriteDao: getFavoriteLocation()

   
    FavoriteDao --)- FavoriteRepository: favorite locations
    FavoriteRepository->>HomeScreenViewModel: getFavoriteLocations()
    HomeScreenViewModel -->> HomeScreen: location is in use


    end
    HomeScreen ->> HomeScreenViewModel: AddFavoriteDialogCorrect(lat, lon)
    HomeScreenViewModel ->> FavoriteRepository: addFavorite(lat, lon)
    FavoriteRepository ->> FavoriteDao: insertFavoriteLocation(lat, lon)
    
    HomeScreenViewModel -)+ FavoriteRepository: getFavoriteLocations()
    
    FavoriteRepository --)- HomeScreenViewModel: favorite locations
    HomeScreen -)+ HomeScreenViewModel: MakeFavoriteCard(favoriteLocations)
    HomeScreenViewModel --)- HomeScreen: favorite location card

  
    
   


```

\
\
Sekvensdiagram : Brukeren skal kunne tilpasse grenseverdiene for værdata til sin rakett
\
Pre betingelse: Bruker har skrevet inn oppdaterte verdier

```mermaid
sequenceDiagram
    actor User
    participant SettingScreen
    participant SettingsViewModel
    participant SettingsRepository
    participant ThresholdDatabase
    User ->> SettingScreen: Interacts with settings
    User ->> SettingScreen: Closes SettingScreen
    SettingScreen ->> SettingsViewModel: updateThresholdValues()
    SettingsViewModel ->> SettingsViewModel: theresholdValues
    SettingsViewModel ->> SettingsRepository: updateThresholdValues(theresholdValues)
    SettingsRepository ->> ThresholdDatabase: updateThresholdValues(theresholdValues)
    SettingsViewModel -)+ SettingsRepository: getThresholdValue()
    SettingsRepository ->> ThresholdDatabase: getThresholdValue()
    ThresholdDatabase -->> SettingsRepository: Thresholds
    SettingsRepository --)- SettingsViewModel: Thresholds
    SettingsViewModel -->> SettingScreen: uses updated thresholds
    SettingScreen -->> User: Shows confirmation of saved setting
```
\
\
Tilpasser grenseverdier for værdata
\
```mermaid
flowchart TB
    start([Start])
    c1[HomeScreen]
    c2[Presses SettingsScreen] -.-> j[(Gets data from ThresholdDatabase)]
    start --> c1
    c1 --> c2
    c2 --> e[Shows settingsScreen]
    e --> f[Change value]
    f -.-> g[(Value is saved in ThresholdDatabase)]
    f1[Change another value?] --> |Yes| f
    f1 --> |No| h
    f --> h([End])
```
