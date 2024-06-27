package resources.database.config;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toList;


public abstract class SQL
{

  private SQL(){}


  public static sealed abstract class Builder permits SelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder
  {
    @Override
    public abstract String toString();
  }


  private static String csv(Collection<String> ts){
    return ts.stream().reduce((t,u) -> t + "," + u).orElse("");
  }


  public static enum Operator {
    LT,
    LTEQ,
    EQ,
    GTEQ,
    GT,
    LIKE,
    IN;

    @Override
    public String toString(){
      switch (this){ 
        case LT:   return "<";
        case LTEQ: return "<=";
        case EQ:   return "=";
        case GTEQ: return ">=";
        case GT:   return ">";
        case LIKE: return "LIKE";
        default:   return null;
      }
    }
  }

  enum Ordering { 
    ASC, DESC
  }


  public static final String ALL = "*";

  private static record Criterion(
    String column,
    Operator op,
    String value
  ){
    @Override
    public String toString(){
      return column + " " + op + " " + value;
    }
  }

  public static final class CriterionBuilder
  {
    private final String column;

    private CriterionBuilder(String column){ 
      this.column = column;
    }

    public Criterion lessThan(Object value){ 
      return new Criterion(column,Operator.LT,valueOf(value));
    }

    public Criterion lessThanOrEqual(Object value){ 
      return new Criterion(column,Operator.LTEQ,valueOf(value));
    }

    public Criterion equal(Object value){ 
      return new Criterion(column,Operator.EQ,valueOf(value));
    }

    public Criterion greaterThanOrEqual(Object value){ 
      return new Criterion(column,Operator.GTEQ,valueOf(value));
    }

    public Criterion greaterThan(Object value){ 
      return new Criterion(column,Operator.GT,valueOf(value));
    }

    public Criterion like(String value){ 
      return new Criterion(column,Operator.LIKE,valueOf(value));
    }

    public <T> Criterion in(Set<T> values){ 
      return new Criterion(
        column,
	Operator.IN,
	values.stream().map(SQL::valueOf).reduce("(",(s,t) -> s + ",") + ")"
      );
    }

  }

  public static CriterionBuilder COLUMN(String column){ 
    return new CriterionBuilder(column);
  }

  public static CriterionBuilder COL(String column){ 
    return new CriterionBuilder(column);
  }


  public static final class SelectBuilder extends Builder
  {

    private String table = null;
    private List<String> columns = null;
    private List<Criterion> criteria = new ArrayList<>();
    private Optional<Map.Entry<List<String>,Ordering>> ordering = Optional.empty();

    private SelectBuilder(List<String> columns){ 
      this.columns = columns;
    }

    public SelectBuilder FROM(String table){ 
      this.table = table;
      return this;
    }


    @SafeVarargs
    public final SelectBuilder WHERE(Criterion... criteria){
      this.criteria.addAll(Stream.of(criteria).collect(toList())); 
      return this;
    }

    public final SelectBuilder WHERE(Criterion criterion){
      this.criteria.add(criterion); 
      return this;
    }

    public SelectBuilder WHERE(String col, Operator op, Object value){
      return switch (op){
        case Operator.IN -> { 
  	  yield this.WHERE(COLUMN(col).in(Set.class.cast(value)));
	}
        default -> {
          yield this.WHERE(new Criterion(col,op,valueOf(value)));
	}
      };

    }

    public SelectBuilder WHERE(String id, Object value){
      return this.WHERE(COLUMN(id).equal(value));
    }

    @SafeVarargs
    public final SelectBuilder ORDER_BY(Ordering ord, String... cols){
      this.ordering = Optional.of(entry(List.of(cols),ord));
      return this;
    }

    @SafeVarargs
    public final SelectBuilder ORDER_BY(String... cols){
      return this.ORDER_BY(Ordering.ASC,cols);
    }


    @Override
    public String toString(){
      var sql =     
        "SELECT " + csv(columns) + " FROM " + table;

      return sql +
        Optional.of(criteria)
	  .filter(cs -> !cs.isEmpty())
	  .map(
            cs -> " WHERE " + cs.stream()
                  .map(Criterion::toString)
                  .reduce((c1,c2) -> c1 + " AND " + c2).get()
          )
          .orElse("") +
       ordering.map(entry -> " ORDER BY " + csv(entry.getKey()) + " " + entry.getValue())
         .orElse("") + ";";

    }

  }


  public static final class InsertBuilder extends Builder {

    private final String table;

    private List<String> columns = new ArrayList<>();
    private List<String> values = new ArrayList<>();


