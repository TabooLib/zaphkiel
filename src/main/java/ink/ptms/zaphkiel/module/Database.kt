package ink.ptms.zaphkiel.module

import ink.ptms.zaphkiel.Zaphkiel
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.*

class Database {

    abstract class Type {

        abstract fun host(): Host<*>

        abstract fun tableVar(): Table<*, *>
    }

    class TypeSQL : Type() {

        val host = Zaphkiel.conf.getHost("Database")

        val tableVar = Table(Zaphkiel.conf.getString("Database.prefix") + "_var", host) {
            add { id() }
            add("user") {
                type(ColumnTypeSQL.VARCHAR, 36) {
                    options(ColumnOptionSQL.KEY)
                }
            }
            add("name") {
                type(ColumnTypeSQL.VARCHAR, 64) {
                    options(ColumnOptionSQL.KEY)
                }
            }
            add("data") {
                type(ColumnTypeSQL.VARCHAR, 64)
            }
        }

        override fun host(): Host<*> {
            return host
        }

        override fun tableVar(): Table<*, *> {
            return tableVar
        }
    }

    class TypeLocal : Type() {

        val host = newFile(getDataFolder(), "data.db").getHost()

        val tableVar = Table("zaphkiel_var", host) {
            add("user") {
                type(ColumnTypeSQLite.TEXT, 36) {
                    options(ColumnOptionSQLite.PRIMARY_KEY)
                }
            }
            add("name") {
                type(ColumnTypeSQLite.TEXT, 64)
            }
            add("data") {
                type(ColumnTypeSQLite.TEXT, 64)
            }
        }

        override fun host(): Host<*> {
            return host
        }

        override fun tableVar(): Table<*, *> {
            return tableVar
        }
    }

    val type = if (Zaphkiel.conf.getBoolean("Database.enable")) {
        TypeSQL()
    } else {
        TypeLocal()
    }

    val dataSource = type.host().createDataSource()

    init {
        type.tableVar().workspace(dataSource) { createTable() }.run()
    }

    operator fun get(user: String): Map<String, String> {
        return type.tableVar().workspace(dataSource) {
            select {
                rows("name", "data")
                where {
                    "user" eq user
                }
            }
        }.map {
            getString("name") to getString("data")
        }.toMap()
    }

    operator fun get(user: String, name: String): String? {
        return type.tableVar().workspace(dataSource) {
            select {
                rows("data")
                limit(1)
                where {
                    and {
                        "user" eq user
                        "name" eq name
                    }
                }
            }
        }.firstOrNull {
            getString("data")
        }
    }

    operator fun set(user: String, name: String, data: String) {
        if (get(user, name) == null) {
            type.tableVar().workspace(dataSource) {
                insert("user", "name", "data") {
                    value(user, name, data)
                }
            }.run()
        } else {
            type.tableVar().workspace(dataSource) {
                update {
                    where {
                        and {
                            "user" eq user
                            "name" eq name
                        }
                    }
                    set("data", data)
                }
            }.run()
        }
    }
}