package mil.tjaglcs.mlrselector.model;


import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelperUtil;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import mil.tjaglcs.mlrselector.search.CustomField;

public class Publication {
	private String name;
	private List<Article> articles;
	private List<Issue> issues;
	private List<Volume> volumes;
	private List<Year> yearsList;
	private RenderRequest request;
	private List<Volume> mostRecentVolumes = new ArrayList<>(); //all volumes in the latest year
	private Volume mostRecentVolume; //the actual latest volume by publish date
	private boolean isPageContainesMostRecent; //if selected volume contains most recent, true, and display most recent label
	private List<Volume> selectedVolumes = new ArrayList<>();
	private String json;
	private boolean isSingleIssue;
	private int startYear;
	private int endYear;
	
	public Publication(String name, RenderRequest request) throws Exception {
		this.request = request;
		this.name = name;
		
		System.out.println("name: " + name);
		
		setArticles(name, request);
		
		System.out.println("articles: " + articles);
		
		//filter volumes by type: if there's both an article and PDF, only show article
		filterArticlePDFs();
		
		setVolumes();
		
		System.out.println("volumes: " + this.volumes);
		
		groupVolumesByYear();

		setMostRecentVolumesByYear();
		setMostRecentVolumeByDate();
		
		
		setIsSingleIssue(request);
		setSelectedContent(this.request);
		this.isPageContainesMostRecent = setIsPageContainesMostRecent();
		setJson();
		//System.out.println("JSON: " + json);
		
		setStartYear();
		setEndYear();
		
	}
	
	public boolean setIsPageContainesMostRecent() {
		for(int i = 0; i<this.selectedVolumes.size(); i++) {
			if(this.selectedVolumes.get(i)==this.mostRecentVolume) {
				return true;
			}
		}
		return false;
	}
	
	
	
	public boolean getIsPageContainesMostRecent() {
		return isPageContainesMostRecent;
	}

	public void groupVolumesByYear() {
		//System.out.println("hash mapping!");
		HashMap<Integer, Year> yearMap = new HashMap<>(); 
		
		//loop volumes and sort into years
		for(int i = 0; i<this.volumes.size(); i++) {
			
			Volume currentVol = this.volumes.get(i);
			int currentYearNumber = currentVol.getYear();
			
		
			if(yearMap.containsKey(currentYearNumber)) {
				Year year = yearMap.get(currentYearNumber);
				year.addVolume(currentVol);
				yearMap.replace(currentYearNumber, year);
			} else {
				List<Volume> volList = new ArrayList<>();
				volList.add(currentVol);
				
				Year currentYearObj = new Year(currentYearNumber, volList);
				
				yearMap.put(currentYearNumber, currentYearObj);
			}
		}

		
		ArrayList<Year> yearArray = new ArrayList<>();

		yearMap.forEach((k,v) -> yearArray.add(v));
		this.yearsList = yearArray;
	}
	
	public List<Year> getYearsList() {
		return yearsList;
	}

