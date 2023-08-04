package example.avaje.notblank;

import io.avaje.validation.constraints.*;

import java.util.List;

@Valid
public class ANumberCheck {

  @NotBlank @Max(10) MyNumericType customNumber;

  @Positive int myScore;

  // @PositiveOrZero @Positive @NegativeOrZero @Negative @Max @Min
  /** @Max(45) */  String notNumericTypeHere;


  /** @Positive */ List<?> someCollection;


  public List<?> someCollection() {
    return someCollection;
  }

  public void setSomeCollection(List<?> someCollection) {
    this.someCollection = someCollection;
  }

  public MyNumericType customNumber() {
    return customNumber;
  }

  public void setCustomNumber(MyNumericType customNumber) {
    this.customNumber = customNumber;
  }

  public int myScore() {
    return myScore;
  }

  public void setMyScore(int myScore) {
    this.myScore = myScore;
  }

  public String notNumericTypeHere() {
    return notNumericTypeHere;
  }

  public void setNotNumericTypeHere(String notNumericTypeHere) {
    this.notNumericTypeHere = notNumericTypeHere;
  }
}
