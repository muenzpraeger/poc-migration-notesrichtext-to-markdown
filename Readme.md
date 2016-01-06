# Proof of Concept
# Migrating Notes RichText to Markdown

This repository holds the content of my blog post series for converting Notes RichText to Markdown. You can read about that on the following links.

[Part I](https://blog.winkelmeyer.com/2016/01/proof-of-concept-migrating-notes-richtext-to-markdown-part-i/)

[Part II](https://blog.winkelmeyer.com/2016/01/proof-of-concept-migrating-notes-richtext-to-markdown-part-ii/)

[Part III](https://blog.winkelmeyer.com/2016/01/proof-of-concept-migrating-notes-richtext-to-markdown-part-iii/)

# Pre-Conditions

You'll need Java 8 to run the code.

# Test contents

Besides the source code you'll find a sample database in ```src/test/resources``` which can be used to test drive the code. The sample database contains a few documents and need no special setup.

You may use any own database. In that case you have to make sure that you change the fields that are read from the view.

# Eclipse setup
The project uses [Gradle](https://gradle.org/).

To setup the project for Eclipse run the following command from your command line.

```gradle cleanEclipe eclipse```

That will create the needed Eclipse project files and also download the dependencies. You can afterwards import the project into Eclipse.

If you don't have Gradle installed - the [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) is attached to the project. Replace ```gradle``` with ```gradlew```in the commands to run it standalone.

This step is not mandatory.

# Application properties

For running the code you've to change the some properties, like the server host name, view etc. Those are configured in the file ```src/main/resources/application.properties```.

## server
Enter the hostname or IP address of the IBM Domino server (without a prepended _http_ or _https_) in here. The PoC code uses _http_ fixed. You can change that if you want. ;-)

## database
Enter the full path (relative to the Domino data directory) to your test database in here.

## view
Enter the view name that'll be used for getting the document data in here.

## fieldBody
Set the field name that contains the to be converted RichText in here.

## username
The PoC code assumes that the connection needs authentication. So set a valid HTTP username in here.

## password.
Well, its clear what should be in here, right?

## pathSave
Enter the target folder on your file system in here. The generated Markdown files will be stored in here.

# Running the code

After you've changed the ```application.properties``` file you've two options to run the code. A runnable class is located for that in ```src/main/java/com.winkelmeyer.richtext2markdown.tests.SimpleTest```.

## Using Eclipse

Just as usual you can run the class as a Java application.

## Using Gradle/command line

Here you've two options. You can either run it directly via your command line or create a runnable/distributable JAR file. Make sure that your _java.home_ property is set correctly within your operating system.

### Run class directly
Enter ```gradle run``` on the command line. That will execute the class directly.

### Create runnable JAR file
Enter ```gradle installDist``` on the command line. This will create a full distributable package located in ```build/install/com.winkelmeyer.richtext2markdown```. Within the ```bin``` folder you'll find two executables (Windows/Unix).

Read more about this [in the Gradle documentation](https://docs.gradle.org/current/userguide/application_plugin.html) and in my [ICON UK presentation](http://www.slideshare.net/muenzpraeger/iconuk-2015-gradle-up).

# Result data
You can inspect the result data within the generated Markdown files. Play with different RichText elements to see and learn about the output (and with that about the stuff you may have to think about).

There are lots of different Markdown viewers and editors for all operating systems available. Pick one that you'll like. ;-)

