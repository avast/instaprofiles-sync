# Instaprofiles-sync

## Project info

- **Project maintainer:** Ladislav Vitásek ([vitasek/@/avast.com](mailto:vitasek/@/avast.com))
- **Requirements:**
    * Gradle 6.6+
    * JDK 11

## Content
[//]: https://imthenachoman.github.io/nGitHubTOC
[//]: https://ecotrust-canada.github.io/markdown-toc/

- [General information](#general-information)
- [Usage](#usage)
    - [Application Config](#application-config)
- [Profiles.yaml file](#profilesyaml-file)
- [Accounts.yaml file](#accountsyaml-file)
## General information
This application is used to regularly synchronize defined cloud profiles for [TeamCity plugin vsphere-instaclone](https://github.com/avast/vsphere-instaclone). 
It's using vsphere-instaclone JSON REST API to create, update or remove given profiles. Profiles are defined via YAML configuration file and this file can be located in either the GITHUB repository or local.    
Only when the change is detected, the profile is being updated on the TC server. With a `--dry-run` program argument, you can just emulate and to list all detected changes without applying them to TC server.
It's also possible to create initial profiles config file from the existing settings on the TC server.

It's written in Java - as a common [Spring Boot application](https://docs.spring.io/spring-boot/docs/1.5.16.RELEASE/reference/html/using-boot-running-your-application.html).


## Usage
The app can be launched as a standard Java application:
```bash
java -jar instaprofiles-sync.jar <optional JVM arguments> <optional program arguments>
```

Program arguments:
```text
##action - list
--list=<path/profiles.yaml>           stores current TC insta profiles into configuration file

##action - synchronize profiles
--dry-run                             don't apply any changes on TC server, just show changes
--profilesFile=<path/profiles.yaml>   path to accounts file

##action - synchronize accounts
--accountsFile=<path/accounts.yaml>   don't load profiles.yaml from GIT, but from the local file system instead - used for devel and debug purposes
--accountsPk=<path/publicKey>         path to public key in PEM format
```

### Application config
The file should be located in the app workdir `config/application.yaml`.     
It should define all secrets and custom configuration. 

#### Example 
```yaml
# app file log location
logging.file.name: logs/app.log
app:
  ## GIT settings - where is the profiles.yaml file located in GIT
  gitVcsId: "<GITHUB VCS ID>"
  gitRepository: "bt/instaprofiles-sync"
  profilesFile: "etc/profiles.yaml"

  #defines if the imageCloudParameters should be cleaned up on profile update API call
  cleanImageParametersOnUpdate: false
  
  # which profiles should be listed - used for --list when multiple versions of VSphereInstaclone are installed 
  listCloudCode: 'vmic2'
  # cloud profile plugin code
  cloudCode: 'vmic2'
  # part of API calls to differentiate URLs 
  webCloudCode: "vmic"

  vcs: {
    "[<GITHUB VCS ID>]": {
      type: "GITHUB",
      url: "<GIT URL>",
      apiUrl: "<GIT URL>/api/v3",
      token: "<SECRET API TOKEN>",
      tokenEnv: "GITHUB_TOKEN"
    }
  }

  teamcity:
    token: <SECRET API TC TOKEN>
    url: <TC URL>
```

## Profiles.yaml file
The file is defined by this [schema](/src/main/resources/schema/profiles-schema.json) and it contains properties to define cloud profile.  
Ids `profileName` with `projectId` (both required) should be unique across the all TC cloud profile definitions otherwise synchronization cannot work properly. 
`vCenterAccount` field value should be in sync with fields `id` values in `accounts.yaml` file. 

### Example
```yaml
profiles:
  - profileName: profile1
    description: "description profile1xxxxxx"
    projectId: Aaa
    vCenterAccount: "vCenterBrno"
    terminateConditions:
      terminateAfterBuild: true
    imageConfigJson:
      "image-name":
        template: datacenter/vm/tc-agent-template
        instanceFolder: datacenter/vm/tc-agents
        agentPool: "asdasdasd"
        resourcePool: aaaa
        maxInstances: 20
        network: "sasda"
      "image-name2":
        template: datacenter/vm/tc-agent-template
        instanceFolder: datacenter/vm/tc-agents
        maxInstances: 10
        network: []
  - profileName: profile2
    description: "profile desc 2"
    projectId: Aaa
    vCenterAccount: "vCenterPraha"
    imageConfigJson:
      "image-name3":
        template: datacenter/vm/tc-agent-template
        instanceFolder: datacenter/vm/tc-agents
        resourcePool: aaaa
        agentPool: 10
        maxInstances: 10
```

### Using of profiles-schema.json in Intellij IDEA
It's recommended to link your `profiles.yaml` file with this [schema](/src/main/resources/schema/profiles-schema.json), it can help you fix typos and to give you more hints (via ctrl+space).
See this [tutorial](https://www.jetbrains.com/help/idea/2021.1/json.html?utm_source=product&utm_medium=link&utm_campaign=IU&utm_content=2021.1#ws_json_schema_add_custom).


## Accounts.yaml file
File contains a list of accounts for vCenter SDK. This file is synchronized by a [separate command](#synchronize-accounts-example). 
The file is transfered via API REST call to the TeamCity server. The content is encrypted by given RSA public key. TeamCity server plugin stores the file in the TC database in encrypted form and use its own private key decrypt it when needed.  

### Example
```yaml 
  #accounts list
  accounts:
    - id: vCenterBrno
      url: https://vCenterURL/sdk
      username: <vCenter SDK username>
      password: <vCenter SDK password>
    - id: vCenterPraha
      url: https://vCenterURL/sdk
      username: <vCenter SDK username>
      password: <vCenter SDK password>
```

### Synchronize accounts example
```bash
java -jar instaprofiles-sync.jar --accountsFile=<path/accounts.yaml> --accountsPk=<path/publicKey> 
```