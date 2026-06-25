alter table daily_meal_plans
    add column if not exists pregnancy_week integer not null default 22,
    add column if not exists hydration_goal varchar(80) not null default '2.7 L',
    add column if not exists reminders jsonb not null default '[]'::jsonb,
    add column if not exists partner_task text not null default '';

insert into pantry_items (id, user_id, name)
select gen_random_uuid(), u.id, item.name
from users u
cross join (values ('Spinach'), ('Rice'), ('Paneer'), ('Oats'), ('Eggs')) as item(name)
where u.username in ('jakka.vikram', 'gaurav.kumar')
  and not exists (
      select 1 from pantry_items existing
      where existing.user_id = u.id
  );
