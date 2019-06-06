# dgapScraper
## Running and Building
To Start the Docker Compose run the _buildAllAndRun.ps1_ File inside Powershell.
It Automates all building and running of the docker commands.

via the flag ```-rebuild``` you can choose which docker container should be rebuild.
If the ```-rebuild``` option is missing all containers will be new created.

If the ```-rebuild``` options is empty or none only ```docker-compose down``` and ```docker-compose up``` will be run sequentally.

### Example Options 
The Last command works with all mircoservices

| Command                                       | Action                                        |
|-----------------------------------------------|-----------------------------------------------|
| buildAllAndRun.ps1                            | Everything will be rebuild                    |
| buildAllAndRun.ps1 -rebuild none              | nothing will be rebuild                       |
| buildAllAndRun.ps1 -rebuild articleprocessor  | only the article processor will be rebuild    |

## Manual Steps if liked
To run the scraper, you need to have docker, docker-compose and maven installed.
Further you need to build the following .jar artifacts via maven with:

```mvn clean install```

in the following folders:

1. _/ArticleProcessor_
2. _/StockProcessor_

You need to build all Node Modules for the SingleScraper via:

```npm install```

inside:

1. _/singleScraper_


To build the docker images from the root directory you need to run each command:

1. _/ArticleProcessor_: ```docker build -t articleprocessor .\articleprocessor```
2. _/StockProcessor_: ```docker build -t articleprocessor .\articleprocessor```
3. _/database_: ```docker build -t database .\database```
3. _/singleScraper_: ```docker build -t singlescraper .\singleScraper```

After that you are able to start the docker-compose via:

```docker-compose up```
