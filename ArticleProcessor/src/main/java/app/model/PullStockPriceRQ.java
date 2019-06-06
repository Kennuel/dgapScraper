package app.model;

import java.util.Date;

public class PullStockPriceRQ {
    private String isin;
    private Date articleId;

    public PullStockPriceRQ(String isin, Date articleId) {
        this.isin = isin;
        this.articleId =articleId;
	}

	public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public Date getArticleId() {
        return articleId;
    }

    public void setArticleId(Date articleId) {
        this.articleId = articleId;
    }

    @Override
    public String toString() {
        return "PullStockPriceRQ [articleId=" + articleId + ", isin=" + isin + "]";
    }
    
}