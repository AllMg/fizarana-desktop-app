package web;

public class SessionItem {
	
	private String origin;
	private String boundary;
	private String fullPath;
	
	public SessionItem () {
		origin = "";
		boundary = "";
		fullPath = "";
	}
	
	public SessionItem (String org, String bound, String fp) {
		this.origin = org;
		this.boundary = bound;
		this.fullPath = fp;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getBoundary() {
		return boundary;
	}

	public void setBoundary(String boundary) {
		this.boundary = boundary;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	
	@Override
	public String toString() {
		return "{ Origin: "+origin+", Boundary: "+boundary+", fullPath: "+fullPath+" }";
	}
	
}
