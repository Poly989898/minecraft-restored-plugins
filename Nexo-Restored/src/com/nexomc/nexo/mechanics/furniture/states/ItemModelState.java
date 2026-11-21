package com.nexomc.nexo.mechanics.furniture.states;

import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.Objects;
import com.nexomc.nexo.items.ItemBuilder;
import kotlin.Metadata;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/ItemModelState;", "Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureState;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "itemModel", "Lnet/kyori/adventure/key/Key;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;Lnet/kyori/adventure/key/Key;)V", "getItemModel", "()Lnet/kyori/adventure/key/Key;", "stateItem", "", "builder", "Lcom/nexomc/nexo/items/ItemBuilder;", "com.nexomc:core"}
)
public final class ItemModelState extends FurnitureState {
   @NotNull
   private final Key itemModel;

   public ItemModelState(@NotNull ConfigurationSection section) {
      this(section, Objects.requireNonNull(NexoYamlKt.key(section, "item_model")));
   }

   public ItemModelState(@NotNull ConfigurationSection section, @NotNull Key itemModel) {
      super(section);
      this.itemModel = itemModel;
   }

   @NotNull
   public final Key getItemModel() {
      return this.itemModel;
   }

   public void stateItem(@NotNull ItemBuilder builder) {
      builder.setItemModel(this.itemModel);
   }
}
