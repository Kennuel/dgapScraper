# dgapScraper
To run the scraper, you need to have docker, docker-compose and maven installed.
Further you need to build ArticleProcessor via maven with:

```mvn clean install```

To build a docker image named as articleprocessor from the dockerfile inside of articleprocessor you need to run:

```docker build -t articleprocessor .\articleprocessor```

when in root directory.

After that you are able to start the docker-compose via:

```docker-compose up```

