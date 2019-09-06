package mil.tjaglcs.mlrselector.model;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalContentSearchLocalServiceUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

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
			//System.out.println("Journal!");

			//trying to find parent page URL. from https://stackoverflow.com/questions/8397679/get-portlet-page-containing-web-content-in-liferay
			long groupId = getGroupId();
			
			//System.out.println(groupId);
			
			ThemeDisplay themeDisplay = getThemeDisplay(request);

			long articleId = this.getId();

			
			List<Long> layoutIds = JournalContentSearchLocalServiceUtil.getLayoutIds(groupId, false, Long.toString(articleId));
			
			if (!layoutIds.isEmpty()) {
				  long layoutId = layoutIds.get(0).longValue();
				  Layout layout = LayoutLocalServiceUtil.getLayout(groupId, false, layoutId);
				  String url = PortalUtil.getLayoutURL(layout, themeDisplay);
				  //String url = PortalUtil.getLayoutFriendlyURL(layout, themeDisplay);
				  //System.out.println("url: " + url);
				  this.url = url;
				}
			
		} else if(this.type.contains(documentClassName)) {
			DLFileEntry entry = DLFileEntryLocalServiceUtil.fetchDLFileEntry(this.id);
			
			String urlTitle = URLEncoder.encode(this.title, "UTF-8");
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
