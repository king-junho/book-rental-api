package kr.ac.hansung.kjh.bookrental.service.model;

public class Page {
    private final int pageSize = 4;
    private final int rangeSize = 4;

    private final int listCount;
    private final int currentPage;
    private int pageCount;
    private int rangeCount;
    private int currentRange;
    private int startPage;
    private int endPage;
    private int startIndex;
    private int nextPage;
    private int prevPage;

    public Page(int listCount, int currentPage) {
        //데이터 개수, 현재 페이지 수 세팅
        this.listCount = listCount;
        this.currentPage = currentPage;

        setPageCount(listCount);
        setRangeCount(pageCount);

        setRangeSetting(currentPage);

        setStartIndex(currentPage);
    }

    public int getPageSize() {
        return pageSize;
    }

    private void setRangeSetting(int currentPage) {
        this.currentRange = (currentPage - 1) / rangeSize + 1;

        this.startPage = (currentRange - 1) * rangeSize + 1;
        this.endPage = startPage + rangeSize - 1;

        if (endPage > pageCount) {
            endPage = pageCount;
        }

        this.prevPage = currentPage - 1 < 1 ? 1 : currentPage - 1;
        this.nextPage = currentPage + 1 > pageCount ? pageCount : currentPage + 1;
    }

    public int getRangeSize() {
        return rangeSize;
    }

    public int getListCount() {
        return listCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    private void setPageCount(int listCount) {
        this.pageCount = (int) Math.ceil((double) listCount / pageSize);
    }

    public int getRangeCount() {
        return rangeCount;
    }

    private void setRangeCount(int pageCount) {
        this.rangeCount = (int) Math.ceil((double) pageCount / rangeSize);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getCurrentRange() {
        return currentRange;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public int getStartIndex() {
        return startIndex;
    }

    private void setStartIndex(int currentPage) {
        this.startIndex = (currentPage - 1) * pageSize;
    }

    public int getNextPage() {
        return nextPage;
    }

    public int getPrevPage() {
        return prevPage;
    }
}
