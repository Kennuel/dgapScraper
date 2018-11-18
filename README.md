# dgapScraper
To run the scraper, you need to have docker and docker-compose installed, and maven.
Further you need to build ArticleProcessor via maven 

```mvn clean install```
and build a docker image named as articleprocessor from the dockerfile inside of articleprocessor

```docker build -t articleprocessor .\articleprocessor```
when in root directory.

After that you are able to start the docker-compose via:
```docker-compose up```

