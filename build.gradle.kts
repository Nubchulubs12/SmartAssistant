import org.gradle.jvm.tasks.Jar
import org.gradle.api.file.DuplicatesStrategy

plugins {
    id ("application")
    id ("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
}




dependencies {
    implementation ("org.openjfx:javafx-controls:21")
    implementation ("org.openjfx:javafx-fxml:21")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("org.example.MainGUI")
}


        tasks.withType<Jar> {
            manifest {
                attributes["Main-Class"] = "org.example.MainGUI"
            }

            duplicatesStrategy = DuplicatesStrategy.EXCLUDE

            from({
                configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
            })
        }


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}




tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.MainGUI"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}


tasks.register<Copy>("copyJarToPortable") {
    dependsOn("clean", "jar")
    from(buildDir.resolve("libs/SmartAssistant.jar"))

    into("C:/Users/nickk/Desktop/smart assist portable/SmartAssistant_jar")
    doLast {
        println("Copied SmartAssistant.jar to app folder!")
    }
}



tasks.test {
    useJUnitPlatform()
}



