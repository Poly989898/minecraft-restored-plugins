package net.Zrips.CMILib.Time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum timeModifier {
  s(1),
  m(60),
  h(3600),
  d(86400),
  w(604800),
  M(2592000),
  Y(31536000);

  static Pattern patern = Pattern.compile("(\\d+[mshdwMY])");
  private int modifier = 0;

  timeModifier(int modifier) {
    this.modifier = modifier;
  }

  public int getModifier() {
    return this.modifier;
  }

  public void setModifier(int modifier) {
    this.modifier = modifier;
  }

  public static Long getTimeRangeFromString(String time) {
    try {
      return Long.parseLong(time);
    } catch (Exception var10) {
      Matcher match = patern.matcher(time);
      Long total = null;

      while (match.find()) {
        String t = match.group(1);

        for (timeModifier one : values()) {
          if (t.endsWith(one.name())) {
            try {
              Long amount = Long.parseLong(t.substring(0, t.length() - one.name().length()));
              time = time.replace(t, "");
              if (total == null) {
                total = 0L;
              }

              total = total + amount * one.getModifier();
            } catch (Exception e) {
              break;
            }
          }
        }
      }

      return !time.isEmpty() ? null : total;
    }
  }

  public static Double getDoubleTimeRangeFromString(String time) {
    try {
      return Double.parseDouble(time);
    } catch (Exception var10) {
      Matcher match = patern.matcher(time);
      Double total = null;

      while (match.find()) {
        String t = match.group(1);

        for (timeModifier one : values()) {
          if (t.endsWith(one.name())) {
            try {
              Double amount = Double.parseDouble(t.substring(0, t.length() - one.name().length()));
              time = time.replace(t, "");
              if (total == null) {
                total = 0.0;
              }

              total = total + amount * Double.valueOf((double) one.getModifier());
            } catch (Exception e) {
              break;
            }
          }
        }
      }

      return !time.isEmpty() ? null : total;
    }
  }
}
