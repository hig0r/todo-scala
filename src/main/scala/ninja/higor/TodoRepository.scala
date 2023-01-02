package ninja.higor

import cats.effect.IO
import ninja.higor.models.*

trait TodoRepository:
  def get(status: Option[TodoStatus]): IO[List[Todo]]
  def add(todo: CreateTodo): IO[Todo]
  def update(id: Int, todo: UpdateTodo): IO[Todo]
  def remove(id: Int): IO[Unit]
