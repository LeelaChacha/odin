<p align="center">
    <a href="https://circleci.com/gh/LeelaChacha/odin/tree/master">
        <img src="https://circleci.com/gh/LeelaChacha/odin/tree/master.svg?style=shield">
    </a>
    <a href="https://sonarcloud.io/dashboard?id=LeelaChacha_odin">
        <img src="https://sonarcloud.io/api/project_badges/measure?project=LeelaChacha_odin&metric=coverage">
    </a>
</p>

<br />
<p align="center">
  <a href="https://github.com/LeelaChacha/odin">
    <img src="https://github.com/LeelaChacha/odin/raw/master/docs/resources/odin-logo.png" alt="Logo">
  </a>

<h3 align="center">Odin</h3>

  <p align="center">
    Odin is a messaging bus framework that works with a standard MongoDB database.
    <br />
    <a href="https://github.com/LeelaChacha/odin"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/LeelaChacha/odin">View Demo</a>
    ·
    <a href="https://github.com/LeelaChacha/odin/issues">Report Bug</a>
    ·
    <a href="https://github.com/LeelaChacha/odin/issues">Request Feature</a>
  </p>
</p>

<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## About The Project

Odin lets your apps communicate with other Odin-integrated apps. It is a lightweight and efficient framework, and
it uses MongoDB as the underlying database. This offers high-availability and cheap/free infrastructure for easy
implementation.


### Built With

* [Java](https://www.java.com/en/)
* [Apache Maven](https://maven.apache.org/)
* [MongoDB](https://www.mongodb.com/)

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

* Java JDK 11
* Apache Maven

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/LeelaChacha/odin.git
   ```
2. Build and Verify
   ```sh
   mvn verify
   ```

## Usage

This framework can be used in your projects with jitpack maven repository.
Add jitpack repository to your settings.xml or your pom.xml

**settings.xml**
```xml
    ...
   <profiles>
        <profile>
            <id>jitpack-repo</id>
            <repositories>
                <repository>
                    <id>jitpack.io</id>
                    <url>https://jitpack.io</url>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <activeProfiles>
    <activeProfile>jitpack-repo</activeProfile>
    </activeProfiles>
    ...
   ```
<h2 align="center">OR</h2>
**pom.xml**
```xml
    ...
   <repositories>
        <repository>
            <id>jitpack.io</id>
            <name>Jitpack Maven Repository</name>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    ...
   ```

After that, Odin can be used directly as an dependency in your project.
```xml
    ...
   <dependency>
      <groupId>com.github.LeelaChacha</groupId>
      <artifactId>odin</artifactId>
      <version>__Tag__</version>
    </dependency>
    ...
   ```

_For more examples, please refer to the [Documentation](https://github.com/LeelaChacha/odin)_

## Roadmap

See the [open issues](https://github.com/LeelaChacha/odin/issues) for a list of proposed features (and known issues).

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the Apache 2.0 License. See `LICENSE` for more information.

## Contact

Hukumraj Singh Deora - [LinkedIn](https://www.linkedin.com/in/hukumraj-singh-deora/)

Project Link: [https://github.com/LeelaChacha/odin](https://github.com/LeelaChacha/odin)