	private void filterArticlePDFs() {
		//loop all journal articles, then check all PDF articles and remove any with the same volume/issue
		//long startTime = Calendar.getInstance().getTimeInMillis();
		long articlesLength = this.articles.size();
		
		List<Article> dlFileArticles = new ArrayList<>();
		List<Article> journalArticles = new ArrayList<>();
		List<Long> articlesToFilter = new ArrayList<>();
		
		//split articles into categories
		for(int i = 0; i<articlesLength; i++) {
			
			Article currentArticle = this.articles.get(i);
			
			if(currentArticle.getType().contains("DLFileEntry")) {
				dlFileArticles.add(currentArticle);
			} else {
				journalArticles.add(currentArticle);
			}
		}
		
		//loop journal articles and look for matching PDFs
		for(int i = 0; i<journalArticles.size(); i++) {
			Article currentJournalArticle = journalArticles.get(i);
			int vol = currentJournalArticle.getVolume();
			int issue = currentJournalArticle.getIssue();
			
			for(int z = 0; z<dlFileArticles.size(); z++) {
				Article currentDlFileArticle = dlFileArticles.get(z);
				
				if(currentDlFileArticle.getVolume()==vol && currentDlFileArticle.getIssue()==issue) {
					articlesToFilter.add(currentDlFileArticle.getId());
				}
			}
		}

		
		//remove out duplicate IDs from articlesToFilter list
		List<Long> articlesToFilterConsolidated = articlesToFilter.stream().distinct().collect(Collectors.toList());
		
		List<Article> articlesToRemove = new ArrayList<>();
		
		//build array of duplicate article objects
		for(int i = 0; i<this.articles.size(); i++) {
			Article article = this.articles.get(i);
			
			for(int z = 0; z<articlesToFilterConsolidated.size(); z++) {
				
				//System.out.println("checking article " + article.getId() + " against " + articlesToFilterConsolidated.get(z));
				
				if(article.getId()==articlesToFilterConsolidated.get(z)) {
					//System.out.println("filtering PDF " + this.articles.get(i).getId() + " with title " + this.articles.get(i).getTitle());
					//this.articles.remove(this.articles.get(i));
					articlesToRemove.add(this.articles.get(i));
				}
				
			}
			
			
		}
		
		//remove articles
		for(int i = 0; i<articlesToRemove.size(); i++) {
			this.articles.remove(articlesToRemove.get(i));
		}
		
		//long endTime = Calendar.getInstance().getTimeInMillis();
		
		//System.out.println("Start: " + startTime);
		//System.out.println("End: " + endTime);
        //System.out.println("For each loop: " + (endTime - startTime)); 
		//System.out.println("end: " + this.articles.size());
	}
	
	public void setStartYear() {
		
		List<Integer> years = new ArrayList<>();
		int startYear = 9999;
		
		for(int i = 0; i<this.articles.size(); i++) {
			years.add(this.articles.get(i).getPublishDate().getYear());
		}
		
		for(int i = 0; i<years.size(); i++) {
			if(years.get(i)<startYear) {
				startYear = years.get(i);
			}
		}
		
		this.startYear = startYear;
	}
	
	public void setEndYear() {
		List<Integer> years = new ArrayList<>();
		int endYear = 0;
		
		for(int i = 0; i<this.articles.size(); i++) {
			years.add(this.articles.get(i).getPublishDate().getYear());
		}
		
		for(int i = 0; i<years.size(); i++) {
			if(years.get(i)>endYear) {
				endYear = years.get(i);
			}
		}
		
		this.endYear = endYear;
	}
	
	
	
	
	
	public int getStartYear() {
		return startYear;
	}


	public int getEndYear() {
		return endYear;
	}


	public void setIsSingleIssue(RenderRequest request) {
		PortletPreferences portletPreferences = request.getPreferences();
		//String configValue = GetterUtil.getString(portletPreferences.getValue("numberOfIssues", ""));
		String configValue = "multi";
		
		//System.out.println("configValue: " + configValue);
		
		if(configValue.contains("multi")) {
			//System.out.println("multi issue!");
			this.isSingleIssue = false;
		} else {
			//System.out.println("single issue!");
			this.isSingleIssue = true;
		}
		
	}
	
	public boolean getIsSingleIssue() {
		return this.isSingleIssue;
	}
	
	public String getName() {
		return name;
	}
	public List<Volume> getVolumes() {
		return volumes;
	}

	public List<Volume> getMostRecentVolumes() {
		return mostRecentVolumes;
	}

	public List<Volume> getSelectedVolumes() {
		Collections.sort(selectedVolumes);
		return selectedVolumes;
	}

	
	
	public String getJson() {
		return json;
	}
	
	public void setJson() {
		String JSON = "{\"publication\":{\"name\":\"" + this.name + "\",\"pubCode\":\"mlr\",\"years\":{";
		
		List<Year> years = getYearsList();
		
		//System.out.println("number of years: " + years.size());
		
		for(int y = 0; y<years.size(); y++) {
			Year currentYear = years.get(y);
			
			JSON += "\"" + currentYear.getName() + "\":{";
			JSON += buildVolumeJson(years.get(y));
			JSON += "}";
			
			if(y!=years.size()-1) {
				JSON+=",";
			}
		}
		
		
	
		
		//end of JSON
		JSON +="}}}";
		
		//System.out.println(JSON);
		
		this.json = JSON;
	}
	
