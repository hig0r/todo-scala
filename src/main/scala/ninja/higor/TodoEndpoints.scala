package ninja.higor
import cats.effect.IO
import sttp.tapir.*
import sttp.model.*
import io.circe.generic.auto.*
import ninja.higor.models.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.Codec.PlainCodec

given todoStatusCodec: PlainCodec[TodoStatus] =
  Codec.derivedEnumeration[String, TodoStatus].defaultStringBased

class TodoEndpoints(todoRepository: TodoRepository):
  private val base = endpoint.in("todos")

  val get = base.get
    .in(query[Option[TodoStatus]]("status"))
    .out(jsonBody[List[Todo]])
    .serverLogicSuccess(todoRepository.get)

  val create = base.post
    .in(jsonBody[CreateTodo])
    .out(jsonBody[Todo])
    .serverLogicSuccess(todoRepository.add)

  val update = base.put
    .in(query[Int]("id").and(jsonBody[UpdateTodo]))
    .out(jsonBody[Todo])
    .serverLogicSuccess(todoRepository.update.tupled)

  val delete = base.delete
    .in(query[Int]("id"))
    .out(statusCode(StatusCode.NoContent))
    .serverLogicSuccess(todoRepository.remove)

  val all: List[ServerEndpoint[Any, IO]] = List(get, create, update, delete)
