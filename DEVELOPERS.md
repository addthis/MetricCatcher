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
```

Maven has performed the necessary preparations for the release, but it is
configured not to push those to origin automatically, to save the world from
half-finished releases and spurious tags.  If everything looks good, push
and continue on; if not, you can reset master to the commit prior to Maven's.

```sh
git push -u origin --tags
mvn release:perform
```

At this point, Maven has built & signed the new tarball, and has uploaded it
to Sonatype's repostiory.  Now you need to go tell Sonatype to publish the
staged artifacts.

Login to the Sonatype OSS Nexus (https://oss.sonatype.org):
    Select a Staging Repositories from Build Promotion on the left
    Select the repository from the main window and click "Close" in the toolbar
    Once succesfully closed, select the repository again and click "Release"

The newly released artifacts can be found at
https://oss.sonatype.org/content/repositories/releases/com/clearspring/metriccatcher/
and after the central repository syncs
http://search.maven.org/#browse|-506365030


## SEE ALSO

https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
