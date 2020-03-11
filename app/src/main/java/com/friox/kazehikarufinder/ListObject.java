package com.friox.kazehikarufinder;

public class ListObject {
    private String title;
    private String size;
    private int subStatus;
    private String url;
    private int viewType;
    private String fileExtn;
    private String episode;

    public ListObject(String title, String size, int subStatus, String url, int viewType, String fileExtn, String episode) {
        this.title = title;
        this.size = size;
        this.subStatus = subStatus;
        this.url = url;
        this.viewType = viewType;
        this.fileExtn = fileExtn;
    }

    public String getTitle() {
        return title;
    }

    public String getSize() {
        return size;
    }

    public int getSubStatus() {
        return subStatus;
    }

    public String getUrl() {
        return url;
    }

    public int getViewType() {
        return viewType;
    }

    public String getFileExtn() {
        return fileExtn;
    }

    public String getEpisode() {
        return episode;
    }

    public void subOk() {
        this.subStatus = 1;
    }

    public void setEpisode(String string) {
        this.episode = string;
    }
}
