alter table users
    add column username varchar(40),
    add column first_name varchar(80),
    add column last_name varchar(80),
    add column phone_number varchar(20),
    add column password_hash text,
    add column email_verified boolean not null default false,
    add column phone_verified boolean not null default false;

create unique index idx_users_username on users(username);
create unique index idx_users_phone_number on users(phone_number);
