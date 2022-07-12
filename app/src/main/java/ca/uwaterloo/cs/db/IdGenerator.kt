package ca.uwaterloo.cs.db

import java.util.*

fun idGenerator(idType: IdType): Id{
    val id: String = UUID.randomUUID().toString()
    return Id(id, idType)
}