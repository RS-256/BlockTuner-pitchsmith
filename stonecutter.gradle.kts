plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT" apply false
    // id("me.modmuss50.mod-publish-plugin") version "1.1.0" apply false
}

stonecutter active "26.1.2"

stonecutter parameters {
    swaps["mod_version"] = "\"${property("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    constants["release"] = property("mod.id") != "template"
    dependencies["fapi"] = node.project.property("deps.fabric_api") as String
}

tasks.register("runClientCurrentVersion") {
    group = "run"
    description = "Runs the client for the active stonecutter version."
    dependsOn(project(":${sc.current?.version}").tasks.named("runClient"))
}

tasks.register("runServerCurrentVersion") {
    group = "run"
    description = "Runs the server for the active stonecutter version."
    dependsOn(project(":${sc.current?.version}").tasks.named("runServer"))
}

val releaseVersions = listOf(
    "1.21.11",
    "26.1.2"
)

tasks.register("buildReleaseRemapped") {
    group = "build"
    description = "Build remapped jars only for the release versions."
    dependsOn(releaseVersions.map { v -> ":$v:buildAndCollectRemapped" })
}
