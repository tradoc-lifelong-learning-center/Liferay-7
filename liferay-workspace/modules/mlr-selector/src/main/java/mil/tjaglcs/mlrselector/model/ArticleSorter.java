package mil.tjaglcs.mlrselector.model;

import java.util.ArrayList; 
import java.util.Collections; 

public class ArticleSorter {
	ArrayList<Article> articles = new ArrayList<>();
	
	public ArticleSorter(ArrayList<Article> articles) {         
	    this.articles = articles;     
	  }      
	
  public ArrayList<Article> getSortedIssues() {         
    Collections.sort(articles);         
    return articles;     
  } 
}
