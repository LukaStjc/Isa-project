insert into student (index_number, first_name, last_name) values ('5', 'Marko', 'Marković');
insert into student (index_number, first_name, last_name) values ('ra2-2014', 'Milan', 'Milanović');
insert into student (index_number, first_name, last_name) values ('ra3-2014', 'Ivana', 'Ivanović');
insert into student (index_number, first_name, last_name) values ('ra4-2014', 'Bojan', 'Bojanović');
insert into student (index_number, first_name, last_name) values ('ra5-2014', 'Pera', 'Perić');
insert into student (index_number, first_name, last_name) values ('ra6-2014', 'Zoran', 'Zoranović');
insert into student (index_number, first_name, last_name) values ('ra7-2014', 'Bojana', 'Bojanović');
insert into student (index_number, first_name, last_name) values ('ra8-2014', 'Milana', 'Milanović');
insert into student (index_number, first_name, last_name) values ('ra9-2014', 'Jovana', 'Jovanić');

insert into course (name) values ('Matematika');
insert into course (name) values ('Osnove programiranja');
insert into course (name) values ('Objektno programiranje');

insert into teacher (first_name, last_name, deleted) values ('Strahinja', 'Simić', false);
insert into teacher (first_name, last_name, deleted) values ('Marina', 'Antić', false);
insert into teacher (first_name, last_name, deleted) values ('Siniša', 'Branković', false);

insert into teaching (course_id, teacher_id) values (1, 1);
insert into teaching (course_id, teacher_id) values (1, 2);
insert into teaching (course_id, teacher_id) values (2, 2);
insert into teaching (course_id, teacher_id) values (3, 3);

insert into exam (student_id, course_id, date, grade) values (1, 1, '2016-02-01', 9);
insert into exam (student_id, course_id, date, grade) values (1, 2, '2016-04-19', 8);
insert into exam (student_id, course_id, date, grade) values (2, 1, '2016-02-01', 10);
insert into exam (student_id, course_id, date, grade) values (2, 2, '2016-04-19', 10);

insert into location (city,country, street_name, street_number, longitude, latitude) values ('Serbia','Novi Sad' ,'Sime Milosevica',11, 19.833549, 45.267136);
insert into hospital (name,location_id) values ('Bolnica Novi Sad', 1);

insert into userr (id, email, first_name, last_name, password) values (5, 'sadas@gmail.com', 'Marko', 'Markovic', 'pass');
insert into userr (id, email, first_name, last_name, password) values (6, 'sada2s@gmail.com', 'Parko', 'Parkovic', 'pass');
insert into userr (id, email, first_name, last_name, password) values (7, 'asjkdgasjk@gmail.com', 'Petar', 'Petric', 'passw');
insert into userr (id, email, first_name, last_name, password) values (8, 'assadasjkdgasjk@gmail.com', 'Bojan', 'Bojanic', 'passw');
insert into userr (id, email, first_name, last_name, password) values (9, 'asjkdgasjsasddk@gmail.com', 'Luka', 'Lukic', 'passw');


insert into system_admin (id) values (7);


-- insert into registered_user (email, first_name, last_name, password, telephone_number, penalty_points, points) values ('asjkdgasjk@gmail.com', 'Petar', 'Petric', 'passw', '0613241802', 0, 0);
insert into loyalty_program (id, type,min_points, max_points, admin_id) values (0, 0, 0, 100, 7);
insert into loyalty_program (id,type,min_points, max_points,  admin_id) values (1, 1, 101, 1000, 7);
insert into loyalty_program (id,type,min_points, max_points,  admin_id) values (2, 2, 1001, 5000, 7);

-- paziti na loyalty_progra_id i dozvoli neunosenje stranog kljuca
insert into registered_user (id , telephone_number, penalty_points, points, occupation, loyalty_program_id, hospital_id) values (5, '0613241802', 0, 0, 'zemljoradnik', 1, 1);
insert into registered_user (id , telephone_number, penalty_points, points, occupation,loyalty_program_id, hospital_id) values (8, '0613241802', 0, 0, 'poljoprivrednik',1, 1);
-- insert into registered_user (id , telephone_number, penalty_points, points, occupation) values (5, '0613241802', 0, 0, 'zemljoradnik');

insert into company (name,description, opening_time, closing_time, average_score, location_id, created_by_admin) values ('Kompanija A','opis1' ,'2020-01-01 07:00:00','2020-01-01 21:00:00', 0, 1, 7);

insert into equipment (name,description, type, price, quantity, company_id) values ('Srafciger','alat za kucu' ,1  , 3000, 100, 1);

insert into company_admin (id,company_id, registered_by_admin) values (6, 1, 7);



