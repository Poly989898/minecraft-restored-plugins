package net.Zrips.CMILib.Container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class CMIConverter {
  private CMIConverter() {}

  public static <O, N> List<N> convert(Iterable<O> iterable, Function<O, N> converter) {
    List<N> converted = new ArrayList<>();

    for (O old : iterable) {
      N result = converter.apply(old);
      if (result != null) {
        converted.add(result);
      }
    }

    return converted;
  }

  public static <O, N> Set<N> convertSet(Iterable<O> iterable, Function<O, N> converter) {
    Set<N> converted = new HashSet<>();

    for (O old : iterable) {
      N result = converter.apply(old);
      if (result != null) {
        converted.add(result);
      }
    }

    return converted;
  }

  public static <O, N> List<N> convert(O[] array, Function<O, N> converter) {
    List<N> converted = new ArrayList<>();

    for (O old : array) {
      converted.add(converter.apply(old));
    }

    return converted;
  }
}
