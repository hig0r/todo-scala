package ninja.higor

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

object Main extends IOApp:

  override def run(args: List[String]): IO[ExitCode] =

    val routes = Http4sServerInterpreter[IO]().toRoutes(new TodoEndpoints(TodoRepositoryImpl(ListBuffer.empty)).all)

    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
