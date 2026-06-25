const API_BASE_URL =
  process.env.REACT_APP_API_BASE_URL || "http://localhost:8081/api";

async function readErrorMessage(response, fallback) {
  try {
    const body = await response.json();
    return body.message || fallback;
  } catch (error) {
    return fallback;
  }
}

export async function fetchDailyPlan(userId) {
  const response = await fetch(`${API_BASE_URL}/daily-plans/today?userId=${encodeURIComponent(userId)}`);

  if (!response.ok) {
    throw new Error("Unable to load daily plan");
  }

  return response.json();
}

export async function generateDailyPlan(userId) {
  const response = await fetch(`${API_BASE_URL}/daily-plans/generate-now?userId=${encodeURIComponent(userId)}`, {
    method: "POST",
  });

  if (!response.ok) {
    throw new Error(await readErrorMessage(response, "Unable to generate daily plan"));
  }

  return response.json();
}

export async function saveGroceryList(username, items) {
  const response = await fetch(`${API_BASE_URL}/daily-plans/grocery-list`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, items }),
  });

  if (!response.ok) {
    throw new Error("Unable to save grocery list");
  }

  return response.json();
}

export async function loginUser(username, password) {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      throw new Error(await readErrorMessage(response, "Invalid username or password"));
    }

    return response.json();
  } catch (error) {
    if (error instanceof TypeError) {
      throw new Error("Backend is not reachable from this hosted site. Configure REACT_APP_API_BASE_URL with a public backend HTTPS URL, or use ?MockFlow=Y for UI testing.");
    }
    throw error;
  }
}


export async function fetchAiStatus() {
  const response = await fetch(`${API_BASE_URL}/ai/status`);

  if (!response.ok) {
    throw new Error("Unable to load AI status");
  }

  return response.json();
}

export async function askAiAssistant(message, context = {}) {
  const response = await fetch(`${API_BASE_URL}/ai/chat`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ message, ...context }),
  });

  if (!response.ok) {
    throw new Error(await readErrorMessage(response, "Unable to reach AI assistant"));
  }

  return response.json();
}

export async function explainMeal(meal, context = {}) {
  const response = await fetch(`${API_BASE_URL}/ai/explain-meal`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      mealTime: meal.time,
      mealTitle: meal.title,
      mealNote: meal.note,
      ...context,
    }),
  });

  if (!response.ok) {
    throw new Error(await readErrorMessage(response, "Unable to explain meal"));
  }

  return response.json();
}

export async function updatePassword(username, currentPassword, newPassword) {
  const response = await fetch(`${API_BASE_URL}/account/password`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, currentPassword, newPassword }),
  });

  if (!response.ok) {
    throw new Error("Unable to update password");
  }

  return response.json();
}

export async function fetchPartnerContacts(username) {
  const response = await fetch(`${API_BASE_URL}/account/partners?username=${encodeURIComponent(username)}`);

  if (!response.ok) {
    throw new Error("Unable to load partner contacts");
  }

  return response.json();
}

export async function savePartnerContact(partner) {
  const response = await fetch(`${API_BASE_URL}/account/partners`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(partner),
  });

  if (!response.ok) {
    throw new Error("Unable to save partner contact");
  }

  return response.json();
}

export async function fetchDietProfile(username) {
  const response = await fetch(`${API_BASE_URL}/diet-profile?username=${encodeURIComponent(username)}`);

  if (!response.ok) {
    throw new Error("Unable to load diet profile");
  }

  return response.json();
}

export async function saveDietProfile(profile) {
  const response = await fetch(`${API_BASE_URL}/diet-profile`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(profile),
  });

  if (!response.ok) {
    throw new Error("Unable to save diet profile");
  }

  return response.json();
}

export async function fetchPantry(username) {
  const response = await fetch(`${API_BASE_URL}/pantry?username=${encodeURIComponent(username)}`);

  if (!response.ok) {
    throw new Error("Unable to load pantry");
  }

  return response.json();
}

export async function savePantry(username, items) {
  const response = await fetch(`${API_BASE_URL}/pantry`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, items }),
  });

  if (!response.ok) {
    throw new Error("Unable to save pantry");
  }

  return response.json();
}
