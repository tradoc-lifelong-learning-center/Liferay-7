package mil.tjaglcs.mlrselector.model;

import java.util.ArrayList; 
import java.util.Collections; 

public class VolumeSorter {
	ArrayList<Volume> volumes = new ArrayList<>();
	
	public VolumeSorter(ArrayList<Volume> volumes) {         
	    this.volumes = volumes;     
	  }      
	
  public ArrayList<Volume> getSortedIssues() {         
    Collections.sort(volumes);         
    return volumes;     
  } 
}
