create database sandbox;
use sandbox;

create table sandbox.product (
    id integer not null,
    version integer,
    description varchar(255),
    price decimal(15, 2)
);

alter table sandbox.product add primary key(id);

insert into sandbox.product values(1,0,'pepsi-cola 12 onz', 3.50);
insert into sandbox.product values(2,0,'cola-cola 12 onz', 4.50);
insert into sandbox.product values(3,0,'7up 12 onz', 5.50);
