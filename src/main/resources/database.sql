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
                                   `type` varchar(255) not null,
                                   id_specialty bigint not null,
                                   creation_date datetime(6) not null,
                                   creation_user varchar(255) not null,
                                   foreign key (id_specialty) references specialties(id)
);
-- agendamentos
create table appointments(
                                    id bigint primary key auto_increment,
                                    request_date datetime(6) not null,
                                    priority int not null,
                                    observation text,
                                    creation_user varchar(255) not null,
                                    id_medical_procedure bigint not null,
                                    id_patient bigint not null,
                                    foreign key (id_medical_procedure) references medical_procedures(id),
                                    foreign key (id_patient) references patients(id)
);
-- agendamentos disponíveis ou vagas para agendamento
create table available_appointments(
                                       id bigint primary key auto_increment,
                                       quantity int not null,
                                       vacancy_date date not null,
                                       id_medical_procedure bigint not null,
                                       creation_date datetime(6) not null,
                                       creation_user varchar(255) not null,
                                       update_date datetime(6),
                                       update_user varchar(255),
                                       foreign key (id_medical_procedure) references medical_procedures(id)
);

-- contemplados (contemplated)
create table contemplations(
                                 id bigint primary key auto_increment,
                                 contemplation_date datetime(6) not null,
                                 priority int not null,
                                 confirmed boolean not null,
                                 id_appointment bigint not null,
                                 creation_date datetime(6) not null,
                                 creation_user varchar(255) not null,
                                 update_date datetime(6),
                                 update_user varchar(255),
                                 foreign key (id_appointment) references appointments(id)
);


create table patient_history(
                                  id bigint primary key auto_increment,
                                  id_appointment bigint not null,
                                  creation_date datetime(6) not null,
                                  creation_user varchar(255) not null,
                                  update_date datetime(6),
                                  update_user varchar(255),
                                  foreign key (id_appointment) references appointments(id)
);

ALTER TABLE patients ADD FULLTEXT(name, sus_card_number, cpf);

ALTER TABLE sccubs.appointments ADD canceled TINYINT DEFAULT 0 NULL;
ALTER TABLE sccubs.appointments CHANGE canceled canceled TINYINT DEFAULT 0 NULL AFTER id_patient;
ALTER TABLE sccubs.appointments ADD update_date datetime(6) NULL;
ALTER TABLE sccubs.appointments ADD update_user varchar(255) NULL;
ALTER TABLE sccubs.contemplations CHANGE priority contemplated_by int(11) NOT NULL;

-- -- -- -- -- --
-- VERSÃO 1.4.0
-- -- -- -- -- --

