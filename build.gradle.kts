import net.minecraftforge.gradle.userdev.UserDevExtension
import org.jetbrains.kotlin.konan.properties.loadProperties
import org.spongepowered.asm.gradle.plugins.MixinExtension

val modGroup: String by extra
val modVersion: String by extra

group = modGroup
version = modVersion

buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/public")
        maven("https://lss233.littleservice.cn/repositories/minecraft")
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }

    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:4.+")
        classpath("org.spongepowered:mixingradle:0.7-SNAPSHOT")
    }
}

plugins {
    idea
    java
    kotlin("jvm") version "1.6.0"
}

apply {
    plugin("net.minecraftforge.gradle")
    plugin("org.spongepowered.mixin")
}

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://lss233.littleservice.cn/repositories/minecraft")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://impactdevelopment.github.io/maven/")
}

val library by configurations.creating

val kotlinVersion: String by project
val kotlinxCoroutineVersion: String by project


dependencies {
    // Jar packaging
    fun ModuleDependency.exclude(moduleName: String): ModuleDependency {
        return exclude(mapOf("module" to moduleName))
    }

    fun jarOnly(dependencyNotation: Any) {
        library(dependencyNotation)
    }

    // Forge
    val minecraft = "minecraft"
    minecraft("net.minecraftforge:forge:1.12.2-14.23.5.2860")

    library(kotlin("stdlib", kotlinVersion))
    library(kotlin("reflect", kotlinVersion))
    library(kotlin("stdlib-jdk8", kotlinVersion))

    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutineVersion")

    // This Baritone will NOT be included in the jar
    implementation("com.github.cabaletta:baritone:1.2.14")

    jarOnly("cabaletta:baritone-api:1.2")

    library("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        exclude("commons-io")
        exclude("gson")
        exclude("guava")
        exclude("launchwrapper")
        exclude("log4j-core")
    }

    library("org.joml:joml:1.10.1")

    annotationProcessor("org.spongepowered:mixin:0.8.2:processor") {
        exclude("gson")
    }

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
                    "fml.coreMods.load" to "studio.coni.epsilon.launch.CoreMod",
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
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xinline-classes")
        }
    }

    jar {
        manifest {
            attributes(
                "FMLCorePluginContainsFMLMod" to "true",
                "FMLCorePlugin" to "studio.coni.epsilon.launch.CoreMod",
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

        exclude(
            "META-INF/versions/**",
            "**/*.RSA",
            "**/*.SF",
            "**/module-info.class",
            "**/LICENSE",
            "**/*.txt"
        )
    }

}