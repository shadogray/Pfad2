java -cp /work/tools/h2/bin/h2-2.2.220.jar org.h2.tools.RunScript -url jdbc:h2:./thisTest -user sa -password sa -script sql/testPfad2_Backup_20240709.sql

java -cp /work/tools/h2/bin/h2-2.2.220.jar org.h2.tools.RunScript -url jdbc:h2:./thisTest -user sa -password sa -continueOnError -script ../../../../doc/changes.sql

java -cp /work/tools/h2/bin/h2-2.2.220.jar org.h2.tools.Script -url jdbc:h2:./thisTest -user sa -password sa -script testPfad2_Backup.sql 

echo AND NOW: mv testPfad2_Backup.sql sql/testPfad2_Backup_20240709.sql 
echo ANDTHEN: rm thisTest.*

