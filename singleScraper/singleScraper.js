const puppeteer = require('puppeteer');

const config = {
    baseUrl: "https://dgap.de/"
}
const kafka = require('kafka-node');
const kafkaConfig = require('./kafkaConfig');
var companyIdentifier = "";

let getSingleCompanyInfos = async () => {

    const browser = await puppeteer.launch({ headless: true, devtools: false, args: ['--no-sandbox', '--disable-setuid-sandbox'] });
    const page = await browser.newPage();
    await page.goto(config.baseUrl);
    let company = await fetchLatestCompany(page);
    company.title = await retrieveTitleForCompany(company, page);
    await page.goto(company.link);
    company = await page.evaluate((company) => {
        let headerNodes = document.querySelector("#content ul").querySelectorAll("li");

        if (headerNodes[0] && headerNodes[0].textContent) {
            company.isinCandiate1 = headerNodes[0].textContent.trim().substring(6);
        }
        if (headerNodes[0] && headerNodes[0].textContent) {
            company.isinCandiate2 = headerNodes[0].textContent.trim().substring(6);
        }
        if (headerNodes[1] && headerNodes[1].textContent) {
            var isinRegex = /\b([A-Z]{2})((?![A-Z]{10}\b)[A-Z0-9]{10})\b/;
            company.isinCandiate3 = document.body.innerText.match(isinRegex)[0];
        }
        if (document.querySelector("#content .break-word.news_main")) {
            company.articleText = document.querySelector("#content .break-word.news_main").textContent;
        }

        return company;
    }, company);
    
    console.info("Checking for changes...")
    if(companyIdentifier != company.articleText) {
        console.info("Changes recognized!");
        companyIdentifier = company.articleText;
        var isinRegex = /\b([A-Z]{2})((?![A-Z]{10}\b)[A-Z0-9]{10})\b/;
        if(isinRegex.test(company.isinCandiate1)) {
            company.isin = company.isinCandiate1;
            sendCompanyInfoToKafka(company)
        } else if(isinRegex.test(company.isinCandiate2)) {
            company.isin = company.isinCandiate2;
           sendCompanyInfoToKafka(company)
        } else if(isinRegex.test(company.isinCandiate3)) {
           company.isin = company.isinCandiate3
           sendCompanyInfoToKafka(company)
        } else {
            console.error("ALL ISIN CANDITATES NO ISIN!");
            console.error("Isin: " + company.isinCandiate1 + " - seems to be invalid!");
            console.error("Isin: " + company.isinCandiate2 + " - seems to be invalid!");
            console.error("Isin: " + company.isinCandiate3 + " - seems to be invalid!");
        }
    } else {
        console.info("No changes recognized!");
    }

    browser.close();
};

fetchLatestCompany = async function (page) {
    console.log("Start fetching values for most recent company...");
    return await page.evaluate(() => {
        // function declaration -  functions cannot be passed to page.evaluate without hack https://stackoverflow.com/a/52176714
        const getCompanyWithValues = function (infoParts, linkElement) {
            let company = {};
            
            company.date = new Date().toJSON();
            company.articleType = infoParts[1].substring(11, infoParts[1].length - 1);
            company.name = infoParts[2];
            for (let infoIndex = 3; infoIndex < infoParts.length; infoIndex++) {
                company.name = company.name + " " + infoParts[infoIndex];
            }
            if (linkElement != null) {
                company.link = linkElement.href;
            }
            return company
        }
        let linkElement = document.querySelector("#content div.content_list.news_list table > tbody tr:nth-child(1) > td.content_text > a")
        let infosElement = linkElement.querySelector("strong")

        if (infosElement != null) {
            let infoParts = infosElement.innerText.split(" ");
            return getCompanyWithValues(infoParts, linkElement);
        }
        return null;
    });
}

retrieveTitleForCompany = async function (company, currentPage) {
    await currentPage.goto(company.link);
    return await currentPage.evaluate(() => {
        let headBaseSelector = ("#content > div.column.left_col_wide > div.column");
        let head = document.querySelector(headBaseSelector + ".left_col > div > div > h1") || document.querySelector(headBaseSelector + " > div > div > h1");
        return head.innerText;
    });
}

sendCompanyInfoToKafka = function (company) {
    var kafka = require('kafka-node');
    var Producer = kafka.Producer;
    var Client = kafka.KafkaClient;
    var client = new Client({kafkaHost: kafkaConfig.kafka_server});
    console.log(kafkaConfig.kafka_server);

    var argv = {};
    var topic = kafkaConfig.kafka_topic || 'topic1';
    var p = argv.p || 0;
    var a = argv.a || 0;
    const producer = new kafka.HighLevelProducer(client);

    producer.on('ready', function () {
        var message = JSON.stringify(company);

        producer.send([{ topic: topic, partition: p, messages: [message], attributes: a }], function (
            err,
            result
        ) {
            console.log(err || result);
        });
    });

    producer.on('error', function (err) {
        console.log('error', err);
    });
}



console.log("********************************************************");
console.log("I am alive!")
console.log("********************************************************");
setInterval(getSingleCompanyInfos, 60000);

