package ninja.higor

import sttp.tapir.*
import Library.*
import cats.effect.IO
import io.circe.generic.auto.*
import sttp.tapir.EndpointIO.annotations.query
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint

object Endpoints:
  case class User(
      @query
      name: Option[String],
      @query
      idade: Int
  )
  val helloEndpoint = endpoint.get
    .in("hello")
    .in(EndpointInput.derived[User])
    .out(stringBody)
  val helloServerEndpoint: ServerEndpoint[Any, IO] =
    helloEndpoint.serverLogicSuccess(user => IO.pure(s"Hello ${user.name.getOrElse("anonimo")}, you're ${user.idade} years old"))

  val booksListing: PublicEndpoint[Unit, Unit, List[Book], Any] = endpoint.get
    .in("books" / "list" / "all")
    .out(jsonBody[List[Book]])
  val booksListingServerEndpoint: ServerEndpoint[Any, IO] = booksListing.serverLogicSuccess(_ => IO.pure(Library.books))

  val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(helloServerEndpoint, booksListingServerEndpoint)

  val all: List[ServerEndpoint[Any, IO]] = apiEndpoints

object Library:
  case class Author(name: String)
  case class Book(title: String, year: Int, author: Author)

  val books = List(
    Book("The Sorrows of Young Werther", 1774, Author("Johann Wolfgang von Goethe")),
    Book("On the Niemen", 1888, Author("Eliza Orzeszkowa")),
    Book("The Art of Computer Programming", 1968, Author("Donald Knuth")),
    Book("Pharaoh", 1897, Author("Boleslaw Prus"))
  )
