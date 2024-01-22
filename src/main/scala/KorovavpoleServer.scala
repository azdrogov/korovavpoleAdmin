import cats.implicits._
import com.comcast.ip4s._
import org.http4s.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.ztapir._
import zio.{RIO, ZIO}

import sys.process._
import org.http4s.HttpRoutes
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio.interop.catz.asyncInstance

object KorovavpoleServer {

    type AppEnv = Any

    type AppRIO[A] = RIO[AppEnv, A]

    private val createUserEndpoint =
        endpoint.post
            .in("user" / "create")
            .in(query[String]("login").example("Korova"))
            .in(query[String]("password").example("qwerty"))
            .out(stringBody.example("Success. Пользователь создан"))
            .errorOut(stringBody.example("Fail. Ошибка при создании пользователя"))
            .description("Создаёт пользователя по логину и паролю")

    private val deleteUserEndpoint =
        endpoint.delete
            .in("user" / "delete")
            .in(query[String]("login").example("Korova"))
            .out(stringBody.example("Success. Пользователь удалён"))
            .errorOut(stringBody.example("Fail. Ошибка при удаление пользователя"))
            .description("Удаляет пользователя по логину")

    private val lockUserEndpoint =
        endpoint.post
            .in("user" / "lock")
            .in(query[String]("login").example("Korova"))
            .out(stringBody.example("Success. Пользователь заблокирован"))
            .errorOut(stringBody.example("Fail. Ошибка при блокировке пользователя"))
            .description("Блокирует пользователя по логину")

    private val unlockUserEndpoint =
        endpoint.post
            .in("user" / "unlock")
            .in(query[String]("login").example("Korova"))
            .out(stringBody.example("Success. Пользователь разблокирован"))
            .errorOut(stringBody.example("Fail. Ошибка при разблокировке пользователя"))
            .description("Разблокирует пользователя по логину")

    private val createUserEndpointLogic: ZServerEndpoint[Any, Any] =
        createUserEndpoint
            .zServerLogic { case (login, password) =>
                (s"echo $password" #| s"ocpasswd -c /home/azdrogov/ocserv.passwd $login").! match {
                    case 0 => ZIO.succeed("Success. Пользователь создан")
                    case 1 => ZIO.fail("Fail. Ошибка при создании пользователя")
                }
            }

    private val deleteUserEndpointLogic: ZServerEndpoint[Any, Any] =
        deleteUserEndpoint
            .zServerLogic { login =>
                s"ocpasswd -c /home/azdrogov/ocserv.passwd -d $login".! match {
                    case 0 => ZIO.succeed("Success. Пользователь удалён")
                    case 1 => ZIO.fail("Fail. Ошибка при удаление пользователя")
                }
            }

    private val lockUserEndpointLogic: ZServerEndpoint[Any, Any] =
        lockUserEndpoint
            .zServerLogic { login =>
                s"ocpasswd -c /home/azdrogov/ocserv.passwd -l $login".! match {
                    case 0 => ZIO.succeed("Success. Пользователь заблокирован")
                    case 1 => ZIO.fail("Fail. Ошибка при блокировке пользователя")
                }
            }

    private val unlockUserEndpointLogic: ZServerEndpoint[Any, Any] =
        unlockUserEndpoint
            .zServerLogic { login =>
                s"ocpasswd -c /home/azdrogov/ocserv.passwd -u $login".! match {
                    case 0 => ZIO.succeed("Success. Пользователь разблокирован")
                    case 1 => ZIO.fail("Fail. Ошибка при разблокировке пользователя")
                }
            }

    private val routes: HttpRoutes[AppRIO] = {
        ZHttp4sServerInterpreter().from(List(createUserEndpointLogic, deleteUserEndpointLogic, lockUserEndpointLogic, unlockUserEndpointLogic)).toRoutes
    }

    private val routesSwagger: HttpRoutes[AppRIO] = {
        ZHttp4sServerInterpreter()
            .from(SwaggerInterpreter()
                .fromEndpoints[AppRIO](List(createUserEndpoint, deleteUserEndpoint, lockUserEndpoint, unlockUserEndpoint), "Korova Admin", "1.0")
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
