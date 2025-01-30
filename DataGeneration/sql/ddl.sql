create table hotel
    (h_id numeric(5,0),
    street varchar(30),
    city varchar(30),
    state varchar(2),
    zip numeric(5,0),
    phone numeric (10,0),
    primary key (h_id));
create table amenity
    (a_id numeric (5,0),
    h_id numeric(5,0),
    a_type varchar(20),
    capacity numeric(3),
    foreign key (h_id) references hotel on delete cascade,
    primary key (a_id,h_id));
create table room_type
    (rm_type varchar(20),
    bed_size varchar(7) check (bed_size in ('Twin', 'Full', 'Queen', 'King')),
    num_beds numeric(1),
    sleeps numeric(2),
    primary key (rm_type));
create table cost
    (h_id numeric(5,0),
    rm_type varchar(20),
    dollar_val numeric(10,0),
    pointval numeric (10,0),
    start_date date,
    end_date date,
    foreign key (h_id) references hotel on delete cascade,
    foreign key (rm_type) references room_type on delete cascade,
    primary key (h_id, rm_type, start_date, end_date));
create table room 
    (r_num numeric (5,0),
    state varchar(20),
    h_id numeric (5,0),
    rm_type varchar(20),
    foreign key (h_id) references hotel on delete cascade,
    primary key (h_id, r_num));
create table customer 
    (cust_id numeric(10,0),
    name varchar(40),
    address varchar(100),
    phone_num numeric(10,0),
    rewards_points numeric (10,0),
    primary key (cust_id));
create table payment
    (p_id numeric(10,0),
    cust_id numeric(10,0),
    card_num numeric(16,0),
    secur numeric(3,0),
    exp date,
    foreign key (cust_id) references customer on delete cascade,
    primary key(p_id));
create table reservation
    (res_id numeric(10,0),
    cust_id numeric(10,0),
    in_date date,
    out_date date,
    h_id numeric(5,0),
    rm_type varchar(20),
    p_id numeric(10,0),
    foreign key (p_id) references payment on delete set null,
    foreign key (h_id) references hotel on delete cascade,
    foreign key (rm_type) references room_type on delete set null,
    foreign key (cust_id) references customer on delete cascade,
    primary key (res_id));
create table check_in
    (cust_id numeric(10,0),
    in_time timestamp,
    res_id numeric(10,0),
    r_num numeric(5,0),
    h_id numeric(5,0),
    p_id numeric(10,0),
    foreign key (p_id) references payment,
    foreign key (res_id) references reservation on delete set null,
    foreign key (cust_id) references customer on delete set null,
    foreign key (h_id,r_num) references room on delete set null,
    primary key (res_id));
create table check_out
    (cust_id numeric(10,0),
    out_time timestamp,
    res_id numeric(10,0),
    r_num numeric(5,0),
    h_id numeric(5,0),
    p_id numeric(10,0),
    foreign key (p_id) references payment,
    foreign key (res_id) references reservation on delete set null,
    foreign key (cust_id) references customer on delete set null,
    foreign key (h_id,r_num) references room on delete set null,
    primary key (res_id));