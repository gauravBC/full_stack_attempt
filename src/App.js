import "./App.css";
import { useState } from "react";
import {
  askAiAssistant,
  explainMeal,
  fetchAiStatus,
  fetchDailyPlan,
  fetchDietProfile,
  fetchPantry,
  fetchPartnerContacts,
  fetchNutritionSchedule,
  fetchHydrationPlan,
  generateDailyPlan,
  loginUser,
  logHydration,
  saveDietProfile,
  saveGroceryList,
  saveHydrationPlan,
  saveNutritionSchedule,
  savePantry,
  savePartnerContact,
  updateNutritionSlot,
  updatePassword,
} from "./api";

const fallbackPlan = {
  pregnancyWeek: 22,
  hydrationGoal: "2.7 L",
  nutrients: [],
  meals: [],
  groceryList: [],
  reminders: [],
  partnerTask: "",
  safetyNotes: [],
};


const mockProfile = {
  id: "mock-profile",
  username: "jakka.vikram",
  age: 29,
  heightCm: 162,
  weightKg: 64,
  pregnancyWeek: 24,
  foodPreference: "vegetarian_only",
  eggsAllowed: false,
  allergies: "peanuts",
  cuisineRegion: "Indian",
  budgetLevel: "medium",
};

const mockPlan = {
  id: "mock-plan",
  userId: "jakka.vikram",
  planDate: new Date().toISOString().slice(0, 10),
  pregnancyWeek: 24,
  hydrationGoal: "2.7 L",
  nutrients: [
    { label: "Protein", value: "75 g", status: "On track" },
    { label: "Iron", value: "27 mg", status: "Needs focus" },
    { label: "Calcium", value: "1,000 mg", status: "On track" },
  ],
  meals: [
    { time: "Breakfast", title: "Vegetable oats with nuts", note: "MockFlow meal based on profile and pantry." },
    { time: "Lunch", title: "Spinach dal with brown rice", note: "Iron-focused meal for week 24. Add lemon to support absorption." },
    { time: "Snack", title: "Fruit with yogurt", note: "Calcium support planned around daily nutrition goals." },
    { time: "Dinner", title: "Paneer curry with chapati", note: "Matched to vegetarian preference and pantry items." },
  ],
  groceryList: ["Milk", "Dates", "Chickpeas"],
  reminders: ["Iron tablet at 4 PM, away from dairy.", "Drink water steadily across the day."],
  partnerTask: "Prepare dinner tonight and encourage a short walk if approved by the doctor.",
  safetyNotes: ["MockFlow guidance only.", "Consult a clinician for medical concerns."],
};

const mockPantry = ["Curd", "Dry Fruits", "Ragi", "Rice", "Spinach"];

const mockNutritionSchedule = {
  username: "jakka.vikram",
  totalCalories: 2280,
  totalProteinGrams: 111,
  completedSlots: 1,
  slots: [
    { id: "mock-0600", time: "06:00", title: "Morning milk", foods: "Milk with pregnancy-safe protein powder if approved by clinician", calories: 220, proteinGrams: 18, reminderEnabled: true, completed: true, sortOrder: 1 },
    { id: "mock-0800", time: "08:00", title: "Breakfast", foods: "Protein-rich breakfast: oats, dal chilla, idli with sambar, or eggs if allowed", calories: 420, proteinGrams: 22, reminderEnabled: true, completed: false, sortOrder: 2 },
    { id: "mock-1000", time: "10:00", title: "Short snack", foods: "Salad, fruit, dry fruits, or fresh juice with no added sugar", calories: 180, proteinGrams: 5, reminderEnabled: true, completed: false, sortOrder: 3 },
    { id: "mock-1300", time: "13:00", title: "Lunch", foods: "Dal or paneer with rice/chapati plus salad and fruit", calories: 560, proteinGrams: 26, reminderEnabled: true, completed: false, sortOrder: 4 },
    { id: "mock-1600", time: "16:00", title: "Evening snack", foods: "Roasted chana, yogurt, nuts, fruit, or sprouts", calories: 240, proteinGrams: 12, reminderEnabled: true, completed: false, sortOrder: 5 },
    { id: "mock-1800", time: "18:00", title: "Light fluids", foods: "Fruit, dry fruits, or fresh juice; keep caffeine limited", calories: 160, proteinGrams: 4, reminderEnabled: true, completed: false, sortOrder: 6 },
    { id: "mock-2000", time: "20:00", title: "Dinner", foods: "Balanced dinner with protein, vegetables, and whole grains", calories: 500, proteinGrams: 24, reminderEnabled: true, completed: false, sortOrder: 7 },
  ],
};

const mockHydrationPlan = {
  username: "jakka.vikram",
  dailyGoalMl: 2700,
  currentIntakeMl: 750,
  progressPercent: 28,
  reminderGapMinutes: 90,
  detoxRecipeTitle: "Cucumber lemon mint water",
  detoxIngredients: "Cucumber slices, lemon slices, mint leaves, water",
  detoxSteps: "Add washed ingredients to water, refrigerate for 30 minutes, and sip as flavored water. Avoid treating it as a medical detox.",
  bestTime: "Mid-morning or early evening; keep a gap from iron/calcium tablets if advised by your clinician.",
  reminderEnabled: true,
};


function mockUserFor(username) {
  if (username === "gaurav.kumar") {
    return {
      id: "mock-gaurav",
      username: "gaurav.kumar",
      firstName: "Gaurav",
      lastName: "Kumar",
      email: "gaurav.kumar@example.com",
    };
  }

  return {
    id: "mock-vikram",
    username: "jakka.vikram",
    firstName: "Jakka",
    lastName: "Vikram",
    email: "jakka.vikram@example.com",
  };
}

