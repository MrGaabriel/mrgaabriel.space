package me.mrgaabriel.manager.views

import org.jooby.Request
import org.jooby.Response

abstract class AbstractView(val path: String) {

    abstract fun handle(req: Request, res: Response): Any

}