package com.nexomc.nexo.mechanics.misc.itemtype;

import com.nexomc.nexo.mechanics.Mechanic;
import kotlin.Metadata;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/itemtype/ItemTypeMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/misc/itemtype/ItemTypeMechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "itemType", "", "<init>", "(Lcom/nexomc/nexo/mechanics/misc/itemtype/ItemTypeMechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;Ljava/lang/String;)V", "getItemType", "()Ljava/lang/String;", "com.nexomc:core"}
)
public final class ItemTypeMechanic extends Mechanic {
   @NotNull
   private final String itemType;

   public ItemTypeMechanic(@NotNull ItemTypeMechanicFactory factory, @NotNull ConfigurationSection section, @NotNull String itemType) {
      super(factory, section);
      this.itemType = itemType;
   }

   @NotNull
   public final String getItemType() {
      return this.itemType;
   }
}
