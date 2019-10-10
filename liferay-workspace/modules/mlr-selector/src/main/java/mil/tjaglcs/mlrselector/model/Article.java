package mil.tjaglcs.mlrselector.model;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalContentSearchLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.RenderRequest;

public class Article implements Comparable<Article> {
	private String title;
	private String publicationName;
	private long id;
	private double version;
	private int volume;
	private String volumeName;
	private int issue;
	private String issueName;
	private String type;
	private int status;
	private String url;
	private LocalDate publishDate;
	private RenderRequest request;
	private long groupId;
	private String authors;
	
	
	public Article(String title, String publicationName, long id, double version, int volume, String volumeName, int issue, String issueName, String type, int status, LocalDate publishDate, RenderRequest request, String authors) throws SystemException, PortalException, UnsupportedEncodingException {
		this.request = request;
		this.groupId = this.setGroupId(request);
		this.publicationName = publicationName;
		this.id = id;
		this.version = version;
		this.volume = volume;
		this.volumeName = volumeName;
		this.issue = issue;
		this.issueName = issueName;
		this.type = type;
		this.status = status;
		this.publishDate = publishDate;
		this.authors = authors;
		
		if(type.contains("DLFileEntry")) {
			//this.title  = assemblePdfTitle();
			this.title  = assemblePdfTitle();
		} else { 
			this.title = title;
		}
		
		setURL(request);
	}
	
	//allow volume to be sorted by date
	public int compareTo(Article compareArticle) {
		//ascending
		//return (this.getPublishDate().isBefore(compareArticle.getPublishDate()) ? -1 : 
        //    (this.getPublishDate() == compareArticle.getPublishDate() ? 0 : 1)); 

		//descending
		return (this.getPublishDate().isBefore(compareArticle.getPublishDate()) ? 1 : 
            (this.getPublishDate() == compareArticle.getPublishDate() ? 0 : -1)); 
		
	}	
	
	public String getVolumeName() {
		return volumeName;
	}

	public void setVolumeName(String volumeName) {
		this.volumeName = volumeName;
	}



	private String assemblePdfTitle() {
		String pdfTitle = "";
		
		pdfTitle += "View the PDF";
		
		//pdfTitle += "View the ";
		
		/*if(this.issueName!="") {
			pdfTitle += this.issueName;
			pdfTitle += " Issue ";
		} else {
			pdfTitle += "Issue ";
			pdfTitle += this.issue;
		}*/
		
		//pdfTitle += " PDF";

		return pdfTitle;
	}
	
