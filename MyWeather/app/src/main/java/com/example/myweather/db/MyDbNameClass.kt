package com.example.myweather.db

import android.provider.BaseColumns

object MyDbNameClass : BaseColumns {
    const val TABLE_NAME = "my_table"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_NAME_CONTENT2 = "content2"
    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "MyWeatherDb.db"

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "$COLUMN_NAME_TITLE TEXT, " +
                "$COLUMN_NAME_CONTENT TEXT, " +
                "$COLUMN_NAME_CONTENT2 TEXT)"

    const val DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}