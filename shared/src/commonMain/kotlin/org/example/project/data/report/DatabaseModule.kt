package org.example.project.data.report

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.concurrent.Volatile

object DatabaseModule {
    @Volatile
    private var _db: AppDatabase? = null

    fun init(factory: DatabaseDriverFactory) {
        if (_db == null) {
            _db = AppDatabase(factory.createDriver())
        }
    }

    internal val db: AppDatabase
        get() = _db ?: error("DatabaseModule.init() not called")
}

class LocalReportDataSource(
    private val db: AppDatabase = DatabaseModule.db,
    private val io: CoroutineDispatcher = Dispatchers.Default
) {
    private val q get() = db.reportQueries
    fun observeAll(): Flow<List<Reports>> =
        q.selectAll().asFlow().mapToList(io)

    fun getAll(): List<Reports> = q.selectAll().executeAsList()
    fun getByUser(userId: String): List<Reports> =
        q.selectByUser(userId).executeAsList()

    fun upsert(model: ReportModel) {
        q.upsertReport(
            id = model.id,
            userId = model.userId,
            description = model.description,
            name = model.name,
            phone = model.phone,
            imageUrl = model.imageUrl,
            isLost = model.isLost,
            location = model.location,
            lat = model.lat,
            lng = model.lng,
            createdAt = model.createdAt
        )
    }

    fun replaceAllForUser(userId: String, items: List<ReportModel>) {
        db.transaction {

            items.forEach { upsert(it) }
        }
    }

    fun deleteById(id: String) = q.deleteReport(id)
}