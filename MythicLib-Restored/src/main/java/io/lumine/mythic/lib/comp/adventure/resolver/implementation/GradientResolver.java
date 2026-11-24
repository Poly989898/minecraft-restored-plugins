package io.lumine.mythic.lib.comp.adventure.resolver.implementation;

import io.lumine.mythic.lib.comp.adventure.argument.AdventureArgumentQueue;
import io.lumine.mythic.lib.comp.adventure.gradient.GradientBuilder;
import io.lumine.mythic.lib.comp.adventure.gradient.Interpolator;
import io.lumine.mythic.lib.comp.adventure.resolver.ContextTagResolver;
import io.lumine.mythic.lib.util.AdventureUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GradientResolver implements ContextTagResolver {
   @Nullable
   @Override
   public String resolve(@NotNull String src, @NotNull AdventureArgumentQueue argsQueue, @NotNull String context, @NotNull List<String> decorations) {
      if (!argsQueue.hasNext()) {
         return GradientBuilder.rgbGradient(context, Color.WHITE, Color.BLACK, 0.0, Interpolator.LINEAR, decorations);
      }

      List<String> args = new ArrayList<>();

      while (argsQueue.hasNext()) {
         args.add(argsQueue.pop().value());
      }

      double phase = this.getPhase(args);
      if (args.size() > 2) {
         return GradientBuilder.multiRgbGradient(
            context, args.stream().map(AdventureUtils::color).toArray(Color[]::new), phase, Interpolator.LINEAR, decorations
         );
      }

      Color c1 = AdventureUtils.color(args.get(0));
      return args.size() == 1
         ? GradientBuilder.rgbGradient(context, c1, Color.BLACK, phase, Interpolator.LINEAR, decorations)
         : GradientBuilder.rgbGradient(context, c1, AdventureUtils.color(args.get(1)), phase, Interpolator.LINEAR, decorations);
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
