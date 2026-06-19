// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

tasks.register<Exec>("startBackend") {
    group = "application"
    description = "Start the Spring Boot backend and wait for port 8080"
    commandLine(
        "powershell.exe",
        "-NoProfile",
        "-ExecutionPolicy",
        "Bypass",
        "-File",
        file("scripts/start-backend.ps1").absolutePath
    )
}
