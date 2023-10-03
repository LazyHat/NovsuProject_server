package ru.lazyhat.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.lazyhat.models.Credentials
import ru.lazyhat.repository.AdminRepository

fun Route.adminRouting() {
    val adminRepository by inject<AdminRepository>()
    get("admin/login") {
        val username = call.request.queryParameters["username"]
        val password = call.request.queryParameters["password"]
        if (username != null && password != null) {
            val credentials = Credentials(username, password)
            adminRepository.validateSuperUser(credentials).let {
                if (it)
                    call.respond(adminRepository.createUserToken(credentials))
                else
                    call.respond(HttpStatusCode.Forbidden)
            }
        } else call.respond(HttpStatusCode.BadRequest)
    }

    authenticate("admin") {
        route("admin") {
            get("lessons"){
                call.respond(adminRepository.getAllLessons())
            }
        }
    }
}