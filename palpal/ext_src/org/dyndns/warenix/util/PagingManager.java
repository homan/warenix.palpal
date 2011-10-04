package org.dyndns.warenix.util;

import java.util.ArrayList;

/**
 * paging manager, page is zero based
 * 
 * @author warenix
 * 
 */
public class PagingManager {

	ArrayList<Object> pagingStack;
	int currentPage;

	PagingManagerDelegate delegate;

	public PagingManager(PagingManagerDelegate delegate) {
		this.delegate = delegate;

		pagingStack = new ArrayList<Object>();
		reset();
	}

	public void loadNextPage(Object nextPage) throws NoNextPageError {
		if (delegate.onLoadNextPage(nextPage)) {
			pagingStack.add(currentPage, nextPage);
			currentPage++;
		}
	}

	public void loadPreviousPage(Object previousPage)
			throws NoPreviousPageError {
		if (delegate.onLoadPreviousPage(previousPage)) {
			--currentPage;
		}
	}

	public void reset() {
		currentPage = 0;
		pagingStack.clear();
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < pagingStack.size(); ++i) {
			Object page = pagingStack.get(i);
			str += page.toString() + " ";
			if (i == currentPage) {
				str += "*";
			}
			str += ">";
		}
		return str;

	}

	public interface PagingManagerDelegate {

		public boolean onLoadNextPage(Object nextPage);

		public boolean onLoadPreviousPage(Object previousPage);
	}

	@SuppressWarnings("serial")
	public class PagingException extends Exception {
	}

	@SuppressWarnings("serial")
	public class NoNextPageError extends PagingException {

	}

	@SuppressWarnings("serial")
	public class NoPreviousPageError extends PagingException {

	}

}
