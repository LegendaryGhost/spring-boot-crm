package site.easy.to.build.crm.util.csv;

import site.easy.to.build.crm.util.csv.exception.CSVException;
import site.easy.to.build.crm.util.csv.parameter.CellCSV;

import java.util.Objects;

public class HeaderCSV {
    String header;
    Integer index;

    CellCSV<?> constraintColumn;

    final DefaultCell DEFAULT=new DefaultCell();

    public HeaderCSV(String header, CellCSV<?> constraintColumn){
        this.header=header;
        this.constraintColumn=constraintColumn;
    }

    Object getValue(String value,int line)throws CSVException{
        return this.constraintColumn.getValue(value,line);
    }

    HeaderCSV(String header, int index){
        this.header=header;
        this.index=index;
        this.constraintColumn=DEFAULT;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Vérifie si c'est la même référence
        if (obj == null || getClass() != obj.getClass()) return false; // Vérifie le type

        HeaderCSV headerCSV = (HeaderCSV) obj;
        return Objects.equals(header, headerCSV.header); // Compare uniquement le champ `name`
    }

    @Override
    public int hashCode() {
        return Objects.hash(header); // Génère un hash basé uniquement sur `name`
    }

    int getIndex(){
        return this.index;
    }

    public class DefaultCell implements CellCSV<String>{

        @Override
        public String getValue(String value,int line) throws CSVException {
            return value;
        }
    }
}