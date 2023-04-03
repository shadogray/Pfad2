alter table member_aud alter column dead set null;
alter table member_aud alter column free set null;

alter table payment_aud alter column aconto set null;
alter table payment_aud alter column type set null;

alter table registration_aud alter column storno set null;
alter table registration_aud alter column aktiv set null;

------------  Create Indexes 
--member: name, vorname, strasse, plz, ort, trupp_id
create index member_search on member (name, vorname, strasse, telefon, email, plz, ort, trupp_id);
create index member_trupp on member (trupp_id);

--activity: name, status, start, end, comment
create index activity_search on activity (name, status, start, end, comment);

--squad: name, leaderMale_id, leaderFemale_id
create index squad_search on squad (name, leaderMale_id, leaderFemale_id);

--booking: 
create index booking_search on booking (status, activity_id, member_id, squad_id, comment);
--payment: finished, aconto, payer_id, comment
create index payment_search on payment (finished, aconto, payer_id, comment);

--registration: 
create index registration_search on registration (storno, aktiv, name, vorname, strasse, telefon, email, plz, ort, member_id, parent_id);
