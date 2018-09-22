package me.mrgaabriel.utils

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty

class SimSimiResponse @BsonCreator constructor(@BsonProperty("_id") _id: String) {

    @BsonProperty("_id")
    val question = _id // Isto equivale a pergunta
    var response = ""
}