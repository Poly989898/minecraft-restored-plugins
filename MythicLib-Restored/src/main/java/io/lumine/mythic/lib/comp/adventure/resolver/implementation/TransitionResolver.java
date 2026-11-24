package io.lumine.mythic.lib.comp.adventure.resolver.implementation;

import io.lumine.mythic.lib.comp.adventure.argument.AdventureArgumentQueue;
import io.lumine.mythic.lib.comp.adventure.resolver.ContextTagResolver;
import io.lumine.mythic.lib.util.AdventureUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransitionResolver implements ContextTagResolver {
   @Nullable
   @Override
   public String resolve(@NotNull String src, @NotNull AdventureArgumentQueue argumentQueue, @NotNull String context, @NotNull List<String> decorations) {
      List<String> args = new ArrayList<>();

      while (argumentQueue.hasNext()) {
         args.add(argumentQueue.pop().value());
      }

      if (args.size() < 3) {
         return null;
      } else {
         double phase = this.getPhase(args);
         if (!(phase < 0.0) && !(phase > 1.0)) {
            List<Color> colors = args.stream().map(AdventureUtils::color).collect(Collectors.toList());
            return colors.size() < 2 ? null : ChatColor.of(this.transition(phase, colors)) + String.join("", decorations) + context;
         } else {
            return null;
         }
      }
   }

   private Color transition(double phase, List<Color> colors) {
      if (colors.size() == 1) {
         return colors.get(0);
      }

      if (phase == 1.0) {
         return colors.get(colors.size() - 1);
      }

      double step = 1.0 / (colors.size() - 1);
      int index = (int)(phase / step);
      double localPhase = (phase - index * step) / step;
      return this.interpolate(colors.get(index), colors.get(index + 1), localPhase);
   }

   private Color interpolate(Color c1, Color c2, double phase) {
      return new Color(
         (int)(c1.getRed() + (c2.getRed() - c1.getRed()) * phase),
         (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * phase),
         (int)(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * phase),
         (int)(c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * phase)
      );
   }

   private double getPhase(List<String> args) {
      String lastArg = args.get(args.size() - 1);

      try {
         double phase = Double.parseDouble(lastArg);
         args.remove(args.size() - 1);
         return phase;
      } catch (NumberFormatException e) {
         return 1.0;
      }
   }
}
