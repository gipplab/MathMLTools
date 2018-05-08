# Deployment Instructions
There are two possible ways to deploy a new version to maven central.
- Locally via `mvn`
- on GitHub

### Locally via `mvn`
It is recommended to use the local way because you can update the versions in the poms directly.
Once you want to deploy a new version, add a tag to your commit.
Afterwards, update versions of all poms by using:
```
mvn versions:set -DnextSnapshot -DallowSnapshots -DprocessAllModules
```
It will ask you enter a new version. Use the next version with `-SNAPSHOT` and press enter.
Commit these changes also and push everything to GitHub.

### On GitHub
To deploy a new version, create a new release on GitHub. As `tag version` use the new version number (without `v.`), e.g., `2.1.0`.
That's it. Keep in mind that this does not change the versions of the POM files.

## Setup
Generally spoken, follow the guide at https://www.phillip-kruger.com/post/continuous_integration_to_maven_central/.
Note that under _Sonatype Nexus OSS_ section 4 the code blocks do not use camelcase for pom-element tags. For examle
``` xml
    <nexusurl>https://oss.sonatype.org/</nexusurl>
```
must be
``` xml
    <nexusUrl>https://oss.sonatype.org/</nexusUrl>
```
It helps to put the deployment settings to a profile and call it via
```
mvn clean deploy --settings .travis/settings.xml -Dgpg.passphrase=$GPG_PASSPHRASE -DskipTests=true -B -U -P release
```
This starts the profile with `-P` with the name `release`. Other deployment methods makes it more difficult to control the deployment process.

## Tagged & Master Branch
Furthermore, it is in general a good idea to only allow deployment from the master branch, when the current commit is tagged. However that's not possible, since tags are not related to branches but commits. Travis do not consider tagged commits on a special brunch. Therefore, a travis check for master-branch & tagged commits will always fail. We switched the conditions to:
``` yaml
deploy:
  -
    provider: script
    script: .travis/deploy.sh
    skip_cleanup: true
    on:
      repo: ag-gipp/MathMLTools
      tags: true
      all_branches: true
      jdk: oraclejdk8
```
This will trigger the deploy process for all branches, as long as the commit get tagged.