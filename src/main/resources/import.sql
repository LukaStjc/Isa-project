insert into location (city,country, street_name, street_number, longitude, latitude) values ('Serbia','Novi Sad' ,'Radnička',28,  19.8525275, 45.2503173);
insert into location (city,country, street_name, street_number, longitude, latitude) values ('Serbia','Novi Sad' ,'Veselina Masleše',2, 19.81433, 45.25328);
insert into location (city,country, street_name, street_number, longitude, latitude) values ('Serbia','Novi Sad' ,'Antona Čehova',1, 19.83055, 45.24880);
insert into location (city,country, street_name, street_number, longitude, latitude) values ('Serbia','Novi Sad' ,'Bulevar cara Lazara',96, 19.82831, 45.24096);
insert into hospital (name,location_id) values ('Bolnica Novi Sad', 4);

-- za sve korisnike ista lozinka: 'pass'
insert into userr (id, last_password_reset_date, email, enabled, first_name, last_name, password) values (5, '2024-01-22', 'luka2001stajic@gmail.com', True, 'Marko', 'Markovic', '$2a$10$8kIhZmKlZwao9SGdp/ZfMeHHMWx84Q9in5Cl374/T4cbv7zzpihG.');
insert into userr (id, last_password_reset_date, email, enabled, first_name, last_name, password) values (6, '2024-01-22', 'parkoparkovic@gmail.com', True, 'Parko', 'Parkovic', '$2a$10$8kIhZmKlZwao9SGdp/ZfMeHHMWx84Q9in5Cl374/T4cbv7zzpihG.');
insert into userr (id, last_password_reset_date, email, enabled, first_name, last_name, password) values (7, '2024-01-22', 'petarpetric@gmail.com', True, 'Petar', 'Petric', '$2a$10$8kIhZmKlZwao9SGdp/ZfMeHHMWx84Q9in5Cl374/T4cbv7zzpihG.');
insert into userr (id, last_password_reset_date, email, enabled, first_name, last_name, password) values (8, '2024-01-22', 'vasilijezzzz@gmail.com', True, 'Bojan', 'Bojanic', '$2a$10$8kIhZmKlZwao9SGdp/ZfMeHHMWx84Q9in5Cl374/T4cbv7zzpihG.');
insert into userr (id, last_password_reset_date, email, enabled, first_name, last_name, password) values (9, '2024-01-22', 'lukalukic@gmail.com', True, 'Luka', 'Lukic', '$2a$10$8kIhZmKlZwao9SGdp/ZfMeHHMWx84Q9in5Cl374/T4cbv7zzpihG.');
insert into userr (id, last_password_reset_date, email, enabled, first_name, last_name, password) values (10, '2024-01-22', 'dragomire@gmail.com', True, 'Dragomir', 'Espic', '$2a$10$8kIhZmKlZwao9SGdp/ZfMeHHMWx84Q9in5Cl374/T4cbv7zzpihG.');
insert into userr (id, last_password_reset_date, email, enabled, first_name, last_name, password) values (11, '2024-01-22', 'pavlekrstic010@gmail.com', True, 'Pavle', 'Krstic', '$2a$10$8kIhZmKlZwao9SGdp/ZfMeHHMWx84Q9in5Cl374/T4cbv7zzpihG.');
insert into userr (id, last_password_reset_date, email, enabled, first_name, last_name, password) values (12, '2024-01-22', 'milosteodosic@gmail.com', True, 'Milos', 'Teodosic', '$2a$10$8kIhZmKlZwao9SGdp/ZfMeHHMWx84Q9in5Cl374/T4cbv7zzpihG.');


insert into system_admin (id) values (7);


-- insert into registered_user (email, enabled, first_name, last_name, password, telephone_number, penalty_points, points) values ('asjkdgasjk@gmail.com', true, 'Petar', 'Petric', 'passw', '0613241802', 0, 0);
insert into loyalty_program (id, type, min_points, max_points, admin_id, discount_rate) values (0, 0, 0, 100, 7, 5);
insert into loyalty_program (id, type, min_points, max_points,  admin_id, discount_rate) values (1, 1, 101, 1000, 7, 10);
insert into loyalty_program (id, type, min_points, max_points,  admin_id, discount_rate) values (2, 2, 1001, 5000, 7, 20);

-- paziti na loyalty_progra_id i dozvoli neunosenje stranog kljuca
insert into registered_user (id, telephone_number, penalty_points, points, occupation, loyalty_program_id, hospital_id, location_id, activation_code) values (5, '0613241802', 0, 0, 'zemljoradnik', 1, 1, 1, 'APwO1SBNEVeJO8xs5zLmvHqGQsjKmgl6');
insert into registered_user (id, telephone_number, penalty_points, points, occupation,loyalty_program_id, hospital_id, location_id, activation_code) values (8, '0651234567', 0, 0, 'poljoprivrednik',1, 1, 2, 'ZA8hEPFFq4nO2eaHjQYkA2UzPUnukSWK');
insert into registered_user (id, telephone_number, penalty_points, points, occupation,loyalty_program_id, hospital_id, location_id, activation_code) values (9, '0628912666', 0, 0, 'direktor',1, 1, 2, 'XY1hEPFFq4nO2eaHjQYkA2UzPUnukSWK');
-- insert into registered_user (id, telephone_number, penalty_points, points, occupation) values (5, '0613241802', 0, 0, 'zemljoradnik');

