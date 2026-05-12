import DeleteTodo from "./DeleteTodo";
import EditTodo from "./EditTodo";

export default function Todos({ todos, saveEdit, deleteTodo, toggleTodo }) {
  return (
    <ol className="todo-list">
      {todos.map((x) => (
        <li className="todo-item" key={x.id}>
          <div className="todo-content">
            <span className="todo-title">{x.text}</span>
            <span
              className={x.completed ? "status done" : "status pending"}
              onClick={() => toggleTodo(x.id)}
            >
              {x.completed ? "Done✅" : "Pending❌"}
            </span>
          </div>
          <div className="todo-actions">
            {/* <UpdateTodo updateTodoFun={updateTodo} todo={x} /> */}
            <EditTodo saveEdit={saveEdit} todo={x} />
            <DeleteTodo deleteTodoFun={deleteTodo} id={x.id} />
          </div>
        </li>
      ))}
    </ol>
  );
}
