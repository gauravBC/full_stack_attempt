# Vikram Feature Notes - NurtureAI Expansion

These notes were captured from the handwritten feature/layout photos shared on 2026-06-27. They extend NurtureAI from pregnancy meal planning into a broader pregnancy companion experience.

## 1. Pregnancy Timeline Tracker

Goal: Track the user from sign-up through the pregnancy journey, potentially up to 9 months.

Feature ideas:
- Start tracking from the day the user signs up.
- Show pregnancy journey columns/cards by phase, week, or month.
- Add animated cards, short GIFs, or visual illustrations for each stage.
- Allow users to upload their own pregnancy images/photos.
- Add a visual timeline with month/week progress.
- After 7+ months, suggest maternity photography options.
- For photography suggestions, eventually show nearby locations or partners.

Possible screens:
- Pregnancy Timeline
- Monthly Progress
- Photo Memories
- Maternity Photoshoot Suggestions

Backend needs:
- Pregnancy milestone table.
- User uploaded media metadata.
- Timeline event records.
- Optional location/preferences for nearby services.

## 2. Hydration / Detox Water Module

Goal: Help users drink enough water and learn safe hydration options.

Feature ideas:
- Detox water recipes.
- Short recipe videos or GIFs.
- Ingredient list.
- Preparation steps.
- Best time to drink.
- Time-gap guidance for drinking water.
- Water reminders and alarms.
- Normal water tracker, not only detox water.
- Visual reminder cards using short GIF/video.

Possible screens:
- Water Tracker
- Detox Water Recipes
- Hydration Schedule
- Reminder Settings

Backend needs:
- Hydration logs.
- Water goals.
- Recipe content.
- Reminder schedule.
- Notification preferences.

Safety note:
- Detox water should be presented as optional flavored water, not medical detox.
- Avoid unsafe ingredients and always include pregnancy safety guidance.

## 3. Walking / Activity Tracker

Goal: Encourage safe pregnancy movement and track walking progress.

Feature ideas:
- Normal walk mode.
- Brisk walk mode, if suitable and approved.
- Daily step target, example: 10k steps.
- Divide target into morning, evening, and night sessions.
- Real-time updates from tracker/app if possible.
- Activity monitor with simple GIF/animation.
- Daily progress bar and session completion state.

Possible screens:
- Walking Tracker
- Daily Step Plan
- Activity Sessions
- Movement History

Backend needs:
- Daily activity goals.
- Step/session logs.
- Integration placeholders for phone health APIs or wearable sources.
- Doctor/safety override flags.

Safety note:
- Walking recommendations must account for pregnancy week, symptoms, doctor advice, and risk flags.

## 4. Nutrition Schedule / Meal Timing

Goal: Move beyond one daily meal plan into a timed nutrition schedule.

Suggested schedule from notes:
- 6 AM: Morning milk with protein powder.
- 8 AM: Breakfast with protein.
- 10 AM: Short food/snack such as salads, dry fruits, juices.
- 12 PM / 2 PM: Lunch with protein, salads, juices, fruits.
- 4 PM: Snacks.
- 6 PM: Juices, dry fruits, fruits.
- 8 PM: Dinner.

Feature ideas:
- Timed meal cards.
- Nutrient and calorie calculation across the day.
- Food alternatives for each time slot.
- Reminder/alarm for meal timing.
- Pregnancy-safe protein guidance.
- Account for allergies, vegetarian/non-vegetarian preference, egg preference, pantry, budget, and cuisine.

Possible screens:
- Daily Nutrition Schedule
- Meal Timing
- Calorie/Nutrient Summary
- Food Alternatives

Backend needs:
- Meal slots table.
- Nutrition target table.
- Calorie and nutrient estimates.
- Reminder events per meal slot.

## 5. Tablets / Supplement Reminders

Goal: Track supplements/tablets by pregnancy stage and remind users at the right time.

Feature ideas:
- Tablet schedule by pregnancy month range:
  - 1-3 months.
  - 3-6 months.
  - 6-9 months.
- Tablet timings.
- Tablet reminders.
- Mark taken/skipped.
- Notes about taking iron away from calcium/dairy.
- Doctor-prescribed supplement list.

Possible screens:
- Tablet Schedule
- Supplement Timeline
- Tablet Reminder Settings
- Taken/Skipped History

Backend needs:
- Medication/supplement table.
- Schedule table.
- Reminder table.
- Taken/skipped event log.

Safety note:
- App should not prescribe tablets. It should track doctor-prescribed tablets and provide timing reminders.

## 6. Yoga / Meditation / Gym Customization

Goal: Provide customizable wellness routines.

Feature ideas:
- Yoga module.
- Meditation module.
- Gym/exercise module.
- Fully customizable routines.
- Pregnancy-safe movement content.
- Preference-based routine builder.

Possible screens:
- Wellness Plan
- Yoga
- Meditation
- Exercise/Gym
- Routine Customization

Backend needs:
- Wellness activities catalog.
- User routine preferences.
- Completion logs.
- Safety flags by pregnancy stage.

Safety note:
- Exercise content should be conservative and should advise clinician approval.

## 7. Health Checks / Common Conditions

Goal: Track common pregnancy health concerns and provide checklists/reminders.

Feature ideas:
- Checks for diabetes.
- Checks for thyroid.
- Common pregnancy issue checklist.
- Health assessment prompts.
- User-entered readings or doctor notes.

Possible screens:
- Health Checks
- Condition Tracker
- Doctor Notes
- Reading History

Backend needs:
- Health condition flags.
- Reading logs.
- Notes/files from doctor visits.
- Reminder schedule for tests/checkups.

Safety note:
- This should not diagnose. It should prompt tracking, reminders, and doctor follow-up.

## 8. Sleep Tracker / Health Assessment

Goal: Track sleep and provide health insights based on sleep patterns.

Feature ideas:
- Sleep tracker.
- Sleep duration and quality inputs.
- Health assessment based on tracker data.
- Optional integration with phone/wearable health data.
- Sleep tips and reminders.

Possible screens:
- Sleep Tracker
- Sleep History
- Sleep Assessment
- Sleep Tips

Backend needs:
- Sleep logs.
- Sleep quality ratings.
- Assessment summary records.
- Integration placeholders for health APIs.

## Suggested Implementation Order

1. Nutrition schedule and meal reminders.
2. Hydration tracker and reminders.
3. Tablet/supplement reminders.
4. Walking/activity tracker.
5. Pregnancy timeline and photo memories.
6. Sleep tracker.
7. Wellness/yoga/meditation customization.
8. Health checks and condition tracking.
9. Maternity photography suggestions and location-based services.

## Product Direction

These features shift NurtureAI from a meal-planning app into a pregnancy companion covering:

- Nutrition.
- Hydration.
- Supplements.
- Activity.
- Sleep.
- Wellness.
- Health reminders.
- Pregnancy memories/timeline.
- Partner/family support.

All medical-adjacent features should remain informational and should direct users to consult their clinician.
