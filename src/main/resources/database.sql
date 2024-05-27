-- VERSÃO 1.0.0

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
-- -- -- -- -- --
-- VERSÃO 1.1.0
-- -- -- -- -- --

create table city_hall(
                          id bigint primary key auto_increment,
                          name varchar(255) not null,
                          creation_date datetime(6) not null,
                          creation_user varchar(255) not null,
                          update_date datetime(6),
                          update_user varchar(255)
);

create table health_center(
                              id bigint primary key auto_increment,
                              name varchar(255) not null,
                              neighborhood varchar(200) not null,
                              creation_date datetime(6) not null,
                              creation_user varchar(255) not null,
                              update_date datetime(6),
                              update_user varchar(255)
);
-- -- -- -- -- --
-- VERSÃO 1.1.1
-- -- -- -- -- --

alter table system_users add active boolean not null after `email`;
alter table system_roles add title varchar(150) not null after `role`;
alter table system_roles add root boolean not null after `description`;
alter table system_users modify column active tinyint(1) DEFAULT 1 NOT NULL;
alter table system_users modify column username varchar(100) not null unique;