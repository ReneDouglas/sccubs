create database sccubs;

use sccubs;

create table system_users(
                             id bigint primary key auto_increment,
                             username varchar(100) not null unique,
                             `password` varchar(255) not null,
                             name varchar(200) not null,
                             email varchar(100) not null,
                             creation_date datetime(6) not null,
                             creation_user varchar(255) not null,
                             update_date datetime(6),
                             update_user varchar(255)
);

create table system_roles(
                             id bigint primary key auto_increment,
                             `role` varchar(100) not null,
                             description varchar(255) not null,
                             creation_date datetime(6) not null,
                             creation_user varchar(255) not null,
                             update_date datetime(6),
                             update_user varchar(255)
);

create table system_users_roles(
                                   id bigint primary key auto_increment,
                                   id_system_user bigint not null,
                                   id_system_role bigint not null,
                                   foreign key (id_system_user) references system_users(id),
                                   foreign key (id_system_role) references system_roles(id)
);