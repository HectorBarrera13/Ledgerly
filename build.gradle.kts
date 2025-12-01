plugins {
    java
    id("org.sonarqube") version "7.1.0.6387"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "toast"
version = "0.0.1-SNAPSHOT"
description = "app-back"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("io.jsonwebtoken:jjwt:0.12.6")
    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(project(":model"))
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sonarqube {
    properties {
        set("sonar.projectKey", "HectorBarrera13_Ledgerly")
        set("sonar.organization", "hectorbarrera13")
        set("sonar.host.url", "https://sonarcloud.io") 
    }
}

