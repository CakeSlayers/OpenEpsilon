import net.minecraftforge.gradle.userdev.UserDevExtension
import org.spongepowered.asm.gradle.plugins.MixinExtension

val modGroup: String by extra
val modVersion: String by extra

group = modGroup
version = modVersion

buildscript {
    repositories {
        jcenter()
        maven("https://files.minecraftforge.net/maven")
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }

    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:4.+")
        classpath("org.spongepowered:mixingradle:0.7-SNAPSHOT")
    }
}

plugins {
    java
    kotlin("jvm")
}

apply {
    plugin("net.minecraftforge.gradle")
    plugin("org.spongepowered.mixin")
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://impactdevelopment.github.io/maven/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://jitpack.io")
}

val library: Configuration by configurations.creating {
}

dependencies {
    val kotlinVersion: String by project
    val kotlinxCoroutineVersion: String by project

    fun minecraft(dependencyNotation: Any): Dependency? =
        "minecraft"(dependencyNotation)

    fun ModuleDependency.exclude(moduleName: String) =
        exclude(mapOf("module" to moduleName))

    library(kotlin("stdlib", kotlinVersion))
    library(kotlin("reflect", kotlinVersion))
    library(kotlin("stdlib-jdk8", kotlinVersion))
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutineVersion")

    minecraft("net.minecraftforge:forge:1.12.2-14.23.5.2854")

    library("org.spongepowered:mixin:0.8-SNAPSHOT") {
        exclude("commons-io")
        exclude("gson")
        exclude("guava")
        exclude("launchwrapper")
        exclude("log4j-core")
    }

    library("org.joml:joml:1.10.1")

    implementation("com.github.cabaletta:baritone:1.2.14")
    library("cabaletta:baritone-api:1.2")

    annotationProcessor("org.spongepowered:mixin:0.8.2:processor") {
        exclude("gson")
    }
	
	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
	
    implementation(library)
}

configure<MixinExtension> {
    defaultObfuscationEnv = "searge"
    add(sourceSets["main"], "mixins.epsilon.refmap.json")
}

configure<UserDevExtension> {
    mappings(
        mapOf(
            "channel" to "stable",
            "version" to "39-1.12"
        )
    )

    runs {
        create("client") {
            workingDirectory = project.file("run").path

            properties(
                mapOf(
                    "forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP",
                    "forge.logging.console.level" to "info",
                    "fml.coreMods.load" to "com.client.epsilon.launch.FMLCoreMod",
                    "mixin.env.disableRefMap" to "true"
                )
            )
        }
    }
}

tasks {

    compileJava {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            useIR = true
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xinline-classes")
        }
    }

    jar {
        manifest {
            attributes(
                "FMLCorePluginContainsFMLMod" to "true",
                "FMLCorePlugin" to "com.client.epsilon.launch.FMLCoreMod",
                "MixinConfigs" to "mixins.epsilon.json",
                "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
                "TweakOrder" to 0,
                "ForceLoadAsMod" to "true"
            )
        }

        from(
            library.map {
                if (it.isDirectory) it
                else zipTree(it)
            }
        )
    }

}