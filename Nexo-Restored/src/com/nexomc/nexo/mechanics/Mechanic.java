package com.nexomc.nexo.mechanics;

import com.nexomc.nexo.utils.NexoYamlKt;
import kotlin.Metadata;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u0003\b&\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0012\u0010\u0006\u001a\n\u0012\u0006\b\u0001\u0012\u00020\b0\u0007\"\u00020\b¢\u0006\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001b\u0010\u0006\u001a\n\u0012\u0006\b\u0001\u0012\u00020\b0\u0007¢\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0012\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015¨\u0006\u0016"},
   d2 = {"Lcom/nexomc/nexo/mechanics/Mechanic;", "", "factory", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "itemModifiers", "", "Lcom/nexomc/nexo/mechanics/ItemModifier;", "<init>", "(Lcom/nexomc/nexo/mechanics/MechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;[Lcom/nexomc/nexo/mechanics/ItemModifier;)V", "getFactory", "()Lcom/nexomc/nexo/mechanics/MechanicFactory;", "getSection", "()Lorg/bukkit/configuration/ConfigurationSection;", "getItemModifiers", "()[Lcom/nexomc/nexo/mechanics/ItemModifier;", "[Lcom/nexomc/nexo/mechanics/ItemModifier;", "itemID", "", "getItemID", "()Ljava/lang/String;", "com.nexomc:core"}
)
public abstract class Mechanic {
   @NotNull
   private final MechanicFactory factory;
   @NotNull
   private final ConfigurationSection section;
   @NotNull
   private final ItemModifier[] itemModifiers;
   @NotNull
   private final String itemID;

   public Mechanic(@NotNull MechanicFactory factory, @NotNull ConfigurationSection section, @NotNull ItemModifier... itemModifiers) {
      super();
      this.factory = factory;
      this.section = section;
      this.itemModifiers = itemModifiers;
      this.itemID = NexoYamlKt.getRootId(this.section);
   }

   @NotNull
   public final MechanicFactory getFactory() {
      return this.factory;
   }

   @NotNull
   public final ConfigurationSection getSection() {
      return this.section;
   }

   @NotNull
   public final ItemModifier[] getItemModifiers() {
      return this.itemModifiers;
   }

   @NotNull
   public final String getItemID() {
      return this.itemID;
   }
}
