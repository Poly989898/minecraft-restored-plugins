package com.nexomc.nexo.mechanics.furniture.states;

import com.nexomc.nexo.items.CustomModelDataValue;
import com.nexomc.nexo.items.ItemBuilder;
import kotlin.Metadata;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/CustomModelDataState;", "Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureState;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "current", "Lcom/nexomc/nexo/items/CustomModelDataValue;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;Lcom/nexomc/nexo/items/CustomModelDataValue;)V", "getCurrent", "()Lcom/nexomc/nexo/items/CustomModelDataValue;", "stateItem", "", "builder", "Lcom/nexomc/nexo/items/ItemBuilder;", "com.nexomc:core"}
)
public final class CustomModelDataState extends FurnitureState {
   @NotNull
   private final CustomModelDataValue current;

   public CustomModelDataState(@NotNull ConfigurationSection section) {
      this(section, CustomModelDataValue.Companion.parseOrThrow(section));
   }

   public CustomModelDataState(@NotNull ConfigurationSection section, @NotNull CustomModelDataValue current) {
      super(section);
      this.current = current;
   }

   @NotNull
   public final CustomModelDataValue getCurrent() {
      return this.current;
   }

   public void stateItem(@NotNull ItemBuilder builder) {
      builder.setCustomModelData(this.current.toCustomModelData());
   }
}
