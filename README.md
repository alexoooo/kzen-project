# kzen-project
Office automation project

Dev mode (two processes for client refresh, and server from IDE):
> > ./gradlew -t kzen-project-js:watch
>
> > cd kzen-project-js && yarn run start
>
> > run KzenProjectApp from IDE

Dist:
> ./gradlew assemble
>
> java -jar server/build/libs/kzen-project-jvm*.jar

Web:
> http://localhost:8080/