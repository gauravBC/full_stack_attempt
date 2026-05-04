import "./App.css";
import Todos from "./components/Todos";
import AddTodo from "./components/AddTodo";
import { useState } from "react";

function App() {
  const [todoState, setTodoState] = useState([
    { id: 1, text: "Learn React", completed: false },
    { id: 2, text: "Build a To-Do App", completed: false },
  ]);
  function handleAddTodo(newTodo) {
    if (!newTodo.text.trim()) return;
    const todoWithId = { ...newTodo, id: Date.now() };
    setTodoState((dos) => [...dos, todoWithId]);
  }

  function handleDeleteTodo(id) {
    setTodoState((prev) => prev.filter((x) => x.id !== id));
  }

  function handleUpdateTodo(updatedTodo) {
    setTodoState((prev) =>
      prev.map((x) =>
        x.id === updatedTodo.id ? { ...x, text: updatedTodo.text } : x,
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
        <Todos
          todos={todoState}
          updateTodo={handleUpdateTodo}
          deleteTodo={handleDeleteTodo}
          toggleTodo={handleToggleTodo}
        />
        <AddTodo onAddTodo={handleAddTodo} />
      </section>
    </main>
  );
}

export default App;