    private InsertBuilder(String table){ 
      this.table = table;
    }	  

    public <T> InsertBuilder VALUE(String col, T t){
      columns.add(col);
      values.add(valueOf(t));
      return this;
    }  

    @SafeVarargs
    public final InsertBuilder VALUES(Map.Entry<String,Object>... columnValues){
      for (Map.Entry<String,Object> cv : columnValues){
        this.VALUE(cv.getKey(),valueOf(cv.getValue()));
      }	      
      return this;
    }  

    @Override
    public String toString(){
      return "INSERT INTO " + table + " (" + csv(columns) + ") VALUES (" + csv(values) + ");";
    }	

  }


  public static final class UpdateBuilder extends Builder {

    private final String table;

    private List<String> updates = new ArrayList<>();
    private List<Criterion> criteria = new ArrayList<>();

    private UpdateBuilder(String table){ 
      this.table = table;
    }	  

    public <T> UpdateBuilder SET(String col, T t){
      updates.add(col + " = " + valueOf(t));
      return this;
    }  

    @SafeVarargs
    public final UpdateBuilder SET(Map.Entry<String,Object>... columnValues){
      for (Map.Entry<String,Object> cv : columnValues){
        this.SET(cv.getKey(),cv.getValue());
      }	      
      return this;
    }  

    @SafeVarargs
    public final UpdateBuilder WHERE(Criterion... criteria){
      this.criteria.addAll(Stream.of(criteria).collect(toList()));
      return this;
    }

    public final UpdateBuilder WHERE(Criterion criterion){
      this.criteria.add(criterion); 
      return this;
    }

    public UpdateBuilder WHERE(String id, Operator op, Object value){
      return this.WHERE(new Criterion(id,op,valueOf(value)));
    }

    public UpdateBuilder WHERE(String id, Object value){
      return this.WHERE(COLUMN(id).equal(value));
    }


    @Override
    public String toString(){

      var sql = "UPDATE " + table + " SET " + csv(updates);

      return sql +
        Optional.of(criteria)
	  .filter(cs -> !cs.isEmpty())
	  .map(
            cs -> " WHERE " + cs.stream()
                  .map(Criterion::toString)
                  .reduce((c1,c2) -> c1 + " AND " + c2).get()
          )
          .orElse("") + ";";

    }	

  }


  public static final class DeleteBuilder extends Builder
  {

    private final String table;
    private List<Criterion> criteria = new ArrayList<>();

    public DeleteBuilder(String table){ 
      this.table = table;
    }


    @SafeVarargs
    public final DeleteBuilder WHERE(Criterion... criteria){
      this.criteria.addAll(Stream.of(criteria).collect(toList())); 
      return this;
    }

    public final DeleteBuilder WHERE(Criterion criterion){
      this.criteria.add(criterion); 
      return this;
    }


    public DeleteBuilder WHERE(String id, Operator op, Object value){
      return this.WHERE(new Criterion(id,op,valueOf(value)));
    }

    public DeleteBuilder WHERE(String id, Object value){
      return this.WHERE(COLUMN(id).equal(value));
    }


    @Override
    public String toString(){

      var sql =     
        "DELETE FROM" + table;

      return sql +
        Optional.of(criteria)
	  .filter(cs -> !cs.isEmpty())
	  .map(
            cs -> " WHERE " + cs.stream()
                  .map(Criterion::toString)
                  .reduce((c1,c2) -> c1 + " AND " + c2).get()
          )
          .orElse("") + ";";

    }

  }


  @SafeVarargs
  public static SelectBuilder SELECT(String... cols){ 
    return new SelectBuilder(
      Stream.of(cols).collect(toList())
    );
  }

  public static InsertBuilder INSERT_INTO(String table){ 
    return new InsertBuilder(table);
  }

  public static UpdateBuilder UPDATE(String table){ 
    return new UpdateBuilder(table);
  }


  public static DeleteBuilder DELETE_FROM(String table){ 
    return new DeleteBuilder(table);
  }




  private static String quoted(String s){
    return String.format("'%s'",s);
  }

  private static String valueOf(Object obj){

    return switch(obj){
      case LocalDate date   -> quoted(Date.valueOf(date).toString());
      case LocalDateTime dt -> quoted(Timestamp.valueOf(dt).toString());
      case Instant t        -> quoted(Timestamp.from(t).toString());
      case Integer n        -> Integer.toString(n);
      case Long n           -> Long.toString(n);
      case Double n         -> Double.toString(n);
      default               -> quoted(obj.toString());

    };

  }


}
