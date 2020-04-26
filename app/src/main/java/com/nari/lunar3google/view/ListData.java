package com.nari.lunar3google.view;

public class ListData {

    private String id;
    private String subject;
    private String base_date;
    private String name;
    private String mobilno;
    private int sync_stat;

    public ListData(String id, String subject, String base_date,
                    String name, String mobilno, int sync_stat) {
        this.id = id;
        this.subject = subject;
        this.base_date = base_date;
        this.name = name;
        this.mobilno = mobilno;
        this.sync_stat = sync_stat;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getBase_date() {
        return base_date;
    }

    public String getName() {
        return name;
    }

    public String getMobilno() {
        return mobilno;
    }

    public int getSync_stat() {
        return sync_stat;
    }
}
