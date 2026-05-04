import UpdateTodo from "./UpdateTodo";
import DeleteTodo from "./DeleteTodo";
export default function Todos({ todos, updateTodo, deleteTodo }) {
  return (
    <ol className="todo-list">
      {todos.map((x) => (
        <li className="todo-item" key={x.id}>
          <div className="todo-content">
            <span className="todo-title">{x.text}</span>
            <span className={x.completed ? "status done" : "status pending"}>
              {x.completed ? "Done" : "Pending"}
            </span>
          </div>
          <div className="todo-actions">
            <UpdateTodo updateTodoFun={updateTodo} todo={x} />
            <DeleteTodo deleteTodoFun={deleteTodo} id={x.id} />
          </div>
        </li>
      ))}
    </ol>
  );
}
