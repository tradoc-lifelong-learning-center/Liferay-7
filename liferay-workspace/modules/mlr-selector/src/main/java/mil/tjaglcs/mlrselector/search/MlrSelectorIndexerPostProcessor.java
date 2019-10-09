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
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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

			//System.out.println("fieldsToIndex[i]: " + fieldsToIndex[i]);
			
			//temporarily disable documents
			if(className == "DLFileEntryImpl") {
				fieldVal = getDLFileMeta(object, fieldsToIndex[i].getFieldValue());

			} else {
				//System.out.println("indexing journal");
				fieldVal = getJournalArticleMeta(object, fieldsToIndex[i].getFieldValue());
			}
			
			//fieldVal = getJournalArticleMeta(object, fieldsToIndex[i].getFieldValue());
			
			////
			
			String fieldName = fieldsToIndex[i].getFieldName();
			
			if(className == "DLFileEntryImpl") {
				//System.out.println("fieldName: " + fieldName);
				//System.out.println("fieldVal: " + fieldVal);
			}
			 
			if(fieldVal.length() > 0 && fieldName.length()>0) {
	        	document.addText(fieldName, fieldVal);
	        	
	        }
			
			
		}
	}
	
	private String getDLFileMeta(Object object, String fieldName) throws PortalException, SystemException {
		DLFileEntry article = (DLFileEntry) object;
		String fieldVal = "";
		
		System.out.println("--------------------");
		System.out.println("processing field");
		
		//System.out.println(article);
		
		//Map<String,Field> fieldMap = article.getFieldsMap(article.getFileVersion().getFileVersionId());
		
		//System.out.println("fieldMap: " + fieldMap);
		
		System.out.println("field name: " + fieldName);
		System.out.println("article ID: " + article.getFileEntryId());
		System.out.println("article title: " + article.getTitle());
		//System.out.println("article PK: " + article.getPrimaryKey());
		//System.out.println("article expando: " + article.getExpandoBridge().getAttributes());
		//System.out.println("article expando: " + article.getExpandoBridge().getAttributes().keySet());
		//System.out.println("article title: " + article.get);
		
		Map<String, DDMFormValues> formValues = article.getDDMFormValuesMap(article.getFileVersion().getFileVersionId());
		
		
		for (Entry<String, DDMFormValues> entry : formValues.entrySet()) {  
			System.out.println("entry: " + entry.getValue().getDDMFormFieldValues());
			List<DDMFormFieldValue> list = entry.getValue().getDDMFormFieldValues();
			//System.out.println(list.get(0).toString());
			System.out.println("name: " + list.get(0).getName());
			Locale locale = new Locale("en_us");
			
			//finally found the values. Is this all of them? How do I get by field name?
			//or can I return them all in key value pairs?
			System.out.println("value: " + list.get(0).getValue().getString(locale)); 
			
		}
		
		/*System.out.println("formValues: " + formValues);
		System.out.println("formValues values: " + formValues.values().toArray()[0].toString());
		System.out.println("formValues entrySet: " + formValues.entrySet());
		System.out.println("formValues values: " + formValues.values());
		System.out.println("formValues keySet: " + formValues.keySet());*/

		//TODO: figure out reliable way to get these values. Not sure where this key comes from.
		/*System.out.println("formValues.get(): " + formValues.get("AUTO_5E6FAE31-2DF2-0D87-75A8-1B64F1ACE0CD"));
		
		DDMFormValues vals = formValues.get("AUTO_5E6FAE31-2DF2-0D87-75A8-1B64F1ACE0CD");
		
		System.out.println("vals: " + vals);
		
		if(vals!=null) {
			System.out.println("vals map: " + vals.getDDMFormFieldValuesMap());
		} else {
			System.out.println("vals map is null");
		}*/
		
		return "";
		
		/*try {
			Map<String,Field> fieldMap = article.getFieldsMap(article.getFileVersion().getFileVersionId());
			
			for (Map.Entry<String,Fields> entry : fieldMap.entrySet()) {  
	            //if(entry.getValue().get(fieldName).getValue()!=null) {
				
				if(entry.getValue().get(fieldName)!=null) {

	            	if(fieldName=="publicationAuthors") {
	            		//author names need to come back as an array in order to handle multiple names
	            		String[] authorsArray = (String[]) entry.getValue().get(fieldName).getValue();
	            		String authorsString = "";

	            		for(int i=0; i<authorsArray.length; i++) {
	            			//System.out.println(i + ": " + authorsArray[i]);
	            			if(i>0) {
	            				authorsString+="|";
	            			}
	            			authorsString+=authorsArray[i];
	            			
	            		}
	            		
	            		fieldVal = authorsString;
	            	} else {
		            	fieldVal = (String) entry.getValue().get(fieldName).getValue().toString();
	            	}
	            	
	            	
	            	
	            }
	            
	            
	            
	    	} 
			
			return fieldVal;	
			
		} catch(Exception e) {
			System.out.println("dlfileentry index error");
			System.out.println(e);
			//e.printStackTrace();
			return "";
		}*/
	}


	private String getJournalArticleMeta(Object object, String fieldName) {
		JournalArticle article = (JournalArticle) object;
		
		//System.out.println("article: " + article.getArticleId());
		
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
				
				//System.out.println("str: " + str);
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