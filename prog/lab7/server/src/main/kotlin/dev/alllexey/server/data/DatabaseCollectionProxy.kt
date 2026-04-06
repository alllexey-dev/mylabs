package dev.alllexey.server.data

import dev.alllexey.common.model.field.FieldType
import dev.alllexey.common.model.field.ObjectMeta
import dev.alllexey.server.App
import dev.alllexey.server.field.ObjectMetaMapper
import dev.alllexey.server.model.IdHolder
import dev.alllexey.server.user.User
import java.sql.PreparedStatement
import java.util.concurrent.locks.ReentrantLock
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class DatabaseCollectionProxy<T : IdHolder>(
    app: App,
    val type: KClass<T>,
    val runtimeManager: CollectionManager<T>,
    val tableName: String = "items",
) : CollectionManager<T>(app) {

    val columns = getColumnTypes(ObjectMetaMapper.buildMeta(type))

    fun createTable() {
        val sql = buildString {
            append("CREATE TABLE IF NOT EXISTS $tableName (")
            append("id BIGSERIAL PRIMARY KEY")

            for (column in columns) {
                if (column.name == "id") continue
                append(", ")
                append("${column.name} ${column.type}")
            }

            append(");")
        }

        app.database.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(sql)
            }
        }
    }

    fun loadAll() {
        val sql = "SELECT * FROM $tableName"

        app.database.connection.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)

                while (rs.next()) {
                    val obj = ResultSetMapper.mapRow(rs, type)
                    runtimeManager.addItem(obj)
                }
            }
        }
    }

    override fun items(): Collection<T> {
        return runtimeManager.items()
    }

    override fun addItem(obj: T) {
        val flat = flattenObject(type, obj).filter { it.first != "id" }

        val columns = flat.map { it.first }
        val values = flat.map { it.second }

        val sql = buildString {
            append("INSERT INTO $tableName (")
            append(columns.joinToString(", "))
            append(") VALUES (")
            append(columns.joinToString(", ") { "?" })
            append(") RETURNING id")
        }

        app.database.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                values.forEachIndexed { i, value ->
                    stmt.setSmart(i + 1, value)
                }

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val id = rs.getLong("id")
                    obj.id = id

                    runtimeManager.addItem(obj)
                } else {
                    throw RuntimeException("Failed to insert object")
                }
            }
        }
    }

    override fun modifyItem(obj: T, user: User?) {
        val existing = runtimeManager.getItemById(obj.id)
            ?: throw IllegalArgumentException("Объект не найден")

        if (user == null || existing.creatorId != user.id) {
            throw IllegalAccessException("Нет прав на изменение объекта")
        }

        val flat = flattenObject(type, obj)

        val assignments = flat.map { "${it.first} = ?" }
        val values = flat.map { it.second }

        val sql = buildString {
            append("UPDATE $tableName SET ")
            append(assignments.joinToString(", "))
            append(" WHERE id = ?")
        }

        app.database.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                values.forEachIndexed { i, value ->
                    stmt.setSmart(i + 1, value)
                }

                stmt.setLong(values.size + 1, obj.id)

                val updated = stmt.executeUpdate()

                if (updated > 0) {
                    runtimeManager.modifyItem(obj, user)
                } else {
                    throw RuntimeException("Update failed")
                }
            }
        }
    }

    override fun getItemById(id: Long): T? {
        return runtimeManager.getItemById(id)
    }

    override fun containsItemById(id: Long): Boolean {
        return runtimeManager.containsItemById(id)
    }

    override fun removeItem(id: Long, user: User?) {
        val existing = runtimeManager.getItemById(id)
            ?: return

        if (user == null || existing.creatorId != user.id) {
            throw IllegalAccessException("Нет прав на удаление объекта")
        }

        val sql = "DELETE FROM $tableName WHERE id = ?"

        app.database.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, id)

                val deleted = stmt.executeUpdate()

                if (deleted > 0) {
                    runtimeManager.removeItem(id, user)
                }
            }
        }
    }

    override fun clear(user: User?) {
        if (user == null) {
            throw IllegalAccessException("Необходим пользователь")
        }

        val sql = "DELETE FROM $tableName WHERE creator_id = ?"

        app.database.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, user.id)

                val deleted = stmt.executeUpdate()

                if (deleted > 0) {
                    runtimeManager.clear(user)
                }
            }
        }
    }

    override fun type(): String = runtimeManager.type()

    override fun size(): Int = runtimeManager.size()

    private fun PreparedStatement.setSmart(index: Int, value: Any?) {
        when (value) {
            null -> setObject(index, null)
            is Enum<*> -> setString(index, value.name)
            else -> setObject(index, value)
        }
    }

    companion object {

        private fun getColumnTypes(objectMeta: ObjectMeta): List<ColumnMeta> {
            return objectMeta.fields
                .flatMap { field ->

                    val columnType = when (val fieldType = field.type) {
                        FieldType.BooleanType -> "BOOLEAN"
                        FieldType.DoubleType -> "DOUBLE PRECISION"
                        is FieldType.EnumType -> "TEXT"
                        FieldType.FloatType -> "REAL"
                        FieldType.IntegerType -> "INTEGER"
                        FieldType.LocalDateTimeType -> "TIMESTAMP"
                        FieldType.LongType -> "BIGINT"

                        is FieldType.ObjectType -> {
                            return@flatMap getColumnTypes(fieldType.objectMeta)
                                .map {
                                    it.copy(name = field.fieldName + SEPARATOR + it.name)
                                }
                        }

                        FieldType.StringType -> "TEXT"
                    }

                    val nullability = if (field.nullable) "" else " NOT NULL"

                    listOf(
                        ColumnMeta(
                            name = field.fieldName,
                            type = columnType + nullability
                        )
                    )
                }
        }

        private fun flattenObject(type: KClass<*>, obj: Any): List<Pair<String, Any>> {
            val meta = ObjectMetaMapper.buildMeta(type)
            val properties = type.memberProperties
                .associateBy { it.name }

            return meta.fields.flatMap { field ->
                val prop = properties[field.fieldName] as KProperty1<Any, *>
                val value = prop.get(obj) ?: return@flatMap listOf()

                if (field.type is FieldType.ObjectType) {
                    return@flatMap flattenObject(value::class, value)
                        .map { field.fieldName + SEPARATOR + it.first to it.second }
                }

                listOf(field.fieldName to value)
            }
        }

        const val SEPARATOR = "_"
    }

}