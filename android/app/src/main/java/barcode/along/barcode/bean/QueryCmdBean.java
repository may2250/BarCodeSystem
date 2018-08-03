package barcode.along.barcode.bean;

public class QueryCmdBean {
    private int querycmd;
    private int page = 1;
    private int number = 1;
    private String data;

    public int getQuerycmd() {
        return querycmd;
    }

    public void setQuerycmd(int querycmd) {
        this.querycmd = querycmd;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
