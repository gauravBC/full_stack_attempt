alter table family_members
    add column first_name varchar(80),
    add column last_name varchar(80),
    add column phone_number varchar(20),
    add column notifications_enabled boolean not null default true;

create index idx_family_members_user_role on family_members(user_id, role);
