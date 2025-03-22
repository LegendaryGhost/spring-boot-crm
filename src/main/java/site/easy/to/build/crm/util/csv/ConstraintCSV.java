package site.easy.to.build.crm.util.csv;

import site.easy.to.build.crm.util.csv.exception.CSVException;
import site.easy.to.build.crm.util.csv.parameter.CellCSV;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class ConstraintCSV {

    public static final LongPositive LONG_POSITIVE=new LongPositive();

    public static final IntPositive INT_POSITIVE=new IntPositive();

    public static final DoublePositive DOUBLE_POSITIVE=new DoublePositive();

    public static final LocalDateConstraint LOCALDATE=new LocalDateConstraint();

    public static final ListForeign LIST_FOREIGN = new ListForeign();

    public static final TimeConstraint LOCAL_TIME = new TimeConstraint();

    public static final LocalDateTimeConstraint LOCALDATE_TIME = new LocalDateTimeConstraint();

    public static class LongPositive implements CellCSV<Long>{

        @Override
        public Long getValue(String cell,int line) throws CSVException {
            try{
                Long value=Long.parseLong(cell);
                if(value<0){
                    throw new CSVException("Valeur négative sur la ligne "+line);
                }
                return value;
            }
            catch (NumberFormatException ex){
                throw new CSVException(ex.getMessage()+" sur la ligne "+line);
            }
        }
    }

    public static class IntPositive implements CellCSV<Integer>{

        @Override
        public Integer getValue(String cell,int line) throws CSVException {
            try{
                Integer value=Integer.parseInt(cell);
                if(value<0){
                    throw new CSVException("Valeur négative sur la ligne "+line);
                }
                return value;
            }
            catch (NumberFormatException ex){
                throw new CSVException(ex.getMessage()+" sur la ligne "+line);
            }
        }
    }

    public static class TimeConstraint implements CellCSV<LocalTime>{

        @Override
        public LocalTime getValue(String cell,int line) throws CSVException {
            try{
                LocalTime value=LocalTime.parse(cell);
                return value;
            }
            catch (Exception ex){
                throw new CSVException(ex.getMessage()+" sur la ligne "+line);
            }
        }
    }

    public static class LocalDateTimeConstraint implements CellCSV<LocalDateTime>{

        @Override
        public LocalDateTime getValue(String cell,int line) throws CSVException {
            try{
                LocalDateTime value=LocalDateTime.parse(cell);
                return value;
            }
            catch (Exception ex){
                throw new CSVException(ex.getMessage()+" sur la ligne "+line);
            }
        }
    }

    public static class DoublePositive implements CellCSV<Double>{

        @Override
        public Double getValue(String cell,int line) throws CSVException {
            try{
                Double value=Double.parseDouble(cell);
                if(value<0){
                    throw new CSVException("Valeur négative sur la ligne "+line);
                }
                return value;
            }
            catch (NumberFormatException ex){
                throw new CSVException(ex.getMessage()+" sur la ligne "+line);
            }
        }
    }

    public static class LocalDateConstraint implements CellCSV<LocalDate>{

        @Override
        public LocalDate getValue(String cell,int line) throws CSVException {
            try{
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate value=LocalDate.parse(cell,formatter);
                return value;
            }
            catch (Exception ex){
                throw new CSVException(ex.getMessage()+" sur la ligne "+line);
            }
        }
    }

    public static class ListForeign implements CellCSV<List<String>>{

        @Override
        public List<String> getValue(String cell,int line) throws CSVException {
            try{
                List<String> value=new ArrayList<>();
                String[] foreignKeySplitted=cell.split(";");
                for(String foreignKey:foreignKeySplitted){
                    value.add(foreignKey.replace(" ",""));
                }
                return value;
            }
            catch (DateTimeException ex){
                throw new CSVException(ex.getMessage()+" sur la ligne "+line);
            }
        }
    }
}