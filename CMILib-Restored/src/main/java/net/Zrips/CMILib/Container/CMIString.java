package net.Zrips.CMILib.Container;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public final class CMIString {
  private CMIString() {}

  public static String joinRange(int from, String[] array) {
    return joinRange(from, array.length, array);
  }

  public static String joinRange(int from, int to, String[] array) {
    return joinRange(from, to, array, " ");
  }

  public static String joinRange(int from, int to, String[] array, String delimiter) {
    StringBuilder joined = new StringBuilder();

    for (int i = from; i < CMINumber.clamp(to, 0, array.length); i++) {
      joined.append(joined.length() == 0 ? "" : delimiter).append(array[i]);
    }

    return joined.toString();
  }

  public static <T> String join(T[] array) {
    return array == null ? "null" : join(Arrays.asList(array));
  }

  public static <T> String join(Iterable<T> iterable) {
    return iterable == null ? "null" : join(iterable, ", ");
  }

  public static <T> String join(T[] array, String delimiter) {
    return join(array, delimiter, object -> object == null ? "" : simplify(object));
  }

  public static <T> String join(Iterable<T> iterable, String delimiter) {
    return join(iterable, delimiter, object -> object == null ? "" : simplify(object));
  }

  public static <T> String join(T[] array, Function<T, String> toString) {
    return join(array, ", ", toString);
  }

  public static <T> String join(T[] array, String delimiter, Function<T, String> toString) {
    return join(Arrays.asList(array), delimiter, toString);
  }

  public static <T> String join(Iterable<T> iterable, Function<T, String> toString) {
    return join(iterable, ", ", toString);
  }

  public static <T> String join(
      Iterable<T> iterable, String delimiter, Function<T, String> toString) {
    StringBuilder message = new StringBuilder();
    Iterator<T> iterator = iterable.iterator();

    while (iterator.hasNext()) {
      T next = iterator.next();
      if (next != null) {
        message.append(toString.apply(next)).append(iterator.hasNext() ? delimiter : "");
      }
    }

    return message.toString();
  }

  public static String simplify(Object object) {
    if (object instanceof Entity) {
      return ((Entity) object).getName();
    }

    if (object instanceof CommandSender) {
      return ((CommandSender) object).getName();
    }

    if (object instanceof World) {
      return ((World) object).getName();
    }

    if (object instanceof Location) {
      return CMILocation.toString((Location) object);
    }

    if (object instanceof Collection) {
      return join((Collection) object, ", ", CMIString::simplify);
    }

    if (object instanceof ChatColor) {
      return ((Enum) object).name().toLowerCase();
    }

    if (object instanceof CMIChatColor) {
      return ((CMIChatColor) object).getName();
    }

    if (object instanceof Enum) {
      return object.toString().toLowerCase();
    }

    try {
      if (object instanceof net.md_5.bungee.api.ChatColor) {
        return ((net.md_5.bungee.api.ChatColor) object).getName();
      }
    } catch (Exception var2) {
    }

    return object.toString();
  }
}
