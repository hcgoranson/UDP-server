# UDP Server

This application is listening on incoming UDP packages on port 8125 and displays them on either 
a Swing based GUI or in a console. 

This is considerably useful during development of services that uses statsD to send metrics since this application
will easily inspect the statsD packages.

## Build & run
Build and create executable jar file
```shell script
mvn clean package
```
Execute the jar file
```shell script
java -jar target/UdpServer-1.0-SNAPSHOT-jar-with-dependencies.jar 
```

## Usage
The UDP Server will automatically start to listening in port 8125 at startup on localhost.
An error message will be displayed ff the port is already allocated and the application will not continue.

* **Clear** - clears all text from the console
* **Timestamp** - toggle if a timestamp should be added or not for every incoming package
* **Autoscroll** - enable/disable auto scroll
* **Searchbar** - highlight text in the console window and filter all incoming packages for the given keyword
* **Exit** - exits the application
![Alt text](swing-app.png?raw=true "UDP Server overview")
