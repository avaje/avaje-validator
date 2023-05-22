package example.jakarta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;

import java.math.BigDecimal;

@Valid
public class JMyNumbers {

  @DecimalMax("10.50")
  final BigDecimal price;

  @DecimalMax(value = "9.30", inclusive = false)
  final BigDecimal priceInc;

  @DecimalMax("9.50")
  final double dprice;

  @DecimalMax(value = "8.30", inclusive = false)
  final double dpriceInc;

  public JMyNumbers(BigDecimal price, BigDecimal priceInc) {
    this.price = price;
    this.priceInc = priceInc;
    this.dprice = 1d;
    this.dpriceInc = 1d;
  }

  public JMyNumbers(double dprice, double dpriceInc) {
    this.price = BigDecimal.ONE;
    this.priceInc = BigDecimal.ONE;
    this.dprice = dprice;
    this.dpriceInc = dpriceInc;
  }

  public BigDecimal price() {
    return price;
  }

  public BigDecimal priceInc() {
    return priceInc;
  }

  public double dprice() {
    return dprice;
  }

  public double dpriceInc() {
    return dpriceInc;
  }
}
