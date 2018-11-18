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
        let data = await fetchCompanys(page);

        // ------- get titles ---------------
        for (dataIndex = 0; dataIndex < data.companies.length; dataIndex++) {
            if (data.companies[dataIndex].stop == true) {
                keepGoing = false
                data.companies.pop()
                console.log("here: ")
                break
            } else {
                await page.goto(data.companies[dataIndex].link);
                let title = await page.evaluate(() => {
                    let head = document.querySelector("#content > div.column.left_col_wide > div.column.left_col > div > div > h1")
                    var title = "";
                    if (head == null) {
                        let headHelp = document.querySelector("#content > div.column.left_col_wide > div.column > div > div > h1")
                        if (headHelp != null) {
                            title = headHelp.innerText;
                        }
                    } else {
                        title = head.innerText;
                    }
                    return { title }
                });
                data.companies[dataIndex].title = title;
            }
            news.push(data)
        } //------- end get titles ---------------

        //------- switching sites ------------------
        if (firstSite) {
            console.log("first:")
            await page.goto(config.baseUrl);
            config.baseUrl = await page.evaluate(() => {
                let start1 = ""
                let site = document.querySelector("#content > div.column.left_col > div.box.darkblue.content_list.news_list > div.bottom_link.list_paging > a")
                if (site != null) {
                    start1 = site.href;
                } else {
                    keepGoing = false;
                }
                return start1
            });
            console.log(config.baseUrl)
        } else {
            await page.goto(config.baseUrl);
            config.baseUrl = await page.evaluate(() => {
                console.log("second")
                let start1 = ""
                let s = document.querySelector("#content > div.column.left_col > div.box.darkblue.content_list.news_list > div.bottom_link.list_paging > div.list_next > a")
                if (s != null) {
                    start1 = s.href;
                } else {
                    keepGoing = false;
                }
                console.log(start1)
                return start1
            });
        }//------- end switching sites ------------------
        firstSite = false;
    } // ----- END WHILE

    writeNewsToFile(news)
    browser.close();
};

fetchCompanys = async function(page) {
    console.log("Start fetching articles...");
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

        debugger;
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
        return { companies };
    });
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