RENAME TABLE sccubs.available_appointments TO sccubs.available_medical_slots;
ALTER TABLE sccubs.available_medical_slots CHANGE quantity total_slots int(11) NOT NULL;
ALTER TABLE sccubs.available_medical_slots ADD filled_slots INT DEFAULT 0 NULL;
ALTER TABLE sccubs.available_medical_slots CHANGE filled_slots filled_slots INT DEFAULT 0 NULL AFTER total_slots;
ALTER TABLE sccubs.available_medical_slots CHANGE reference_month reference_month date NOT NULL AFTER id;
ALTER TABLE sccubs.available_medical_slots CHANGE filled_slots current_slots int(11) DEFAULT 0 NULL;
ALTER TABLE sccubs.contemplations ADD id_available_medical_slot bigint(20) NOT NULL;
ALTER TABLE sccubs.contemplations CHANGE id_available_medical_slot id_available_medical_slot bigint(20) NOT NULL AFTER id_appointment;
alter table contemplations add foreign key (id_available_medical_slot) references available_medical_slots(id);
ALTER TABLE sccubs.available_medical_slots ADD id_basic_health_unit bigint NOT NULL;
ALTER TABLE sccubs.available_medical_slots CHANGE id_basic_health_unit id_basic_health_unit bigint NOT NULL AFTER id_medical_procedure;
ALTER TABLE sccubs.available_medical_slots ADD CONSTRAINT available_medical_slots_FK FOREIGN KEY (id_basic_health_unit) REFERENCES sccubs.basic_health_units(id);
RENAME TABLE sccubs.available_medical_slots TO sccubs.medical_slots;
ALTER TABLE sccubs.medical_slots DROP COLUMN reference_month;
ALTER TABLE sccubs.contemplations ADD canceled TINYINT(1) NULL;
ALTER TABLE sccubs.contemplations CHANGE canceled canceled TINYINT(1) NULL AFTER id_available_medical_slot;
ALTER TABLE sccubs.contemplations ADD observation varchar(255) NULL;
ALTER TABLE sccubs.contemplations CHANGE observation observation varchar(255) NULL AFTER canceled;
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- -- -- -- -- -- -- INSERTS PARA TESTES -- -- -- -- -- -- -- --
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(1, 'Abner Da Silva De Moraes', '903 2850 9183 0006', '832.915.970-10', 'Feminino', '1990-01-01', 2, '12345', 'test', '1', 'test', 'test', 'test', 1, '2024-06-13 10:35:41.753172000', 'sms', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(2, 'Ademir Aparecido De Alvarenga', '953 3764 3378 0003', '383.709.600-90', 'Masculino', '2024-05-29', 2, '5', '5', '5', '5', '5', '5', 1, '2024-06-13 10:54:44.591829000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(3, 'Adriana Abujanra Ferreira ', '193 2665 1338 0000', '009.661.780-25', 'Feminino', '2024-05-26', 3, 't5', 't5', 't5', 't5', 't5', 't5', 1, '2024-06-13 10:59:41.473798000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(4, 'Adriana Aparecida Dos Santos', '816 2315 3817 0006', '180.956.550-23', 'Feminino', '2024-06-04', 2, 't6', 't6', 't6', 't6', 't6', 't6', 1, '2024-06-13 11:03:28.082158000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(5, 'Adriana Celi De Santana Alves', '251 6083 7040 0002', '135.346.100-91', 'Feminino', '2024-05-27', 1, 't7', 't7', 't7', 't7', 't7', 't7', 1, '2024-06-13 11:06:33.491451000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(6, 'Adriana De Jesus Amorim ', '798 1394 5640 0004', '136.076.000-82', 'Feminino', '2024-06-03', 2, 't11', 't11', 't11', 't11', 't11', 't11', 1, '2024-06-13 11:20:59.692978000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(7, 'Adriana Do Nascimento Nunes ', '287 9618 6033 0002', '028.371.500-61', 'Feminino', '2024-06-11', 2, 'tt1', 'tt1', 'tt1', 'tt1', 'tt1', 'tt1', 1, '2024-06-13 11:29:10.068440000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(10, 'Ailton Petroni ', '231 1011 5406 0006', '356.068.630-06', 'Masculino', '2024-05-31', 4, 'rr1', 'rr1', 'rr1', 'rr1', 'rr1', 'rr1', 1, '2024-06-13 14:54:39.309012000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(11, 'Alan Elton Ramos ', '736 7821 0436 0004', '998.529.480-75', 'Masculino', '2024-06-12', 2, 'rr2', 'rr2', 'rr2', 'rr2', 'rr2', 'rr2', 1, '2024-06-13 15:00:24.239587000', 'user', '2024-06-13 15:00:34.914561000', 'user');
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(12, 'Alessandra Cristina Ferri Pereira', '882 0294 5133 0009', '031.469.700-40', 'Feminino', '2024-06-12', 3, 'rr3', 'rr3', 'rr3', 'rr3', 'rr3', 'rr3', 1, '2024-06-13 15:01:26.816529000', 'user', '2024-06-13 15:01:29.429448000', 'user');
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(13, 'Alessandra Meneghel Codognoto De Almeida ', '135 0466 0319 0000', '890.574.990-96', 'Feminino', '2024-06-12', 3, 'rr4', 'rr4', 'rr4', 'rr4', 'rr4', 'rr4', 1, '2024-06-13 15:04:36.383394000', 'user', '2024-06-13 15:04:41.000295000', 'user');
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(14, 'Alex Dos Santos Feitosa ', '195 2676 2557 0003', '127.008.440-25', 'Masculino', '2024-06-12', 2, 'rr', 'rr', 'rr', 'rr', 'rr', 'rr', 1, '2024-06-13 15:05:49.852462000', 'user', '2024-06-13 15:07:39.746445000', 'user');
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(16, 'Aline Alves Ramos ', '138 6128 7273 0018', '691.339.780-18', 'Feminino', '2024-06-12', 2, 'rr5', 'rr5', 'rr5', 'rr5', 'rr5', 'rr5', 1, '2024-06-13 15:16:23.599548000', 'user', '2024-06-13 15:16:54.908335000', 'user');
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(17, 'Aline Caldeira Marques', '828 1933 8847 0006', '961.979.150-90', 'Feminino', '2024-06-04', 2, 'r3', 'r3', 'r3', 'r3', 'r3', 'r3', 1, '2024-06-15 11:25:24.903563000', 'user', '2024-06-15 11:25:35.447193000', 'user');
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(18, 'Aline Da Costa Silva', '146 3159 9322 0009', '137.573.090-81', 'Feminino', '2024-06-13', 1, 'r22', 'r22', 'r22', 'r22', 'r22', 'r22', 1, '2024-06-15 11:28:39.953181000', 'user', '2024-06-15 11:28:54.908217000', 'user');
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(19, 'Aline Da Nóbrega Sobrinho ', '147 2217 1285 0009', '811.589.970-43', 'Feminino', '2024-06-20', 2, 'r33', 'r33', 'r33', 'r33', 'r33', 'r33', 1, '2024-06-15 11:36:53.989348000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(20, 'Ana Carla Muñoz Dentello ', '709 8243 4080 0008', '604.298.530-25', 'Feminino', '2024-06-13', 3, 'r44', 'r44', 'r44', 'r44', 'r44', 'r44', 1, '2024-06-15 11:44:19.766366000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(21, 'Ana Claudia De Oliveira', '128 5814 4968 0005', '860.659.050-97', 'Feminino', '2024-06-12', 2, 'r66', 'r66', 'r66', 'r66', 'r66', 'r66', 1, '2024-06-15 11:52:31.729610000', 'user', '2024-06-15 11:54:37.907911000', 'user');
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(27, 'Ana Maria Toschi Gonçalves', '194 3149 7435 0007', '976.467.990-09', 'Feminino', '2024-06-12', 1, '400', '400', '400', '400', '400', '400', 1, '2024-06-15 12:16:09.753934000', 'user', '2024-06-15 12:16:20.536714000', 'user');
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(28, 'Ana Patrícia Gondim Da Conceição', '171 5096 7396 0004', '909.257.080-03', 'Feminino', '2024-06-13', 1, 'qwe', 'qwe', 'qwe', 'qew', 'qwe', 'qwe', 1, '2024-06-15 12:38:16.960930000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(29, 'Ana Paula De Moraes Sant Anna Pinto ', '783 4863 6709 0006', '612.878.490-91', 'Feminino', '2024-06-13', 2, 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 1, '2024-06-15 12:41:14.813333000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(30, 'Ana Paula Teixeira Marcovecchio ', '129 9549 3937 0018', '714.783.000-86', 'Feminino', '2024-06-13', 3, 'zxc', 'zxc', 'zxc', 'zxc', 'zxc', 'zxc', 1, '2024-06-15 12:46:10.292386000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(31, 'Andre Luis Monte Olivio ', '273 3339 4350 0018', '858.758.330-17', 'Masculino', '2024-06-05', 3, 'cvb', 'cvb', 'cvb', 'cvb', 'cvb', 'cvb', 1, '2024-06-15 12:49:40.597102000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(32, 'Andréa Christine Oliveira Andrade', '207 6432 3770 0007', '988.713.090-73', 'Feminino', '2024-06-13', 2, 'tyu', 'tyu', 'tyu', 'tyu', 'tyu', 'tyu', 1, '2024-06-15 12:50:47.485307000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(34, 'Andrea Cristina Dos Santos Viviani ', '214 1988 2615 0003', '927.295.730-38', 'Feminino', '2024-06-07', 3, 'ghj', 'ghj', 'ghj', 'ghj', 'ghj', 'ghj', 1, '2024-06-15 17:40:22.633418000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(35, 'Andressa De Souza Francisco', '173 0133 2604 0018', '941.898.630-25', 'Feminino', '2024-06-19', 4, 'pacientUser', 'pacientUser', 'pacientUser', 'pacientUser', 'pacientUser', 'pacientUser', 1, '2024-06-21 11:25:34.669547000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(36, 'Andreza Katiucia Batista ', '709 3461 7081 0004', '341.666.750-60', 'Feminino', '1979-06-19', 7, 'pacientTest1', 'pacientTest1', 'pacientTest1', 'pacientTest1', 'pacientTest1', 'pacientTest1', 1, '2024-06-21 11:25:34.813166000', 'test1', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(37, 'Renê Douglas Nobre de Morais', '150 1782 5154 0005', '486.315.966-80', 'Masculino', '1992-07-01', 3, '87999148083', 'Rua José Belizário Costa', '62', 'casa', '', 'Teste', 1, '2024-06-22 20:47:14.289027000', 'user', NULL, NULL);
INSERT INTO patients (id, name, sus_card_number, cpf, gender, birth_date, social_sit_rating, phone_number, address_street, address_number, address_complement, address_ref, acs_name, id_basic_health_unit, creation_date, creation_user, update_date, update_user) VALUES(38, 'Renia Raynne Nobre de Morais', '279 0275 7046 0003', '338.631.467-86', 'Feminino', '1994-12-02', 3, '87999999999', 'Rua José Belizário Costa', '62', 'casa', '', 'teste', 1, '2024-06-22 20:52:46.065648000', 'user', NULL, NULL);
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(1, 'Cardiologia', 'Diagnostica e trata doenças do coração e do sistema circulatório.', 1, '2024-06-16 13:50:33.832000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(2, 'Mastologia', 'Cuida da saúde das mamas, incluindo a prevenção e tratamento do câncer de mama.', 1, '2024-06-16 13:51:35.275000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(3, 'Endocrinologia', 'Trata distúrbios hormonais e doenças das glândulas endócrinas.', 1, '2024-06-16 13:52:05.982000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(4, 'Otorrinolaringologia', 'Diagnostica e trata doenças do ouvido, nariz e garganta.', 1, '2024-06-16 13:52:09.970000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(5, 'Urologia', 'Foca no trato urinário de homens e mulheres e no sistema reprodutor masculino.', 1, '2024-06-16 13:53:54.725000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(6, 'Oftalmologia', 'Cuida da saúde dos olhos e trata problemas visuais.', 1, '2024-06-16 13:53:59.213000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(7, 'Ortopedia', 'Trata doenças e lesões do sistema musculoesquelético.', 1, '2024-06-16 13:54:57.239000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(8, 'Cirurgia Geral', 'Realiza intervenções para tratar doenças, lesões e deformidades.', 1, '2024-06-16 13:55:10.065000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(9, 'Neurologia', 'Trata distúrbios do sistema nervoso central e periférico.', 1, '2024-06-16 13:56:02.484000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(10, 'Obstetricia', 'Cuida da gestação, parto e saúde da mãe e do bebê.', 1, '2024-06-16 13:56:05.674000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(12, 'Reumatologia', 'Trata doenças reumáticas e autoimunes que afetam articulações, músculos e ossos.', 1, '2024-06-16 13:56:11.842000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(13, 'Ginecologia', 'Cuida da saúde do sistema reprodutor feminino.', 1, '2024-06-16 13:56:15.064000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(14, 'Dermatologia', 'Diagnostica e trata doenças da pele, cabelos e unhas.', 1, '2024-06-16 13:58:43.618000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(15, 'Gastroenterologia', 'Trata doenças do sistema digestivo.', 1, '2024-06-16 13:59:21.266000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(16, 'Angiologia', 'Cuida das doenças dos vasos sanguíneos e linfáticos.', 1, '2024-06-16 14:00:09.474000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(17, 'Nutrição', 'Estuda os alimentos e sua influência na saúde, elaborando planos alimentares.', 1, '2024-06-16 14:04:42.464000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(18, 'Pediatria', 'Cuida da saúde de bebês, crianças e adolescentes.', 1, '2024-06-16 14:05:15.731000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(19, 'Psiquiatria', 'Diagnostica e trata transtornos mentais e emocionais.', 1, '2024-06-16 14:06:20.747000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(20, 'Radiologia', 'Utiliza técnicas de imagem para diagnosticar e tratar doenças.', 1, '2024-06-16 14:12:55.219000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(23, 'Fonoaudiologia', 'Trata distúrbios da comunicação, fala, linguagem e audição.', 1, '2024-06-16 14:19:20.286000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(25, 'Fisioterapia', 'Utiliza exercícios terapêuticos para tratar e prevenir doenças e lesões físicas.', 1, '2024-06-16 14:25:20.370000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(27, 'Nefrologia ', 'Diagnostica e trata doenças dos rins e do sistema urinário.', 1, '2024-06-16 14:35:44.917000000', 'rene.morais');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(32, 'testeEdit', 'testeEdit', 0, '2024-06-20 10:53:42.082576000', 'sms');
INSERT INTO specialties (id, title, description, active, creation_date, creation_user) VALUES(33, 'enumTest', 'enumTest', 1, '2024-06-21 14:45:43.758524000', 'sms');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(1, 'Histerectomia', 'CIRURGIA', 13, '2024-06-18 00:31:47.115000000', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(2, 'Cirurgia HREC', 'CIRURGIA', 8, '2024-06-18 00:32:05.706000000', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(3, 'Cirurgia de Hérnia', 'CIRURGIA', 8, '2024-06-18 00:32:19.980000000', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(4, 'Cirurgia de Hérnia Inguinal', 'CIRURGIA', 8, '2024-06-18 00:32:32.045000000', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(5, 'Cirurgia de Hérnia Epigástrica', 'CIRURGIA', 8, '2024-06-18 00:32:42.856000000', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(6, 'Mamografia', 'EXAME', 2, '2024-06-18 00:36:27.771000000', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(7, 'Densitometria', 'EXAME', 12, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(8, 'Tomografia', 'EXAME', 20, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(9, 'Ecocardiograma', 'EXAME', 1, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(10, 'Eletrocardiograma', 'EXAME', 1, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(11, 'Teste Ergonométrico', 'EXAME', 1, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(12, 'Raio X', 'EXAME', 20, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(13, 'Colonoscopia', 'EXAME', 15, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(14, 'Endoscopia', 'EXAME', 15, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(15, 'Colposcopia ', 'EXAME', 13, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(16, 'Ultrassonografia', 'EXAME', 20, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(17, 'Ressonância Magnética', 'EXAME', 20, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(18, 'Histeroscopia', 'EXAME', 13, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(19, '-', 'CONSULTA', 1, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(20, '-', 'CONSULTA', 2, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(21, '-', 'CONSULTA', 3, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(22, '-', 'CONSULTA', 4, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(23, '-', 'CONSULTA', 5, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(24, '-', 'CONSULTA', 6, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(25, '-', 'CONSULTA', 7, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(27, '-', 'CONSULTA', 9, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(28, '-', 'CONSULTA', 10, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(29, '-', 'CONSULTA', 12, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(30, '-', 'CONSULTA', 13, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(31, '-', 'CONSULTA', 14, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(32, '-', 'CONSULTA', 15, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(33, '-', 'CONSULTA', 16, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(34, '-', 'CONSULTA', 17, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(35, '-', 'CONSULTA', 18, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(36, '-', 'CONSULTA', 19, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(37, '-', 'CONSULTA', 20, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(38, '-', 'CONSULTA', 23, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(39, '-', 'CONSULTA', 25, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(40, '-', 'CONSULTA', 27, '2024-06-17 21:36:27', 'rene.morais');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(41, 'p2', 'CIRURGIA', 32, '2024-06-20 10:53:42.082576000', 'sms');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(42, '-', 'CONSULTA', 32, '2024-06-20 10:53:42.082576000', 'sms');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(43, 'p1', 'EXAME', 32, '2024-06-20 10:53:42.082576000', 'sms');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(44, 'p3', 'EXAME', 32, '2024-06-20 12:15:26.136663000', 'sms');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(45, 'p4', 'CIRURGIA', 32, '2024-06-20 12:15:40.163071000', 'sms');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(46, 'pE1', 'EXAME', 33, '2024-06-21 14:45:43.763509000', 'sms');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(47, '-', 'CONSULTA', 33, '2024-06-21 14:45:43.763509000', 'sms');
INSERT INTO medical_procedures (id, description, `type`, id_specialty, creation_date, creation_user) VALUES(48, 'pE2', 'CIRURGIA', 33, '2024-06-21 14:45:43.763509000', 'sms');
