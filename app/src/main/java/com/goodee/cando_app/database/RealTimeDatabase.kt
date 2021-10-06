package com.goodee.cando_app.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

object RealTimeDatabase {
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    public fun getDatabase(): DatabaseReference {
        if (mDatabase == null) throw Exception("Database is null")

        return mDatabase
    }
}