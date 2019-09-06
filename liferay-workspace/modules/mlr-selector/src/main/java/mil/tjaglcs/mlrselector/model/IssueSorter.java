package mil.tjaglcs.mlrselector.model;


import java.util.ArrayList; 
import java.util.Collections; 

public class IssueSorter {
	ArrayList<Issue> issues = new ArrayList<>();
	
	public IssueSorter(ArrayList<Issue> issues) {         
	    this.issues = issues;     
	  }      
	
  public ArrayList<Issue> getSortedIssues() {         
    Collections.sort(issues);         
    return issues;     
  } 
}
