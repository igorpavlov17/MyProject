package com.example.myweather.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class MyDbManager(context: Context) {
    private val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDB() {
        db = myDbHelper.writableDatabase
    }

    fun insertToDb(title: String, content: String, content2: String?) {
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_CONTENT2, content2)
        }
        db?.insert(MyDbNameClass.TABLE_NAME, null, values)
    }

    @SuppressLint("Range")
    fun readDbData(): ArrayList<Cell> {
        val list = ArrayList<Cell>()
        val cursor = db?.query(MyDbNameClass.TABLE_NAME, null, null, null, null, null, null)
        while (cursor?.moveToNext()!!) {
            list.add(
                Cell(
                    cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT)),
                    cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT2))
                )
            )
        }
        cursor.close()
        return list
    }

    fun getTitleByContent(content: String): String{
        val list = readDbData()
        var title = ""
        list.onEach {
            if (it.content == content) {
                title = it.title
            }
        }
        return title
    }

    fun getContentByTitle(title: String): String {
        val list = readDbData()
        var content = ""
        list.onEach {
            if (it.title == title) {
                content = it.content
            }
        }
        return content
    }

    fun getContent2ByTitle(title: String): String{
        val list = readDbData()
        var content2 = ""
        list.onEach {
            if (it.title == title) {
                content2 = it.content2
            }
        }
        return content2
    }

    fun closeDb() {
        myDbHelper.close()
    }
}