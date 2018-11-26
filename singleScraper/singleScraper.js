const puppeteer = require('puppeteer');

const config = {
    baseUrl : "https://dgap.de/"
}

let getSingleCompanyInfos = async () => {
    const browser = await puppeteer.launch({ headless: false, devtools: true});
    const page = await browser.newPage();
    await page.goto(config.baseUrl);
    let company = await fetchLatestCompany(page);
    company.title = await retrieveTitleForCompany(company, page);
    await page.goto(company.link);
    company = await page.evaluate((company) => {
        let headerNodes = document.querySelector("#content ul").querySelectorAll("li");
        company.wkn = headerNodes[0].textContent.trim().substring(5);
        company.isin = headerNodes[1].textContent.trim().substring(6);
        company.articleText = document.querySelector("#content .break-word.news_main").textContent;
        return company;
    }, company);
    writeCompanyToFile(company)
    browser.close();

};

fetchLatestCompany = async function(page) {
    console.log("Start fetching values for most recent company...");
    return await page.evaluate(() => {
        // function declaration -  functions cannot be passed to page.evaluate without hack https://stackoverflow.com/a/52176714
        const getCompanyWithValues = function(infoParts, linkElement) {
            let company = {};
            company.date = infoParts[0];
            company.time = infoParts[1].substring(0, 5);
            company.articleType = infoParts[1].substring(11, infoParts[1].length-1);
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

retrieveTitleForCompany = async function(company, currentPage) {
    await currentPage.goto(company.link);
    return await currentPage.evaluate(() => {
        let headBaseSelector = ("#content > div.column.left_col_wide > div.column");
        let head = document.querySelector(headBaseSelector + ".left_col > div > div > h1") || document.querySelector(headBaseSelector + " > div > div > h1");
        return head.innerText;
    });
}

writeCompanyToFile = function(company) {
    var companyJson = JSON.stringify(company);
    var fs = require('fs');
    fs.writeFile(new Date().toLocaleDateString() + "-" + company.name + ".json", companyJson, function (err) {
        if (err) throw err;
        console.log('Saved!');
    });
}

getSingleCompanyInfos();