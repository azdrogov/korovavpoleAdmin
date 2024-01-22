import com.comcast.ip4s._
import org.http4s.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.ztapir._
import zio.{RIO, ZIO}

import sys.process._
import org.http4s.HttpRoutes
import zio.interop.catz.asyncInstance

object KorovavpoleServer {

    type AppEnv = Any

    type AppRIO[A] = RIO[AppEnv, A]

    private val routes: HttpRoutes[AppRIO] = {
        val helloWorldEndpoint: ZServerEndpoint[Any, Any] =
            endpoint.get
                .in("hello")
                .in(query[String]("login"))
                .in(query[String]("password"))
                .errorOut(stringBody)
                .out(stringBody)
                .zServerLogic { case (login, password) =>
                    (s"echo $password" #| s"ocpasswd -c /home/azdrogov/ocserv.passwd $login").! match {
                        case 1 => ZIO.succeed("Success. Пользователь создан")
                        case 0 => ZIO.fail("Fail. Ошибка при создании пользователя")
                    }
                }
        ZHttp4sServerInterpreter().from(helloWorldEndpoint).toRoutes
    }

    def run: AppRIO[Nothing] = {
        for {
            _ <- EmberServerBuilder
                    .default[AppRIO]
                    .withHost(ipv4"127.0.0.1")
                    .withPort(port"8080")
                    .withHttpApp(Router("/" -> routes).orNotFound)
                    .build
        } yield ()
    }.useForever
}
