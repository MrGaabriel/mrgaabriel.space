package me.mrgaabriel.manager.handler

import me.mrgaabriel.manager.views.AbstractView
import me.mrgaabriel.manager.views.api.APIViewSimSimi
import me.mrgaabriel.manager.views.api.APIViewTranslate
import org.jooby.Request
import org.jooby.Response
import java.io.File

object GlobalHandler {

    val views = mutableListOf<AbstractView>()

    fun handle(req: Request, res: Response) {
        loadViews()

        views.filter { it.path == req.path() }.forEach {
            res.send(it.handle(req, res))

            return
        }

        if (req.path() == "/") {
            res.send(File("index.html").readText(Charsets.UTF_8))
            return
        }

        res.status(404)
        res.send("Erro 404!")
    }

    fun loadViews() {
        views.clear()

        views.add(APIViewSimSimi())
		views.add(APIViewTranslate())
    }
}