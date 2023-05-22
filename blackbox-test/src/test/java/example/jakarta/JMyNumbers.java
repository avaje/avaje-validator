package example.jakarta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

@Valid
public class JMyNumbers {

  @DecimalMax("10.50")
  final BigDecimal price;

  @DecimalMax(value = "9.30", inclusive = false)
  final BigDecimal priceInc;

  @Digits(integer = 5, fraction = 3)
  final String someDigits;

  public JMyNumbers(BigDecimal price, BigDecimal priceInc, String someDigits) {
    this.price = price;
    this.priceInc = priceInc;
    this.someDigits = someDigits;
  }

  public BigDecimal price() {
    return price;
  }

  public BigDecimal priceInc() {
    return priceInc;
  }

  public String someDigits() {
    return someDigits;
  }
}
