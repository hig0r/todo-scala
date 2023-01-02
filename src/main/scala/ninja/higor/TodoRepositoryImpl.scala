package ninja.higor
import cats.effect.*
import ninja.higor.models.{CreateTodo, Todo, TodoStatus, UpdateTodo}

import scala.collection.mutable.ListBuffer

case class TodoRepositoryImpl(nextIdRef: Ref[IO, Int], todosRef: Ref[IO, List[Todo]]) extends TodoRepository:
  override def get(status: Option[TodoStatus]): IO[List[Todo]] =
    status match
      case Some(TodoStatus.Completed) => todosRef.get.map(_.filter(x => x.done))
      case Some(TodoStatus.Active)    => todosRef.get.map(_.filter(x => !x.done))
      case _                          => todosRef.get

  override def add(t: CreateTodo): IO[Todo] =
    for {
      nextId <- nextIdRef.modify(x => (x + 1, x))
      todo = Todo(nextId, t.title, t.description, false)
      _ <- todosRef.update(_ :+ todo)
    } yield todo

  override def update(id: Int, t: UpdateTodo): IO[Todo] =
    val todo = Todo(id, t.title, t.description, t.done)
    todosRef
      .update { todos =>
        val i = todos.indexWhere(_.id == id)
        todos.updated(i, todo)
      }
      .map(_ => todo)

  override def remove(id: Int): IO[Unit] =
    todosRef.update(_.filter(_.id != id))
