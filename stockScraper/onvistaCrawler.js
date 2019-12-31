const puppeteer = require('puppeteer');
const express = require('express')
const app = express()

const config = {
    baseUrl: "http://onvista.de/aktien/"
}

const port = 3000;

app.listen(port, () => {
    console.log("********************************************************");
    console.log("I am awake, listening on " + port)
    console.log("********************************************************");
});

app.get('/', (req, res) => {
    console.log("I understood to search for: " + req.query.isin)
    if (req.query.isin) {
        getPriceForIsin(req.query.isin, res);
    }
})


let getPriceForIsin = async (isin, res) => {
    console.info("Checking for stockprice..." + isin)
    const browser = await puppeteer.launch(
        { headless: true, devtools: false, args: ['--no-sandbox', '--disable-setuid-sandbox'] }
    );
    const page = await browser.newPage();
    page.goto(config.baseUrl + isin).finally(()=> browser.close());
    page.waitForSelector(".KURSDATEN li span")
    .then(x => {
        page.evaluate(() => Number(document.querySelector(".KURSDATEN li span").innerHTML.replace(",", ".")), this)
            .then(price => {
                res.send({ "price": price });

            })
            .catch(x => { 
                console.log(x); res.send({ "price": null });
            });
    });
};

