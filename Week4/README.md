NOTE: the timezone of the environment is on UTC time. I couldn't change the timezone to Pacific Time, as I was
getting this error: "user@5b7894f787bf:/projects$ timedatectl set-timezone US/Pacific
Failed to create bus connection: No such file or directory" or this one: "E: Unable to locate package tzdata"


How to run in command line:

within /projects/besito-inf212_mycode/Week4 directory: 

Compile by running:
    javac Eleven.java
    javac Twelve.java
 
Run file by running:
    java Eleven "../pride-and-prejudice.txt"
    java Twelve "../pride-and-prejudice.txt"