package com.aiblooddiagnostics.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.aiblooddiagnostics.routes.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("AI Blood Diagnostics API - Server is running!")
        }
        
        get("/health") {
            call.respond(mapOf("status" to "healthy", "service" to "AI Blood Diagnostics API"))
        }
        
        // API Routes
        authRoutes()
        doctorRoutes()
        patientRoutes()
        connectionRoutes()
        testUploadRoutes()
        chatRoutes()
        diagnosisRoutes()
    }
}
