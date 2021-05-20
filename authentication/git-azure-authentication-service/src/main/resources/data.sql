-- Insert data in User table. --
insert into public.user (id,username,password)
values
(11,'testuser','$2a$04$UDQWU8myYBaj.FQunbWdLObd8/wBgRRc0dLng3NndYYiEUPqND0LS')

insert into public.role (id,name) values ('12','ADMIN')

insert into public.user_role values (11,12)