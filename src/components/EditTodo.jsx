import { useState } from "react";

export default function EditTodo({ saveEdit, todo }) {
  const [text, setText] = useState(todo?.text ?? "");
  const [todoStatus, setTodoStatus] = useState(todo.completed);
  const [isEditing, setIsEditing] = useState(false);
  if (!todo) {
    return null;
  }

  if (!isEditing) {
    return (
      <button
        onClick={() => setIsEditing(true)}
        aria-label={`Edit ${todo.text}`}
      >
        Edit
      </button>
    );
  }

  function handleSubmit(e) {
    e.preventDefault();
    e.stopPropagation();
    if (!text.trim()) return;
    saveEdit({
      ...todo,
      text: text.trim(),
      completed: todoStatus,
    });
    setIsEditing(false);
  }
  function onCancel() {
    setIsEditing(false);
    setText(todo.text);
    setTodoStatus(todo.completed);
  }

  return (
    <div className="modal">
      <div className="modal__overlay" onClick={onCancel} />
      <div className="modal__content" role="dialog" aria-modal="true">
        <form className="edit-todo" onSubmit={handleSubmit}>
          <label htmlFor="edit-todo-input">Edit todo</label>
          <input
            id="edit-todo-input"
            type="text"
            value={text}
            onChange={(event) => setText(event.target.value)}
            autoFocus
          />
          <select
            value={String(todoStatus)}
            onChange={(e) => setTodoStatus(e.target.value === "true")}
          >
            <option value="false">Pending</option>
            <option value="true">Done</option>
          </select>

          <div className="edit-todo__actions">
            <button type="button" onClick={onCancel}>
              Cancel
            </button>
            <button type="submit" disabled={!text.trim()}>
              Save
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
