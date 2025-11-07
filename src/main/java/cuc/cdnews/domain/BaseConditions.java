package cuc.cdnews.domain;

public class BaseConditions {

	private int pageSize;
	private int currentPage;
	private boolean allowPage;
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public boolean isAllowPage() {
		return allowPage;
	}
	public void setAllowPage(boolean allowPage) {
		this.allowPage = allowPage;
	}
}
