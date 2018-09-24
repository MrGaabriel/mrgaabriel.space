package me.mrgaabriel.manager

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.FileLoader
import me.mrgaabriel.manager.handler.GlobalHandler
import org.jooby.Kooby
import org.jooby.Request
import org.slf4j.LoggerFactory
import java.io.File
import java.io.StringWriter

class Website(val folder: String) : Kooby({
    val logger = LoggerFactory.getLogger(Website::class.java)

    port(4615)
    assets("/**", File("static/").toPath()).onMissing(0)

    before { req, rsp ->
        logger.info("${req.ip} -> ${req.path} - Processando...")
        logger.info("(User-Agent: ${req.header("User-Agent").value()})")
    }

    complete("*") { req, rsp, e ->
        if (e.isPresent) { // oh no
            logger.info("${req.ip} -> ${req.path} - ERROR!")
            return@complete
        }

        logger.info("${req.ip} -> ${req.path} - OK!")
    }

    get("*") { req, rsp ->
        GlobalHandler.handle(req, rsp)
    }

    post("*") { req, rsp ->
        GlobalHandler.handle(req, rsp)
    }
}) {

    companion object {
        lateinit var ENGINE: PebbleEngine
    }

    init {
        ENGINE = PebbleEngine.Builder().cacheActive(false).strictVariables(true).build()
    }

}

fun evaluate(file: String, variables: MutableMap<String, Any?> = mutableMapOf()): String {
    val writer = StringWriter()
    Website.ENGINE.getTemplate(file).evaluate(writer, variables)
    return writer.toString()
}

val Request.path: String get() {
    return this.path() + if (this.queryString().isPresent) "?${this.queryString().get()}" else ""
}

val Request.ip: String get() {
    val header = this.header("X-Forwarded-For")

    if (header.isSet) {
        return header.value()
    }

    return this.ip()
}