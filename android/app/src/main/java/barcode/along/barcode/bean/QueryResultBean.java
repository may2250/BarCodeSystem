package barcode.along.barcode.bean;

import java.util.ArrayList;

public class QueryResultBean {
    private int resultcnt;
    private int page;
    private ArrayList result;

    public int getResultcnt() {
        return resultcnt;
    }

    public void setResultcnt(int resultcnt) {
        this.resultcnt = resultcnt;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList getResult() {
        return result;
    }

    public void setResult(ArrayList result) {
        this.result = result;
    }
}
