-- Authentication for Members
drop table if exists login;
create table LOGIN (id bigint auto_increment, member_id bigint, groups varchar(64), primary key (id));
alter table login add constraint member_has_login foreign key (member_id) references member(id) on delete cascade;
--alter table member add (login varchar(128), password varchar(1024));
alter table member_aud add (login varchar(128), login_mod boolean, password varchar(1024), password_mod boolean);
