# l10n-maven-plugin

This is a plugin to provide reporting and build utilities surrounding localization.

## Prerequisites

This requires:

* Maven 3
* Java 7

## Usage

The following describes usage details of this plugin.

### Messages Verification Build Plugin

Starting with version 2.1, you can now have the plugin bind to the `verify` lifecycle phase and verify the following about a configured messages properties file:

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

The plugin, by default, looks like the following:

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

## FAQ

The following may be questions frequently asked about this project.

### Why Aren't The Missing and Extra Keys Listed?

The report, currently, only lists the number of extra and missing keys in each translation; as this number can be quite high (and be multipled by the number of translations), in the interest of making a readable report, the list of keys are omitted from the report.
