package ru.etalon5.draftsman;

import java.util.ArrayList;

public class Request {
	
	private ArrayList<String> mKeys;
	private int mLimit;
	private int mOffset;
	private String mSortBy;
    private String mSortOrder;
    private String mSearchStr;
    private boolean mIsOutstanding;
    private int mSynchronization;

	public Request() {
		initialization(); 
	}
	
	private void initialization() {
		mKeys = new ArrayList<String>();
		mLimit = 0;
		mOffset = 0;
		mSortBy = "";
		mSortOrder = "";
		mSearchStr = "";
		mIsOutstanding = false;
	}
	
	public String getURLString() {
		String result = "";
		if (mKeys.size() > 0)
			result = result + "&key=" + mKeys.get(0);
		if (mLimit > 0)
			result = result + "&limit=" + mLimit;
		if (mOffset > 0)
			result = result + "&offset=" + mOffset;
		if (!mSortBy.isEmpty())
			result = result + "&sortBy=" + mSortBy;		
		if (!mSortOrder.isEmpty())
			result = result + "&sortOrder=" + mSortOrder;		
		if (!mSearchStr.isEmpty())
			result = result + "&search=" + mSearchStr;	
		if (mIsOutstanding)
			result = result + "&status=0";
		if (mSynchronization > 0)
			result = result + "&synchronizationTime=" + mSynchronization;
		
		return result;
	}
	
	public void assign(Request request) {
		setLimit(request.getLimit());
		setOffset(request.getOffset());
		setSortBy(request.getSortBy());
		setSortOrder(request.getSortOrder());
		setSearchStr(request.getSearchStr());
		setIsOutstanding(request.getIsOutstanding());
	}

	public int getLimit() {
		return mLimit;
	}

	public void setLimit(int limit) {
		mLimit = limit;
	}

	public int getOffset() {
		return mOffset;
	}

	public void setOffset(int offset) {
		mOffset = offset;
	}

	public String getSortBy() {
		return mSortBy;
	}

	public void setSortBy(String sortBy) {
		mSortBy = sortBy;
	}

	public String getSortOrder() {
		return mSortOrder;
	}

	public void setSortOrder(String sortOrder) {
		mSortOrder = sortOrder;
	}

	public String getSearchStr() {
		return mSearchStr;
	}

	public void setSearchStr(String searchStr) {
		mSearchStr = searchStr;
	}
	
	public boolean getIsOutstanding() {
		return mIsOutstanding;
	}
	
	public void setIsOutstanding(boolean isOutstanding) {
		mIsOutstanding = isOutstanding;
	}

	public int getSynchronization() {
		return mSynchronization;
	}
	
	public void setSynchronization(int synchronization) {
		mSynchronization = synchronization;
	}
}
