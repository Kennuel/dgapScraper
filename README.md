# dgapScraper
To run the scraper, you need to have docker, docker-compose and maven installed.
Further you need to build the following .jar artifacts via maven with:

```mvn clean install```

in the following folders:

1. _/ArticleProcessor_
2. _/Persistence_

To build the docker images from the root directory you need to run each command:

1. _/ArticleProcessor_: ```docker build -t articleprocessor .\articleprocessor```
2. _/Persistence/database_: ```docker build -t database .\persistence\database```
3. _/Persistence_: ```docker build -t persistence .\persistence```

After that you are able to start the docker-compose via:

```docker-compose up```


## Persistence standalone usage
1. Run the database container via: `docker run --name database -p 5432:5432 -d database`
2. After this, run the persistence container via: `docker run --name persistence -p 2002:2002 --link database:postgres -d persistence`
3. You should now be able to access the persistence application via `localhost:2002/` plus any of the offered REST endpoints

## REST-Endpoints
| Method | Endpoint                            | Requirement                                              |
|--------|-------------------------------------|----------------------------------------------------------|
| GET    | localhost:2002/findByIsin?isin=ISIN | ISIN = string (e.g. US0378331005)                        |
| POST   | localhost:2002/save                 | Request body ```{ "isin": "ISIN", "symbol": "SYMBOL" }```|