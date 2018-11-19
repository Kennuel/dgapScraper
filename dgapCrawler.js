const puppeteer = require('puppeteer');

const config = {
    baseUrl : "https://dgap.de/"
}

let crawl = async () => {
    const browser = await puppeteer.launch({ headless: false, devtools: true});
    const page = await browser.newPage();
    var news = [];

    var keepGoing = true;
    var firstSite = true;

    while (keepGoing) {
        await page.goto(config.baseUrl);
        let companies = await fetchCompanys(page);
        let anyCompanyHasStopFlag = companies.find((company) => {
            return company.stop;
        });
        keepGoing = !anyCompanyHasStopFlag; 
        news = await retrieveTitleForLinks(companies, page, keepGoing);
        //------- switching sites ------------------
        await page.goto(config.baseUrl);
        config.baseUrl = await page.evaluate((firstSite) => {
            let selectorLinkBase = "#content > div.column.left_col > div.box.darkblue.content_list.news_list > div.bottom_link.list_paging";
            let site = firstSite ? document.querySelector(selectorLinkBase + " > a") : document.querySelector(selectorLinkBase + " > div.list_next > a");
            if (site != null) {
                return site.href;
            }
            keepGoing = false;
        }, firstSite);
        firstSite = false;
    } // ----- END WHILE

    writeNewsToFile(news)
    browser.close();
};

fetchCompanys = async function(page) {
    console.log("Start fetching articles from the next page...");
    return await page.evaluate(() => {
        // function declaration -  functions cannot be passed to page.evaluate without hack https://stackoverflow.com/a/52176714
        const isNews = function(infoParts) {
            return infoParts[1].includes("DGAP-News");
        }

        const getCompanyWithValues = function(infoParts, linkElement) {
            var today = new Date().toLocaleDateString("de-DE", { day:"2-digit", month:"2-digit", year:"2-digit" });
            let company = {
                name: "",
                title: "",
                date: "",
                time: "",
                link: "",
                stop: false
            }
            company.date = infoParts[0];
            if (infoParts[0] != today) {
                company.stop = true;
                return company; 
            }
            company.time = infoParts[1].substring(0, 5);
            company.name = infoParts[2];
            for (let infoIndex = 3; infoIndex < infoParts.length; infoIndex++) {
                company.name = company.name + " " + infoParts[infoIndex];
            }
            if (linkElement != null) {
                company.link = linkElement.href;
            }
            return company
        }

        const companies = []; 
        const articleTableBody = document.querySelector("#content div.content_list.news_list table > tbody");
        let articles = articleTableBody.querySelectorAll("tr");
        
        for (let articleIndex = 1; articleIndex <= articles.length; articleIndex++) {
            let linkElement = articleTableBody.querySelector("tr:nth-child(" + articleIndex + ") > td.content_text > a")
            let infosElement = linkElement.querySelector("strong")
            
            if (infosElement != null) {
                let infoParts = infosElement.innerText.split(" ");
                if (isNews(infoParts)) {
                    let company = getCompanyWithValues(infoParts, linkElement)
                    companies.push(company);
                    if(company.stop) break;
                }
            }
        }
        return companies;
    });
}

retrieveTitleForLinks = async function(companies, currentPage) {
    let news = [];
    for (let i = 0; i < companies.length; i++) {
        if (companies[i].stop) {
            companies.pop();
            break;
        } else {
            await currentPage.goto(companies[i].link);
            companies[i].title = await currentPage.evaluate(() => {
                let headBaseSelector = ("#content > div.column.left_col_wide > div.column");
                let head = document.querySelector(headBaseSelector + ".left_col > div > div > h1") || document.querySelector(headBaseSelector + " > div > div > h1");
                let title = head.innerText;
                return { title }
            });
        }
        news.push(companies)
    }
    return news;
}

writeNewsToFile = function(news) {
    var newsJson = JSON.stringify(news[0]);
    var fs = require('fs');
    fs.writeFile("news.json", newsJson, function (err) {
        if (err) throw err;
        console.log('Saved!');
    });
}

crawl();