const defaultDietProfile = {
  age: "",
  heightCm: "",
  weightKg: "",
  pregnancyWeek: 22,
  foodPreference: "vegetarian_with_eggs",
  eggsAllowed: true,
  allergies: "",
  cuisineRegion: "Indian",
  budgetLevel: "medium",
};

function splitItems(value) {
  return value
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

function isMockFlowEnabled() {
  return new URLSearchParams(window.location.search).get("MockFlow") === "Y";
}

function mockMealExplanation(meal) {
  return `${meal.title} is suggested for ${meal.time.toLowerCase()} because it fits the saved pregnancy profile, pantry, food preference, and safety constraints. It supports steady energy and key pregnancy nutrients while keeping allergy and clinician-guidance reminders visible.`;
}

function mockChatAnswer(message) {
  return `MockFlow response: I would answer "${message}" using the current profile, pantry, allergies, nutrition goals, and pregnancy week. Use normal mode to call the configured AI provider.`;
}


function summarizeNutrition(slots) {
  const safeSlots = slots || [];
  return {
    totalCalories: safeSlots.reduce((sum, slot) => sum + Number(slot.calories || 0), 0),
    totalProteinGrams: safeSlots.reduce((sum, slot) => sum + Number(slot.proteinGrams || 0), 0),
    completedSlots: safeSlots.filter((slot) => slot.completed).length,
  };
}

function normalizeHydration(plan) {
  const goal = Number(plan.dailyGoalMl || 0);
  const intake = Number(plan.currentIntakeMl || 0);
  return {
    ...plan,
    progressPercent: goal > 0 ? Math.min(100, Math.round((intake / goal) * 100)) : 0,
  };
}

function formatFoodPreference(value) {
  const labels = {
    vegetarian_only: "Vegetarian only",
    vegetarian_with_eggs: "Vegetarian + eggs",
    non_vegetarian: "Veg + non-veg",
  };

  return labels[value] || "Not set";
}

function App() {
  const [authMode, setAuthMode] = useState("login");
  const [loginMethod, setLoginMethod] = useState("password");
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [activeView, setActiveView] = useState("dashboard");
  const [authUser, setAuthUser] = useState(null);
  const [username, setUsername] = useState("jakka.vikram");
  const [password, setPassword] = useState("Vikram@123");
  const [loginError, setLoginError] = useState("");
  const [plan, setPlan] = useState(fallbackPlan);
  const [pantryItems, setPantryItems] = useState([]);
  const [pantryText, setPantryText] = useState("");
  const [groceryText, setGroceryText] = useState("");
  const [isDashboardLoading, setIsDashboardLoading] = useState(false);
  const [dashboardError, setDashboardError] = useState("");
  const [aiStatus, setAiStatus] = useState({ provider: "unknown", model: "", realAiEnabled: false });
  const [mockFlow] = useState(isMockFlowEnabled());
  const [isGenerating, setIsGenerating] = useState(false);
  const [generationError, setGenerationError] = useState("");
  const [listMessage, setListMessage] = useState("");
  const [chatMessages, setChatMessages] = useState([
    {
      role: "assistant",
      text: "Hi, I am your AI nutrition copilot. Ask me about meals, groceries, hydration, or why a food was suggested.",
    },
  ]);
  const [chatInput, setChatInput] = useState("");
  const [isChatLoading, setIsChatLoading] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [accountMessage, setAccountMessage] = useState("");
  const [accountError, setAccountError] = useState("");
  const [dietProfile, setDietProfile] = useState(defaultDietProfile);
  const [dietMessage, setDietMessage] = useState("");
  const [dietError, setDietError] = useState("");
  const [partner, setPartner] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    relationship: "partner",
    notificationsEnabled: true,
  });
  const [nutritionSchedule, setNutritionSchedule] = useState({ username: "", totalCalories: 0, totalProteinGrams: 0, completedSlots: 0, slots: [] });
  const [nutritionMessage, setNutritionMessage] = useState("");
  const [nutritionError, setNutritionError] = useState("");
  const [hydrationPlan, setHydrationPlan] = useState(mockHydrationPlan);
  const [hydrationMessage, setHydrationMessage] = useState("");
  const [hydrationError, setHydrationError] = useState("");
  const [waterAmount, setWaterAmount] = useState(250);

  const displayName = [authUser?.firstName, authUser?.lastName].filter(Boolean).join(" ") || authUser?.username || "there";
  const hasProfileBasics = Boolean(dietProfile.age && dietProfile.heightCm && dietProfile.weightKg);
  const groceries = plan.groceryList || plan.groceries || [];

  async function loadWorkspace(user) {
    setIsDashboardLoading(true);
    setDashboardError("");

    if (mockFlow) {
      setDietProfile({ ...defaultDietProfile, ...mockProfile, username: user.username });
      setPlan({ ...mockPlan, userId: user.username });
      setPantryItems(mockPantry);
      setPantryText(mockPantry.join(", "));
      setGroceryText(mockPlan.groceryList.join(", "));
      setAiStatus({ provider: "mock-flow", model: "query-param", realAiEnabled: false });
      setNutritionSchedule({ ...mockNutritionSchedule, username: user.username });
      setHydrationPlan(normalizeHydration({ ...mockHydrationPlan, username: user.username }));
      setListMessage("MockFlow is enabled. This hosted page is running without a backend.");
      setIsDashboardLoading(false);
      return;
    }

    try {
      const [partners, profile, savedPlan, pantry, schedule, hydration] = await Promise.all([
        fetchPartnerContacts(user.username).catch(() => []),
        fetchDietProfile(user.username),
        fetchDailyPlan(user.username),
        fetchPantry(user.username),
        fetchNutritionSchedule(user.username),
        fetchHydrationPlan(user.username),
      ]);
      const status = await fetchAiStatus().catch(() => ({ provider: "unknown", model: "", realAiEnabled: false }));

      if (partners.length > 0) {
        setPartner(partners[0]);
      }

      setDietProfile({ ...defaultDietProfile, ...profile });
      setPlan(savedPlan);
      setPantryItems(pantry);
      setPantryText(pantry.join(", "));
      setGroceryText((savedPlan.groceryList || []).join(", "));
      setNutritionSchedule(schedule);
      setHydrationPlan(hydration);
      setAiStatus(status);

      if (!profile.age || !profile.heightCm || !profile.weightKg) {
        setActiveView("onboarding");
      }
    } catch (error) {
      setDashboardError("Some saved data could not be loaded. Confirm the backend is running, then refresh.");
    } finally {
      setIsDashboardLoading(false);
    }
  }

  async function handleLogin() {
    setLoginError("");

    if (mockFlow) {
      const user = mockUserFor(username);
      setAuthUser(user);
      setIsAuthenticated(true);
      await loadWorkspace(user);
      return;
    }

    try {
      const response = await loginUser(username, password);
      setAuthUser(response.user);
      setIsAuthenticated(true);
      await loadWorkspace(response.user);
    } catch (error) {
      setLoginError(error.message || "Invalid username or password. Try jakka.vikram / Vikram@123 or gaurav.kumar / Gaurav@123.");
    }
  }

  function aiContext() {
    return {
      pregnancyWeek: dietProfile.pregnancyWeek || plan.pregnancyWeek,
      age: dietProfile.age,
      heightCm: dietProfile.heightCm,
      weightKg: dietProfile.weightKg,
      foodPreference: dietProfile.foodPreference,
      eggsAllowed: dietProfile.eggsAllowed,
      allergies: dietProfile.allergies,
      pantryItems,
      nutritionGoals: (plan.nutrients || []).map((item) => `${item.label}: ${item.value}`),
      safetyNotes: ["Informational guidance only", "Consult a clinician for medical concerns"],
    };
  }

  async function handleAskAi(messageOverride) {
    const message = (messageOverride || chatInput).trim();

    if (!message) return;

    setChatMessages((messages) => [...messages, { role: "user", text: message }]);
    setChatInput("");
    setIsChatLoading(true);

    if (mockFlow) {
      setChatMessages((messages) => [...messages, { role: "assistant", text: mockChatAnswer(message) }]);
      setIsChatLoading(false);
      return;
    }

    try {
      const response = await askAiAssistant(message, aiContext());
      setChatMessages((messages) => [...messages, { role: "assistant", text: response.answer }]);
    } catch (error) {
      setChatMessages((messages) => [
        ...messages,
        { role: "assistant", text: error.message || "I could not reach the AI assistant. Please confirm the backend is running." },
      ]);
    } finally {
      setIsChatLoading(false);
    }
  }

  async function handleExplainMeal(meal) {
    const intent = `Why was ${meal.title} suggested for ${meal.time}?`;
    setChatMessages((messages) => [...messages, { role: "user", text: intent }]);
    setIsChatLoading(true);

    if (mockFlow) {
      setChatMessages((messages) => [...messages, { role: "assistant", text: mockMealExplanation(meal) }]);
      setIsChatLoading(false);
      return;
    }

    try {
      const response = await explainMeal(meal, aiContext());
      setChatMessages((messages) => [...messages, { role: "assistant", text: response.answer }]);
    } catch (error) {
      setChatMessages((messages) => [
        ...messages,
        { role: "assistant", text: error.message || "I could not explain that meal right now. Please try again once the backend is running." },
      ]);
    } finally {
      setIsChatLoading(false);
    }
  }

  async function handleGeneratePlan() {
    setIsGenerating(true);
    setGenerationError("");
    setListMessage("");

    try {
      if (mockFlow) {
        setListMessage("MockFlow is enabled. Page interactions are using local mock responses.");
        return;
      }

      const generatedPlan = await generateDailyPlan(authUser.username);
      setPlan(generatedPlan);
      setGroceryText((generatedPlan.groceryList || []).join(", "));
      setListMessage("Plan generated and saved for today.");
    } catch (error) {
      setGenerationError(error.message || "Start the Spring Boot API, then try Generate Plan again.");
    } finally {
      setIsGenerating(false);
    }
  }

  async function handlePantrySave(event) {
    event.preventDefault();
    setListMessage("");
    setGenerationError("");

    try {
      if (mockFlow) {
        const savedItems = splitItems(pantryText);
        setPantryItems(savedItems);
        setPantryText(savedItems.join(", "));
        setListMessage("MockFlow pantry saved locally for this session.");
        return;
      }

      const savedItems = await savePantry(authUser.username, splitItems(pantryText));
      setPantryItems(savedItems);
      setPantryText(savedItems.join(", "));
      setListMessage("Pantry saved. Generate a plan to use the updated ingredients.");
    } catch (error) {
      setGenerationError("Pantry could not be saved right now.");
    }
  }

  async function handleGrocerySave(event) {
    event.preventDefault();
    setListMessage("");
    setGenerationError("");

    try {
      if (mockFlow) {
        const updatedItems = splitItems(groceryText);
        setPlan((current) => ({ ...current, groceryList: updatedItems }));
        setGroceryText(updatedItems.join(", "));
        setListMessage("MockFlow grocery list saved locally for this session.");
        return;
      }

      const updatedPlan = await saveGroceryList(authUser.username, splitItems(groceryText));
      setPlan(updatedPlan);
      setGroceryText((updatedPlan.groceryList || []).join(", "));
      setListMessage("Grocery list saved for today.");
    } catch (error) {
      setGenerationError("Grocery list could not be saved right now.");
    }
  }

  async function handlePasswordUpdate(event) {
    event.preventDefault();
    setAccountMessage("");
    setAccountError("");

    try {
      await updatePassword(authUser.username, currentPassword, newPassword);
      setPassword(newPassword);
      setCurrentPassword("");
      setNewPassword("");
      setAccountMessage("Password updated. Use the new password the next time you log in.");
    } catch (error) {
      setAccountError("Password update failed. Check your current password and try again.");
    }
  }

  async function handlePartnerSave(event) {
    event.preventDefault();
    setAccountMessage("");
    setAccountError("");

    try {
      const savedPartner = await savePartnerContact({ username: authUser.username, ...partner });
      setPartner(savedPartner);
      setAccountMessage("Partner contact saved. Future notifications can include both parents.");
    } catch (error) {
      setAccountError("Partner contact could not be saved. Please check the details and try again.");
    }
  }

  function updatePartnerField(field, value) {
    setPartner((current) => ({ ...current, [field]: value }));
  }

  async function handleDietSave(event) {
    event.preventDefault();
    setDietMessage("");
    setDietError("");

    try {
      if (mockFlow) {
        setDietProfile((current) => ({ ...current, username: authUser.username }));
        setPlan((current) => ({ ...current, pregnancyWeek: dietProfile.pregnancyWeek }));
        setDietMessage("MockFlow profile saved locally for this session.");
        if (activeView === "onboarding") {
          setActiveView("dashboard");
        }
        return;
      }

      const savedProfile = await saveDietProfile({ username: authUser.username, ...dietProfile });
      setDietProfile(savedProfile);
      setPlan((current) => ({ ...current, pregnancyWeek: savedProfile.pregnancyWeek }));
      setDietMessage("Profile saved. Meal plans will use these inputs.");
      if (activeView === "onboarding") {
        setActiveView("dashboard");
      }
    } catch (error) {
      setDietError("Profile could not be saved. Please check the fields and try again.");
    }
  }

  function updateDietField(field, value) {
    setDietProfile((current) => ({ ...current, [field]: value }));
  }


  async function handleNutritionFieldChange(slotId, field, value) {
    setNutritionMessage("");
    setNutritionError("");
    setNutritionSchedule((current) => {
      const slots = current.slots.map((slot) => slot.id === slotId ? { ...slot, [field]: value } : slot);
      return { ...current, ...summarizeNutrition(slots), slots };
    });
  }

  async function handleNutritionToggle(slot, field, value) {
    setNutritionMessage("");
    setNutritionError("");

    if (mockFlow) {
      handleNutritionFieldChange(slot.id, field, value);
      setNutritionMessage("MockFlow nutrition reminder updated locally.");
      return;
    }

    try {
      const savedSlot = await updateNutritionSlot(authUser.username, slot.id, { [field]: value });
      setNutritionSchedule((current) => {
        const slots = current.slots.map((item) => item.id === slot.id ? savedSlot : item);
        return { ...current, ...summarizeNutrition(slots), slots };
      });
      setNutritionMessage(field === "completed" ? "Meal completion updated." : "Reminder setting updated.");
    } catch (error) {
      setNutritionError("Nutrition reminder could not be updated.");
    }
  }

  async function handleNutritionSave(event) {
    event.preventDefault();
    setNutritionMessage("");
    setNutritionError("");

    try {
      const slots = nutritionSchedule.slots.map((slot, index) => ({
        ...slot,
        calories: Number(slot.calories || 0),
        proteinGrams: Number(slot.proteinGrams || 0),
        sortOrder: Number(slot.sortOrder || index + 1),
        reminderEnabled: Boolean(slot.reminderEnabled),
        completed: Boolean(slot.completed),
      }));

      if (mockFlow) {
        setNutritionSchedule((current) => ({ ...current, ...summarizeNutrition(slots), slots }));
        setNutritionMessage("MockFlow nutrition schedule saved locally.");
        return;
      }

      const savedSchedule = await saveNutritionSchedule(authUser.username, slots);
      setNutritionSchedule(savedSchedule);
      setNutritionMessage("Nutrition schedule and reminders saved.");
    } catch (error) {
      setNutritionError("Nutrition schedule could not be saved.");
    }
  }

  function updateHydrationField(field, value) {
    setHydrationPlan((current) => normalizeHydration({ ...current, [field]: value }));
  }

  async function handleHydrationSave(event) {
    event.preventDefault();
    setHydrationMessage("");
    setHydrationError("");

    try {
      const payload = normalizeHydration({
        ...hydrationPlan,
        username: authUser.username,
        dailyGoalMl: Number(hydrationPlan.dailyGoalMl || 0),
        currentIntakeMl: Number(hydrationPlan.currentIntakeMl || 0),
        reminderGapMinutes: Number(hydrationPlan.reminderGapMinutes || 90),
        reminderEnabled: Boolean(hydrationPlan.reminderEnabled),
      });

      if (mockFlow) {
        setHydrationPlan(payload);
        setHydrationMessage("MockFlow hydration plan saved locally.");
        return;
      }

      const savedPlan = await saveHydrationPlan(payload);
      setHydrationPlan(savedPlan);
      setHydrationMessage("Hydration tracker and reminders saved.");
    } catch (error) {
      setHydrationError("Hydration plan could not be saved.");
    }
  }

  async function handleHydrationLog(amount) {
    setHydrationMessage("");
    setHydrationError("");
    const safeAmount = Number(amount || 0);
    if (safeAmount <= 0) {
      setHydrationError("Enter a water amount greater than zero.");
      return;
    }

    try {
      if (mockFlow) {
        setHydrationPlan((current) => normalizeHydration({ ...current, currentIntakeMl: Number(current.currentIntakeMl || 0) + safeAmount }));
        setHydrationMessage(`${safeAmount} ml added locally in MockFlow.`);
        return;
      }

      const savedPlan = await logHydration(authUser.username, safeAmount);
      setHydrationPlan(savedPlan);
      setHydrationMessage(`${safeAmount} ml added to today's tracker.`);
    } catch (error) {
      setHydrationError("Water intake could not be logged.");
    }
  }

  function renderProfileForm(isOnboarding = false) {
    return (
      <section className="account-grid">
        <section className="section-block account-hero">
          <p className="eyebrow">{isOnboarding ? "Onboarding" : "Profile Setup"}</p>
          <h2>{isOnboarding ? "Complete your nutrition profile" : "Personalize recommendations"}</h2>
          <p>Recommendations use pregnancy week, height, age, weight, allergies, food preference, eggs, cuisine, budget, and pantry data.</p>
        </section>

        {(dietMessage || dietError) && (
          <div className={dietMessage ? "success-banner" : "error-banner"}>{dietMessage || dietError}</div>
        )}

        <section className="section-block account-card diet-card">
          <div className="section-heading">
            <p className="eyebrow">Nutrition Inputs</p>
            <h2>Your diet details</h2>
          </div>
          <form className="account-form partner-form" onSubmit={handleDietSave}>
            <label>Age<input min="13" max="60" onChange={(event) => updateDietField("age", Number(event.target.value))} required type="number" value={dietProfile.age || ""} /></label>
            <label>Height (cm)<input min="90" max="230" onChange={(event) => updateDietField("heightCm", Number(event.target.value))} required type="number" value={dietProfile.heightCm || ""} /></label>
            <label>Weight (kg)<input min="30" max="250" onChange={(event) => updateDietField("weightKg", Number(event.target.value))} required type="number" value={dietProfile.weightKg || ""} /></label>
            <label>Pregnancy Week<input min="1" max="42" onChange={(event) => updateDietField("pregnancyWeek", Number(event.target.value))} required type="number" value={dietProfile.pregnancyWeek || ""} /></label>
            <label>Food Preference<select onChange={(event) => updateDietField("foodPreference", event.target.value)} value={dietProfile.foodPreference || "vegetarian_with_eggs"}><option value="vegetarian_only">Vegetarian only</option><option value="vegetarian_with_eggs">Vegetarian + eggs</option><option value="non_vegetarian">Vegetarian + non-vegetarian</option></select></label>
            <label>Cuisine Region<input onChange={(event) => updateDietField("cuisineRegion", event.target.value)} required type="text" value={dietProfile.cuisineRegion || ""} /></label>
            <label>Budget Level<select onChange={(event) => updateDietField("budgetLevel", event.target.value)} value={dietProfile.budgetLevel || "medium"}><option value="low">Low</option><option value="medium">Medium</option><option value="high">High</option></select></label>
            <label>Food Allergic To?<input onChange={(event) => updateDietField("allergies", event.target.value)} placeholder="Peanuts, shellfish, lactose..." type="text" value={dietProfile.allergies || ""} /></label>
            <label className="checkbox-row"><input checked={Boolean(dietProfile.eggsAllowed)} onChange={(event) => updateDietField("eggsAllowed", event.target.checked)} type="checkbox" />Can eat eggs</label>
            <button className="primary-action" type="submit">{isOnboarding ? "Save and Continue" : "Save Profile"}</button>
          </form>
        </section>
      </section>
    );
  }


  function renderNutritionSchedule() {
    const slots = nutritionSchedule.slots || [];
    return (
      <section className="account-grid">
        <section className="section-block account-hero">
          <p className="eyebrow">Nutrition Schedule</p>
          <h2>Timed meals and reminders</h2>
          <p>Plan the full day: morning milk, breakfast, snacks, lunch, evening fluids, and dinner. Calories and protein are tracked so the plan is easier to review.</p>
          <div className="schedule-summary">
            <span>{nutritionSchedule.totalCalories || 0} kcal planned</span>
            <span>{nutritionSchedule.totalProteinGrams || 0} g protein</span>
            <span>{nutritionSchedule.completedSlots || 0}/{slots.length} completed</span>
          </div>
        </section>

        {(nutritionMessage || nutritionError) && <div className={nutritionMessage ? "success-banner" : "error-banner"}>{nutritionMessage || nutritionError}</div>}

        <form className="section-block schedule-editor" onSubmit={handleNutritionSave}>
          {slots.length === 0 ? <div className="empty-state inline-empty"><strong>No nutrition schedule yet.</strong><span>Save a schedule from the defaults or restart the backend after migrations run.</span></div> : slots.map((slot) => (
            <article className="schedule-row" key={slot.id}>
              <label>Time<input onChange={(event) => handleNutritionFieldChange(slot.id, "time", event.target.value)} type="time" value={slot.time || ""} /></label>
              <label>Meal<input onChange={(event) => handleNutritionFieldChange(slot.id, "title", event.target.value)} type="text" value={slot.title || ""} /></label>
              <label className="wide-field">Suggested food<textarea onChange={(event) => handleNutritionFieldChange(slot.id, "foods", event.target.value)} rows="2" value={slot.foods || ""} /></label>
              <label>Calories<input min="0" onChange={(event) => handleNutritionFieldChange(slot.id, "calories", Number(event.target.value))} type="number" value={slot.calories || 0} /></label>
              <label>Protein (g)<input min="0" onChange={(event) => handleNutritionFieldChange(slot.id, "proteinGrams", Number(event.target.value))} type="number" value={slot.proteinGrams || 0} /></label>
              <label className="checkbox-row compact-check"><input checked={Boolean(slot.reminderEnabled)} onChange={(event) => handleNutritionToggle(slot, "reminderEnabled", event.target.checked)} type="checkbox" />Reminder</label>
              <label className="checkbox-row compact-check"><input checked={Boolean(slot.completed)} onChange={(event) => handleNutritionToggle(slot, "completed", event.target.checked)} type="checkbox" />Done</label>
            </article>
          ))}
          <button className="primary-action" type="submit">Save Nutrition Schedule</button>
        </form>
      </section>
    );
  }

  function renderHydrationTracker() {
    return (
      <section className="account-grid">
        <section className="section-block hydration-hero">
          <div>
            <p className="eyebrow">Hydration Tracker</p>
            <h2>{hydrationPlan.currentIntakeMl || 0} ml of {hydrationPlan.dailyGoalMl || 0} ml</h2>
            <p>Track normal water intake and keep gentle reminders through the day. Flavored water is optional and should not replace clinician guidance.</p>
          </div>
          <div className="water-ring" aria-label="Hydration progress"><strong>{hydrationPlan.progressPercent || 0}%</strong><span>today</span></div>
        </section>

        {(hydrationMessage || hydrationError) && <div className={hydrationMessage ? "success-banner" : "error-banner"}>{hydrationMessage || hydrationError}</div>}

        <section className="hydration-grid">
          <form className="section-block hydration-card" onSubmit={handleHydrationSave}>
            <div className="section-heading"><p className="eyebrow">Daily Water</p><h2>Goal and reminders</h2></div>
            <div className="progress-track"><span style={{ width: `${hydrationPlan.progressPercent || 0}%` }} /></div>
            <div className="quick-log">
              <button onClick={() => handleHydrationLog(250)} type="button">+250 ml</button>
              <button onClick={() => handleHydrationLog(500)} type="button">+500 ml</button>
              <label>Custom ml<input min="1" onChange={(event) => setWaterAmount(Number(event.target.value))} type="number" value={waterAmount} /></label>
              <button className="secondary-action" onClick={() => handleHydrationLog(waterAmount)} type="button">Add Water</button>
            </div>
            <label>Daily goal (ml)<input min="500" onChange={(event) => updateHydrationField("dailyGoalMl", Number(event.target.value))} type="number" value={hydrationPlan.dailyGoalMl || 0} /></label>
            <label>Reminder gap (minutes)<input min="15" onChange={(event) => updateHydrationField("reminderGapMinutes", Number(event.target.value))} type="number" value={hydrationPlan.reminderGapMinutes || 90} /></label>
            <label className="checkbox-row"><input checked={Boolean(hydrationPlan.reminderEnabled)} onChange={(event) => updateHydrationField("reminderEnabled", event.target.checked)} type="checkbox" />Enable hydration reminders</label>
            <button className="primary-action" type="submit">Save Hydration Plan</button>
          </form>

          <form className="section-block hydration-card" onSubmit={handleHydrationSave}>
            <div className="section-heading"><p className="eyebrow">Flavored Water</p><h2>Recipe and best time</h2></div>
            <label>Recipe title<input onChange={(event) => updateHydrationField("detoxRecipeTitle", event.target.value)} type="text" value={hydrationPlan.detoxRecipeTitle || ""} /></label>
            <label>Ingredients<textarea onChange={(event) => updateHydrationField("detoxIngredients", event.target.value)} rows="3" value={hydrationPlan.detoxIngredients || ""} /></label>
            <label>How to prepare<textarea onChange={(event) => updateHydrationField("detoxSteps", event.target.value)} rows="4" value={hydrationPlan.detoxSteps || ""} /></label>
            <label>Best time<input onChange={(event) => updateHydrationField("bestTime", event.target.value)} type="text" value={hydrationPlan.bestTime || ""} /></label>
            <button className="primary-action" type="submit">Save Recipe</button>
          </form>
        </section>
      </section>
    );
  }

  if (!isAuthenticated) {
    return (
      <main className="auth-shell">
        <section className="auth-visual">
          <p className="eyebrow">NurtureAI</p>
          <h1>Private pregnancy nutrition guidance for every day.</h1>
          <p>Sign in to generate meal plans, pantry-aware grocery lists, reminders, and family support tasks.</p>
        </section>

        <section className="auth-panel">
          <div className="auth-tabs" role="tablist" aria-label="Authentication mode">
            <button className={authMode === "login" ? "active" : ""} onClick={() => setAuthMode("login")} type="button">Login</button>
            <button className={authMode === "signup" ? "active" : ""} onClick={() => setAuthMode("signup")} type="button">Sign Up</button>
          </div>

          {authMode === "login" ? (
            <form className="auth-form" onSubmit={(event) => event.preventDefault()}>
              <div className="section-heading"><p className="eyebrow">Welcome Back</p><h2>Login to your account</h2></div>
              <div className="auth-methods"><button className={loginMethod === "password" ? "active" : ""} onClick={() => setLoginMethod("password")} type="button">Username</button><button className={loginMethod === "otp" ? "active" : ""} onClick={() => setLoginMethod("otp")} type="button">Phone OTP</button></div>
              {loginMethod === "password" ? <><label>Username<input autoComplete="username" name="username" onChange={(event) => setUsername(event.target.value)} type="text" value={username} /></label><label>Password<input autoComplete="current-password" name="password" onChange={(event) => setPassword(event.target.value)} type="password" value={password} /></label></> : <><label>Phone Number<input autoComplete="tel" name="phoneNumber" type="tel" /></label><div className="todo-note">TODO: Connect SMS OTP provider and backend verification flow.</div></>}
              {loginError && <div className="error-banner">{loginError}</div>}
              <button className="primary-action" onClick={handleLogin} type="button">Continue</button>
            </form>
          ) : (
            <form className="auth-form" onSubmit={(event) => event.preventDefault()}>
              <div className="section-heading"><p className="eyebrow">Create Account</p><h2>Sign up securely</h2></div>
              <label>Username<input autoComplete="username" name="username" onChange={(event) => setUsername(event.target.value)} type="text" value={username} /></label>
              <label>First Name<input autoComplete="given-name" name="firstName" type="text" /></label>
              <label>Last Name<input autoComplete="family-name" name="lastName" type="text" /></label>
              <label>Phone Number<input autoComplete="tel" name="phoneNumber" type="tel" /></label>
              <label>Email<input autoComplete="email" name="email" type="email" /></label>
              <label>Password<input autoComplete="new-password" name="password" type="password" /></label>
              <div className="todo-note">TODO: Connect sign-up form to backend account creation.</div>
            </form>
          )}
        </section>
      </main>
    );
  }

  return (
    <main className="app-shell">
      <section className="overview wide-overview">
        <header className="topbar app-topbar">
          <div>
            <p className="eyebrow">NurtureAI Phase 1</p>
            <h1>{activeView === "dashboard" ? "Pregnancy nutrition dashboard" : activeView === "nutrition" ? "Nutrition Schedule" : activeView === "hydration" ? "Hydration Tracker" : activeView === "account" ? "My Account" : "Profile Setup"}</h1>
            <p className="topbar-subtitle">Signed in as {displayName}</p>
            <div className={aiStatus.realAiEnabled ? "ai-status live" : "ai-status mock"}>
              {mockFlow ? "MockFlow enabled" : aiStatus.realAiEnabled ? `${aiStatus.provider} live` : "Mock AI"}{aiStatus.model ? ` · ${aiStatus.model}` : ""}
            </div>
          </div>
          <div className="topbar-actions">
            <button className={activeView === "dashboard" ? "nav-action active" : "nav-action"} onClick={() => setActiveView("dashboard")} type="button">Dashboard</button>
            <button className={activeView === "nutrition" ? "nav-action active" : "nav-action"} onClick={() => setActiveView("nutrition")} type="button">Nutrition</button>
            <button className={activeView === "hydration" ? "nav-action active" : "nav-action"} onClick={() => setActiveView("hydration")} type="button">Hydration</button>
            <button className={activeView === "onboarding" ? "nav-action active" : "nav-action"} onClick={() => setActiveView("onboarding")} type="button">Profile Setup</button>
            <button className={activeView === "account" ? "nav-action active" : "nav-action"} onClick={() => setActiveView("account")} type="button">My Account</button>
            {activeView === "dashboard" && <button className="primary-action" disabled={isGenerating || !hasProfileBasics} onClick={handleGeneratePlan}>{isGenerating ? "Generating..." : "Generate Plan"}</button>}
          </div>
        </header>

        {activeView === "dashboard" ? (
          <>
            {(dashboardError || generationError) && <div className="error-banner">{dashboardError || generationError}</div>}
            {listMessage && <div className="success-banner">{listMessage}</div>}
            {!hasProfileBasics && <div className="empty-state"><strong>Complete your profile first.</strong><span>Add age, height, weight, food preference, and allergy details before generating a recommendation.</span><button onClick={() => setActiveView("onboarding")} type="button">Open Profile Setup</button></div>}
            {isDashboardLoading ? <div className="loading-panel">Loading your saved profile, pantry, and meal plan...</div> : (
              <>
                <div className="dashboard-hero">
                  <section className="today-summary spacious-summary">
                    <p className="eyebrow">Good morning, {displayName}</p>
                    <h2>{dietProfile.pregnancyWeek || plan.pregnancyWeek} weeks pregnant</h2>
                    <p>Your plan uses your profile, pantry, allergy notes, and food preference: {formatFoodPreference(dietProfile.foodPreference)}.</p>
                    <div className="profile-pills"><span>{dietProfile.age || "Age not set"} yrs</span><span>{dietProfile.heightCm || "Height not set"} cm</span><span>{dietProfile.weightKg || "Weight not set"} kg</span><span>{dietProfile.allergies ? `Avoid: ${dietProfile.allergies}` : "No allergies saved"}</span></div>
                  </section>

                  <section className="metric-board" aria-label="Daily nutrition goals">
                    {(plan.nutrients || []).length > 0 ? plan.nutrients.map((nutrient) => <article className="metric" key={nutrient.label}><span>{nutrient.label}</span><strong>{nutrient.value}</strong><small>{nutrient.status}</small></article>) : <article className="metric empty-metric"><span>No nutrition goals yet</span><strong>Generate</strong><small>Plan needed</small></article>}
                    <article className="metric hydration"><span>Water</span><strong>{plan.hydrationGoal || "--"}</strong><small>Daily hydration goal</small></article>
                  </section>
                </div>

                <section className="content-grid roomy-grid">
                  <div className="main-column">
                    <section className="section-block">
                      <div className="section-heading"><p className="eyebrow">AI Nutrition Copilot</p><h2>Meal plan</h2></div>
                      {(plan.meals || []).length === 0 ? <div className="empty-state inline-empty"><strong>No meal plan yet.</strong><span>Generate a plan after completing profile setup.</span></div> : <div className="meal-list relaxed-meal-list">{plan.meals.map((meal) => <article className="meal-card" key={meal.time}><span>{meal.time}</span><h3>{meal.title}</h3><p>{meal.note}</p><button className="text-link" onClick={() => handleExplainMeal(meal)} type="button">Why this?</button></article>)}</div>}
                    </section>

                    <section className="section-block two-column editable-lists">
                      <form onSubmit={handlePantrySave}>
                        <div className="section-heading"><p className="eyebrow">Pantry Intelligence</p><h2>Available ingredients</h2></div>
                        <textarea aria-label="Pantry items" onChange={(event) => setPantryText(event.target.value)} rows="4" value={pantryText} />
                        <div className="chip-list">{pantryItems.map((item) => <span className="chip" key={item}>{item}</span>)}</div>
                        <button className="secondary-action" type="submit">Save Pantry</button>
                      </form>
                      <form onSubmit={handleGrocerySave}>
                        <div className="section-heading"><p className="eyebrow">Shopping List</p><h2>Missing essentials</h2></div>
                        <textarea aria-label="Grocery items" onChange={(event) => setGroceryText(event.target.value)} rows="4" value={groceryText} />
                        {groceries.length === 0 ? <p className="muted-copy">No grocery items saved yet.</p> : <ul className="check-list">{groceries.map((item) => <li key={item}>{item}</li>)}</ul>}
                        <button className="secondary-action" type="submit">Save Grocery List</button>
                      </form>
                    </section>
                  </div>

                  <aside className="side-column">
                    <section className="section-block chat-panel"><div className="section-heading"><p className="eyebrow">AI Chat</p><h2>Ask NurtureAI</h2></div><div className="chat-messages" aria-live="polite">{chatMessages.map((message, index) => <div className={`chat-message ${message.role}`} key={`${message.role}-${index}`}>{message.text}</div>)}{isChatLoading && <div className="chat-message assistant">Thinking...</div>}</div><form className="chat-form" onSubmit={(event) => { event.preventDefault(); handleAskAi(); }}><input aria-label="Ask NurtureAI" onChange={(event) => setChatInput(event.target.value)} placeholder="Ask about food, cravings, groceries..." type="text" value={chatInput} /><button disabled={isChatLoading} type="submit">Ask</button></form></section>
                    <section className="section-block"><div className="section-heading"><p className="eyebrow">Reminders</p><h2>Today</h2></div>{(plan.reminders || []).length === 0 ? <p className="muted-copy">No reminders generated yet.</p> : <ul className="timeline">{plan.reminders.map((reminder) => <li key={reminder}>{reminder}</li>)}</ul>}</section>
                    <section className="partner-panel"><p className="eyebrow">Family Mode</p><h2>Partner task</h2><p>{plan.partnerTask || "Generate a plan to create today's support task."}</p></section>
                    <section className="safety-note"><p>NurtureAI gives evidence-informed guidance and should not replace your obstetrician, dietitian, or emergency care.</p></section>
                  </aside>
                </section>
              </>
            )}
          </>
        ) : activeView === "nutrition" ? renderNutritionSchedule() : activeView === "hydration" ? renderHydrationTracker() : activeView === "onboarding" ? renderProfileForm(true) : (
          <section className="account-grid">
            <section className="section-block account-hero"><p className="eyebrow">My Account</p><h2>{displayName}</h2><p>Manage sign-in security and partner contact details so future reminders can be sent to both parents.</p><div className="account-meta"><span>{authUser?.username}</span><span>{authUser?.email}</span></div></section>
            {(accountMessage || accountError) && <div className={accountMessage ? "success-banner" : "error-banner"}>{accountMessage || accountError}</div>}
            <section className="section-block account-card"><div className="section-heading"><p className="eyebrow">Security</p><h2>Update password</h2></div><form className="account-form" onSubmit={handlePasswordUpdate}><label>Current Password<input autoComplete="current-password" onChange={(event) => setCurrentPassword(event.target.value)} type="password" value={currentPassword} /></label><label>New Password<input autoComplete="new-password" onChange={(event) => setNewPassword(event.target.value)} type="password" value={newPassword} /></label><button className="primary-action" type="submit">Update Password</button></form></section>
            <section className="section-block account-card"><div className="section-heading"><p className="eyebrow">Family Notifications</p><h2>Partner contact</h2></div><form className="account-form partner-form" onSubmit={handlePartnerSave}><label>First Name<input onChange={(event) => updatePartnerField("firstName", event.target.value)} type="text" value={partner.firstName || ""} /></label><label>Last Name<input onChange={(event) => updatePartnerField("lastName", event.target.value)} type="text" value={partner.lastName || ""} /></label><label>Email<input onChange={(event) => updatePartnerField("email", event.target.value)} type="email" value={partner.email || ""} /></label><label>Phone Number<input onChange={(event) => updatePartnerField("phoneNumber", event.target.value)} type="tel" value={partner.phoneNumber || ""} /></label><label>Relationship<select onChange={(event) => updatePartnerField("relationship", event.target.value)} value={partner.relationship || "partner"}><option value="partner">Partner</option><option value="spouse">Spouse</option><option value="caregiver">Caregiver</option></select></label><label className="checkbox-row"><input checked={Boolean(partner.notificationsEnabled)} onChange={(event) => updatePartnerField("notificationsEnabled", event.target.checked)} type="checkbox" />Include this person in future reminders and notifications</label><button className="primary-action" type="submit">Save Partner Contact</button></form></section>
            <section className="safety-note account-note"><p>Next notification milestone: send reminders to both the mother and saved partner contact through SMS, email, or push notifications.</p></section>
          </section>
        )}
      </section>
    </main>
  );
}

export default App;
