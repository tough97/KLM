package cs.designer.swing.bean;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/9/12
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class PageBean implements CoBean {

    private int currentPage = 0;
    private int pageSize = 5;
    private int dataSize;


    public PageBean() {
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void last() {
        if (hasLast()) {
            currentPage--;
        }
    }

    public void next() {
        if (hasNext()) {
            currentPage++;
        }
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String createDescription() {
        return "";
    }

    public String getCode() {
        return "";
    }

    public String getName() {
        return "[" + currentPage + "]";
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public boolean hasNext() {
        return (currentPage + 1) <= getMaxPageNumber();
    }

    public boolean hasLast() {
        return (currentPage - 1) >= 0;
    }

    public int getMaxPageNumber() {
        int maxPageNumber = dataSize % pageSize == 0 ?
                dataSize / pageSize : (dataSize / pageSize) + 1;
        return maxPageNumber - 1;
    }

    public void reset() {
        currentPage = 0;
        dataSize = 0;

    }
}
