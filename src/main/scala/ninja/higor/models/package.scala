package ninja.higor.models

case class Todo(id: Int, title: String, description: String, done: Boolean)
case class CreateTodo(title: String, description: String)
case class UpdateTodo(title: String, description: String, done: Boolean)

enum TodoStatus:
  case Completed, Active
