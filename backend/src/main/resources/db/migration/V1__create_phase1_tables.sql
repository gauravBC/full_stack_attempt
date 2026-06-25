create table users (
    id uuid primary key,
    email varchar(255) not null unique,
    display_name varchar(120) not null,
    created_at timestamptz not null default now()
);

create table pregnancy_profiles (
    id uuid primary key,
    user_id uuid not null references users(id),
    due_date date,
    pregnancy_week integer not null,
    diet_type varchar(80) not null,
    cuisine_region varchar(120) not null,
    allergies text,
    medical_flags text,
    created_at timestamptz not null default now()
);

create table pantry_items (
    id uuid primary key,
    user_id uuid not null references users(id),
    name varchar(140) not null,
    quantity varchar(80),
    expiry_date date,
    created_at timestamptz not null default now()
);

create table daily_meal_plans (
    id uuid primary key,
    user_id uuid not null references users(id),
    plan_date date not null,
    meals jsonb not null,
    nutrients jsonb not null,
    grocery_list jsonb not null,
    safety_notes jsonb not null,
    created_at timestamptz not null default now(),
    unique(user_id, plan_date)
);

create table family_members (
    id uuid primary key,
    user_id uuid not null references users(id),
    email varchar(255) not null,
    role varchar(80) not null,
    access_level varchar(80) not null,
    created_at timestamptz not null default now()
);

create table reminders (
    id uuid primary key,
    user_id uuid not null references users(id),
    reminder_type varchar(80) not null,
    message text not null,
    remind_at timestamptz not null,
    created_at timestamptz not null default now()
);

create table ai_requests (
    id uuid primary key,
    user_id uuid not null references users(id),
    request_type varchar(80) not null,
    status varchar(40) not null,
    model varchar(120),
    cost_cents integer,
    latency_ms integer,
    created_at timestamptz not null default now()
);

create index idx_daily_meal_plans_user_date on daily_meal_plans(user_id, plan_date);
create index idx_pantry_items_user on pantry_items(user_id);
create index idx_reminders_due on reminders(remind_at);
