const puppeteer = require('puppeteer');

let crawl = async () => {
    const browser = await puppeteer.launch({ headless: false });
    const page = await browser.newPage();
    var news = []
    var start = "https://dgap.de/";

    var keepGoing = true;
    var firstSite = true;

    while (keepGoing) {
        await page.goto(start);
        // ----------------------- fetching data -------------------------------------
        let data = await page.evaluate(() => {
            const firms = [];
            var today = new Date()
            var todayString = today.getDate() + "." + today.getMonth() + "." + today.getFullYear(); 
            var time = todayString;

            let articles = document.querySelectorAll("#content > div.column.left_col > div.box.darkblue.content_list.news_list > div.content > table > tbody > tr");

            for (articleIndex = 1; articleIndex <= articles.length; articleIndex++) {
                let firm = {
                    name: "",
                    title: "",
                    date: "",
                    time: "",
                    link: "",
                    stop: false,
                }

                let infos = document.querySelector("#content > div.column.left_col > div.box.darkblue.content_list.news_list > div.content > table > tbody > tr:nth-child(" + articleIndex + ")> td.content_text > a > strong")

                if (infos == null) {
                    continue
                }
                let info = infos.innerText.split(" ");

                if (!info[1].includes("DGAP-News")) {
                    continue
                }
                firm.date = info[0]
                if (info[0] != time) {
                    firm.stop = true;
                    firms.push(firm)
                    break
                }
                firm.time = info[1].substring(0, 5);
                firm.name = info[2]
                for (infoIndex = 3; infoIndex < info.length; infoIndex++) {
                    firm.name = firm.name + " " + info[infoIndex]
                }
                let link = document.querySelector("#content > div.column.left_col > div.box.darkblue.content_list.news_list > div.content > table > tbody > tr:nth-child(" + articleIndex + ") > td.content_text > a")
                if (link != null) {
                    firm.link = link.href;
                }
                firms.push(firm)
            }

            return { firms }
        }); // ---------- end fetching data ----------------

        // ------- get titles ---------------
        for (dataIndex = 0; dataIndex < data.firms.length; dataIndex++) {
            if (data.firms[dataIndex].stop == true) {
                keepGoing = false
                data.firms.pop()
                console.log("here: ")
                break
            } else {
                await page.goto(data.firms[dataIndex].link);
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
                data.firms[dataIndex].title = title;
            }
            news.push(data)
        } //------- end get titles ---------------

        //------- switching sites ------------------
        if (firstSite) {
            console.log("first:")
            await page.goto(start);
            start = await page.evaluate(() => {
                let start1 = ""
                let site = document.querySelector("#content > div.column.left_col > div.box.darkblue.content_list.news_list > div.bottom_link.list_paging > a")
                if (site != null) {
                    start1 = site.href;
                } else {
                    keepGoing = false;
                }
                return start1
            });
            console.log(start)
        } else {
            await page.goto(start);
            start = await page.evaluate(() => {
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

crawl()

