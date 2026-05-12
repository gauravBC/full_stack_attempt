import "./App.css";
import Todos from "./components/Todos";
import AddTodo from "./components/AddTodo";
import { useState } from "react";

function App() {
  const [todoState, setTodoState] = useState([
    { id: 1, text: "Learn React", completed: false },
    { id: 2, text: "Build a To-Do App", completed: false },
  ]);

  const [filter, setFilter] = useState("all");
  const todoCount = todoState.length;
  const todoCompleteCount = todoState.filter((x) => x.completed).length;

  const filteredTodos = todoState.filter((x) => {
    if (filter === "all") {
      return true;
    }

    if (filter === "completed") {
      return x.completed;
    }

    return !x.completed;
  });
  function handleAddTodo(newTodo) {
    if (!newTodo.text.trim()) return;
    const todoWithId = { ...newTodo, id: Date.now() };
    setTodoState((dos) => [...dos, todoWithId]);
  }

  function handleDeleteTodo(id) {
    setTodoState((prev) => prev.filter((x) => x.id !== id));
  }

  // function handleUpdateTodo(updatedTodo) {
  //   setTodoState((prev) =>
  //     prev.map((x) =>
  //       x.id === updatedTodo.id ? { ...x, text: updatedTodo.text } : x,
  //     ),
  //   );
  // }

  function handleSaveEditTodo(todo) {
    setTodoState((prev) =>
      prev.map((x) =>
        x.id === todo.id
          ? { ...x, text: todo.text, completed: todo.completed }
          : x,
      ),
    );
  }

  function handleToggleTodo(id) {
    setTodoState((prev) =>
      prev.map((todo) =>
        todo.id === id ? { ...todo, completed: !todo.completed } : todo,
      ),
    );
  }

  return (
    <main className="app-shell">
      <section className="todo-panel">
        <header className="app-header">
          <p>Simple Todo</p>
          <h1>Plan the next thing</h1>
        </header>
        <div className="todo-filters">
          <button className="filer-button" onClick={() => setFilter("all")}>
            ALL
          </button>
          <button
            className="filer-button"
            onClick={() => setFilter("completed")}
          >
            {" "}
            COMPLETED{" "}
          </button>
          <button className="filer-button" onClick={() => setFilter("pending")}>
            {" "}
            PENDING{" "}
          </button>
        </div>
        <Todos
          todos={filteredTodos}
          saveEdit={handleSaveEditTodo}
          deleteTodo={handleDeleteTodo}
          toggleTodo={handleToggleTodo}
        />
        <AddTodo onAddTodo={handleAddTodo} />
        <div className="todo-stats">
          <footer>
            {todoCount > 0 &&
              `${todoCompleteCount} completed / ${todoCount} total tasks roughly ${Math.round(
                (todoCompleteCount / todoCount) * 100,
              )}%`}
          </footer>
        </div>
      </section>
    </main>
  );
}

export default App;
