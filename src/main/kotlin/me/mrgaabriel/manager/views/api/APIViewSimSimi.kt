package me.mrgaabriel.manager.views.api

import com.github.salomonbrys.kotson.nullString
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.set
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mongodb.client.model.Filters
import me.mrgaabriel.WebsiteLauncher
import me.mrgaabriel.manager.views.AbstractView
import me.mrgaabriel.utils.SimSimiResponse
import org.jooby.MediaType
import org.jooby.Request
import org.jooby.Response

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

            val question = questionParam.value()

            val found = WebsiteLauncher.simsimiColl.find(
                    Filters.eq("_id", question)
            ).firstOrNull()

            if (found == null) {
                json["api:code"] = 2
                json["message"] = "There's no response for this question"

                return json
            }

            json["api:code"] = 0
            json["response"] = found.response

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
                val responseWrapper = SimSimiResponse(question).apply { this.response = response }

                WebsiteLauncher.simsimiColl.insertOne(
                        responseWrapper
                )
            } else {
                res.status(400)

                return ""
            }
        }

        return ""
    }
}