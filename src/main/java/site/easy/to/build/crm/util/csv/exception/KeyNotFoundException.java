package site.easy.to.build.crm.util.csv.exception;

public class KeyNotFoundException extends RuntimeException{
    public KeyNotFoundException(String key){
        super(key+" Not found on the CSV");
    }
}