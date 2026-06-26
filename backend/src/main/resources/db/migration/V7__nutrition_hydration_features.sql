create table if not exists nutrition_schedule_slots (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    slot_time varchar(20) not null,
    title varchar(140) not null,
    foods text not null,
    calories integer,
    protein_grams integer,
    reminder_enabled boolean not null default true,
    completed boolean not null default false,
    sort_order integer not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index if not exists idx_nutrition_schedule_slots_user_sort
    on nutrition_schedule_slots(user_id, sort_order, slot_time);

create table if not exists hydration_plans (
    id uuid primary key,
    user_id uuid not null unique references users(id) on delete cascade,
    daily_goal_ml integer not null default 2700,
    current_intake_ml integer not null default 0,
    reminder_gap_minutes integer not null default 90,
    detox_recipe_title varchar(140) not null default 'Cucumber lemon mint water',
    detox_ingredients text not null default 'Cucumber slices, lemon slices, mint leaves, water',
    detox_steps text not null default 'Add washed ingredients to water, refrigerate for 30 minutes, and sip as flavored water.',
    best_time varchar(120) not null default 'Mid-morning or early evening, away from medicine timing if your clinician advised it',
    reminder_enabled boolean not null default true,
    updated_at timestamptz not null default now()
);

create table if not exists hydration_logs (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    amount_ml integer not null,
    logged_at timestamptz not null default now()
);

create index if not exists idx_hydration_logs_user_time
    on hydration_logs(user_id, logged_at desc);

insert into nutrition_schedule_slots (
    id, user_id, slot_time, title, foods, calories, protein_grams, reminder_enabled, completed, sort_order
)
select gen_random_uuid(), u.id, slot.slot_time, slot.title, slot.foods, slot.calories, slot.protein_grams, true, false, slot.sort_order
from users u
cross join (values
    ('06:00', 'Morning milk', 'Milk with pregnancy-safe protein powder if approved by clinician', 220, 18, 1),
    ('08:00', 'Breakfast', 'Protein-rich breakfast: oats, dal chilla, idli with sambar, or eggs if allowed', 420, 22, 2),
    ('10:00', 'Short snack', 'Salad, fruit, dry fruits, or fresh juice with no added sugar', 180, 5, 3),
    ('13:00', 'Lunch', 'Dal or paneer with rice/chapati plus salad and fruit', 560, 26, 4),
    ('16:00', 'Evening snack', 'Roasted chana, yogurt, nuts, fruit, or sprouts', 240, 12, 5),
    ('18:00', 'Light fluids', 'Fruit, dry fruits, or fresh juice; keep caffeine limited', 160, 4, 6),
    ('20:00', 'Dinner', 'Balanced dinner with protein, vegetables, and whole grains', 500, 24, 7)
) as slot(slot_time, title, foods, calories, protein_grams, sort_order)
where u.username in ('jakka.vikram', 'gaurav.kumar')
  and not exists (
      select 1 from nutrition_schedule_slots existing where existing.user_id = u.id
  );

insert into hydration_plans (
    id, user_id, daily_goal_ml, current_intake_ml, reminder_gap_minutes, detox_recipe_title,
    detox_ingredients, detox_steps, best_time, reminder_enabled
)
select gen_random_uuid(), u.id, 2700, 0, 90, 'Cucumber lemon mint water',
       'Cucumber slices, lemon slices, mint leaves, water',
       'Add washed ingredients to water, refrigerate for 30 minutes, and sip as flavored water. Avoid calling it a medical detox.',
       'Mid-morning or early evening; keep a gap from iron/calcium tablets if your clinician advised it.',
       true
from users u
where u.username in ('jakka.vikram', 'gaurav.kumar')
on conflict (user_id) do nothing;
