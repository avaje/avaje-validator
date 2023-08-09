package example.avaje.custom.adapter;

import example.avaje.custom.MyCustomALong;
import io.avaje.validation.adapter.ConstraintAdapter;
import io.avaje.validation.adapter.PrimitiveAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;

@ConstraintAdapter(MyCustomALong.class)
public final class MyCustomALongAdapter extends PrimitiveAdapter<Long> {

  public MyCustomALongAdapter(AdapterCreateRequest request) {
    super(request);
  }

  @Override
  public boolean isValid(long value) {
    return value == 4;
    // assert on primitive long different to object Long to show
    // that we generally need both isValid() method implemented appropriately
  }

  @Override
  protected boolean isValid(Long value) {
    return value == null || value == 3;
  }
}
