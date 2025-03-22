package site.easy.to.build.crm.util.csv;

import site.easy.to.build.crm.util.csv.exception.KeyNotFoundException;

import java.util.HashMap;

public class LineValue {
    private final HashMap<String,Object> values;

    public LineValue(){
        this.values=new HashMap<>();
    }

    public Object get(String key)throws KeyNotFoundException{
        Object value = this.values.get(key);
        if(value==null){
            throw new KeyNotFoundException(key);
        }
        return value;
    }

    void add(String key,Object value){
        this.values.put(key,value);
    }
}