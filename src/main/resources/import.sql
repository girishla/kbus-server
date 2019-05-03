INSERT INTO users(id, version, authorities, email, enabled, firstname, lastname, lastpasswordreset, password, username,phone) VALUES (1,1, 'USER, ADMIN, ROOT', 'girish@bigmantra.com', true, 'Girish', 'Lakshmanan', null,'$2a$10$R1VkS.16ISzGqKEhJhq98uL.kYYimbknto/rlRA5wybmRYc4g9lge', 'girish','+44 7545894530');
INSERT INTO users(id, version, authorities, email, enabled, firstname, lastname, lastpasswordreset, password, username,phone) VALUES (2,1, 'USER', 'dharma@gmail.com', true, 'Dharmendran', 'Driver', null,'$2y$12$2a8V9E1OBb.1A83spQwPGem1lJaieqzAHtr4YhKheBVIUd7mBzk9m', 'dharma','+91 98420 212121');
INSERT INTO users(id, version, authorities, email, enabled, firstname, lastname, lastpasswordreset, password, username,phone) VALUES (3,1, 'USER', 'karthi.skt@gmail.com', true, 'Karthik', 'Manager', null,'$2y$12$hKZIpHi/l7g.GWUJh4m3PuUTyE8hJqyGMa2741mOkPgzB/6n/Vo6m', 'karthi','+91 95975 20800');



INSERT INTO usergroup(id, createddate, updateddate, version, about, groupname, monthlybudget, name, weeklybudget, userid) VALUES (1, current_date, current_date, 1, 'Salem Route', 'TN29BD3444', 1200000, 'TN29BD3444', 300000,1);
INSERT INTO usergroup(id, createddate, updateddate, version, about, groupname, monthlybudget, name, weeklybudget, userid) VALUES (2, current_date, current_date, 1, 'Hosur Route', 'TN29BD3777', 1200000, 'TN29BD3777', 300000,1);
INSERT INTO usergroup(id, createddate, updateddate, version, about, groupname, monthlybudget, name, weeklybudget, userid) VALUES (3, current_date, current_date, 1, 'Paavakkal Route', 'TN29BD2324', 400000, 'TN29BD2324',100000 ,1);



INSERT INTO usergroupmember(id, createddate, updateddate, version, isaccepted, groupid, userid) VALUES (1, current_date, current_date, 1, true, 1, 1);
INSERT INTO usergroupmember(id, createddate, updateddate, version, isaccepted, groupid, userid) VALUES (2, current_date, current_date, 1, true, 2, 1);
INSERT INTO usergroupmember(id, createddate, updateddate, version, isaccepted, groupid, userid) VALUES (3, current_date, current_date, 1, true, 3, 1);


INSERT INTO usergroupmember(id, createddate, updateddate, version, isaccepted, groupid, userid) VALUES (4, current_date, current_date, 1, true, 1, 3);
INSERT INTO usergroupmember(id, createddate, updateddate, version, isaccepted, groupid, userid) VALUES (5, current_date, current_date, 1, true, 2, 3);
INSERT INTO usergroupmember(id, createddate, updateddate, version, isaccepted, groupid, userid) VALUES (6, current_date, current_date, 1, true, 3, 3);



INSERT INTO expensecategory( id, createddate, updateddate, version, color, groupid, icon, name, userid) VALUES (1, current_date, current_date, 1, '#ff916a6a', 1, 'Bubble Chart', 'Office Sundry', 1);