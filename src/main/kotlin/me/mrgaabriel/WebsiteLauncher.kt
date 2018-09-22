package me.mrgaabriel

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import me.mrgaabriel.manager.Website
import me.mrgaabriel.utils.SimSimiResponse
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.slf4j.LoggerFactory

object WebsiteLauncher {

    lateinit var mongo: MongoClient
    lateinit var mongoDatabase: MongoDatabase

    lateinit var simsimiColl: MongoCollection<SimSimiResponse>

    val logger = LoggerFactory.getLogger(WebsiteLauncher::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("Iniciando website...")

        initMongo()

        val website = Website("frontend")
        org.jooby.run({
            website
        })
    }

    fun initMongo() {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        context.getLogger("org.mongodb.driver.connection").level = Level.OFF

        val pojo = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()))

        val options = MongoClientOptions.builder()
                .codecRegistry(pojo)
                .build()

        mongo = MongoClient("127.0.0.1:27017", options)

        mongoDatabase = mongo.getDatabase("mrgaabriel-website")
        simsimiColl = mongoDatabase.getCollection("simsimi", SimSimiResponse::class.java)
    }
}