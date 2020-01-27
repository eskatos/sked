plugins {
    `java-library`
}

group = "org.codeartisans"
version = "2.2-SNAPSHOT"

dependencies {
    compileOnly("org.slf4j:slf4j-api:1.7.7")
    testImplementation("org.codeartisans:junit-toolbox:1.0")
    testImplementation("joda-time:joda-time:1.6.2")
    testImplementation("ch.qos.logback:logback-classic:1.1.2")
}

repositories {
    mavenCentral()
}
 
