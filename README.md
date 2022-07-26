
## Prerequisites
In order to be able to setup and run this application, the following need to be installed and setup
- [Android Studio](https://developer.android.com/studio)

## Project Setup

To setup the project in your machine:

- Open and Run Project in android studio
- Run the application.


### TASK 

The task is a simple app to help launch SimplePay in Binaria .
SimplePay sends money from Binaria to several countries in Africa (Kenya, Uganda, NNigeria, Tanzania)

## Architecture

The project design pattern is clean architecture MVVM:
- Data layer
- Domain layer
- Presentation layer
- Di
- Utils
  

### Data layer
- The data layer handles all logic to do with saving/fetching data from the  different data sources and provides it to the app.
- It  has all the repositories which the domain layer can use.
- Holds classes that define how network requests are made.
    - [CurrencyApi] defines how network requests are made
  
### Domain Layer
- The layer  acts as the mediator between the ViewModels and the data layer.
- It includes all the apps models *Rates* *CurrencyConverterResponse* and and the data repository Interfaces *MainRepository* 

### Presentation layer
- This layer hold classes that display and present the data, it includes *MainActivity* and viewModels *MainViewModel*
- The activity gets data to display from the viewModel and the viewModel talks to the domain layer to perform actions.

### Di
- This layer has all the dependencyInjection logic:*AppModule* 


### Utils
- These are classes that define a set of methods that perform common, often re-used functions across the app.
    - [NetworkResponse] is a class for displaying the different UI states i.e:
      -*Loading* when a network request is initiated to show the loading spinner.
      -*Success* when a network response is successful to display the recipe data loaded.
      -*Error* when an exception occurs to show an error snack bar in the UI.
    - [Extensions] has extension functions to validate textInputLayout inputs.
    - [ProgressDialog] holds the progress dialog show and dismiss functions that I can re use across the app.
    - [NetworkStatusTracker] is a helper class for observing the app's internet connectivity.
    - [Utils] has functions such as *convertDecimalToBinary* that can be used across the app

### UtilsUnitTest
- This class has the Utils class unit tests

## External Links/dependencies
- [Retrofit] for network requests
- [Coroutines]for executing  asynchronous code


