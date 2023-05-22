package example.jakarta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

@Valid
public class JMyMinNumbers {

  @DecimalMin("10.50")
  final BigDecimal price;

  @DecimalMin(value = "9.30", inclusive = false)
  final BigDecimal priceInc;

  public JMyMinNumbers(BigDecimal price, BigDecimal priceInc) {
    this.price = price;
    this.priceInc = priceInc;
  }

  public BigDecimal price() {
    return price;
  }

  public BigDecimal priceInc() {
    return priceInc;
  }

}
