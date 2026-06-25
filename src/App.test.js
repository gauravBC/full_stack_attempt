import { render, screen } from "@testing-library/react";
import App from "./App";

test("renders NurtureAI dashboard", () => {
  render(<App />);
  expect(screen.getByText(/Private pregnancy nutrition guidance/i)).toBeInTheDocument();
  expect(screen.getByText(/Login to your account/i)).toBeInTheDocument();
});
