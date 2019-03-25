import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "me.zeroeightsix"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

configurations.api.get().extendsFrom(configurations.shadow.get())
dependencies {
    api(group = "blue.endless", name = "jankson", version = "1.1.1")
    api(group = "com.google.guava", name = "guava", version = "27.1-jre")
}

val shadowJar by tasks.getting(ShadowJar::class) {
    archiveClassifier.set("")
    configurations = listOf(
        project.configurations.shadow.get()
    )
    exclude("META-INF")
}

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadoc = tasks.getByName<Javadoc>("javadoc") {}
val javadocJar = tasks.create<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(javadoc)
}

publishing {
    publications {
        create("main", MavenPublication::class.java) {
            artifact(shadowJar)
            artifact(sourcesJar)
            artifact(javadocJar)
        }
    }
    repositories {
        maven(url = "http://mavenupload.modmuss50.me/") {
            val mavenPass: String? = project.properties["mavenPass"] as String?
            mavenPass?.let {
                credentials {
                    username = "buildslave"
                    password = mavenPass
                }
            }
        }
    }
}