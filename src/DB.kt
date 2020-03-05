package io.kraftsman

import io.kraftsman.entities.Task
import io.kraftsman.tables.Tasks
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

fun main() {
    Database.connect(
        url = "jdbc:mysql://127.0.0.1:8889/todo_api?useUnicode=true&characterEncoding=utf-8&useSSL=false",
        driver = "com.mysql.jdbc.Driver",
        user = "root",
        password = "root"
    )

    transaction {
        SchemaUtils.drop(Tasks)
        SchemaUtils.create(Tasks)
    }

    transaction {
        for (index in 1..10) {
            val task = Task.new {
                title = "Task $index"
                completed = listOf(true, false, false).shuffled().first()
                createdAt = DateTime.now()
                updatedAt = DateTime.now()
            }
            println("Task ${task.id} created")
        }
    }
}
