# Googol - A Distributed Web Crawler and Search Engine

## Introduction

Googol is a distributed web crawler and search engine designed using Java RMI
(Remote Method Invocation). This project allows you to crawl the web and index
content for search functionality, leveraging the power of distributed systems.

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Building the Project](#building-the-project)
- [Running the Project](#running-the-project)
- [Generating Javadoc](#generating-javadoc)
- [Creating Archives](#creating-archives)
- [Contributing](#contributing)
- [License](#license)

## Requirements

Before you start, ensure you have the following installed:

- Java Development Kit (JDK) 8 or higher
- Apache Maven
- Git

## Installation

1. Clone the repository:

```sh
git clone https://github.com/ShellTux/Googol.git
cd Googol
```

2. Navigate to the project directory.

## Building the Project

To compile the project and create a JAR file:

```bash
mvn package
```

This command will compile the Java source files and create an executable JAR in the `target` directory.

## Running the Project

```shell
mvn exec:java --quiet --offline --define exec.mainClass=com.googol.IndexStorageBarrel
mvn exec:java --quiet --offline --define exec.mainClass=com.googol.Downloader
mvn exec:java --quiet --offline --define exec.mainClass=com.googol.Gateway
mvn exec:java --quiet --offline --define exec.mainClass=com.googol.Client
```

Or you can run the wrapper shell script:

```shell
./run IndexStorageBarrel
./run Downloader
./run Gateway
./run Client
```

## Generating Javadoc

To generate the Javadoc documentation for the project, run the following command:

```bash
mvn javadoc:javadoc
```

The generated Javadoc will be available in the `target/reports/apidocs/` directory.

## Creating Archives

To create a compressed archive of the project and its documentation, run:

```bash
make archive
```

## Contributing

Contributions are welcome! If you have suggestions for improvements, please
fork the repository and submit a pull request. Ensure your code adheres to
existing styles and includes appropriate tests.

## License

This project is licensed under the MIT License - see the
[LICENSE.md](LICENSE.md) file for details.
