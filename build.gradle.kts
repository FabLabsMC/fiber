import moe.nikky.counter.CounterExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("moe.nikky.persistentCounter") version "0.0.8-SNAPSHOT"
}

//applyy {
//
//}

val major: String by project
val minor : String by project
val patch: String by project

val branch = System.getenv("GIT_BRANCH")
    ?.takeUnless { it == "master" }
    ?.let { "-$it" }
    ?: ""
val isCI = System.getenv("BUILD_NUMBER") != null

val counter: CounterExtension = project.extensions.getByType()
val buildnumber = counter.variable(id = "buildnumber", key = "$major.$minor.$patch$branch")

group = "me.zeroeightsix"
version = "$major.$minor.$patch" + if (isCI) "-$buildnumber" else "-dev"

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
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.4.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
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
        if(isCI) {
            create("snapshot", MavenPublication::class.java) {
                artifact(shadowJar)
                artifact(sourcesJar)
                artifact(javadocJar)
                
                version = "$major.$minor.$patch-SNAPSHOT"
            }
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