## Data Generation

The contents of this directory should be used to create and populate the database tables for the Hotel Management System project.
Here's a brief overview of the contents of this directory:
- The subdirectory `/sql` contains DDL for the tables as well as a few triggers written in PL/SQL for the database.
- The subdirectory `/src` contains the Java code that generates and populates the tables, as well as two classes that assist in this process.
    - `/src/datafiles` contains .txt files with randomly generated source data for the tables.
- The subdirectory `/build` contains files necessary to build and run the program.

### Setup
- Have an Oracle Database available and install Java on your machine - the latest version of Java is reccomended.
- Execute the files ddl.sql amd dbtriggers.sql on the database.
- Execute datagen.sql.
- Next, build the jar:
    - Compile all .java files in `/src` with: `javac /src/*.java`
    - Using the files in `/build`, create the jar file by executing: `jar cmfv DataGen.jar Manifest.txt *.class`


### Execution
- Execute the jar file: `java -jar DataGen.jar`
- The program will prompt you for your Oracle DB username and password.
- Once logged in, the program will automatically execute all inserts to the appropriate tables.
- You can verify that all data was inserted by querying each table.