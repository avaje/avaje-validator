package example.avaje.notblank;

import io.avaje.validation.constraints.*;

import java.util.List;

@Valid
public class ANumberCheck {

  @Max(10) MyNumericType customNumber;

  @Positive int myScore;

  // @PositiveOrZero @Positive @NegativeOrZero @Negative @Max @Min
  /** @Max(45) */  String notNumericTypeHere;


   /** @Positive */  List<?> someCollection;

   int foo;
   @AssertTrue boolean active;

   @AssertTrue Boolean objectActive;

   //@AssertTrue
   String stringNotBoolean;

   @AssertTrue
   public boolean methodValidation() {
     return active;
   }

  public boolean active() {
    return active;
  }

  public ANumberCheck setActive(boolean active) {
    this.active = active;
    return this;
  }

  public Boolean objectActive() {
    return objectActive;
  }

  public ANumberCheck setObjectActive(Boolean objectActive) {
    this.objectActive = objectActive;
    return this;
  }

  public String stringNotBoolean() {
    return stringNotBoolean;
  }

  public ANumberCheck setStringNotBoolean(String stringNotBoolean) {
    this.stringNotBoolean = stringNotBoolean;
    return this;
  }

  public List<?> someCollection() {
    return someCollection;
  }

  public ANumberCheck setSomeCollection(List<?> someCollection) {
    this.someCollection = someCollection;
    return this;
  }

  public MyNumericType customNumber() {
    return customNumber;
  }

  public ANumberCheck setCustomNumber(MyNumericType customNumber) {
    this.customNumber = customNumber;
    return this;
  }

  public int myScore() {
    return myScore;
  }

  public ANumberCheck setMyScore(int myScore) {
    this.myScore = myScore;
    return this;
  }

  public String notNumericTypeHere() {
    return notNumericTypeHere;
  }

  public ANumberCheck setNotNumericTypeHere(String notNumericTypeHere) {
    this.notNumericTypeHere = notNumericTypeHere;
    return this;
  }
}
