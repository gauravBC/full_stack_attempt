import { useState } from "react";

export default function UpdateTodo(props) {
  const [updateText, setUpdateText] = useState(props.todo.text);
  function handleOnClick(e) {
    e.stopPropagation();
    if (!updateText.trim()) return;
    props.updateTodoFun({
      ...props.todo,
      text: updateText,
    });
  }
  //quxgon-merky7-cyCrew
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
