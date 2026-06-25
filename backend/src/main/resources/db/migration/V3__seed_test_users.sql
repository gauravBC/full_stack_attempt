insert into users (
    id,
    email,
    display_name,
    username,
    first_name,
    last_name,
    phone_number,
    password_hash,
    email_verified,
    phone_verified
) values
(
    '11111111-1111-1111-1111-111111111111',
    'jakka.vikram@example.com',
    'Jakka Vikram',
    'jakka.vikram',
    'Jakka',
    'Vikram',
    '+15550001001',
    'sha256:05d021dd0fa1429bf38839bcd33df400deb3b2b371acf77c4d39901d747ef656',
    true,
    false
),
(
    '22222222-2222-2222-2222-222222222222',
    'gaurav.kumar@example.com',
    'Gaurav Kumar',
    'gaurav.kumar',
    'Gaurav',
    'Kumar',
    '+15550001002',
    'sha256:09fbae4f774eac4bd974df5231dd79b5349e355c367989db17f3f7d443a7382d',
    true,
    false
)
on conflict (email) do nothing;
