import moe.nikky.counter.CounterExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("moe.nikky.persistentCounter") version "0.0.8-SNAPSHOT"
}

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
    shadow(group = "blue.endless", name = "jankson", version = "1.2.0")
    compileOnly(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")
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
        val main = create("main", MavenPublication::class.java) {
            artifact(shadowJar) {
                classifier = "" // why do i need this GRADLE ?
            }
            artifact(sourcesJar)
            artifact(javadocJar)
            project.shadow.component(this)
        }
        if(isCI) {
            create("snapshot", MavenPublication::class.java) {
                version = "$major.$minor.$patch-SNAPSHOT"
                artifact(shadowJar) {
                    classifier = "" // why do i need this GRADLE ?
                }
                artifact(sourcesJar)
                artifact(javadocJar)
                project.shadow.component(this)
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