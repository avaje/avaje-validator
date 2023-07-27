package example.hibernate;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

@Valid
public class HContact {

  @NotEmpty
  final String name;
  @Positive
  final long score;

  public HContact(String name, long score) {
    this.name = name;
    this.score = score;
  }

  public String name() {
    return name;
  }

  public long score() {
    return score;
  }

}
