export default function Todos({ todos }) {
  return (
    <ol>
      {todos.map((x) => (
        <li key={x.id}>
          <span>{x.text}</span>
          <span>{x.completed ? "Done ✅" : "Nope ❌"}</span>
        </li>
      ))}
    </ol>
  );
}
