import { useState } from "react";

export default function AddTodo({ onAddTodo }) {
  let [description, setDescription] = useState("");
  let [status, setStatus] = useState(false);

  function handleSubmit(e) {
    e.preventDefault();
    let newTodo = { text: description, completed: status };
    onAddTodo(newTodo);
    setDescription("");
    setStatus(false);
  }

  return (
    <>
      <form className="add-form" onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Add a new task"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <label htmlFor="logic-select">Choose an option:</label>
        <select
          id="logic-select"
          name="logic-select"
          value={String(status)}
          onChange={(e) => setStatus(e.target.value === "true")}
        >
          <option value="true">True</option>
          <option value="false">False</option>
        </select>
        <span className="form-hint">
          {description.length > 0
            ? `You've selected ${description} and it's current status is ${status} click on submit to add the task`
            : ""}
        </span>
        <button>Submit</button>
      </form>
    </>
  );
}
