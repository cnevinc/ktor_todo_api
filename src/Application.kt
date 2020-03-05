package io.kraftsman

import io.kraftsman.entities.Task
import io.kraftsman.requests.TaskRequest
import io.kraftsman.responses.TaskResponse
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson {

        }
    }

    Database.connect(
        url = "jdbc:mysql://127.0.0.1:8889/todo_api?useUnicode=true&characterEncoding=utf-8&useSSL=false",
        driver = "com.mysql.jdbc.Driver",
        user = "root",
        password = "root"
    )

    routing {
        get("/api/tasks") {
            val tasks = transaction {
                Task.all().map {
                    TaskResponse(
                        it.id.value,
                        it.title,
                        it.completed,
                        it.createdAt.toString("yyyy-MM-dd HH:mm:ss"),
                        it.updatedAt.toString("yyyy-MM-dd HH:mm:ss")
                    )
                }
            }

            call.respond(mapOf("data" to tasks))
        }

        post("/api/tasks") {
            val request = call.receive<TaskRequest>()
            transaction {
                Task.new {
                    title = request.title
                    completed = false
                    createdAt = DateTime.now()
                    updatedAt = DateTime.now()
                }
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/api/tasks/{id}/complete") {
            val id = call.parameters["id"]?.toInt()
            transaction {
                if (id != null) {
                    val task = Task.findById(id)
                    task?.completed = true
                }
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/api/tasks/{id}/delete") {
            val id = call.parameters["id"]?.toInt()
            transaction {
                if (id != null) {
                    val task = Task.findById(id)
                    task?.delete()
                }
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}
