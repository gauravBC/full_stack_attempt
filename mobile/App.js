import { StatusBar } from "expo-status-bar";
import { useState } from "react";
import { ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";

const plan = {
  week: 22,
  hydration: "2.7 L",
  nutrients: [
    ["Protein", "75 g"],
    ["Iron", "27 mg"],
    ["Calcium", "1,000 mg"],
  ],
  meals: [
    ["Breakfast", "Vegetable oats with boiled eggs"],
    ["Lunch", "Spinach dal with brown rice"],
    ["Snack", "Greek yogurt with fruit"],
    ["Dinner", "Paneer curry with chapati"],
  ],
  groceries: ["Milk", "Oranges", "Chickpeas", "Almonds"],
  schedule: [
    ["06:00", "Morning milk", "Milk with protein powder if approved"],
    ["08:00", "Breakfast", "Protein-rich breakfast"],
    ["10:00", "Short snack", "Fruit, salad, dry fruits, or juice"],
    ["13:00", "Lunch", "Dal/paneer with grains and salad"],
    ["16:00", "Snack", "Roasted chana, yogurt, nuts, or sprouts"],
    ["20:00", "Dinner", "Balanced protein, vegetables, and grains"],
  ],
  hydrationTracker: {
    goalMl: 2700,
    currentMl: 750,
    gap: "Every 90 min",
    flavoredWater: "Cucumber lemon mint water",
  },
};

export default function App() {
  const [mode, setMode] = useState("login");
  const [loginMethod, setLoginMethod] = useState("password");
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  if (!isAuthenticated) {
    return (
      <ScrollView contentContainerStyle={styles.screen}>
        <StatusBar style="dark" />
        <View style={styles.header}>
          <Text style={styles.eyebrow}>NurtureAI</Text>
          <Text style={styles.title}>Private pregnancy nutrition guidance.</Text>
          <Text style={styles.copy}>
            Login or create an account to protect your nutrition plans, pantry,
            reminders, and family support tasks.
          </Text>
        </View>

        <View style={styles.panel}>
          <View style={styles.switcher}>
            <TouchableOpacity
              onPress={() => setMode("login")}
              style={[styles.switchButton, mode === "login" && styles.activeSwitch]}
            >
              <Text style={styles.switchText}>Login</Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={() => setMode("signup")}
              style={[styles.switchButton, mode === "signup" && styles.activeSwitch]}
            >
              <Text style={styles.switchText}>Sign Up</Text>
            </TouchableOpacity>
          </View>

          {mode === "login" ? (
            <>
              <Text style={styles.heading}>Login to your account</Text>
              <View style={styles.switcher}>
                <TouchableOpacity
                  onPress={() => setLoginMethod("password")}
                  style={[styles.switchButton, loginMethod === "password" && styles.activeSwitch]}
                >
                  <Text style={styles.switchText}>Username</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  onPress={() => setLoginMethod("otp")}
                  style={[styles.switchButton, loginMethod === "otp" && styles.activeSwitch]}
                >
                  <Text style={styles.switchText}>Phone OTP</Text>
                </TouchableOpacity>
              </View>
              {loginMethod === "password" ? (
                <>
                  <AuthInput label="Username" />
                  <AuthInput label="Password" secureTextEntry />
                </>
              ) : (
                <>
                  <AuthInput label="Phone Number" keyboardType="phone-pad" />
                  <Text style={styles.todo}>TODO: Add SMS OTP request and verification.</Text>
                </>
              )}
            </>
          ) : (
            <>
              <Text style={styles.heading}>Sign up securely</Text>
              <AuthInput label="Username" />
              <AuthInput label="First Name" />
              <AuthInput label="Last Name" />
              <AuthInput label="Phone Number" keyboardType="phone-pad" />
              <AuthInput label="Email" keyboardType="email-address" />
              <AuthInput label="Password" secureTextEntry />
            </>
          )}
          <TouchableOpacity style={styles.button} onPress={() => setIsAuthenticated(true)}>
            <Text style={styles.buttonText}>Continue</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.screen}>
      <StatusBar style="dark" />
      <View style={styles.header}>
        <Text style={styles.eyebrow}>NurtureAI Phase 1</Text>
        <Text style={styles.title}>Today's pregnancy nutrition plan</Text>
        <TouchableOpacity style={styles.button}>
          <Text style={styles.buttonText}>Generate Plan</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.panel}>
        <Text style={styles.eyebrow}>Good morning, Sarah</Text>
        <Text style={styles.heading}>You are {plan.week} weeks pregnant.</Text>
        <Text style={styles.copy}>
          Your plan uses pantry ingredients and focuses on protein, iron,
          calcium, and hydration.
        </Text>
      </View>

      <View style={styles.metrics}>
        {plan.nutrients.map(([label, value]) => (
          <View style={styles.metric} key={label}>
            <Text style={styles.metricLabel}>{label}</Text>
            <Text style={styles.metricValue}>{value}</Text>
          </View>
        ))}
        <View style={styles.metric}>
          <Text style={styles.metricLabel}>Water</Text>
          <Text style={styles.metricValue}>{plan.hydration}</Text>
        </View>
      </View>

      <View style={styles.panel}>
        <Text style={styles.eyebrow}>AI Nutrition Copilot</Text>
        <Text style={styles.heading}>Meal plan</Text>
        {plan.meals.map(([time, title]) => (
          <View style={styles.row} key={time}>
            <Text style={styles.rowLabel}>{time}</Text>
            <Text style={styles.rowText}>{title}</Text>
          </View>
        ))}
      </View>


      <View style={styles.panel}>
        <Text style={styles.eyebrow}>Nutrition Schedule</Text>
        <Text style={styles.heading}>Timed meals and reminders</Text>
        {plan.schedule.map(([time, title, foods]) => (
          <View style={styles.scheduleRow} key={`${time}-${title}`}>
            <Text style={styles.scheduleTime}>{time}</Text>
            <View style={styles.scheduleCopy}>
              <Text style={styles.rowText}>{title}</Text>
              <Text style={styles.copy}>{foods}</Text>
            </View>
          </View>
        ))}
      </View>

      <View style={styles.panel}>
        <Text style={styles.eyebrow}>Hydration Tracker</Text>
        <Text style={styles.heading}>{plan.hydrationTracker.currentMl} ml of {plan.hydrationTracker.goalMl} ml</Text>
        <View style={styles.progressTrack}>
          <View style={[styles.progressFill, { width: `${Math.round((plan.hydrationTracker.currentMl / plan.hydrationTracker.goalMl) * 100)}%` }]} />
        </View>
        <Text style={styles.copy}>Reminder gap: {plan.hydrationTracker.gap}</Text>
        <Text style={styles.copy}>Optional flavored water: {plan.hydrationTracker.flavoredWater}</Text>
      </View>

      <View style={styles.panel}>
        <Text style={styles.eyebrow}>Shopping List</Text>
        <Text style={styles.heading}>Missing essentials</Text>
        {plan.groceries.map((item) => (
          <Text style={styles.listItem} key={item}>
            {item}
          </Text>
        ))}
      </View>

      <View style={styles.partnerPanel}>
        <Text style={styles.eyebrow}>Family Mode</Text>
        <Text style={styles.heading}>Partner task</Text>
        <Text style={styles.copy}>
          Prepare dinner tonight and refill the water bottle before bedtime.
        </Text>
      </View>
    </ScrollView>
  );
}

