create table user_diet_profiles (
    id uuid primary key,
    user_id uuid not null unique references users(id),
    age integer,
    height_cm numeric(5,2),
    weight_kg numeric(5,2),
    pregnancy_week integer not null default 22,
    food_preference varchar(80) not null default 'vegetarian_with_eggs',
    eggs_allowed boolean not null default true,
    allergies text,
    cuisine_region varchar(120) not null default 'Indian',
    budget_level varchar(40) not null default 'medium',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

insert into user_diet_profiles (
    id, user_id, age, height_cm, weight_kg, pregnancy_week, food_preference, eggs_allowed, allergies, cuisine_region, budget_level
)
select
    '33333333-3333-3333-3333-333333333333'::uuid,
    id,
    30,
    165,
    68,
    22,
    'vegetarian_with_eggs',
    true,
    '',
    'Indian',
    'medium'
from users
where username = 'jakka.vikram'
on conflict (user_id) do nothing;

insert into user_diet_profiles (
    id, user_id, age, height_cm, weight_kg, pregnancy_week, food_preference, eggs_allowed, allergies, cuisine_region, budget_level
)
select
    '44444444-4444-4444-4444-444444444444'::uuid,
    id,
    31,
    170,
    72,
    22,
    'vegetarian_with_eggs',
    true,
    '',
    'Indian',
    'medium'
from users
where username = 'gaurav.kumar'
on conflict (user_id) do nothing;
