export default function DeleteTodo({ deleteTodoFun, id }) {
  return <button onClick={() => deleteTodoFun(id)}>Delete</button>;
}
