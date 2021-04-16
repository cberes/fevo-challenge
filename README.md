# fevo-challenge

This is my solution to Fevo's coding challenge. I implemented it using Java.

## Build the project

    ./gradlew clean build

## Run the app

    ./gradlew run    

By default, NASA's demo key will be used. However, a [NASA API key](https://api.nasa.gov/)
can be specified using the `NASA_API_KEY` environment variable. For example:

    NASA_API_KEY=YOUR_KEY_HERE ./gradlew run
