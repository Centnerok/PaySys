create table users (
    id bigint primary key,
    first_name text NOT NULL,
    last_name text NOT NULL,
    email text NOT NULL unique,
    password text NOT NULL,
    created_at timestamp with time zone NOT NULL
);

create table accounts (
    id bigint primary key,
    user_id bigint references users(id),
    balance NUMERIC(19, 2) NOT NULL default(0) check(balance >= 0),
    created_at timestamp with time zone NOT NULL
);

create table transactions (
    id bigint primary key,
    type text NOT NULL check(type in ('DEPOSIT', 'TRANSFER')),
    status text NOT NULL check(status in ('CREATED', 'SUCCESS', 'FAILED')),
    created_at timestamp with time zone NOT NULL
);

create table ledger_entries (
    id bigint primary key,
    transaction_id bigint references transactions(id),
    account_id bigint references accounts(id),
    amount NUMERIC(19, 2) NOT NULL check(amount <> 0),
    created_at timestamp with time zone NOT NULL
);