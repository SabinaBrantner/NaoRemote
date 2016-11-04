package com.nao.sabina.projectnao;

/**
 * Created by Sabina on 26.06.2016.
 */
public class ActionFile {
    private String fileName;
    private byte[] content;
    private byte[] imageBytes;
    private String filePath;
    private String actionName;

    public String getFileName(){
        return this.fileName;
    }

    public byte[] getContent(){
        return this.content;
    }

    public byte[] getImageBytes(){
        return this.imageBytes;
    }

    public String getFilePath(){
        return this.filePath;
    }

    public String getActionName(){
        return this.actionName;
    }
}
