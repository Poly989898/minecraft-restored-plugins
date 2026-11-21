package com.nexomc.nexo.recipes.builders;

import com.nexomc.nexo.recipes.RecipeType;
import kotlin.Metadata;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005¨\u0006\u0006"},
   d2 = {"Lcom/nexomc/nexo/recipes/builders/FurnaceBuilder;", "Lcom/nexomc/nexo/recipes/builders/CookingBuilder;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lorg/bukkit/entity/Player;)V", "com.nexomc:core"}
)
public final class FurnaceBuilder extends CookingBuilder {
   public FurnaceBuilder(@NotNull Player player) {
      super(player, RecipeType.FURNACE);
   }
}
