package io.lumine.mythic.lib.util.formula.preprocess;

import net.objecthunter.exp4j.Expression;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface ExpressionPreprocessor<C> {
   ExpressionPreprocessor<Void> EMPTY = new EmptyPreprocessor();
   ExpressionPreprocessor<OfflinePlayer> PLAYER = new PlayerPreprocessor();

   @NotNull
   String preprocess(@NotNull String text);

   void process(@NotNull Expression expression, @NotNull C value);

   @NotNull
   String quickProcess(@NotNull String text, @NotNull C value);
}
