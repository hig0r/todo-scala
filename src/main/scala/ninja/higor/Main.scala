package ninja.higor

import cats.effect.{ExitCode, IO, IOApp, Ref}
import com.comcast.ip4s.*
import ninja.higor.models.Todo
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

object Main extends IOApp:

  override def run(args: List[String]): IO[ExitCode] =
    for {
      nextIdRef <- Ref[IO].of(1)
      todosRef <- Ref[IO].of(List.empty[Todo])
      routes = Http4sServerInterpreter[IO]().toRoutes(new TodoEndpoints(TodoRepositoryImpl(nextIdRef, todosRef)).all)
      server <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(Router("/" -> routes).orNotFound)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)
    } yield server
