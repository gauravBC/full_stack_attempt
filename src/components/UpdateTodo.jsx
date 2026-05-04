import { useState } from "react";

export default function UpdateTodo(props) {
  const [updateText, setUpdateText] = useState("");
  function handleOnClick(e) {
    props.updateTodoFun({
      ...props.todo,
      text: updateText,
    });
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
