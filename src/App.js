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

  function handleUpdateTodo(todo) {
    if (!todo.text.trim()) return;
    const newTodoState = todoState.map((x) => (x.id === todo.id ? todo : x));

    setTodoState(newTodoState);
  }

  function handleDeleteTodo(todo) {
    if (!todo.text.trim()) return;
    const newTodoState = todoState
      .filter((x) => x.id !== todo.id)
      .map((x) => x);
    setTodoState(newTodoState);
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
      />
      <AddTodo onAddTodo={handleAddTodo} />
      </section>
    </main>
  );
}

export default App;