	public String getTitle() {
		return title;
	}
	public String getPublicationName() {
		return publicationName;
	}
	public long getId() {
		return id;
	}
	public double getVersion() {
		return version;
	}
	public int getVolume() {
		return volume;
	}
	public int getIssue() {
		return issue;
	}
	public String getType() {
		return type;
	}
	public LocalDate getPublishDate() {
		return publishDate;
	}
	public void getPublishDate(LocalDate publishDate) {
		this.publishDate = publishDate;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getURL() {
		return this.url;
	}
	public long getGroupId() {
		return this.groupId;
	}
	
	public String getIssueName() {
		return issueName;
	}
	
	public String getAuthors() {
		return formatAuthors(this.authors);
	}
	
	
	
	
	private String formatAuthors(String authorInputString) {
		//adding "by ", commas, and "and" between author names where needed
				//by Joe Smith
				//by Joe Smith and Bill Smith
				//by Joe Smith, Bill Smith, and Phil Smith
				//by Joe Smith, Bill Smith, Biff Smith, and Phil Smith
		
		String authorOutputString = "";
		
		if(authorInputString==null || authorInputString=="") {
			return null;
		}
		
		authorOutputString += "by ";
		
		String[] authorList = authorInputString.split("\\|");
		
		for(int i = 0; i<authorList.length; i++) {
			
			if(authorList.length-1==i && authorList.length>2) {
				authorOutputString += ", and ";
			} else if(authorList.length-1==i && authorList.length==2) {
				authorOutputString += " and ";
			} else if(authorList.length>1 && i>0) {
				authorOutputString += ", ";
			}
			
			authorOutputString += authorList[i];
			
		}
		
		//System.out.println("author string: " + authorOutputString);
		
		return authorOutputString;
	}
	
	
	public void setURL(RenderRequest request) throws SystemException, PortalException, UnsupportedEncodingException {

		String documentClassName = "DLFileEntry";
		String journalClassName = "JournalArticle";

		if(this.type.contains(journalClassName)) {
			//IF this has a layout (is placed on a page with web content display), use that URL
			//ELSE IF it has a display page configured, use friendly URL
			//ELSE, empty URL
			
			//System.out.println("-------------");
			String friendlyUrl = "";
			long groupId = getGroupId();
			ThemeDisplay themeDisplay = getThemeDisplay(request);
			long articleId = this.getId();
			Locale locale = new Locale("en", "US");
			JournalArticle article = JournalArticleLocalServiceUtil.getArticle(groupId, String.valueOf(this.id));
			Layout articleLayout = article.getLayout(); // <-- this layout is the display page
			List<Long> layoutIds = JournalContentSearchLocalServiceUtil.getLayoutIds(groupId, false, Long.toString(articleId)); // <-- these layout IDs are for pages that contain the article in a web content display portlet
			String layoutUrl = ""; //<--the url for the page that contains article in web content portlet

			//System.out.println("layout: " + articleLayout);
			Boolean hasDisplayPage = false;
			Boolean hasLayoutPage = false;
			
			//System.out.println("articleId: "+ articleId);
			
			//if there's a display page set up, get friendly URL
			if(articleLayout != null) {
				hasDisplayPage = true;
				
				//System.out.println("display page URL: " + articleLayout.getFriendlyURL()); // <--looks like this is the display page URL!
				
				Map<Locale, String> map = article.getFriendlyURLMap();
				//System.out.println("map: " + map);
				
				friendlyUrl = themeDisplay.getURLPortal() + "/-/" + map.get(locale);
				
				//System.out.println("friendlyUrl: " + friendlyUrl);
			}

			//the the content exists on a page in a web content display portlet, get that URL
			if (!layoutIds.isEmpty()) {
				hasLayoutPage = true;
				//System.out.println("layout ids: " + layoutIds);
				  long layoutId = layoutIds.get(0).longValue();
				  Layout layout = LayoutLocalServiceUtil.getLayout(groupId, false, layoutId);
				  String url = PortalUtil.getLayoutURL(layout, themeDisplay);
				  //System.out.println("url: " + url);
				  layoutUrl = url;
				  //System.out.println("layoutUrl: " + layoutUrl);
				}
			
			//then decide which to use
			if (hasLayoutPage) { //if it has a layout page, use that URL
				  this.url = layoutUrl;
				} else if(!hasLayoutPage && hasDisplayPage) {  //if no layout page but has display page, us display page url
					this.url = friendlyUrl;
				} else { //otherwise, no url
					this.url = null;
				}
			
		} else if(this.type.contains(documentClassName)) {
			DLFileEntry entry = DLFileEntryLocalServiceUtil.fetchDLFileEntry(this.id);
			
			
			String fileName = entry.getFileName();
			
			String urlTitle = URLEncoder.encode(fileName, "UTF-8");
			
			long folderId = entry.getFolderId();
			String uuid = entry.getUuid();

			this.url = "/documents/" + this.groupId + "/" + folderId + "/" + urlTitle + "/" + uuid;

		}

		
		
	}
	
	private long setGroupId(RenderRequest req) {
		ThemeDisplay themeDisplay = getThemeDisplay(req);
		long portletGroupId = themeDisplay.getScopeGroupId();
		
		return portletGroupId;
	}
	
	private ThemeDisplay getThemeDisplay(RenderRequest req) {
		return (ThemeDisplay) req.getAttribute(WebKeys.THEME_DISPLAY);
	}
}
