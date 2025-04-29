package io.avaje.validation.generator.models.valid.pkg_private;

import java.util.List;

import javax.validation.constraints.Negative;
import javax.validation.constraints.NotEmpty;

import org.jspecify.annotations.Nullable;

import io.avaje.validation.constraints.Pattern;
import io.avaje.validation.constraints.RegexFlag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Valid
class PackagePrivateTestClass {

  @NotNull
  @NotBlank(message = "blankLmao")
  String alias;

  @Nullable String s;

  int i;
  @NotNull Integer integer;

  char ch;
  @NotNull Character chara;

  @NotEmpty List<String> list;

  @Pattern(
      regexp = "ded",
      flags = {RegexFlag.CANON_EQ, RegexFlag.CASE_INSENSITIVE})
  String getS() {
    return s;
  }

  @Negative(message = "message")
  int getI() {
    return i;
  }

  List<String> getList() {
    return list;
  }

  void setList(List<String> list) {
    this.list = list;
  }
}
