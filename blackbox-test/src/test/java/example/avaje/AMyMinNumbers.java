package example.avaje;

import jakarta.validation.Valid;
import io.avaje.validation.constraints.DecimalMin;

import java.math.BigDecimal;

@Valid
public class AMyMinNumbers {

  private static final BigDecimal VALID = new BigDecimal("20");

  @DecimalMin("10.50")
  final BigDecimal price;

  @DecimalMin(value = "9.30", inclusive = false)
  final BigDecimal priceInc;

  @DecimalMin("9.50")
  final double dmin;

  @DecimalMin(value = "8.30", inclusive = false)
  final double dminInc;

  public AMyMinNumbers(BigDecimal price, BigDecimal priceInc) {
    this.price = price;
    this.priceInc = priceInc;
    this.dmin = 20;
    this.dminInc = 20;
  }

  public AMyMinNumbers(double dmin, double dminInc) {
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
