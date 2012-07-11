## CUTTING A RELEASE

Note: releases are GPG signed, so you need to have GPG setup

Add the following to your .m2/settings.xml

``` xml
    <servers>
        <server>
            <id>sonatype-nexus-snapshots</id>
            <username>SONATYPE_USERNAME</username>
            <password>SONATYPE_PASSWORD</password>
        </server>
        <server>
            <id>sonatype-nexus-staging</id>
            <username>SONATYPE_USERNAME</username>
            <password>SONATYPE_PASSWORD</password>
        </server>
    </servers>
```

``` sh
    mvn release:clean
    mvn release:prepare
    mvn release:perform
```

Login to the Sonatype OSS Nexus (https://oss.sonatype.org) to close & release the staged artifacts:
    Select a Staging Repositories from Build Promotion on the left
    Select the appropriate repository from the main window and click "Close" in the toolbar above
    Once succesfully closed, you can select the repository again and click "Release"

The newly released artifacts can be found at
https://oss.sonatype.org/content/repositories/releases/com/clearspring/metriccatcher/
and after the central repository syncs
http://search.maven.org/#browse|-506365030


## SEE ALSO

https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