-- TODO promeniti average_score na 0, za kt1 su stavljene neke vrednosti
insert into company (name,description, opening_time, closing_time, average_score, location_id, created_by_admin) values ('Kompanija A','opis1' ,'2020-01-01 07:00:00','2020-01-01 21:00:00', 1.8, 1, 7); --id 1
insert into company (name,description, opening_time, closing_time, average_score, location_id, created_by_admin) values ('Belmex','kvalitetna medicinska oprema' ,'2020-01-01 07:00:00','2020-01-01 21:00:00', 2.1, 2, 7); -- id 2
insert into company (name,description, opening_time, closing_time, average_score, location_id, created_by_admin) values ('Neomedica','sve za vas, od nas' ,'2020-01-01 07:00:00','2020-01-01 21:00:00', 3.3, 3, 7); -- id 3


insert into equipment (name,description, type, price, quantity, company_id, available_quantity, version) values ('Srafciger','alat za kucu' ,1  , 3000, 100, 1, 100, 0);
insert into equipment (name,description, type, price, quantity, company_id, available_quantity, version) values ('Rendgen aprat x021','uredjaj za snimanje grudnog kosa', 2, 600000, 5, 1, 5, 0);
insert into equipment (name,description, type, price, quantity, company_id, available_quantity, version) values ('Magnetna rezonanca p442','uredjaj za snimanje glave', 0, 700000, 6, 2, 6, 0);
insert into equipment (name,description, type, price, quantity, company_id, available_quantity, version) values ('Kamerica 2mpx', 'Za snimanje creva', 0, 10000, 10, 2, 10, 0);
insert into equipment (name,description, type, price, quantity, company_id, available_quantity, version) values ('Mala kamera 3mpx', 'Za snimanje grla', 0, 9000, 25, 3, 25, 0);
insert into equipment (name,description, type, price, quantity, company_id, available_quantity, version) values ('Srafciger','alat za majstorisanje' ,1  , 2500, 30, 3, 30, 0);
insert into equipment (name,description, type, price, quantity, company_id, available_quantity, version) values ('Srafciger','alat za sve' ,1  , 2900, 80, 2, 80, 0);


insert into company_admin (id,company_id, registered_by_admin, password_changed) values (6, 1, 7, false);
insert into company_admin (id,company_id, registered_by_admin, password_changed) values (10, 1, 7, false);
insert into company_admin (id,company_id, registered_by_admin, password_changed) values (11, 2, 7, false);
insert into company_admin (id,company_id, registered_by_admin, password_changed) values (12, 2, 7, false);


insert into complaint(id, comment, issued_by_user, company_admin_id) values (1, 'Nije mi dobro vracen kusur', 5, 6); -- TODO potrebno je ispostovati uslov da je ovaj korisnik vec imao neku rezervaciju opreme vezanu za tu kompaniju/admina kompanije

-- Rezervacije bi trebalo izmeniti, jer nisu vezane za equipmente preko reservationItemsa
insert into reservation( user_id, admin_id, hospital_id, status, total_sum, starting_date, duration_minutes, version) values (8, 6, 1, 0, 30000, '2023-12-19 10:55:00', 25, 0);
insert into reservation( user_id, admin_id, hospital_id, status, total_sum, starting_date, duration_minutes, version) values (5, 6, 1, 0, 30000, '2023-12-19 08:15:00', 25, 0);
insert into reservation( user_id, admin_id, hospital_id, status, total_sum, starting_date, duration_minutes, version) values (5, 6, 1, 0, 1000, '2023-12-20 14:00:00', 35, 0);
insert into reservation( user_id, admin_id, hospital_id, status, total_sum, starting_date, duration_minutes, version) values (8, 6, 1, 0, 2000, '2024-01-08 11:00:00', 35, 0);
insert into reservation( user_id, admin_id, hospital_id, status, total_sum, starting_date, duration_minutes, version) values (NULL, 6, 1, 0, NULL, '2024-01-08 13:00:00', 40, 0);

INSERT INTO ROLE (name) VALUES ('ROLE_REGISTERED_USER');
INSERT INTO ROLE (name) VALUES ('ROLE_COMPANY_ADMIN');
INSERT INTO ROLE (name) VALUES ('ROLE_SYSTEM_ADMIN');

INSERT INTO USER_ROLE (user_id, role_id) VALUES (5, 1); -- registered user-u dodeljujemo ROLE_REGISTERED_USER
INSERT INTO USER_ROLE (user_id, role_id) VALUES (8, 1); -- registered user-u dodeljujemo ROLE_REGISTERED_USER
INSERT INTO USER_ROLE (user_id, role_id) VALUES (9, 1); -- registered user-u dodeljujemo ROLE_REGISTERED_USER
INSERT INTO USER_ROLE (user_id, role_id) VALUES (6, 2); -- company admin-u dodeljujemo ROLE_COMPANY_ADMIN
INSERT INTO USER_ROLE (user_id, role_id) VALUES (10, 2); -- company admin-u dodeljujemo ROLE_COMPANY_ADMIN
INSERT INTO USER_ROLE (user_id, role_id) VALUES (11, 2); -- company admin-u dodeljujemo ROLE_COMPANY_ADMIN
INSERT INTO USER_ROLE (user_id, role_id) VALUES (12, 2); -- company admin-u dodeljujemo ROLE_COMPANY_ADMIN

-- todo: system admin ima sve privilegije?
-- INSERT INTO USER_ROLE (user_id, role_id) VALUES (7, 1);
-- INSERT INTO USER_ROLE (user_id, role_id) VALUES (7, 2);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (7, 3); -- system admin-u dodeljujemo ROLE_SYSTEM_ADMIN
