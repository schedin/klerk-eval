package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.migration.MigrationModelV1
import dev.klerkframework.klerk.migration.MigrationStepV1toV1

object MyMigrationStep1to2 : MigrationStepV1toV1 {
    override val description = "My first migration"
    override val migratesToVersion = 2
    override fun migrateModel(original: MigrationModelV1): MigrationModelV1? =
        if (original.type == "Todo")
            renameKey(original, "coAuthors", "changed")
        else
            original
}