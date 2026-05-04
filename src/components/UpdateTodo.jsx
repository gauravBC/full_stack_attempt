import { useState } from "react";

export default function UpdateTodo(props) {
  const [updateText, setUpdateText] = useState("");
  function handleOnClick(e) {
    const { ...updatedTodoItem } = props.todo;
    if (updatedTodoItem.text === updateText) return;
    updatedTodoItem.text = updateText;
    props.updateTodoFun(updatedTodoItem);
  }
  return (
    <>
      <input
        type="text"
        placeholder="New title"
        value={updateText}
        onChange={(e) => setUpdateText(e.target.value)}
      />
      <button onClick={handleOnClick}>Update</button>
    </>
  );
}
