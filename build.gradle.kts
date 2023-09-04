plugins {
    id("java")
    id("io.freefair.lombok") version "8.2.2"
}

group = "org.clever_bank"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("com.itextpdf:itextpdf:5.5.13.3")


    compileOnly("javax.servlet:javax.servlet-api:4.0.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}