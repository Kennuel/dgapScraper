const puppeteer = require('puppeteer');

const config = {
    baseUrl : "https://dgap.de/"
}

let crawl = async () => {
    const browser = await puppeteer.launch({ headless: false });
    const page = await browser.newPage();
    var news = [];

    var keepGoing = true;
    var firstSite = true;

    while (keepGoing) {
        await page.goto(config.baseUrl);
        let data = await page.evaluate(fetchData);

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

    var newsJson = JSON.stringify(news[0]);

    var fs = require('fs');
    fs.writeFile("news.json", newsJson, function (err) {
        if (err) throw err;
        console.log('Saved!');
    });

    browser.close();
};

let fetchData = () => {
    const companies = []; 
    var time = new Date().toLocaleDateString("de-DE");
    const articleTableBody = document.querySelector("#content div.content_list.news_list table > tbody");
    let articles = articleTableBody.querySelectorAll("tr");

    for (let articleIndex = 1; articleIndex <= articles.length; articleIndex++) {
        let company = {
            name: "",
            title: "",
            date: "",
            time: "",
            link: "",
            stop: false
        }

        let link = articleTableBody.querySelector("tr:nth-child(" + articleIndex + ") > td.content_text > a")
        let infos = link.querySelector("strong")

        if (infos == null) {
            continue
        }
        let info = infos.innerText.split(" ");

        if (!info[1].includes("DGAP-News")) {
            continue
        }
        company.date = info[0]
        if (info[0] != time) {
            company.stop = true;
            companies.push(company)
            break
        }
        company.time = info[1].substring(0, 5);
        company.name = info[2]
        for (infoIndex = 3; infoIndex < info.length; infoIndex++) {
            company.name = company.name + " " + info[infoIndex]
        }
        if (link != null) {
            company.link = link.href;
        }
        companies.push(company)
    }

    return { companies }
}
crawl()

