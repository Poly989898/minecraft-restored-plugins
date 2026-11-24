package io.lumine.mythic.lib.util.formula.preprocess;

import io.lumine.mythic.lib.MythicLib;
import net.objecthunter.exp4j.Expression;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerPreprocessor implements ExpressionPreprocessor<OfflinePlayer> {
   @NotNull
   @Override
   public String preprocess(@NotNull String expression) {
      return expression;
   }

   public void process(@NotNull Expression expression, @NotNull OfflinePlayer context) {
   }

   @NotNull
   public String quickProcess(@NotNull String expression, @NotNull OfflinePlayer player) {
      return MythicLib.plugin.getPlaceholderParser().parse(player, expression);
   }
}
