package example.jakarta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Valid
public class JNums {

  @Digits(integer = 5, fraction = 3)
  public String digits = "1234.12";

  @Digits(integer = 4, fraction = 2)
  public BigDecimal digitsDecimal;

  @Positive
  public int positive = 3;

  @PositiveOrZero
  public int positiveOrZero = 0;

  @Negative
  public int negative = -3;

  @NegativeOrZero
  public int negativeOrZero = 0;

  @Max(20)
  public int max = 0;

  @Min(5)
  public int min = 6;

}
