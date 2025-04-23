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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}







tasks.test {
    useJUnitPlatform()
}



