package example.avaje.notblank;

public final class MyNumericType extends Number {

  private final double value;

  private MyNumericType(double value) {
    this.value = value;
  }

  public static MyNumericType of(double value) {
    return new MyNumericType(value);
  }

  @Override
  public int intValue() {
    return Double.valueOf(value).intValue();
  }

  @Override
  public long longValue() {
    return Double.valueOf(value).longValue();
  }

  @Override
  public float floatValue() {
    return Double.valueOf(value).floatValue();
  }

  @Override
  public double doubleValue() {
    return value;
  }
}
