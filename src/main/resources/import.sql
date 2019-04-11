INSERT INTO users(id, version, authorities, email, enabled, firstname, lastname, lastpasswordreset, password, username) VALUES (1,1, 'USER, ADMIN, ROOT', 'girish@bigmantra.com', true, 'Girish', 'Lakshmanan', null,'$2a$10$R1VkS.16ISzGqKEhJhq98uL.kYYimbknto/rlRA5wybmRYc4g9lge', 'girish')

INSERT INTO usergroup(id, createddate, updateddate, version, about, groupname, monthlybudget, name, weeklybudget, userid) VALUES (1, current_date, current_date, 1, 'Bus 1', 'Bus1', 100, 'Bus1', 100,1)


INSERT INTO public.usergroupmember(id, createddate, updateddate, version, isaccepted, groupid, userid) VALUES (1, current_date, current_date, 1, true, 1, 1);