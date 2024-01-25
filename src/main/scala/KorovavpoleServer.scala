import EndPoint._
import ServerLogic._
import cats.implicits._
import com.comcast.ip4s._
import org.http4s.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import zio.RIO

import org.http4s.HttpRoutes
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio.interop.catz.asyncInstance

object KorovavpoleServer {

    type AppEnv = Any

    type AppRIO[A] = RIO[AppEnv, A]

    private val routes: HttpRoutes[AppRIO] = {
        ZHttp4sServerInterpreter().from(List(healthCheckEndpointLogic, createUserEndpointLogic, deleteUserEndpointLogic, lockUserEndpointLogic, unlockUserEndpointLogic)).toRoutes
    }

    private val routesSwagger: HttpRoutes[AppRIO] = {
        ZHttp4sServerInterpreter()
            .from(SwaggerInterpreter()
                .fromEndpoints[AppRIO](List(healthCheckEndpoint, createUserEndpoint, deleteUserEndpoint, lockUserEndpoint, unlockUserEndpoint), "Korova Admin", "1.0")
            ).toRoutes
    }

    def run: AppRIO[Nothing] = {
        for {
            _ <- EmberServerBuilder
                    .default[AppRIO]
                    .withHost(ipv4"127.0.0.1")
                    .withPort(port"8080")
                    .withHttpApp(Router("/" -> (routes <+> routesSwagger)).orNotFound)
                    .build
        } yield ()
    }.useForever
}
