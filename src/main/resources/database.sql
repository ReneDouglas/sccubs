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
                              update_user varchar(255),
                              id_city_hall  bigint,
                              foreign key (id_city_hall) references city_hall(id)
);
-- -- -- -- -- --
-- VERSÃO 1.1.1
-- -- -- -- -- --

alter table system_users add active boolean not null after `email`;
alter table system_roles add title varchar(150) not null after `role`;
alter table system_roles add root boolean not null after `description`;
alter table system_users modify column active tinyint(1) DEFAULT 1 NOT NULL;
alter table system_users modify column username varchar(100) not null unique;

-- -- -- -- -- --
-- VERSÃO 1.1.2
-- -- -- -- -- --
alter table city_hall add active boolean default 1 after `name` ;
alter table system_users add id_city_hall bigint after `active`;
alter table system_users add foreign key (id_city_hall) references city_hall(id);
RENAME TABLE health_center TO basic_health_unit;
RENAME TABLE city_hall TO city_halls;
RENAME TABLE basic_health_unit TO basic_health_units;
alter table system_users add id_basic_health_unit bigint after `id_city_hall`;
alter table system_users add foreign key (id_basic_health_unit) references basic_health_units(id);

-- -- -- -- -- --
-- VERSÃO 1.2.0
-- -- -- -- -- --
create table patients(
                         id bigint primary key auto_increment,
                         name varchar(255) not null,
                         birth_date date not null,
                         gender varchar(100) not null,
                         social_sit_rating int not null,
                         sus_card_number varchar(20) unique,
                         cpf varchar(14) unique,
                         phone_number varchar(20),
                         address_street varchar(255),
                         address_number varchar(50),
                         address_complement varchar(255),
                         address_ref varchar(255),
                         acs_name varchar(150),
                         id_basic_health_unit bigint,
                         creation_date datetime(6) not null,
                         creation_user varchar(255) not null,
                         update_date datetime(6),
                         update_user varchar(255),
                         foreign key (id_basic_health_unit) references basic_health_units(id)
);

-- -- -- -- -- --
-- VERSÃO 1.3.0
-- -- -- -- -- --
create table specialties(
                            id bigint primary key auto_increment,
                            description varchar(255),
                            active boolean not null default true,
                            creation_date datetime(6) not null,
                            creation_user varchar(255) not null
);

/**
  A tabela abaixo MUITO PROVAVELMENTE não é necessária!
 */
create table basic_health_units_specialties(
                                               id bigint primary key auto_increment,
                                               id_basic_health_unit bigint not null,
                                               id_specialties bigint not null,
                                               foreign key (id_basic_health_unit) references basic_health_units(id),
                                               foreign key (id_specialties) references specialties(id)
);

ALTER TABLE specialties CHANGE description title varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL;
ALTER TABLE specialties ADD description varchar(255) NULL;
ALTER TABLE specialties CHANGE description description varchar(255) NULL AFTER title;

create table medical_procedures(
                                   id bigint primary key auto_increment,
                                   description varchar(255),
                                   `type` int not null,
                                   id_specialty bigint not null,
                                   creation_date datetime(6) not null,
                                   creation_user varchar(255) not null,
                                   foreign key (id_specialty) references specialties(id)
);

create table available_appointments(
                                       id bigint primary key auto_increment,
                                       quantity int not null,
                                       vacancy_date date not null,
                                       id_medical_procedure bigint not null,
                                       id_exam bigint,
                                       id_surgery bigint,
                                       foreign key (id_medical_procedure) references medical_procedures(id)
);