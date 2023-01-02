package ninja.higor
import cats.effect.IO
import ninja.higor.models.{CreateTodo, Todo, TodoStatus, UpdateTodo}

import scala.collection.mutable.ListBuffer

case class TodoRepositoryImpl(todos: ListBuffer[Todo]) extends TodoRepository:
  override def get(status: Option[TodoStatus]): IO[List[Todo]] =
    IO.delay {
      status match
        case Some(TodoStatus.Completed) => todos.filter(x => x.done).toList
        case Some(TodoStatus.Active)    => todos.filter(x => !x.done).toList
        case _                          => todos.toList
    }

  override def add(t: CreateTodo): IO[Todo] =
    IO.delay {
      val nextId = todos.lastOption match
        case Some(t) => t.id + 1
        case None    => 1
      val todo = Todo(nextId, t.title, t.description, false)
      todos.append(todo)
      todo
    }

  override def update(id: Int, t: UpdateTodo): IO[Todo] =
    IO.delay {
      val todo = Todo(id, t.title, t.description, t.done)
      todos.update(todos.indexWhere(x => x.id == id), todo)
      todo
    }

  override def remove(id: Int): IO[Unit] =
    IO.delay(todos.filterInPlace(x => x.id != id))
