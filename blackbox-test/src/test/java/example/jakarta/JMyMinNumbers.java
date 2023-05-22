package example.jakarta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

@Valid
public class JMyMinNumbers {

  private static final BigDecimal VALID = new BigDecimal("20");

  @DecimalMin("10.50")
  final BigDecimal price;

  @DecimalMin(value = "9.30", inclusive = false)
  final BigDecimal priceInc;

  @DecimalMin("9.50")
  final double dmin;

  @DecimalMin(value = "8.30", inclusive = false)
  final double dminInc;

  public JMyMinNumbers(BigDecimal price, BigDecimal priceInc) {
    this.price = price;
    this.priceInc = priceInc;
    this.dmin = 20;
    this.dminInc = 20;
  }

  public JMyMinNumbers(double dmin, double dminInc) {
    this.price = VALID;
    this.priceInc = VALID;
    this.dmin = dmin;
    this.dminInc = dminInc;
  }

  public BigDecimal price() {
    return price;
  }

  public BigDecimal priceInc() {
    return priceInc;
  }


  public double dmin() {
    return dmin;
  }

  public double dminInc() {
    return dminInc;
  }
}
