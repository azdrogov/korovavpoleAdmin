import cats.syntax.all._
import io.circe.generic.auto._
import com.comcast.ip4s._
import org.http4s.Method.GET
import org.http4s.Uri.Path.Root
import org.http4s._
import org.http4s.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.PublicEndpoint
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._
import zio.{ExitCode, RIO, Task, UIO, URIO, ZIO, ZIOAppDefault}
import org.http4s.server.middleware.Logger

import com.comcast.ip4s.{Host, Port}
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio._
import zio.interop.catz.asyncInstance

object KorovavpoleServer {

/*    val helloWorldEndpoint: PublicEndpoint[(String, String), String, String, Any] =
        endpoint.get
            .in("hello")
            .in(query[String]("login"))
            .in(query[String]("password"))
            .errorOut(stringBody)
            .out(stringBody)

    val helloWorldRoutes: HttpRoutes[Task] = ZHttp4sServerInterpreter().from(
            helloWorldEndpoint.zServerLogic { case (login, password) =>
            ZIO.succeed(s"$login, $password, МОЛОДЕЦ")
        }).toRoutes*/
/*
    val healthCheck = endpoint.get.in("health").out(stringBody)
    val healthRoute: HttpRoutes[Task] = ZHttp4sServerInterpreter()
        .from(healthCheck.zServerLogic(_ => ZIO.succeed("Привет"))).toRoutes


    def run = {
        for {
            _ <- EmberServerBuilder
                    .default[Task]
                    .withHost(ipv4"127.0.0.1")
                    .withPort(port"9090")
                    .withHttpApp(Router("/" -> healthRoute).orNotFound)
                    .build
        } yield ()
    }.useForever*/

    type AppEnv = Any

    type AppRIO[A] = RIO[AppEnv, A]

    private val routes: HttpRoutes[AppRIO] = {
        val serverEndpoints: ZServerEndpoint[Any, Any] =
            endpoint.get.in("health").out(stringBody)
                .zServerLogic {
                    _ => {
                        ZIO.succeed(println("AAA"))
                        ZIO.succeed("Привет")
                    }
                }

        ZHttp4sServerInterpreter().from(List(serverEndpoints) ).toRoutes
    }

    def run() =
        for {
            _ <-
                EmberServerBuilder
                    .default[AppRIO]
                    .withHost(ipv4"127.0.0.1")
                    .withPort(port"8080")
                    .withHttpApp(Router("/" -> routes).orNotFound)
                    .build
                    .useForever
        } yield ()
}
