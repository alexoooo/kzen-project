# kzen-project
Office automation project

Dev mode (two processes for client refresh, and server from IDE):
> > ./gradlew -t kzen-project-js:watch
>
> > cd kzen-project-js && yarn run start
>
> > run KzenProjectApp from IDE

Command-line dev (initial build for client, can be subsequently skipped):
> ./gradlew build
>
> ./gradlew bootRun

Dist:
> ./gradlew build
>
> java -jar kzen-project-jvm/build/libs/kzen-project-jvm-*.jar

Web:
> http://localhost:8080/