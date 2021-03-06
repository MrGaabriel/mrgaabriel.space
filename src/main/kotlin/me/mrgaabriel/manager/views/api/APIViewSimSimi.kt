package me.mrgaabriel.manager.views.api

import com.github.salomonbrys.kotson.nullString
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.set
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import me.mrgaabriel.WebsiteLauncher
import me.mrgaabriel.manager.views.AbstractView
import me.mrgaabriel.utils.SimSimiResponse
import me.mrgaabriel.utils.extensions.random
import org.apache.commons.lang3.StringUtils
import org.jooby.MediaType
import org.jooby.Request
import org.jooby.Response
import java.util.*

class APIViewSimSimi : AbstractView("/api/simsimi") {

    override fun handle(req: Request, res: Response): Any {
        val json = JsonObject()

        res.type(MediaType.json)
        if (req.method() == "GET") {
            val questionParam = req.param("question")

            if (!questionParam.isSet) {
                json["api:code"] = 1
                json["error"] = "Param \"question\" is not set"

                return json
            }

            val question = StringUtils.stripAccents(questionParam.value().toLowerCase())

            val found = WebsiteLauncher.simsimiColl.find(
                    Filters.eq("_id", question)
            ).firstOrNull()

            if (found == null) {
                json["api:code"] = 2
                json["message"] = "There's no response for this question"

                return json
            }

            json["api:code"] = 0
            json["response"] = found.responses.random()

            return json
        } else if (req.method() == "POST") {
            val bodyMutant = req.body()

            if (!bodyMutant.isSet) {
                res.status(400)

                return ""
            }

            val body = bodyMutant.value()
            val payload = JsonParser().parse(body).obj // Payload enviado no request

            // O payload deve seguir o seguinte formato: {"question":"pergunta", "response":"resposta"}
            // Caso não siga, código 400

            val question = payload["question"].nullString
            val response = payload["response"].nullString

            if (question != null && response != null) {
                val qst = StringUtils.stripAccents(question).toLowerCase()

                val found = WebsiteLauncher.simsimiColl.find(
                        Filters.eq("_id", qst)
                ).firstOrNull()

                if (found != null) {
                    found.responses.add(response)

                    WebsiteLauncher.simsimiColl.replaceOne(Filters.eq("_id", qst), found, UpdateOptions().upsert(true))
                } else {
                    val wrapper = SimSimiResponse(qst)

                    wrapper.responses.add(response)
                    WebsiteLauncher.simsimiColl.insertOne(wrapper)
                }
            } else {
                res.status(400)

                return ""
            }
        }

        return ""
    }
}