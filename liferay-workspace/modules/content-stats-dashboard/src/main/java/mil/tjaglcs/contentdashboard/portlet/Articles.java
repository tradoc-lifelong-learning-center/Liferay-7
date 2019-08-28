package mil.tjaglcs.contentdashboard.portlet;

import java.util.ArrayList;
import java.util.List;

public class Articles {
	private List<Article> articles;
	
	

	public Articles() {
		this.articles = new ArrayList<Article>();
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void addArticle(Article article) {
		this.articles.add(article);
	}
	
	
	
}
