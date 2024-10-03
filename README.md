# Nova-Clients

**v1.0.1**

This project, based on Java and the Spring Boot framework, is an open source tool to manage and
improve the developments of your releases.
Improve the development of your releases with **Nova**!

This repository contains the client versions of **Nova**, so if you want to customize you can fork
it
and work on it, if there are any errors, fixes to do or some idea to upgrade this project, please
open
a ticket or contact us to talk about, thanks and good use!

## 🛠 Skills

- Java
- Kotlin

## Architecture

This project will be constantly developed to reach different platforms to work on, following the platforms releases
steps:

- Mobile
  - <a href="https://play.google.com/store/apps/details?id=com.tecknobit.nova">Android</a>
  - iOS -> planned
- <a href="https://github.com/N7ghtm4r3/Nova-Clients/releases/tag/1.0.1">Nova desktop version</a>
- <a href="https://github.com/N7ghtm4r3/Nova/releases/tag/1.0.1">Backend service "out-of-the-box"</a>

## Usages

See how to use the **Nova** service reading the <a href="https://github.com/N7ghtm4r3/Nova#readme">Nova backend procedures</a>

## Customize the application

To customize and create your own version of this application you need to have
the <a href="https://github.com/N7ghtm4r3/Nova/tree/main/core">
core library</a> implemented in your project and published into maven local system

### Clone the core library and publish to maven local

- Clone the repository or download the zip file of the current version available

- Open the folder file in your development environment and publish to maven local with the
  **publishMavenPublicationToMavenLocal** gradle task, take a look <a href="https://docs.gradle.org/current/userguide/publishing_maven.html">here</a>
  for a help

### Implement the core library to your application

- #### Gradle (Short)

```gradle
repositories {
  ...
  mavenLocal()
}

dependencies {
  implementation 'com.tecknobit.novacore:novacore:1.0.1'
}
```

#### Gradle (Kotlin)

```gradle
repositories {
  ...
  mavenLocal()
}

dependencies {
  implementation("com.tecknobit.novacore:novacore:1.0.1")
}
```

## Desktop appearance

<details>
  <summary>Desktop UI</summary>
  <img src="https://github.com/N7ghtm4r3/Nova-Clients/blob/main/images/projects_eng.png" alt="projects"/>
  <img src="https://github.com/N7ghtm4r3/Nova-Clients/blob/main/images/project_eng.png" alt="project"/>
  <img src="https://github.com/N7ghtm4r3/Nova-Clients/blob/main/images/release_eng.png" alt="release"/>
  <img src="https://github.com/N7ghtm4r3/Nova-Clients/blob/main/images/comment_eng.png" alt="comment"/>
</details>

## Authors

- [@N7ghtm4r3](https://www.github.com/N7ghtm4r3)

## Support

If you need help using the library or encounter any problems or bugs, please contact us via the
following links:

- Support via <a href="mailto:infotecknobitcompany@gmail.com">email</a>
- Support via <a href="https://github.com/N7ghtm4r3/Nova-Clients/issues/new">GitHub</a>

Thank you for your help!

## Badges

[![](https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=google-play&logoColor=white)](https://play.google.com/store/apps/developer?id=Tecknobit)
[![Twitter](https://img.shields.io/badge/Twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/tecknobit)

[![](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://play.google.com/store/apps/details?id=com.tecknobit.nova)

## Donations

If you want support project and developer

| Crypto                                                                                              | Address                                        | Network  |
|-----------------------------------------------------------------------------------------------------|------------------------------------------------|----------|
| ![](https://img.shields.io/badge/Bitcoin-000000?style=for-the-badge&logo=bitcoin&logoColor=white)   | **3H3jyCzcRmnxroHthuXh22GXXSmizin2yp**         | Bitcoin  |
| ![](https://img.shields.io/badge/Ethereum-3C3C3D?style=for-the-badge&logo=Ethereum&logoColor=white) | **0x1b45bc41efeb3ed655b078f95086f25fc83345c4** | Ethereum |

If you want support project and developer
with <a href="https://www.paypal.com/donate/?hosted_button_id=5QMN5UQH7LDT4">PayPal</a>

## Privacy policy

This section concerns Privacy and policy for Android's application published on Google Play
Store. <br>
Pandoro application **will not share any personal data of the user with third part applications.** 

Copyright © 2024 Tecknobit
