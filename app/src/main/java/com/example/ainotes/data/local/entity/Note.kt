package com.example.ainotes.data.local.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Note(
    @PrimaryKey
    var id: Long = 0,
    var title: String = "",
    var note: String = ""
) : RealmObject()