function AuthInput({ label, ...props }) {
  return (
    <View style={styles.inputGroup}>
      <Text style={styles.inputLabel}>{label}</Text>
      <TextInput style={styles.input} autoCapitalize="none" {...props} />
    </View>
  );
}

const styles = StyleSheet.create({
  screen: {
    padding: 20,
    paddingTop: 64,
    backgroundColor: "#eef4f1",
    gap: 16,
  },
  header: {
    gap: 14,
  },
  eyebrow: {
    color: "#0f766e",
    fontSize: 12,
    fontWeight: "800",
    letterSpacing: 1,
    textTransform: "uppercase",
  },
  title: {
    color: "#111827",
    fontSize: 34,
    fontWeight: "800",
    lineHeight: 38,
  },
  button: {
    alignItems: "center",
    borderRadius: 6,
    backgroundColor: "#0f766e",
    paddingVertical: 14,
  },
  buttonText: {
    color: "#ffffff",
    fontWeight: "800",
  },
  switcher: {
    flexDirection: "row",
    gap: 8,
  },
  switchButton: {
    flex: 1,
    alignItems: "center",
    borderColor: "#cbd5e1",
    borderRadius: 6,
    borderWidth: 1,
    backgroundColor: "#ffffff",
    paddingVertical: 11,
  },
  activeSwitch: {
    borderColor: "#0f766e",
    backgroundColor: "#f0fdfa",
  },
  switchText: {
    color: "#334155",
    fontWeight: "800",
  },
  inputGroup: {
    gap: 7,
  },
  inputLabel: {
    color: "#475569",
    fontSize: 14,
    fontWeight: "800",
  },
  input: {
    minHeight: 46,
    borderColor: "#cbd5e1",
    borderRadius: 6,
    borderWidth: 1,
    backgroundColor: "#ffffff",
    paddingHorizontal: 12,
  },
  todo: {
    borderColor: "#fde68a",
    borderRadius: 8,
    borderWidth: 1,
    backgroundColor: "#fffbeb",
    color: "#92400e",
    padding: 12,
    fontWeight: "700",
  },
  panel: {
    borderColor: "#d9e2e7",
    borderRadius: 8,
    borderWidth: 1,
    backgroundColor: "#ffffff",
    padding: 20,
    gap: 10,
  },
  partnerPanel: {
    borderColor: "#fed7aa",
    borderRadius: 8,
    borderWidth: 1,
    backgroundColor: "#fff7ed",
    padding: 20,
    gap: 10,
  },
  heading: {
    color: "#172033",
    fontSize: 22,
    fontWeight: "800",
    lineHeight: 27,
  },
  copy: {
    color: "#536174",
    fontSize: 16,
    lineHeight: 24,
  },
  metrics: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10,
  },
  metric: {
    flexGrow: 1,
    minWidth: "47%",
    borderColor: "#dbeafe",
    borderRadius: 8,
    borderWidth: 1,
    backgroundColor: "#f8fbff",
    padding: 16,
  },
  metricLabel: {
    color: "#64748b",
    fontSize: 12,
    fontWeight: "800",
    textTransform: "uppercase",
  },
  metricValue: {
    marginTop: 8,
    color: "#1f2937",
    fontSize: 24,
    fontWeight: "800",
  },
  row: {
    borderTopColor: "#e5e7eb",
    borderTopWidth: 1,
    paddingTop: 12,
  },
  rowLabel: {
    color: "#64748b",
    fontSize: 12,
    fontWeight: "800",
    textTransform: "uppercase",
  },
  rowText: {
    marginTop: 4,
    color: "#111827",
    fontSize: 16,
    fontWeight: "700",
  },
  listItem: {
    color: "#334155",
    fontSize: 16,
    fontWeight: "700",
  },

  scheduleRow: {
    flexDirection: "row",
    gap: 12,
    borderTopColor: "#e5e7eb",
    borderTopWidth: 1,
    paddingTop: 12,
  },
  scheduleTime: {
    width: 56,
    color: "#0f766e",
    fontSize: 14,
    fontWeight: "900",
  },
  scheduleCopy: {
    flex: 1,
    gap: 3,
  },
  progressTrack: {
    height: 12,
    overflow: "hidden",
    borderRadius: 999,
    backgroundColor: "#e2e8f0",
  },
  progressFill: {
    height: "100%",
    borderRadius: 999,
    backgroundColor: "#0f766e",
  },

});
