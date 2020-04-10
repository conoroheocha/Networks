## Authors
#### Laura Owens, Conor O'Heocha, Oscar Crowley, Sriom Chakrabarti

## Instructions for Reviewing:
#### Requirements:

To build this project you'll require Java, (I'm using Version 1.8.0_201 but newer versions here should work https://www.oracle.com/java/technologies/javase-downloads.html#JDK8).
You'll also need a IDE like Eclipse or IntelliJ(available here: https://www.eclipse.org/).
You'll then need to install Maven from https://maven.apache.org/
(Make sure that Maven has been added to your PATH)


#### To Run:

To demonstrate the program, open the project in your IDE and run the file TestRun.java. This will run seperate instances of a the various programs in seperate windows. There will be one Global Server, one Server and three Clients, one of which is attached to the instance of the Server. The Server will have a seperate window open for each Client connection.

By typing any string into any of the client Windows, we can demonstrate that string being encrypted and sent to the server where it is decrypted. To demonstrate, how the program keeps track of symptom populations, type 'covid'. This will increment the local population counter immediately. This new value is then immediately available to the other client. The global population value is updated periodically.

The project is also available as GitHub Repo at https://github.com/conoroheocha/Networks
