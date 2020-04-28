# kzen-project
Office automation project

Dev mode (one process for client refresh, and one server process from IDE):

1) Run KzenProjectApp from IDE: --server.port=8081
    to start https://localhost:8081
    
2) Run from terminal: `gradlew -t :kzen-project-js:run`
    to run client proxy at https://localhost:8080 with live reload
    - Web UI JavaScript will be provided by webpack          
    - Everything expect `*.js` files is served by port 8081


Dist:
> ./gradlew build
>
> java -jar kzen-project-jvm/build/libs/kzen-project-jvm-*.jar

Web:
> http://localhost:8080/