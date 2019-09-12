<%@ include file="/init.jsp" %>

<jsp:useBean id="mlr" class="mil.tjaglcs.mlrselector.portlet.MlrSelectorPortlet"/>

<c:catch var ="catchException">
	<c:set var="pubData" value="${mlr.fetchPublication(renderRequest) }" />
	<c:set var="selectedVolumes" value="${pubData.getSelectedVolumes() }" />

	<c:set var="isSingleIssue" value="${pubData.getIsSingleIssue() }" />
</c:catch>

<c:choose>

	<c:when test = "${catchException != null}">
		<div class="alert alert-info">No articles found.</div>
		
	</c:when>
	
	<c:otherwise>
	
	<div class="mlr-selector">
	
	
		<aui:form cssClass="mlr-selector-form">
		
		<c:if test="${pubData.getIsPageContainesMostRecent()==true }"><p class="most-recent-label">Most Recent Issue</p></c:if>
		
		
		<div id="noUiSlider"  class="noUi--slider-outer-container">
			<!-- TODO get values from bean -->
			<span class="noUi--year-label" id="noUiSliderMin">${pubData.getStartYear() }</span>
			<div class="noUi--slider-inner-container">
				<div id="noUiSliderRange" data-min-year="${pubData.getStartYear() }" data-max-year="${pubData.getEndYear() }"></div>
			</div>
			
			<span class="noUi--year-label" id="noUiSliderMax">${pubData.getEndYear() }</span>
		</div>
			
	    <aui:fieldset cssClass="selector-fieldset">
	
	        <aui:select label="" id="volumeOptions" name="volume" showEmptyOption="false" cssClass="dropdown" helpMessage="Select a volume.">
	
				<aui:option value="selectAVolume">Select a volume</aui:option>
				
				<%-- populated by JSON from Java bean --%>
			    
				
	        </aui:select>
	        
	        <c:if test="${isSingleIssue }">
		        <aui:select label="" id="issueOptions" name="issue" showEmptyOption="false" cssClass="dropdown" helpMessage="Select an issue." disabled="true">
		
					<aui:option value="selectAnIssue">Select an issue</aui:option>
				    <%-- populated by JSON from Java bean --%>
					
		        </aui:select>
	        </c:if>
	
			
			
			<!--<aui:button value=" > " id="btnSubmit" cssClass="btn btn-primary" aria-label="Submit"/>-->
	
	        
	    </aui:fieldset>
	    
	</aui:form>
	
	<liferay-ui:error key="no-volume-found" message="no-volume-found"/>
	<liferay-ui:error key="no-issue-found" message="no-issue-found"/>
	
	<p class="year-label"><c:out value="${selectedVolumes.get(0).getYear() }"/></p>
	
	<div class="volumes-container">
	
	<c:forEach items="${selectedVolumes}" var = "currentVolume" varStatus="i">
	
		<c:choose>
			<c:when test="${currentVolume.getName() != ''}">
				<c:set var="volumeLabel" value="${currentVolume.getName() } Volume"/>
			</c:when>
			<c:otherwise>
				<c:set var="volumeLabel" value="Volume ${currentVolume.getNumber() }"/>
			</c:otherwise>
		</c:choose>
	
	
		<section class="volume-container">
		
		<%--<h2 id="volume${currentVolume.getNumber() }"><c:out value="${volumeLabel }"/></h2> --%>
		

		<c:forEach items="${currentVolume.getSelectedIssues() }" var = "currentIssue" varStatus="i">
			<c:set var="issue" value="${currentVolume.getIssue(currentIssue) }" />

			<c:choose>
				<c:when test="${issue.getName() != ''}">
					<c:set var="issueLabel" value="${issue.getName() } Issue"/>
				</c:when>
				<c:otherwise>
					<c:set var="issueLabel" value="Issue ${issue.getNumber() }"/>
				</c:otherwise>
			</c:choose>
		
				

				 <nav class="table-of-contents-container" aria-labelledby="volume${currentVolume.getNumber() }">
				 	<h2 id="volume${currentVolume.getNumber() }"><c:out value="${volumeLabel }"/>, <c:out value="${issueLabel }"/></h2>
				 	
				 	<c:if test="${currentVolume.getEditionType()=='Online' }">
				 		<p class="edition-label">Online Edition</p>
				 		<hr/>
				 	</c:if>
				 	
				 	<%--<h3><c:out value="${issueLabel }"/></h3>--%>
					 <c:forEach items="${issue.getArticles()}" var = "article" varStatus="i">
					 
					<c:choose>
						<c:when test="${article.getType().contains('DLFileEntry') }">
							<c:set var="tocEntryClass" value="toc-entry toc-entry--pdf"/>
						</c:when>
						<c:otherwise>
							<c:set var="tocEntryClass" value="toc-entry"/>
						</c:otherwise>
					</c:choose>

					 	<p class="${tocEntryClass }">
					 		<c:choose>
						 		<c:when test="${article.getURL()==null }">
						 			<c:out value="${article.getTitle() }"/>
						 		</c:when>
						 		<c:otherwise>
						 			<a href="${article.getURL() }" target="_blank"><c:out value="${article.getTitle() }"/></a>
						 		</c:otherwise>
					 		</c:choose>
					 	
					 	</p>
					 	<p class="author-names">${article.getAuthors() }</p>
					 </c:forEach>
				 </nav>
				 
			
			
			 
			    	
		</c:forEach>
	</section>

	
	</c:forEach>
	</div>
	


	<aui:script use="aui-base, event, node">
	    (function(){
	    
	    	var config = {
	        		'namespace': '<portlet:namespace/>',
	                'jsonData': ${pubData.getJson() },
	                'volumeDropdown':document.getElementById('<portlet:namespace/>' + 'volumeOptions'),
	                'issueDropdown':document.getElementById('<portlet:namespace/>' + 'issueOptions'),
	                'isSingleIssue':${isSingleIssue }
	        } 
	        		
	        //console.log(config.jsonData)
	        		
	        //build slider and populate volume menu		
	    	buildSlider();		
	    	
	    	//bind event handlers
       		if(config.isSingleIssue){		
   	        	
   		        config.volumeDropdown.addEventListener('change', function(event){
   		        	getIssues();
   		         });
   		        
   		        config.issueDropdown.addEventListener('change', function(event){
   		        	navigate();
   		         });
   		        
   	        } else if(!config.isSingleIssue){
   	        	config.volumeDropdown.addEventListener('change', function(event){
   	        		navigate();
   		         }); 
   	        	
   	        }
	        
	        function getIssues(){
	        	var volumeDropdown = config.volumeDropdown;
	        	var issueDropdown = config.issueDropdown;
	        	var jsonData = ${pubData.getJson() };
				
	        	if(volumeDropdown.value=="selectAVolume"){
	        		disableMenu(issueDropdown);
	        		issueDropdown.value="selectAnIssue"; 
	        		return false;
	        	}
	        	
	        	issueDropdown.removeAttribute("disabled");
	        	
	        	var yearStr = volumeDropdown.value.split(":")[0];
	        	var yearObj = jsonData.publication.years[yearStr];

	        	populateIssueMenu(issueDropdown, yearObj);
	
	        } 
	        
	        
	        function clearMenu(menu){
	        	//clear existing options, skipping the first
				while (menu.children.length > 1) {
					menu.removeChild(menu.lastChild);
				}
	        }
	        
	        function populateIssueMenu(menu,yearObj){
	        	
	        	clearMenu(menu);
	        	
	        	var fragment = document.createDocumentFragment();
	        	var optionArray = [];
	        	
	        	//loop volumes in year
	        	//if single volume, add its issues to menu
	        	//if 2+, and opt groups and add child issues
	        	
	        	var volCount = 0;
	        	
	        	for(var vol in yearObj){
	        		volCount++;
	        	}
	        	
	        	//sorter function (descending)
	        	var sortByValue = function(a, b) {
	        		return parseInt(b.value) - parseInt(a.value);
	            }
	        	
	        	for(var vol in yearObj){
	        		
	        		var optionGroup = volCount==1 ? document.createDocumentFragment() : document.createElement("optgroup");
	        		
	        		if(volCount>1){
	        			if(yearObj[vol].name!=""){
	        				optionGroup.setAttribute("label", yearObj[vol].name);
	        			} else {
	        				optionGroup.setAttribute("label", "Volume " + yearObj[vol].number);	
	        			}
	        			
	        		}
	        		
	        		var optionArray = [];
	        		
        			for(issue in yearObj[vol].issues){
        				var currentIssue = yearObj[vol].issues[issue];
        				
        				var option = document.createElement("option");
        				
        				//display name if present
        				if(currentIssue.name!=""){
        					option.innerHTML = currentIssue.name + " Issue";
        				} else{
        					option.innerHTML = "Issue " + currentIssue.number;
        				}
        				
        				//but always use number as value
        				//also include volume number so navigate can grab the correct vol
        				var issueVal = yearObj[vol].number + ":" + currentIssue.number;
        				
        				option.setAttribute("value",issueVal);
        				optionArray.push(option);
        			}
        			optionArray.sort(sortByValue);
					
    	        	for(var i = 0; i < optionArray.length; i++){
    	        		optionGroup.appendChild(optionArray[i]);
    	        	}
    	        	
					
    	        	fragment.appendChild(optionGroup);
	        	}
	        	
	        	menu.appendChild(fragment);
	        }
	        
	        function populateVolumeMenu(menu, items, startYear, endYear){
	        	console.log("populating volume menu!");
	        	
	        	if(!startYear){
	        		startYear=0;
	        	}
	        	
	        	if(!endYear){
	        		endYear=9999;
	        	}
	        	
	        	//if single issue, clear sub (issue) menu
	        	clearMenu(menu);	
	        	
	        	var fragment = document.createDocumentFragment();
	        	var optionArray = [];
	        	
	        	for(var prop in items){
	        		
	        		if(parseInt(prop)<startYear || parseInt(prop)>endYear){
	        			continue;
	        		}
	        		
	        		var option = document.createElement("option");

        			var volNumArray = buildVolNumArray(items[prop]);
        			var volNameArray = buildVolNameArray(items[prop]);
        			
        			var volLable = volNumArray>1 ? " (volume " : " (volumes "
        			var volString = volLable + volNameArray.join(", ") + ")";

        			var optionString = prop + volString;

	        		
	        		option.innerHTML = optionString;
	        		option.setAttribute("value",prop + ":" + volNumArray.join("-"));
	        		optionArray.push(option);

	            }
	        	
	        	//sort and add to fragment
	        	var sortByValue = function(a, b) {
	        		return parseInt(b.value) - parseInt(a.value);
	            }

	        	optionArray.sort(sortByValue);

	        	for(var i = 0; i<optionArray.length; i++){
	        		fragment.appendChild(optionArray[i]);
	        	}
	        	
	        	menu.appendChild(fragment);
	        } 
	        
	        function buildVolNameArray(yearJson){
		        
	        	var volArray = [];
	        	
	        	for(var vol in yearJson){
	        		
	        		if(yearJson[vol].name!=""){
	        			volArray.push(yearJson[vol].name);
	        		} else {
	        			volArray.push(yearJson[vol].number);
	        		}
	        		
	        			
    			}
	        	
	        	//return volArray.join("-");
	        	return volArray.sort();
	        }
	        
	        function buildVolNumArray(yearJson){
	        
	        	var volArray = [];
	        	
	        	for(var vol in yearJson){
	        		volArray.push(yearJson[vol].number);	
    			}
	        	
	        	//return volArray.join("-");
	        	return volArray.sort();
	        }
	        
	        function navigate(){
	        	var jsonData = ${pubData.getJson() };
	        	var volumeDropdown = config.volumeDropdown;
	        	
	        	if(config.isSingleIssue){		
	        		var issueDropdown = config.issueDropdown;
	        		} else{
	        		var issueDropdown = null;
	        		}
	        	
	        	
	        	//var pubCode = jsonData.publication.pubCode;
	        	var volumeNumber = volumeDropdown.value.split(":")[1];
	        	
	        	//for single issue, use vol/issue number from issue dropdown
	        	if(config.isSingleIssue){		
	        		volumeNumber = issueDropdown.value.split(":")[0];
	        		var issueNumber = issueDropdown.value.split(":")[1];
	        		} else{
	        			var issueNumber=-1;
	        		}
	        	
	        	
	        	var queryString = getQueryString(volumeNumber,issueNumber);
	        	
	         	if(volumeDropdown.value=="selectAVolume" || (issueDropdown && issueDropdown.value=="selectAnIssue")) {
	             	return false;
	             } else {
	            	var baseUrl = window.location.href.split('#')[0];
	             	var url = baseUrl.split('?')[0] + queryString;
	             	//console.log("navigating to " + url);
	             }
	
	             window.location.href = url;
	        }
	        
	        
	        
	        function getQueryString(volumeNumber,issueNumber){
	            
	        	var queryString = "";
	        	queryString+="?vol=" + volumeNumber;
	        	if(issueNumber>0){
	        	queryString+= "&no=" + issueNumber;
	        	}
	        	
	        	return queryString;
	        }
	        
	        
	        function buildSlider() {
	        	var instance = this;
	        	
	        	var slider = document.querySelector('#noUiSliderRange');
	        	var min = parseInt(slider.dataset.minYear);
	        	var max = parseInt(slider.dataset.maxYear);
	        	
	        	var minInput = document.querySelector('#noUiSliderMin');
	        	var maxInput = document.querySelector('#noUiSliderMax');
	        	
	        	console.log("min: " + min);
	        	console.log("max: " + max);
	        	
	        	//slider can't handle it when there's just one year
	        	if(min==max){
	        		return false;
	        	}
	        	
	        	noUiSlider.create(slider, {
	        	    start: [min, max],
	        	    connect: true,
	        	    padding:0,
	        	    step:1,
	        	    range: {
	        	        'min': min,
	        	        'max': max
	        	    },
	        	    format:{
	        	    	to: function(value){
	        	    		return parseInt(value);
	        	    	},
	        	    	from: function(value){
	        	    		return parseInt(value);
	        	    	},
	        	    }
	        	});
		
	        	slider.noUiSlider.on('update', function( values, handle ) {
	        		minInput.innerHTML = values[0];
	        		maxInput.innerHTML = values[1];
	        		
	        		//re-populate volume selector, clear and disable issue selector
	        		//populateMenu(config.volumeDropdown, config.jsonData.publication.volumes, "Volume", values[0],values[1]);
	        		populateVolumeMenu(config.volumeDropdown, config.jsonData.publication.years, values[0],values[1]);
	        		
	        		if(config.isSingleIssue){
	        			clearMenu(config.issueDropdown);
	        			disableMenu(config.issueDropdown);
	            	}
	        		
	        		
	        	});
	        	
	
	        		
	        }
	        
	        function disableMenu(menu){
	        	menu.setAttribute("disabled", "disabled");
	        }
		

			        
	    })();
	 	
	    
	
	    
	
	</aui:script>
	
	</div>
	
	</c:otherwise>

</c:choose>
