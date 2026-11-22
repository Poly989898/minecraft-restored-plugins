package com.Zrips.CMI.Modules.Fireworks;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;

public class CMIFirework {
  int count = 1;
  int power = 1;
  boolean shootAtAngle = false;
  private Set<FireworkEffect> explosions = new LinkedHashSet<>();
  private static String prefix = "{";
  private static String suffix = "}";
  static Pattern p = Pattern.compile("^(?i)(power|p)\\:");
  static Pattern c = Pattern.compile("^(?i)(count|c)\\:");
  static Pattern ex = Pattern.compile("^(?i)(explosions|explosion|e)\\" + prefix);

  public CMIFirework() {}

  public CMIFirework(String string) {
    String[] strings;
    int value = (strings = string.split(";")).length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = strings[argIndex];
      Matcher matcher = p.matcher(arg);
      if (matcher.find()) {
        String powerText = arg.substring(matcher.group().length());

        try {
          this.power = Integer.parseInt(powerText);
        } catch (Throwable throwable) {
        }
      } else {
        Matcher countMatcher = c.matcher(arg);
        if (countMatcher.find()) {
          String countText = arg.substring(countMatcher.group().length());

          try {
            this.count = Integer.parseInt(countText);
          } catch (Throwable throwable2) {
          }
        } else {
          Matcher exMatcher = ex.matcher(arg);
          if (exMatcher.find()) {
            String altCountText = arg.substring(exMatcher.group().length());

            try {
              this.count = Integer.parseInt(altCountText);
            } catch (Throwable throwable3) {
            }
          }
        }
      }
    }

    Builder builder = FireworkEffect.builder();
    builder.withColor(CMIChatColor.getByCustomName("brown").getRGBColor());
    builder.withTrail().withFlicker();
    builder.with(Type.STAR);
  }
}
