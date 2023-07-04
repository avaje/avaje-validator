package io.avaje.validation.generator.models.valid;

import io.avaje.validation.Valid;
import io.avaje.validation.constraints.DecimalMax;

import java.math.BigDecimal;

@Valid
public class AMyNumbers {

  @DecimalMax("10.50")
  final BigDecimal price;

  @DecimalMax(value = "9.30", inclusive = false)
  final BigDecimal priceInc;

  @DecimalMax("9.50")
  final double dprice;

  @DecimalMax(value = "8.30", inclusive = false)
  final double dpriceInc;

  public AMyNumbers(BigDecimal price, BigDecimal priceInc) {
    this.price = price;
    this.priceInc = priceInc;
    this.dprice = 1d;
    this.dpriceInc = 1d;
  }

  public AMyNumbers(double dprice, double dpriceInc) {
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
