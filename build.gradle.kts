plugins {
    java
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.modPublishPlugin)
    `maven-publish`
}

val modId = property("mod.id").toString()
val modName = property("mod.name").toString()
val modGroup = property("mod.group").toString()
val modVersion = property("mod.version").toString()

val githubRepo = property("repo.github").toString()
val modrinthId = property("modrinth.id").toString()

base.archivesName = modId
group = modGroup
version = "$modVersion+mc${libs.versions.minecraft.get()}"

loom {
    runtimeOnlyLog4j = true

    splitEnvironmentSourceSets()

    runs {
        configureEach {
            jvmArguments.addAll(
                "-enableassertions",

                // Mixin debugging, should make failures happen quicker
                "-Dmixin.debug.verify=true",
                "-Dmixin.debug.strict=true",
                "-Dmixin.debug.countInjections=true",

                // Memory Usage Optimization
                "-XX:+UseZGC",
                "-XX:+UseCompactObjectHeaders",
                "-XX:+UseStringDeduplication",

                "-XX:+AlwaysPreTouch" // Apparently makes startup faster
            )
        }
    }

    mods.create(modId) {
        sourceSet("main")
    }

    accessWidenerPath = file("src/main/resources/$modId.classtweaker")
}

repositories {
    mavenCentral()

    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
}

dependencies {
    minecraft(libs.minecraft)

    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)

    localRuntime(libs.modMenu)
    localRuntime(libs.noChatRestrictions)
    localRuntime(libs.sodium)
}

val resourcesVars = mapOf(
    "mod_id" to modId,
    "mod_name" to modName,
    "mod_group" to modGroup,
    "mod_version" to version,
    "repo_github" to githubRepo,

    "fabric_loader_version" to libs.versions.fabric.loader.get(),
    "fabric_api_version" to libs.versions.fabric.api.get(),
    "minecraft_version" to libs.versions.minecraft.get(),
)

tasks.withType<ProcessResources> {
    inputs.properties(resourcesVars)
    filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(resourcesVars) }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 25
}

tasks.jar {
    inputs.property("archivesName", project.base.archivesName)
    from("LICENSE.md")
    from("LICENSE-MPL-2.0.txt")
}

publishMods {
    file = tasks.jar.get().archiveFile
    modLoaders.add("fabric")

    version = modVersion
    type = STABLE

    changelog = providers.fileContents(layout.projectDirectory.file("CHANGELOG.md")).asText

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")

        projectId = modrinthId
        minecraftVersions.add(libs.versions.minecraft.get())

        incompatible { id = "r0v8vy1s" }

        environment = SERVER_ONLY_CLIENT_OPTIONAL

        requires("fabric-api")
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")

        displayName = "$modName $version for MC ${libs.versions.minecraft.get()}"

        repository = githubRepo
        tagName = version
        commitish = providers.environmentVariable("GIT_BRANCH")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "SpiritStudiosReleases"
            url = uri("https://maven.spiritstudios.dev/releases")
            credentials(PasswordCredentials::class)
        }

        maven {
            name = "SpiritStudiosSnapshots"
            url = uri("https://maven.spiritstudios.dev/snapshots")
            credentials(PasswordCredentials::class)
        }

        mavenLocal()
    }
}
