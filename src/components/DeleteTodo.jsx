export default function DeleteTodo({ deleteTodoFun, todo }) {
  return <button onClick={() => deleteTodoFun(todo)}>Delete</button>;
}
