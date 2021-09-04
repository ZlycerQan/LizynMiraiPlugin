package org.zlycerqan.mirai.lizyn.services.score.connection.model.scoremodel;

import java.util.List;

public class QueryModel {

    private int currentPage;

    private int currentResult;

    private boolean entityOrField;

    private int limit;

    private int offset;

    private int pageNo;

    private int pageSize;

    private int showCount;

    private List<String> sorts;

    private int totalCount;

    private int totalPage;

    private int totalResult;

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentResult(int currentResult) {
        this.currentResult = currentResult;
    }

    public int getCurrentResult() {
        return currentResult;
    }

    public void setEntityOrField(boolean entityOrField) {
        this.entityOrField = entityOrField;
    }

    public boolean getEntityOrField() {
        return entityOrField;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }

    public int getShowCount() {
        return showCount;
    }

    public void setSorts(List<String> sorts) {
        this.sorts = sorts;
    }

    public List<String> getSorts() {
        return sorts;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }

    public int getTotalResult() {
        return totalResult;
    }
}