	public String buildVolumeJson(Year yearObj) {
		
		String JSON = "";
		List<Volume> volumes = yearObj.getVolumes();
		
		if(volumes.size()<1) {
			return "";
		}
		
		for(int v = 0; v<volumes.size(); v++) {
			int volNo = volumes.get(v).getNumber();
			String volName = volumes.get(v).getName();
			int year = volumes.get(v).getYear();
			
			JSON+="\"volume" + volNo + "\":{\"number\":\"" + volNo + "\",";
			JSON+="\"name\":\"" + volName + "\",";
			JSON+="\"year\":\"" + year + "\"";
			
			JSON += buildIssueJson(volumes.get(v));
			
			//end of volume
			JSON+="}";

			if(v!=volumes.size()-1) {
				JSON+=",";
			}
			
		}

		

		
		//System.out.println("volume json: " + JSON);
		
		return JSON;
	}
	
	private String buildIssueJson(Volume volume) {
		String JSON = "";
		List<Issue> issues = volume.getIssues();
		
		if(issues.size()<1) {
			return "";
		}
		
		JSON+=",\"issues\":{";
		
		for(int i = 0; i<issues.size();  i++) {
			int issueNo = issues.get(i).getNumber();
			String issueName = escapeJSON(issues.get(i).getName());
			
			JSON+="\"issue" + issueNo + "\":{";
			
			JSON+="\"number\":\"" + issueNo + "\",";
			
			JSON+="\"name\":\"" + issueName + "\"";
			
			JSON+="}";
			
			if(i!=issues.size()-1) {
				JSON+=",";
			}
		}
		
		
		JSON+="}";

		
		
		
		return JSON;
		
	}
	
	private String escapeJSON(String str) {
		return str.replaceAll("(\"|\\\\)", "\\\\$1");
	}
	
	public Volume getVolume(int volumeNumber){
		for(int i = 0; i<this.volumes.size(); i++) {
			if(this.volumes.get(i).getNumber()==volumeNumber) {
				return this.volumes.get(i);
			} 
		}
		
		System.out.println("No volume with the number " + volumeNumber);
		return null;
	}
	
	//this should grab the selected content
	public boolean setSelectedContent(RenderRequest request) {

		//System.out.println("setting selected content!");
		
		String volString = this.getQueryStringValue("vol");
		
		//System.out.println("volString: " + volString);
		
		String issueString = this.getQueryStringValue("no");
		
		//System.out.println("issueString: " + issueString);
		
		int volNumber=-1;
		int issueNum = -1;
		
		//System.out.println("getting volume!");
		//if there's no query string and this is in single issue mode, just get most recent volume
		if(volString==null && this.isSingleIssue) {
			//System.out.println("no volume selected by query string. Getting most recent");
			this.selectedVolumes.add(this.mostRecentVolume);
		} else if(volString==null){ //if there's no query string in multi mode, get year's worth of volumes
			this.selectedVolumes = this.mostRecentVolumes;
		} else {
			
			
			try {
				
				String[] volStringArray = volString.split("-");
				
				//System.out.println("len: " + volStringArray.length);
				
				for(int i = 0; i<volStringArray.length; i++) {
					volNumber = Integer.parseInt(volStringArray[i]);
					//System.out.println("trying to get volume " + volNumber);
					
					Volume selectedVolume = getVolume(volNumber);
					
					//System.out.println("got volume " + volNumber + ": " + selectedVolume);
					
					if(selectedVolume==null) {
						SessionErrors.add(request, "no-volume-found");
						//if there's an error with one of the volumes, give up and just display most recent
						
						//this.selectedVolumes.clear();
						this.selectedVolumes = this.mostRecentVolumes;
						
						//System.out.println("nope! no volume " + volNumber);
						
						return false;
					} else {
						this.selectedVolumes.add(selectedVolume);
					}
					
					//System.out.println("this.selectedVolumes length: " + this.selectedVolumes.size());
					
				}
				
				
				
			} catch (NumberFormatException e) {
				
				e.printStackTrace();
				//System.out.println("couldn't get volume number from query string");
				this.selectedVolumes = this.mostRecentVolumes;
				return false;
			}
		}
		
		//System.out.println("getting issue!");
		//System.out.println("issue string: " + issueString);
		//System.out.println("is single issue?: " + this.isSingleIssue);
		
		//Select single issue if:
		//--is in single mode
		//--has issue query string
		//--only one volume in query string
		//default to most recent single issue if:
		//--is in single mode
		//--no issue query string
		if(this.isSingleIssue && (this.selectedVolumes.size()==1)) {
			Volume vol = this.selectedVolumes.get(0);
			
			System.out.println("volume: " + vol);
			
			if(issueString!=null) {
				vol.setSelectedIssue(Integer.parseInt(issueString));
			} else {
				vol.setSelectedIssue(vol.getMostRecentIssue().getNumber());
			}
			
			
		} 

		
		return true;
	}
	

