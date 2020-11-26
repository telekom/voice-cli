# Voice Platform Commandline Client

<p align="center">
    <a href="https://github.com/telekom/voice-cli/commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/telekom/voice-cli?style=flat"></a>
    <a href="https://github.com/telekom/voice-cli/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/telekom/voice-cli?style=flat"></a>
    <a href="https://github.com/telekom/voice-cli/blob/master/LICENSE" title="License"><img src="https://img.shields.io/badge/License-MIT-green.svg?style=flat"></a>
</p>

<p align="center">
  <a href="#development">Development</a> •
  <a href="#documentation">Documentation</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#licensing">Licensing</a>
</p>

A commandline client to interact with Telekom Voice Platform. 
Type `help` to see all available commands.

### Features
 
* Invoke via text
* Invoke via audio
* Retrieve conversation history entries
* Stored commands: You can define often used commands in `commands.txt` and execute them using `! [n]`, which executes the command in line `[n]`. List all stored commands with `!`.
* Device metadata: You can define the device metadata in `options.json`.
* Device capabilities: You can define the device capabilities in `options.json`.
* Repeat last command with `!!`
* Password database
 
## Prerequisites
 
1. JDK 8
2. Maven >3.5
 
## Running via Maven
 
`mvn clean package exec:java -Dexec.args="--apikey <PUT-API-KEY> --idm-client-id <PUT-IDM-CLIENT-ID>"`
 
You can omit the `clean package` phases after first invocation of the above command.
 
## Usage
 
Start with `--help` to see commandline options.

```
usage: voice-cli
 -a,--apikey <arg>          Sets the API key
 -c,--idm-client-id <arg>   Sets the Telekom IDM Client ID
 -h,--help                  Prints this help
```
 
### Password database
 
The client contains a password database. The available passwords are stored in the file `passwords.txt`.
Each line is a username/password pair, separated by space.
 
The available credentials can be listed with `print-passwords`.
 
When asked for a username or password, the password database can be queried by entering `?`. A username / password
can be selected by entering `! <num>`. To access the first username / password, enter `! 1`.
 
#### Example
 
`passwords.txt` contains
 
```
alice super-secret-a
bob super-secret-b
```
 
When asked for a username, `! 1` selects "alice", `! 2` selects "bob". When asked for a password, `! 1` selects
"super-secret-a", `! 2` selects "super-secret-b". Entering `?` lists all available entries.
 
### Options
 
In the file `options.json` you can define multiple options:
 
```json
{
  "serialNumber": "xxx", // Serial number of the device
  "dynamicDeviceMetadata": {
    "zipCode": "81549" // can be arbitray key-value map
  },
  "wakeUpWord": "Hallo Magenta", // Wake up word, if all audio is streamed including the wakeup word
  "capabilities": "ssml no-ncs" // Device capabilities
}
```

If this file doesn't exist, default values are assumed:

```json
{
  "serialNumber": null,
  "dynamicDeviceMetadata": {},
  "wakeUpWord": null,
  "capabilities": null
}
```
 
### Examples
 
#### Login with Telekom IDM

```
Command: login-idm
```
 
#### Ask for the weather
 
```
Command: invoke-text Wie ist das Wetter in Frankfurt
Server: Das aktuelle Wetter in Frankfurt am Main ist 4 Grad und regnerisch
```
 
## Building
 
1. Install JDK 8
1. Run `mvn clean package`
1. Check the `target` folder for a `voice-cli-*-distribution.zip`
 
## How tos
 
* [Create WAV file](doc/howto-create-wav.md)

## Code of Conduct

This project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) in version 2.0 as our code of conduct. Please see the details in our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). All contributors must abide by the code of conduct.

## Working Language

We decided to apply _English_ as the primary project language.  

Consequently, all content will be made available primarily in English. We also ask all interested people to use English as language to create issues, in their code (comments, documentation etc.) and when you send requests to us. The application itself and all end-user facing content will be made available in other languages as needed.

## Documentation

The full documentation for the telekom can be found in _TBD_
## Support and Feedback
The following channels are available for discussions, feedback, and support requests:

| Type                     | Channel                                                |
| ------------------------ | ------------------------------------------------------ |
| **Issues**   | <a href="https://github.com/telekom/voice-cli/issues/new/choose" title="General Discussion"><img src="https://img.shields.io/github/issues/telekom/voice-cli?style=flat-square"></a> </a>   |
| **Other Requests**    | <a href="mailto:opensource@telekom.de" title="Email Open Source Team"><img src="https://img.shields.io/badge/email-Open%20Source%20Team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |

## How to Contribute

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Contributors

Our commitment to open source means that we are enabling -in fact encouraging- all interested parties to contribute and become part of its developer community.

## Licensing

Copyright (c) 2020 Deutsche Telekom AG.

Licensed under the **MIT License** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License by reviewing the file [LICENSE](./LICENSE) in the repository.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.
