package io.kraftsman

import io.kraftsman.entities.Task
import io.kraftsman.tables.Tasks
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

fun main() {
    Database.connect(
        //url = "jdbc:h2:~/test;DB_CLOSE_DELAY=-1",
        url = "jdbc:h2:mem:todo_api;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver"
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