	public void setMostRecentVolumeByVolNumber(){
		int latestVolumeNumber = 0;
		Volume latestVolume = null;
		
		for(int i = 0; i<this.volumes.size(); i++) {
			if(this.volumes.get(i).getNumber()>latestVolumeNumber) {
				latestVolume = this.volumes.get(i);
				latestVolumeNumber = latestVolume.getNumber();
			} 
		}
		
		//System.out.println("Latest volume: " + latestVolumeNumber);
		this.mostRecentVolumes.add(latestVolume);
	}
	
	public void setMostRecentVolumeByDate() {
		//get the most recent volume in the latest year
		Volume latestVolume = null;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
		LocalDate publishDate = LocalDate.parse("1776-07-04", formatter);
		
		for(int i = 0; i<this.mostRecentVolumes.size(); i++) {
			
			Volume vol = this.mostRecentVolumes.get(i);
			
			if(vol.getPublishDate().isAfter(publishDate)) {
				latestVolume = vol;
				publishDate = vol.getPublishDate();
			}
		}
		
		this.mostRecentVolume = latestVolume;
		
	}
	
	
	
	public Volume getMostRecentVolume() {
		return mostRecentVolume;
	}

	public void setMostRecentVolumesByYear(){
		//first, figure out the latest year
		int latestYear = 0;
		Volume latestVolume = null;
		
		List<Volume> latestVolumes = new ArrayList<>();
		
		for(int i = 0; i<this.volumes.size(); i++) {
			Volume vol = this.volumes.get(i);
			if(vol.getYear()>latestYear) {
				latestVolume = vol;
				latestYear = latestVolume.getYear();
			} 
		}
		
		//then, get all volumes in that year
		for(int i = 0; i<this.volumes.size(); i++) {
			Volume vol = this.volumes.get(i);
			if(vol.getYear()==latestYear) {
				latestVolumes.add(vol);
			} 
		}
		
		//sort by pub date
		Collections.sort(latestVolumes);
		
		this.mostRecentVolumes = latestVolumes;

	}
	
		
	public void setVolumes() throws Exception {
			
			HashMap<String, List<Article>> volumeMap = new HashMap<>();
			
			for(int i = 0; i<this.articles.size(); i++) {
				Article currentArticle = this.articles.get(i);
				
				String currentVol = Integer.toString(this.articles.get(i).getVolume());
			
				if (!volumeMap.containsKey(currentVol)) {
				    List<Article> list = new ArrayList<Article>();
				    list.add(currentArticle);

				    volumeMap.put(currentVol, list);
				} else {
					volumeMap.get(currentVol).add(currentArticle);
				}
			}

			
			ArrayList<Volume> volumeArray = new ArrayList<>();
			
			volumeMap.forEach((k,v) -> {
				try {
					//TODO probably need a better way to get volume name than just the first one?
					volumeArray.add(new Volume(this.name, Integer.parseInt(k), v.get(0).getVolumeName(), v));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					System.out.println("NumberFormatException in volumeMap");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception in volumeMap");
				}
			});

			this.volumes = volumeArray;
		}
	
