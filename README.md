# l10n-maven-plugin

This is a plugin to provide reporting and build utilities surrounding localization.

## Prerequisites

This requires:

* Maven 3
* Java 7

## Usage

The following describes usage details of this plugin.

### Messages Verification Build Plugin

Starting with version 1.2, you can now have the plugin bind to the `verify` lifecycle phase and verify the following about a configured messages properties file:

* It has no duplicate translation keys
* Its translation keys reference existent classes
* Its translation keys reference existent fields in those classes

#### Configuration

An example of how to configure this might look like the following:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.jrh3k5</groupId>
            <artifactId>l10n-maven-plugin</artifactId>
            <executions>
                <execution>
                    <id>verify-messages</id>
                    <goals>
                        <goal>verify-messages</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

By default, it looks at `${project.basedir}/src/main/resources/messages.properties` to analyze.

An example of the output will look like the following:

```
[INFO] --- l10n-maven-plugin:1.2:verify-messages (verify-messages) @ my-project ---
[WARNING] File messages.properties contains 1 duplicate keys.
[WARNING] File messages.properties contains 8 references to non-existent translation key classes.
[WARNING] File messages.properties contains 8 references to non-existent translation class keys.
```

##### Pointing to Alternative Locations

To change it from its default location for analysis of translation keys, add the following configuration element to the plugin:

```xml
<plugin>
    <groupId>com.github.jrh3k5</groupId>
    <artifactId>l10n-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>verify-messages-en</id>
            <goals>
                <goal>verify-messages</goal>
            </goals>
            <configuration>
                <messagesFile>${project.basedir}/src/main/resources/messages_en.properties</messagesFile>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This will have it verify the state of the properties file located at `${project.basedir}/src/main/resources/messages_en.properties`.

##### Failing the Build

By default, this goal will not fail the build, but merely emit WARN-level messages about the issues. If you wish to have your build fail, you can add the following configuration element:

```xml
<plugin>
    <groupId>com.github.jrh3k5</groupId>
    <artifactId>l10n-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>verify-messages</id>
            <goals>
                <goal>verify-messages</goal>
            </goals>
            <configuration>
                <failBuild>true</failBuild>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This will produce output similar to the following:

```
[INFO] --- l10n-maven-plugin:1.2:verify-messages (verify-messages-en) @ my-project ---
[ERROR] File messages.properties contains 1 duplicate keys.
[ERROR] File messages.properties contains 8 references to non-existent translation key classes.
[ERROR] File messages.properties contains 8 references to non-existent translation class keys.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 57.843 s
[INFO] Finished at: 2015-01-18T08:39:26-06:00
[INFO] Final Memory: 56M/456M
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal com.github.jrh3k5:l10n-maven-plugin:1.2:verify-messages (verify-messages) on project my-project: The file messages.properties has one or more verification errors. Refer to messages above for more information. -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
```

### Translation Key Verification Report

This plugin provides a "Translation Key Verification" report. This specific report assumes that you use classes with class-level fields (most commonly `enum` objects) to represent your translation keys like the following examples:

```java
package com.example;

public class MyBusinessClass {
    private enum TranslationKeys {
        ERROR;
    }
}
```

```java
package com.example;

public enum TranslationKeys {
    INFO;
}
```

...which would result in a messages properties file like the following:

```
com.example.MyBusinessClass.TranslationKeys.ERROR = Error!
com.example.TranslationKeys.INFO = You should know this!
```

It provides the following information:

* Supported locale of each messages bundle
* Number of translation keys in each bundle
* The names of any duplicate translation keys

In addition, for the authoritative source, the following information is provided:

* The names of listed translation classes containing keys that could not be resolved into an actual class
* The names of keys in each translation class that could not be resolved into an actual field on the class

In addition, for each translation of the authoritative source, the following information is provided:

* Number of translation keys found in the authoritative source and not this translation
* Number of translation keys found in this translation and not found in the authoritative source
* The percentage of completion of the translation as compared to the authoritative source

#### Configuration

The plugin, by default, looks like the following (shown below are default values):

```xml
<plugin>
    <groupId>com.github.jrh3k5</groupId>
    <artifactId>l10n-maven-plugin</artifactId>
    <configuration>
        <messagesFile>${project.basedir}/src/main/resources/messages.properties</messagesFile>
        <translatedMessagesPattern>src/main/resources/messages*.properties</translatedMessagesPattern>
    </configuration>
</plugin>
```

These configuration parameters control the following:

* *messagesFile*: This is the properties file containing what is presumed to be the "authoritative" list of translation keys (and of which all other messages properties files are translations)
* *translatedMessagesPattern*: This is an Ant pattern that is applied against the base directory of the project (`${project.basedir}`) to find translations of the configured authoritative messages properties file.

##### Pointing to Alternative Locations

Your project may not follow the defaults assumed by this report - for example, if you store all of your string localizations in `src/main/webapp/WEB-INF/messages` beneath the base directory of your project, and you treated `messages_en.properties` as your source of truth (and have no `messages.properties`), your configuration might look something like this:


```xml
<plugin>
    <groupId>com.github.jrh3k5</groupId>
    <artifactId>l10n-maven-plugin</artifactId>
    <configuration>
        <messagesFile>${project.basedir}/src/main/webapp/WEB-INF/messages/messages_en.properties</messagesFile>
        <translatedMessagesPattern>src/main/webapp/WEB-INF/messages/messages*.properties</translatedMessagesPattern>
    </configuration>
</plugin>
```

##### Finding Translation Classes Without Messages

The plugin can be configured to look for instances of configured class names and implementations and extensions of configured class names, compare it against the configured authoritative messages properties file, and report any translation keys that are not found in the configured properties file.

This configuration, if you were using, as an example, `enum` objects implementing the `com.example.TranslationKey` interface, would look like:

```xml
<plugin>
    <groupId>com.github.jrh3k5</groupId>
    <artifactId>l10n-maven-plugin</artifactId>
    <configuration>
        <keyClasses>
            <keyClass>com.example.TranslationKey</keyClass>
        </keyClasses>
    </configuration>
</plugin>
```

## FAQ

The following may be questions frequently asked about this project.

### Why Aren't The Missing and Extra Keys Listed?

The report, currently, only lists the number of extra and missing keys in each translation; as this number can be quite high (and be multipled by the number of translations), in the interest of making a readable report, the list of keys are omitted from the report.
