package mil.tjaglcs.mlrselector.model;


import java.util.ArrayList;
import java.util.List;

public class Year {
	private int name;
	private List<Volume> volumes;

	public Year(int name) {
		this.name = name;
		this.volumes = new ArrayList<>();
	}
	
	public Year(int name, List<Volume> volumes) {
		this.name = name;
		this.volumes = volumes;
	}

	public List<Volume> getVolumes() {
		return volumes;
	}

	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
	}
	
	public int getName() {
		return name;
	}

	public void setName(int name) {
		this.name = name;
	}

	public void addVolume(Volume vol) {
		this.volumes.add(vol);
	}
	
	
	
}
