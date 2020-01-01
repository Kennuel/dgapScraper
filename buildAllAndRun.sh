docker-compose down

if ["$0" == "articleprocessor"] || ["$0" == ""] 
    cd .\ArticleProcessor
    mvn clean install
    docker build -t articleprocessor .
    cd ..
fi

if ["$0" == "persistence"] || ["$0" == ""] 
    cd .\database
    docker build -t database .
    cd ..
fi

if ["$0" == "stockProcessor"] || ["$0" == ""] 
    cd .\StockProcessor
    mvn clean install
    docker build -t stockprocessor .
    cd ..
fi

if ["$0" == "stockProcessor"] || ["$0" == ""] 
    cd .\StockProcessor
    mvn clean install
    docker build -t stockprocessor .
    cd ..
fi

if ["$0" == "singleScraper"] || ["$0" == ""] 
    cd .\singleScraper
    mvn clean install
    docker build -t singlescraper .
    cd ..
fi

if ["$0" == "stockScraper"] || ["$0" == ""] 
    cd .\stockScraper
    mvn clean install
    docker build -t stockscraper .
    cd ..
fi

docker-compose up