	public List<Issue> getIssues() {
		return this.issues;
	}
	
	
	private String getQueryStringValue(String stringParam) {
		//System.out.print("checking " + stringParam);
		
		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(this.request));
		String queryString = httpReq.getParameter(stringParam);
		//System.out.println(queryString);
		
		return queryString;
	}
	
		
	public List<Article> getArticles() {
		return this.articles;
	}
	
	public void setArticles(String pubName, RenderRequest request) throws Exception {

			HttpServletRequest httpRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			SearchContext searchContext = SearchContextFactory.getInstance(httpRequest);

			//BooleanQuery searchQuery = BooleanQueryFactoryUtil.create(searchContext);
			
			//Query stringQuery = StringQueryFactoryUtil.create("(publicationName: " + pubName + ") AND (status:0) AND ((entryClassName:com.liferay.portlet.journal.model.JournalArticle AND head:true) OR entryClassName:com.liferay.portlet.documentlibrary.model.DLFileEntry)");
			
			
			
			//Query stringQuery = new StringQuery("(publicationName: " + pubName + ") AND (status:0) AND ((entryClassName:com.liferay.portlet.journal.model.JournalArticle AND head:true) OR entryClassName:com.liferay.portlet.documentlibrary.model.DLFileEntry)");
			
			
			//Query stringQuery = new StringQuery("(publicationName: " + pubName + ") AND (status:0) AND ((entryClassName:com.liferay.journal.model.JournalArticle AND head:true))");
			//Query stringQuery = new StringQuery("(publicationName:\"Military Law Review\") AND (status:0) AND ((entryClassName:com.liferay.journal.model.JournalArticle AND head:true))");
			Query stringQuery = new StringQuery("(publicationName:\"Military Law Review\") AND (status:0) AND ((entryClassName:com.liferay.journal.model.JournalArticle AND head:true))");
			//Query stringQuery = new StringQuery("testField: \"Military Law Review Document\"");

			
			BooleanQuery searchQuery = new BooleanQueryImpl();
			
			
			
			searchQuery.add(stringQuery,BooleanClauseOccur.MUST);
			
			//Hits hits = SearchEngineUtil.search(searchContext,searchQuery);
			
			Hits hits = IndexSearcherHelperUtil.search(searchContext,searchQuery);
			
			System.out.println("hits: " + hits.getLength());
			
			List<Document> hitsDocs = hits.toList();
			
			List<Article> articles = new ArrayList<>();
			
			//System.out.println("hitsDocs: " + hitsDocs);
			//System.out.println("hitsDocs.size(): " + hitsDocs.size());
			

			//System.out.println("pub name: " + hitsDocs.get(0).get("title_en_US"));
			//System.out.println("fs: " + hitsDocs.get(0).get("fs"));
			//System.out.println("here ");
			//System.out.println("doc: " + hitsDocs.get(0));
			
			for(int i = 0; i<hitsDocs.size(); i++) {
				
				Document currentDoc = hitsDocs.get(i);
				
				//System.out.println("string: " + currentDoc.getField(Field.TITLE).getValue());
				//System.out.println("currentDoc: " + currentDoc);
				//System.out.println("TITLE: " + currentDoc.getField("TITLE"));
				//System.out.println("Title: " + currentDoc.getField("Title"));
				//System.out.println("title: " + currentDoc.getField("title"));
				
				//System.out.println("fiels: " + currentDoc.getField("title"));
				
				String title = currentDoc.get("title_en_US");
				String type = currentDoc.get("entryClassName");
				long articleId = fetchArticleId(currentDoc,type);
				//TODO: Int, long, double will need some kind of try/catch in case it returns ""
				double version = fetchValidDouble("version", currentDoc);
				int volume = fetchValidInt("publicationVolume", currentDoc);
				String volumeName = currentDoc.get("publicationVolumeName");
				int issue = fetchValidInt("publicationIssue", currentDoc); 
				String issueName = currentDoc.get("publicationIssueName");
				
				LocalDate articleDate = parseDate(currentDoc.get("publicationPublishDate"));
				int status = Integer.parseInt(currentDoc.get("status"));
				String authors = currentDoc.get("publicationAuthors");
				String pdfType = currentDoc.get("publicationPdfType");
				
				
				try {
					LocalDate now = LocalDate.now();
					
					//skip articles with invalid meta, or with publish dates that are sometime in the future
					if(volume==-1 || issue==-1 || articleDate == null) {
						//System.out.println("skipping " + articleId + " due to invalid vol/issue/year");
						continue;
					} else if(pdfType.contains("Article")){
						//System.out.println("Skipping article PDF");
						continue;
					} else if(articleDate.isAfter(now)) {
						//System.out.println("article " + title + " will not be published yet");
						continue;
					}
					Article article = new Article(title, pubName, articleId, version, volume, volumeName, issue, issueName, type, status, articleDate, request, authors);
					articles.add(article);
					
				} catch(Exception e) {
					System.out.println("Couldn't create article object");
					e.printStackTrace();
				}
				
				
				//System.out.println("title: " + title);
				//System.out.println("articleId: " + articleId);
				//System.out.println("version: " + version);
				//System.out.println("volume: " + volume);
				//System.out.println("volumeName: " + volumeName);
				//System.out.println("issue: " + issue);
				//System.out.println("issueName: " + issueName);
				//System.out.println("type: " + type);
				//System.out.println("articleDate: " + articleDate);
				//System.out.println("status: " + status);
				//System.out.println("authors: " + authors);
				//System.out.println("pdfType: " + pdfType);
			}
			
			
			
			System.out.println("made it!");
			this.articles = articles;
		}
	
	public int fetchValidInt(String name, Document currentDoc) {
		
		String value = currentDoc.get(name);
		
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			//e.printStackTrace();
			return -1;
		}
	}
	
	public double fetchValidDouble(String name, Document currentDoc) {
		
		String value = currentDoc.get(name);
		
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			//e.printStackTrace();
			return -1;
		}
	}
	
	/*private Integer fetchIntField(Document currentDoc, String name) {
		int value = Integer.parseInt(currentDoc.get(name));
		
		if(value=="") {
			
		} else {
			
		}
		
		
		
		return value;
	}*/
	
	private Long fetchArticleId(Document currentDoc, String type) {
		Long articleId = (long) -1;
		
		try {
			
			if(type.contains("JournalArticle")) {
				if(currentDoc.get("articleId") != null) {
					//System.out.println("long: " + Long.parseLong(currentDoc.get("articleId")));
					articleId = Long.parseLong(currentDoc.get("articleId"));
				}
			} else if(type.contains("DLFileEntry")) {
				//getting fileEntryId (NOT PK)
				long groupId = Long.parseLong(currentDoc.getField("groupId").getValue());						
				long folderId = Long.parseLong(currentDoc.getField("folderId").getValue());
				String docTitle = currentDoc.getField("title").getValue();
				
				DLFileEntry entry = DLFileEntryLocalServiceUtil.fetchFileEntry(groupId, folderId, docTitle);
			
                articleId = entry.getFileEntryId();
			} 
			
			
			
		} catch(Exception e) {
			System.out.println("article id error");
			e.printStackTrace();
		}
		
		return articleId;
		
	}
	
	private LocalDate parseDate(String dateString) {
		//set a default date which will show for null dates
		LocalDate date = null;
		
		//if this will parse to long, it's epoch
		/*try {
			long fieldValue = Long.parseLong(dateString);
			date = Instant.ofEpochMilli(fieldValue).atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (NumberFormatException e) {
			//e.printStackTrace();
		}
		
		//otherwise, try to parse string
        try {
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
			date = LocalDate.parse(dateString, formatter);
		} catch (Exception e) {
			//e.printStackTrace();
		}*/
        
        try {
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
			date = LocalDate.parse(dateString, formatter);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return date;
	}
}
