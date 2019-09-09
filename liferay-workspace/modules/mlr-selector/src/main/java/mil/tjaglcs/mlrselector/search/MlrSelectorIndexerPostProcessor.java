/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mil.tjaglcs.mlrselector.search;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;

import java.util.List;
import java.util.Locale;
import org.osgi.service.component.annotations.Component;

@Component(
	immediate = true,
	property = {
		"indexer.class.name=com.liferay.journal.model.JournalArticle",
		"indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry"
	},
	service = IndexerPostProcessor.class
)
public class MlrSelectorIndexerPostProcessor implements IndexerPostProcessor {

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter booleanFilter, SearchContext searchContext)
		throws Exception {
		
		//System.out.println("postProcessContextBooleanFilter");

		if (_log.isInfoEnabled()) {
			_log.info("postProcessContextBooleanFilter");
		}
	}

	/*@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("postProcessContextQuery");
		}
	}*/

	@Override
	public void postProcessDocument(Document document, Object object)
		throws Exception {
		
		String objectClass = object.getClass().toString();
		
		//System.out.println("postProcessDocument. working with " + objectClass);
		
		
		String documentClassName = "DLFileEntryImpl";
		String journalClassName = "JournalArticleImpl";
		
		if(objectClass.contains(journalClassName)) {
			//System.out.println("class name: " + objectClass);
			//System.out.println("Journal!");
			objectClass = journalClassName;
		} else if(objectClass.contains(documentClassName)) {
			//System.out.println("class name: " + objectClass);
			//System.out.println("Document!");
			objectClass = documentClassName;
		}
		
		//document.addText("testField", "testFieldValue");
		
		//System.out.println(document.get("testField"));
		
		indexDocument(document, object, objectClass);
		
		/*if(objectClass == "DLFileEntryImpl") {
			document.addText("testField", "Military law Review Document");
		} else if(objectClass == "JournalArticleImpl"){
			document.addText("testField", "Military law Review Journal");
		} else {
			document.addText("testField", "Military law Review Other");
		}*/

		if (_log.isInfoEnabled()) {
			_log.info("postProcessDocument");
		}
	}
	
	private void indexDocument(Document document, Object object, String className) throws NumberFormatException, PortalException, SystemException {
		
		FieldToIndex[] fieldsToIndex = new FieldToIndex[] {
			new FieldToIndex("publicationName",CustomField.PUBLICATION_NAME),
			new FieldToIndex("publicationSubtitle",CustomField.PUBLICATION_SUBTITLE),
			new FieldToIndex("publicationVolume",CustomField.PUBLICATION_VOLUME),
			new FieldToIndex("publicationVolumeName",CustomField.PUBLICATION_VOLUME_NAME),
			new FieldToIndex("publicationIssue",CustomField.PUBLICATION_ISSUE),
			new FieldToIndex("publicationIssueName",CustomField.PUBLICATION_ISSUE_NAME),
			new FieldToIndex("publicationAuthors",CustomField.PUBLICATION_AUTHORS),
			new FieldToIndex("publicationPublishDate",CustomField.PUBLICATION_DATE),
			new FieldToIndex("publicationPdfType",CustomField.PUBLICATION_PDF_TYPE)
		};
		
		for(int i = 0; i<fieldsToIndex.length; i++) {
			
			String fieldVal;

			
			//temporarily disable documents
			/*if(className == "DLFileEntryImpl") {
				fieldVal = getDLFileMeta(object, fieldsToIndex[i].getFieldValue());

			} else {
				//System.out.println("indexing journal");
				fieldVal = getJournalArticleMeta(object, fieldsToIndex[i].getFieldValue());
			}*/
			
			fieldVal = getJournalArticleMeta(object, fieldsToIndex[i].getFieldValue());
			
			////
			
			String fieldName = fieldsToIndex[i].getFieldName();
			
			System.out.println("fieldName: " + fieldName);
			System.out.println("fieldVal: " + fieldVal);
			 
			if(fieldVal.length() > 0 && fieldName.length()>0) {
	        	document.addText(fieldName, fieldVal);
	        	
	        }
			
			
		}
	}


	private String getJournalArticleMeta(Object object, String fieldName) {
		JournalArticle article = (JournalArticle) object;
		
		System.out.println("article: " + article.getArticleId());
		
		//don't need a PDF type for journals
		if(fieldName=="publicationPdfType") {
			return "";
		}
		
		try {
			String xmlContent = article.getContent();
			com.liferay.portal.kernel.xml.Document document = SAXReaderUtil.read(xmlContent);
	
			//System.out.println("document: " + document);
			
			//get all nodes with field name (all in case it's a repeatable field like author
			List<Node> nodes = document.selectNodes("/root/dynamic-element[@name='"  + fieldName + "']/dynamic-content");
			
			//System.out.println("nodes: " + nodes);
			
			if(nodes.size()>0) {
				String str = "";
	
				for(int i = 0; i<nodes.size(); i++) {
					//System.out.println(nodes.get(i).getText());
					if(i>0) {
						str += "|";
					}
					
					str += nodes.get(i).getText();
					
				}
				
				System.out.println("str: " + str);
				return str;
			} else {
				return "";
			}
	
			
		} catch(Exception e) {
			System.out.println("journal pub index error");
			System.out.println(e);
			return "";
		}
	}

	@Override
	public void postProcessFullQuery(
			BooleanQuery fullQuery, SearchContext searchContext)
		throws Exception {
		
		//System.out.println("postProcessFullQuery");

		if (_log.isInfoEnabled()) {
			_log.info("postProcessFullQuery");
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter booleanFilter,
			SearchContext searchContext)
		throws Exception {
		
		//System.out.println("postProcessSearchQuery");

		if (_log.isInfoEnabled()) {
			_log.info("postProcessSearchQuery");
		}
	}

	/*@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("postProcessSearchQuery");
		}
	}*/

	@Override
	public void postProcessSummary(
		Summary summary, Document document, Locale locale, String snippet) {
		
		//System.out.println("postProcessSummary");

		if (_log.isInfoEnabled()) {
			_log.info("postProcessSummary");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MlrSelectorIndexerPostProcessor.class);

}