package mil.tjaglcs.mlrselector.search;

public class FieldToIndex {
	private String fieldValue;
	private String fieldNameString ="";
	private int fieldNameInt = -1;
	
	
	public FieldToIndex(String expandoName, String fieldNameString) {
		this.fieldValue = expandoName;
		this.fieldNameString = fieldNameString;
	}
	
	public FieldToIndex(String expandoName, int fieldNameInt) {
		this.fieldValue = expandoName;
		this.fieldNameInt = fieldNameInt;
	}
	

	public String getFieldName() {
		
		if(this.fieldNameInt>0) {
			return Integer.toString(this.fieldNameInt);
		} else {
			return fieldNameString;
		}
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public String getFieldNameString() {
		return fieldNameString;
	}

	public int getFieldNameInt() {
		return fieldNameInt;
	}


	
	
	
}
