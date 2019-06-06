package app.repository;


import java.util.Date;

import org.springframework.data.repository.CrudRepository;

import app.model.Article;

public interface ArticleRepository extends CrudRepository<Article, Date> {
    
}
