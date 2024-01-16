alter table activity rename column end to endDate;
alter table activity rename column start to startDate;

alter table activity_aud rename column end to endDate;
alter table activity_aud rename column start to startDate;
alter table activity_aud rename column end_mod to endDate_mod;
alter table activity_aud rename column start_mod to startDate_mod;


alter table function rename column key to fkey;

alter table function_aud rename column key to fkey;
alter table function_aud rename column key_mod to fkey_mod;

alter table participation rename column end to endDate;
alter table participation rename column start to startDate;

alter table participation_aud rename column end to endDate;
alter table participation_aud rename column start to startDate;
alter table participation_aud rename column end_mod to endDate_mod;
alter table participation_aud rename column start_mod to startDate_mod;

java -cp /work/tools/h2/bin/h2-1.4.197.jar org.h2.tools.Script -user sa -url jdbc:h2:./testPfad -script testPfad.sql
java -cp /work/tools/h2/bin/h2-2.2.200.jar org.h2.tools.RunScript -user sa -password sa -url jdbc:h2:./testPfad2 -script testPfad.sql

