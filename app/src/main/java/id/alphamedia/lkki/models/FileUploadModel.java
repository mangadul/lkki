package id.alphamedia.lkki.models;


public class FileUploadModel {

    private String filename;
    private String fileabs;

    public void setFilename(String filename){
        this.filename = filename;
    }

    public String getFilename(){
        return filename;
    }

    public void setFileabs(String fileabs){
        this.fileabs = fileabs;
    }

    public String getFileabs(){
        return fileabs;
    }
}
