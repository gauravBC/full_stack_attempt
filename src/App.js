import "./App.css";
import Todos from "./components/Todos";
import AddTodo from "./components/AddTodo";
import { useState } from "react";

function App() {
  let todos = [
    { id: 1, text: "Learn React", completed: false },
    { id: 2, text: "Build a To-Do App", completed: false },
  ];

  let [todoState, setTodoState] = useState(todos);

  function handleAddTodo(newTodo) {
    newTodo.id = todoState.length + 1;
    setTodoState((dos) => [...dos, newTodo]);
  }

  return (
    <div>
      <Todos todos={todoState} />
      <AddTodo onAddTodo={handleAddTodo} />
    </div>
  );
}

export default App;
