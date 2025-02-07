#
# Set a variable that can be used in all stages.
#
ARG BUILD_HOME=/build

#
# Gradle image for the build stage.
#
FROM gradle:jdk21-alpine as build-image

#
# Set the working directory.
#
ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
WORKDIR $APP_HOME

#
# Copy the Gradle config and source code.
#
COPY --chown=gradle:gradle build.gradle settings.gradle $APP_HOME/
COPY --chown=gradle:gradle src $APP_HOME/src

#
# Build the application.
#
RUN gradle --no-daemon build

#
# Java image for the application to run in.
#
FROM eclipse-temurin:21-jre-alpine

#
# Copy the jar file in and name it app.jar.
#
ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
COPY --from=build-image $APP_HOME/build/libs/hh-autoupdate-resume-0.0.1-SNAPSHOT.jar app.jar

#
# The command to run when the container starts.
#
CMD java -jar app.jar