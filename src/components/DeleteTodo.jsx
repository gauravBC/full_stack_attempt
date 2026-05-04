export default function DeleteTodo({ deleteTodoFun, id }) {
  return (
    <button
      onClick={(e) => {
        e.stopPropagation();
        deleteTodoFun(id);
      }}
    >
      Delete
    </button>
  );
}
