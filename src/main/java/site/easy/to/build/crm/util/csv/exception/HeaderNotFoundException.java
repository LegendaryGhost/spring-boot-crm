package site.easy.to.build.crm.util.csv.exception;

public class HeaderNotFoundException extends RuntimeException{
    public HeaderNotFoundException(String headerName){
        super("Header "+headerName+" not found");
    }
}