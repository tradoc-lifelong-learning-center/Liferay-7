package mil.tjaglcs.contentdashboard.portlet;

public class Article {
	private String title;
	private long articleId;
	private String createDate;
	private String modifiedDate;
	private String type;
	
	public Article(String title, long articleId, String createDate, String modifiedDate, String type) {
		this.title = title;
		this.articleId = articleId;
		this.createDate = createDate;
		this.modifiedDate = modifiedDate;
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public long getArticleId() {
		return articleId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}
	
	public String getType() {
		return type;
	}
	
	
	

	
	
	

}
