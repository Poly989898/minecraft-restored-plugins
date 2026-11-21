package com.nexomc.nexo.mechanics;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\b&\u0018\u00002\u00020\u0001B\u0011\b\u0014\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u0011\b\u0014\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\u0004\u0010\bJ\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u0004J\u0012\u0010\u000f\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0002\u001a\u00020\u0003H&J\u0012\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u000bH\u0004J\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00070\u0014J\u0010\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0007J\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u0019J\u0014\u0010\u001a\u001a\u0004\u0018\u00010\u000b2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0007H\u0016J\u0014\u0010\u001a\u001a\u0004\u0018\u00010\u000b2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0016J\u001f\u0010\u001b\u001a\u00020\u00112\u0012\u0010\u001c\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001e0\u001d\"\u00020\u001e¢\u0006\u0002\u0010\u001fR\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\f\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006 "},
   d2 = {"Lcom/nexomc/nexo/mechanics/MechanicFactory;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "mechanicId", "", "(Ljava/lang/String;)V", "mechanicByItemId", "", "Lcom/nexomc/nexo/mechanics/Mechanic;", "mechanicID", "getMechanicID", "()Ljava/lang/String;", "parse", "addToImplemented", "", "mechanic", "items", "", "isNotImplementedIn", "", "itemID", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "getMechanic", "registerListeners", "listeners", "", "Lorg/bukkit/event/Listener;", "([Lorg/bukkit/event/Listener;)V", "com.nexomc:core"}
)
public abstract class MechanicFactory {
   @NotNull
   private final Map<String, Mechanic> mechanicByItemId;
   @NotNull
   private final String mechanicID;
   @Nullable
   private final ConfigurationSection section;

   @NotNull
   public final String getMechanicID() {
      return this.mechanicID;
   }

   protected MechanicFactory(@NotNull ConfigurationSection section) {
      super();
      this.mechanicByItemId = (Map)(new LinkedHashMap());
      this.section = section;
      String name = section.getName();
      this.mechanicID = name;
   }

   protected MechanicFactory(@NotNull String mechanicId) {
      super();
      this.mechanicByItemId = (Map)(new LinkedHashMap());
      this.mechanicID = mechanicId;
      this.section = null;
   }

   @Nullable
   protected final ConfigurationSection section() {
      return this.section;
   }

   @Nullable
   public abstract Mechanic parse(@NotNull ConfigurationSection configurationSection);

   protected final void addToImplemented(@Nullable Mechanic mechanic) {
      if (mechanic != null) {
         this.mechanicByItemId.put(mechanic.getItemID(), mechanic);
      }
   }

   @NotNull
   public final Set<String> items() {
      return CollectionsKt.toSet((Iterable)this.mechanicByItemId.keySet());
   }

   public final boolean isNotImplementedIn(@Nullable String itemID) {
      return !this.mechanicByItemId.containsKey(itemID);
   }

   public final boolean isNotImplementedIn(@NotNull ItemStack itemStack) {
      return !this.mechanicByItemId.containsKey(NexoItems.idFromItem(itemStack));
   }

   @Nullable
   public Mechanic getMechanic(@Nullable String itemID) {
      return this.mechanicByItemId.get(itemID);
   }

   @Nullable
   public Mechanic getMechanic(@Nullable ItemStack itemStack) {
      return this.mechanicByItemId.get(NexoItems.idFromItem(itemStack));
   }

   public final void registerListeners(@NotNull Listener... listeners) {
      MechanicsManager.INSTANCE.registerListeners(NexoPlugin.Companion.instance(), this.mechanicID, Arrays.copyOf(listeners, listeners.length));
   }
}
