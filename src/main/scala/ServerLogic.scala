import EndPoint.{createUserEndpoint, deleteUserEndpoint, healthCheckEndpoint, lockUserEndpoint, unlockUserEndpoint}
import sttp.tapir.ztapir._
import zio.ZIO

import sys.process._

object ServerLogic {
    val healthCheckEndpointLogic: ZServerEndpoint[Any, Any] =
        healthCheckEndpoint.zServerLogic(_ => ZIO.succeed("Здарова, заебал"))

    val createUserEndpointLogic: ZServerEndpoint[Any, Any] =
        createUserEndpoint
            .zServerLogic { case (login, password) =>
                (s"echo $password" #| s"ocpasswd -c /home/azdrogov/ocserv.passwd $login").! match {
                    case 0 => ZIO.succeed("Success. Пользователь создан")
                    case 1 => ZIO.fail("Fail. Ошибка при создании пользователя")
                }
            }

    val deleteUserEndpointLogic: ZServerEndpoint[Any, Any] =
        deleteUserEndpoint
            .zServerLogic { login =>
                s"ocpasswd -c /home/azdrogov/ocserv.passwd -d $login".! match {
                    case 0 => ZIO.succeed("Success. Пользователь удалён")
                    case 1 => ZIO.fail("Fail. Ошибка при удаление пользователя")
                }
            }

    val lockUserEndpointLogic: ZServerEndpoint[Any, Any] =
        lockUserEndpoint
            .zServerLogic { login =>
                s"ocpasswd -c /home/azdrogov/ocserv.passwd -l $login".! match {
                    case 0 => ZIO.succeed("Success. Пользователь заблокирован")
                    case 1 => ZIO.fail("Fail. Ошибка при блокировке пользователя")
                }
            }

    val unlockUserEndpointLogic: ZServerEndpoint[Any, Any] =
        unlockUserEndpoint
            .zServerLogic { login =>
                s"ocpasswd -c /home/azdrogov/ocserv.passwd -u $login".! match {
                    case 0 => ZIO.succeed("Success. Пользователь разблокирован")
                    case 1 => ZIO.fail("Fail. Ошибка при разблокировке пользователя")
                }
            }
}
