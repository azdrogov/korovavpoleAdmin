import zio.{ExitCode, URIO, ZIOAppDefault}

object Main extends ZIOAppDefault {
    override def run: URIO[Any, ExitCode] =
        KorovavpoleServer.run.provide().exitCode
}
