CLASSPATH=/proj/scorpion/rrl/SRW/src:/proj/scorpion/rrl/java/lib/Pears.jar:/proj/scorpion/rrl/java/lib/lucene.jar:/proj/scorpion/rrl/java/lib/dspace.jar:/proj/scorpion/rrl/java/lib/axis-1_2.jar:/proj/scorpion/rrl/java/lib/commons-logging.jar:/proj/scorpion/rrl/java/lib/cql-java.jar:/proj/scorpion/rrl/java/lib/Dbutils.jar:/proj/scorpion/rrl/java/lib/servlet.jar:/proj/scorpion/rrl/java/lib/saaj.jar:/proj/scorpion/rrl/java/lib/jaxrpc.jar:/proj/scorpion/rrl/java/lib/gwen.jar:/proj/scorpion/rrl/java/lib/commons-discovery.jar

all:
	-( cd src/ORG/oclc/os/SRW ; javac -g -classpath $(CLASSPATH) *.java )
	-( cd src/gov/loc/www/zing/srw/srw_bindings ; javac -g -classpath $(CLASSPATH) *.java )

jar:
	-@rm -f SRW.jar
	-(cd src/java; jar cf SRW.jar ORG/oclc/os/SRW/*.class gov/loc/www/zing/*/*.class gov/loc/www/zing/*/*/*.class; mv SRW.jar ../..)

sourcejar:
	-@rm -f SRWSource.jar
	-(cd src; jar cf SRWSource.jar ORG/oclc/os/SRW/*.java gov/loc/www/zing/*/*.java gov/loc/www/zing/*/*/*.java; mv SRWSource.jar ..)
