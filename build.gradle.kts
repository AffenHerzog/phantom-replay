plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

val paperVersion = "1.21.11-R0.1-SNAPSHOT"
val protocolLibVersion = "5.4.0"
val hikariVersion = "7.1.0"
val mariaDbVersion = "3.5.9"
val gsonVersion = "2.14.0"
val gsonExtrasVersion = "2.13.2-rc1"
val lombokVersion = "1.18.46"
val junitVersion = "6.1.2"

dependencies {
    compileOnly("io.papermc.paper:paper-api:${paperVersion}")
    compileOnly("net.dmulloy2:ProtocolLib:${protocolLibVersion}")
    compileOnly("com.google.code.gson:gson:${gsonVersion}")
    compileOnly("org.projectlombok:lombok:$lombokVersion")

    implementation("com.zaxxer:HikariCP:${hikariVersion}")
    implementation("org.mariadb.jdbc:mariadb-java-client:${mariaDbVersion}")
    implementation("com.google.code.gson:gson-extras:${gsonExtrasVersion}")

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("io.papermc.paper:paper-api:${paperVersion}")
    testImplementation("com.google.code.gson:gson:${gsonVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")

        downloadPlugins {
            url("https://github.com/dmulloy2/ProtocolLib/releases/download/5.4.0/ProtocolLib.jar")
        }

        systemProperty("terminal.jline", "false")
        systemProperty("terminal.ansi", "true")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    test {
        useJUnitPlatform()
    }
}
