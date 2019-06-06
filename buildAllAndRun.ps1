param(
    [string]$rebuild = "")
 
docker-compose down

If ($rebuild -eq "" -or $rebuild -eq "articleprocessor") {
    cd .\ArticleProcessor
    mvn clean install
    docker build -t articleprocessor .
    cd ..
}

If ($rebuild -eq "" -or $rebuild -eq "persistence") {
    cd .\database
    docker build -t database .
    cd ..
}

If ($rebuild -eq "" -or $rebuild -eq "stockProcessor") {
    cd .\StockProcessor
    mvn clean install
    docker build -t stockprocessor .
    cd ..
}

If ($rebuild -eq "" -or $rebuild -eq "singleScraper") {
    cd .\singleScraper
    npm install
    docker build -t singlescraper .
    cd ..
}

docker-compose up