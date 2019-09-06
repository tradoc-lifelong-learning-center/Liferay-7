package mil.tjaglcs.mlrselector.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Issue implements Comparable<Issue>{
	private String publicationName;
	private int number;
	private String name;
	private List<Article> articles;
	private int volume;
	private LocalDate publishDate;
	private int year;
	private int indexNumber;
	private String editionType; //online or PDF edition
	
	
	public Issue(String publicationName, int number, String name, List<Article> articles) {
		this.publicationName = publicationName;
		this.number = number;
		this.name = name;
		this.articles = articles;
		setVolume();
		setYear();
		setPublishDate();
		setIndexNumber();
		setIssueType();
		//System.out.println("building issue " + this.number);
	}
	
	public void setPublishDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
		LocalDate publishDate = LocalDate.parse("1776-07-04", formatter);
		
		//publish date will be the latest date in articles
		for(int i = 0; i<this.articles.size(); i++) {
			Article art = this.articles.get(i);
			
			if(art.getPublishDate().isAfter(publishDate)) {
				publishDate = art.getPublishDate();
			}
			
			
		}
		
		this.publishDate = publishDate;
		
		//System.out.println("issue " + this.getNumber() +  " was published on " + this.publishDate);
		
	}
	
	public LocalDate getPublishDate() {
		return publishDate;
	}
	
	public void setIssueType() {
		String issueType = "Online"; //default
		
		for(int i = 0; i<articles.size(); i++) {
			//if there's a single online, I'm assuming this is or will soon be an online edition
			if(articles.get(i).getType().contains("DLFileEntry")) {
				issueType = "PDF";
			} else if(articles.get(i).getType().contains("JournalArticle")) {
				issueType = "Online";
				break;
			}
		}
		
		this.editionType = issueType;
	}
	
	public String getEditionType() {
		return editionType;
	}

	public void setIndexNumber() {
		
		int num = 0;
		
		if(this.name.toLowerCase().contains("winter")) {
			num = 1;
		} else if(this.name.toLowerCase().contains("spring")) {
			num = 2;
		} else if(this.name.toLowerCase().contains("summer")) {
			num = 3;
		} else if(this.name.toLowerCase().contains("fall")) {
			num = 4;
		} else {
			num = this.number;
		}

		this.indexNumber = num;
	}
	
	
	
	public int getIndexNumber() {
		return indexNumber;
	}

	//allow issues to be sorted by number
	public int compareTo(Issue compareIssue) {
		//ascending
		//return (this.getNumber() < compareIssue.getNumber() ? -1 : 
        //    (this.getNumber() == compareIssue.getNumber() ? 0 : 1));
		
		
		
		//descending
		return (this.getIndexNumber() < compareIssue.getIndexNumber() ? 1 : 
            (this.getIndexNumber() == compareIssue.getIndexNumber() ? 0 : -1)); 
		
	}	
	
	public String getPublicationName() {
		return this.publicationName;
	}
	
		public int getNumber() {
		return number;
	}

	public List<Article> getArticles() {
		Collections.sort(articles);
		return articles;
	}
	
	

	public String getName() {
		return name;
	}

	public void setYear() {
		
		int[] years = new int[articles.size()];
		
		for(int i = 0; i<articles.size(); i++) {
			//System.out.println("article year is " + articles.get(i).getArticleDate().getYear());
			
			try {
				//System.out.println("article year: " + articles.get(i).getArticleDate().getYear());
				years[i] = articles.get(i).getPublishDate().getYear();
			} catch (Exception e) {
				//System.out.println("no date");
				e.printStackTrace();
			}
		}
		
		Average average = new Average(years);
		List<Integer> yearMode = average.getMode();
		
		this.year = yearMode.get(0);
	}
	
	public int getYear() {
		return year;
	}

	public int getVolume() {
		return this.volume;
	}

	private void setVolume(){
		//need to determine the volume based on article list. SHOULD all be the same
		//System.out.println("Set  volume in issue: " + this.articles.get(0).getVolume());
		int volume = this.articles.get(0).getVolume();
		
		
		for(int i = 0; i<this.articles.size(); i++) {
			if(volume==this.articles.get(i).getVolume()) {
				//System.out.println(this.articles.get(i).getTitle() + " in volume " + this.articles.get(i).getVolume() + " with issue " + this.articles.get(i).getIssue());
				continue;
			} else {
				System.out.println("Error: volumes in issue " + this.number + " don't match.");
			}
		}

		this.volume = volume;
	}

}
