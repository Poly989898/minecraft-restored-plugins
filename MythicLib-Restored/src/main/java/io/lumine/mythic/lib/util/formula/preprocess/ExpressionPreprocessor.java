package io.lumine.mythic.lib.util.formula.preprocess;

import net.objecthunter.exp4j.Expression;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface ExpressionPreprocessor<C> {
   ExpressionPreprocessor<Void> EMPTY = new EmptyPreprocessor();
   ExpressionPreprocessor<OfflinePlayer> PLAYER = new PlayerPreprocessor();

   @NotNull
   String preprocess(@NotNull String var1);

   void process(@NotNull Expression var1, @NotNull C var2);

   @NotNull
   String quickProcess(@NotNull String var1, @NotNull C var2);
}
