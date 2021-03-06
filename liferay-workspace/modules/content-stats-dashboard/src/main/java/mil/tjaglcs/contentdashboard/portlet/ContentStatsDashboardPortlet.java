package mil.tjaglcs.contentdashboard.portlet;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelperUtil;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

import mil.tjaglcs.contentdashboard.constants.ContentStatsDashboardPortletKeys;

/**
 * @author Jag
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=ContentStatsDashboard",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + ContentStatsDashboardPortletKeys.CONTENTSTATSDASHBOARD,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class ContentStatsDashboardPortlet extends MVCPortlet {
	
	private Articles articles;
	
	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
	    throws PortletException, IOException {

		renderRequest.setAttribute("message", test());
		
		this.articles = new Articles();
		try {
			searchArticles(renderRequest);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		renderRequest.setAttribute("entries", this.articles);


	    super.render(renderRequest, renderResponse);
	}
	
	public void searchArticles(RenderRequest request) throws PortalException {
				
		//List<String> entries = new ArrayList<String>();
		
		//HttpServletRequest httpRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
		//SearchContext searchContext = SearchContextFactory.getInstance(httpRequest);
		//BooleanQuery searchQuery = BooleanQueryFactoryUtil.create(searchContext);
		//Query stringQuery = StringQueryFactoryUtil.create("(status:0) AND ((entryClassName:com.liferay.portlet.journal.model.JournalArticle AND head:true) OR entryClassName:com.liferay.portlet.documentlibrary.model.DLFileEntry)");
				//List<JournalArticle> articles = JournalArticleLocalServiceUtil.getArticles(20123);
		
		//System.out.println("count: " + JournalArticleLocalServiceUtil.getArticlesCount(20123));
		//System.out.println(articles.get(0));
		
		//JournalArticle article = JournalArticleLocalServiceUtil.fetchArticle(45013);
		//JournalArticleLocalServiceUtil.getArticle(45013);
		
		//System.out.println(article);
		
		//for(int i = 0; i<articles.size(); i++) {
			//System.out.println(articles.get(i));
		//}
		
		
		HttpServletRequest httpRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
		SearchContext searchContext = SearchContextFactory.getInstance(httpRequest);
		
		//System.out.println("searchContext: " + searchContext);
		

		//Query stringQuery = new StringQuery("(status:0 AND entryClassName: com.liferay.journal.model.JournalArticle AND head: true) OR entryClassName: com.liferay.document.library.kernel.model.DLFileEntry");
		//On current LR7.2 setups, documents aren't working. Just finding journals for now.
		Query stringQuery = new StringQuery("(status:0 AND entryClassName: com.liferay.journal.model.JournalArticle AND head: true)");

		
		BooleanQuery searchQuery = new BooleanQueryImpl();
		
		//System.out.println("searchQuery: " + searchQuery);
		
		searchQuery.add(stringQuery,BooleanClauseOccur.MUST);
		
		//System.out.println("searchQuery: " + searchQuery);

		Hits hits = IndexSearcherHelperUtil.search(searchContext,searchQuery);
		
		//System.out.println("hits: " + hits.getLength());
		
		List<Document> hitsDocs = hits.toList();
		
		for(int i = 0; i<hitsDocs.size(); i++) {
			Document currentDoc = hitsDocs.get(i);
			
			//System.out.println("working on doc " + i);
			//System.out.println("doc: " + currentDoc);
			//System.out.println("fields: " + currentDoc.getFields());
			
			String title = "";
			
			long articleId = 0;
			String createDate = "";
			String modifiedDate = "";
			String type = "";
			
			try {
				if(currentDoc.getField(Field.TITLE) != null) {
					//System.out.println("string: " + currentDoc.getField(Field.TITLE).getValue());
					title = currentDoc.getField(Field.TITLE).getValue();
					//System.out.println("title: " + title);
				} else if(currentDoc.get("title_en_US") != null) {
					title = currentDoc.get("title_en_US");
					//System.out.println("title (local): " + title);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			try {
				if(currentDoc.getField(Field.ENTRY_CLASS_NAME) != null) {
					String typeString = currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue();
					//System.out.println("ENTRY_CLASS_NAME: " + currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
					
					if(typeString.contains("JournalArticle")) {
						type = "Web Content Article";
					} else if(typeString.contains("DLFileEntry")){
						type = "Document";
					} else {
						type="undefined";
					}
					
					//type = currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				if(currentDoc.getField(Field.CREATE_DATE) != null) {
					//System.out.println("CREATE_DATE: " + currentDoc.getField(Field.CREATE_DATE).getValue());
					createDate = formatDate(currentDoc.getField(Field.CREATE_DATE).getValue());
				}
				
				if(currentDoc.getField(Field.MODIFIED_DATE) != null) {
					//System.out.println("MODIFIED_DATE: " + currentDoc.getField(Field.MODIFIED_DATE).getValue());
					modifiedDate = formatDate(currentDoc.getField(Field.MODIFIED_DATE).getValue());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				if(type.contains("Web Content Article")) {
					if(currentDoc.getField(Field.ARTICLE_ID) != null) {
						//System.out.println("ARTICLE_ID: " + currentDoc.getField(Field.ARTICLE_ID).getValue());
						articleId = Long.parseLong(currentDoc.getField(Field.ARTICLE_ID).getValue());
					}
				} else if(type.contains("Document")) {
					long groupId = Long.parseLong(currentDoc.getField("groupId").getValue());						
					long folderId = Long.parseLong(currentDoc.getField("folderId").getValue());
					String docTitle = currentDoc.getField("title").getValue();
					
					DLFileEntry entry = DLFileEntryLocalServiceUtil.fetchFileEntry(groupId, folderId, docTitle);
				
				    articleId = entry.getFileEntryId();
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			Article article = new Article(title, articleId, createDate, modifiedDate, type);
			
			this.articles.addArticle(article);
		}
		

	}
	
	public String formatDate(String inputDateString) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.US);
		LocalDate date1 = LocalDate.parse(inputDateString, formatter);
		
		//System.out.println(date1.toString());

		return date1.toString();
	}
	
	public String test() {
		return "hello world!";
	}
}