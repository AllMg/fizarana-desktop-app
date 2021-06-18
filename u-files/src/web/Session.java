package web;

import java.util.ArrayList;
import java.util.List;

public class Session {
	
	private List<SessionItem> items;
	
	public Session() {
		items = new ArrayList<> (0);
	}
	
	public void add(SessionItem si) {
		if(isIn(si) == null) {
			items.add(si);
		}
	}
	
	public SessionItem isIn(SessionItem sessionItem) {
		for(SessionItem si : items) {
			if(si.getBoundary().compareTo(sessionItem.getBoundary()) == 0 && si.getOrigin().compareTo(sessionItem.getOrigin()) == 0) {
				return si;
			}
		}
		return null;
	}

}
