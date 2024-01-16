import zio.{ExitCode, URIO, ZIOAppDefault}
import org.http4s.implicits._
import cats.syntax.all._

object Main extends ZIOAppDefault {
    override def run: URIO[Any, ExitCode] =
        KorovavpoleServer.run().provide().exitCode
}
