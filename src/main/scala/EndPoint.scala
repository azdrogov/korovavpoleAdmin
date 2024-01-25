import sttp.tapir.Endpoint
import sttp.tapir.ztapir._

object EndPoint {
    private val baseUrl = endpoint.in("api")

    val createUserEndpoint: Endpoint[Unit, (String, String), String, String, Any] =
        baseUrl.post
            .in("user" / "create")
            .in(query[String]("login").example("Korova"))
            .in(query[String]("password").example("qwerty"))
            .out(stringBody.example("Success. Пользователь создан"))
            .errorOut(stringBody.example("Fail. Ошибка при создании пользователя"))
            .description("Создаёт пользователя по логину и паролю")

    val deleteUserEndpoint: Endpoint[Unit, String, String, String, Any] =
        baseUrl.delete
            .in("user" / "delete")
            .in(query[String]("login").example("Korova"))
            .out(stringBody.example("Success. Пользователь удалён"))
            .errorOut(stringBody.example("Fail. Ошибка при удаление пользователя"))
            .description("Удаляет пользователя по логину")

    val lockUserEndpoint: Endpoint[Unit, String, String, String, Any] =
        baseUrl.post
            .in("user" / "lock")
            .in(query[String]("login").example("Korova"))
            .out(stringBody.example("Success. Пользователь заблокирован"))
            .errorOut(stringBody.example("Fail. Ошибка при блокировке пользователя"))
            .description("Блокирует пользователя по логину")

    val unlockUserEndpoint: Endpoint[Unit, String, String, String, Any] =
        baseUrl.post
            .in("user" / "unlock")
            .in(query[String]("login").example("Korova"))
            .out(stringBody.example("Success. Пользователь разблокирован"))
            .errorOut(stringBody.example("Fail. Ошибка при разблокировке пользователя"))
            .description("Разблокирует пользователя по логину")
}
