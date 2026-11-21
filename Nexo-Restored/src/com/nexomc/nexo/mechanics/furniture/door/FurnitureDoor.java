package com.nexomc.nexo.mechanics.furniture.door;

import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.BlockLocation;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.FurnitureProperties;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.hitbox.BarrierHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.FurnitureHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.InteractionHitbox;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000 12\u00020\u0001:\u00011BO\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0002\u0010\b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\u0004\b\r\u0010\u000eB\u0011\b\u0016\u0012\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0004\b\r\u0010\u0011J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\"\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001fJ\u0018\u0010 \u001a\u00020\u001c2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u000b\u0010!\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\"\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\t\u0010#\u001a\u00020\u0006HÂ\u0003J\t\u0010$\u001a\u00020\u0006HÂ\u0003J\t\u0010%\u001a\u00020\u0006HÂ\u0003J\u0010\u0010&\u001a\u00020\nHÆ\u0003¢\u0006\u0004\b'\u0010\u0013J\u000b\u0010(\u001a\u0004\u0018\u00010\fHÆ\u0003J\\\u0010)\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\fHÆ\u0001¢\u0006\u0004\b*\u0010+J\u0014\u0010,\u001a\u00020\u00062\b\u0010-\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010.\u001a\u00020/HÖ\u0081\u0004J\n\u00100\u001a\u00020\u0003HÖ\u0081\u0004R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\t\u001a\u00020\n¢\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016¨\u00062"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/door/FurnitureDoor;", "", "openSound", "", "closeSound", "toggleHitboxOnOpen", "", "delayHitboxToggleDelay", "isSliding", "automaticCloseDelay", "Lkotlin/time/Duration;", "openProperties", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;", "<init>", "(Ljava/lang/String;Ljava/lang/String;ZZZJLcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getAutomaticCloseDelay-UwyO8pc", "()J", "J", "getOpenProperties", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;", "toggleState", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "ensureHitboxState", "player", "Lorg/bukkit/entity/Player;", "openHitboxMechanic", "component1", "component2", "component3", "component4", "component5", "component6", "component6-UwyO8pc", "component7", "copy", "copy-x2RqbK4", "(Ljava/lang/String;Ljava/lang/String;ZZZJLcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;)Lcom/nexomc/nexo/mechanics/furniture/door/FurnitureDoor;", "equals", "other", "hashCode", "", "toString", "Companion", "com.nexomc:core"}
)
public final class FurnitureDoor {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private final String openSound;
   @Nullable
   private final String closeSound;
   private final boolean toggleHitboxOnOpen;
   private final boolean delayHitboxToggleDelay;
   private final boolean isSliding;
   private final long automaticCloseDelay;
   @Nullable
   private final FurnitureProperties openProperties;
   @NotNull
   private static final NamespacedKey DOOR_OPEN_STATE;

   private FurnitureDoor(String openSound, String closeSound, boolean toggleHitboxOnOpen, boolean delayHitboxToggleDelay, boolean isSliding, long automaticCloseDelay, FurnitureProperties openProperties) {
      this.openSound = openSound;
      this.closeSound = closeSound;
      this.toggleHitboxOnOpen = toggleHitboxOnOpen;
      this.delayHitboxToggleDelay = delayHitboxToggleDelay;
      this.isSliding = isSliding;
      this.automaticCloseDelay = automaticCloseDelay;
      this.openProperties = openProperties;
   }

   public final long getAutomaticCloseDelay_UwyO8pc/* $FF was: getAutomaticCloseDelay-UwyO8pc*/() {
      return this.automaticCloseDelay;
   }

   @Nullable
   public final FurnitureProperties getOpenProperties() {
      return this.openProperties;
   }

   public FurnitureDoor(@NotNull ConfigurationSection section) {
      String str = NexoYamlKt.stringOrNull(section, "open_sound");
      String str2 = NexoYamlKt.stringOrNull(section, "close_sound");
      if (str2 == null) {
         str2 = NexoYamlKt.stringOrNull(section, "open_sound");
      }

      ConfigurationSection configurationSection;
      boolean flag;
      boolean flag2;
      boolean flag3;
      long value;
      block: {
         flag = section.getBoolean("toggle_hitbox_on_open", true);
         flag2 = section.getBoolean("delay_hitbox_toggle", false);
         flag3 = section.getBoolean("is_sliding", false);
         value = NexoYamlKt.duration(section, "automatic_close_delay", (DurationUnit)null);
         configurationSection = section.getParent();
         if (configurationSection != null) {
            configurationSection = NexoYamlKt.section(configurationSection, "properties");
            if (configurationSection != null) {
               configurationSection = NexoYamlKt.clone(configurationSection);
               if (configurationSection != null) {
                  break block;
               }
            }
         }

         configurationSection = (ConfigurationSection)(new YamlConfiguration());
      }

      ConfigurationSection p0 = NexoYamlKt.copyFrom(configurationSection, NexoYamlKt.section(section, "open_properties"));
      long value2 = value;
      boolean flag4 = flag3;
      boolean flag5 = flag2;
      boolean flag6 = flag;
      String str3 = str2;
      String str4 = str;
      int value3 = 0;
      FurnitureProperties furnitureProperties = new FurnitureProperties(p0);
      FurnitureProperties furnitureProperties2 = furnitureProperties;
      value3 = 0;
      if (!section.getBoolean("is_sliding", false) && Intrinsics.areEqual(furnitureProperties.getLeftRotation(), new Quaternionf())) {
         furnitureProperties.getLeftRotation().set(0.0F, 0.707F, 0.0F, 0.707F);
      }

      Unit unit = Unit.INSTANCE;
      this(str4, str3, flag6, flag5, flag4, value2, furnitureProperties2);
   }

   public final void toggleState(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      boolean isOpen = Companion.isOpen(baseEntity);
      baseEntity.getPersistentDataContainer().set(DOOR_OPEN_STATE, PersistentDataType.BOOLEAN, !isOpen);
      this.ensureHitboxState(baseEntity, mechanic, (Player)null);
      if (!isOpen) {
         String str = this.openSound;
         if (str != null) {
            String it = str;
            int value = 0;
            baseEntity.getWorld().playSound(baseEntity.getLocation(), it, 1.0F, 1.0F);
         }
      } else {
         String str2 = this.closeSound;
         if (str2 != null) {
            String it = str2;
            int value2 = 0;
            baseEntity.getWorld().playSound(baseEntity.getLocation(), it, 1.0F, 1.0F);
         }
      }

      if (!isOpen && Duration.compareTo_LRDsOJo(this.automaticCloseDelay, Duration.Companion.getZERO_UwyO8pc()) > 0 && Duration.compareTo_LRDsOJo(this.automaticCloseDelay, Duration.Companion.getINFINITE_UwyO8pc()) < 0) {
         FurnitureProperties furnitureProperties = this.openProperties;
         if (furnitureProperties == null) {
            furnitureProperties = mechanic.getProperties();
         }

         long openDelay = furnitureProperties.getDelay_UwyO8pc();
         SchedulerUtils.INSTANCE.runTaskLater(DurationUtilsKt.getInWholeTicks_LRDsOJo(Duration.plus_LRDsOJo(this.automaticCloseDelay, openDelay)), () -> FurnitureDoor.toggleState$lambda$2(baseEntity, this, mechanic));
      }

   }

   public final void ensureHitboxState(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @Nullable Player player) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager = iFurniturePacketManager;
            Boolean flag = baseEntity.getPersistentDataContainer().get(DOOR_OPEN_STATE, PersistentDataType.BOOLEAN);
            boolean isOpen = flag != null ? flag : false;
            if (player == null) {
               mechanic.getHitbox().refreshHitboxes(baseEntity, mechanic);
               mechanic.getTextEntities().refreshTextEntities(baseEntity, mechanic);
            }

            if (isOpen) {
               FurnitureMechanic fakeMechanic = this.openHitboxMechanic(baseEntity, mechanic);
               if (player != null) {
                  packetManager.sendFurnitureMetadataPacket(baseEntity, fakeMechanic, player);
               } else {
                  packetManager.sendFurnitureMetadataPacket(baseEntity, fakeMechanic);
               }

               Function0 runnable = (() -> FurnitureDoor.ensureHitboxState$lambda$0(player, packetManager, baseEntity, mechanic, fakeMechanic));
               if (this.toggleHitboxOnOpen) {
                  if (this.delayHitboxToggleDelay && Duration.isPositive_impl(mechanic.getProperties().getDelay_UwyO8pc())) {
                     SchedulerUtils.INSTANCE.runTaskLater(DurationUtilsKt.getInWholeTicks_LRDsOJo(fakeMechanic.getProperties().getDelay_UwyO8pc()), runnable);
                  } else {
                     runnable.invoke();
                  }
               }
            } else if (player != null) {
               packetManager.sendFurnitureMetadataPacket(baseEntity, mechanic, player);
            } else {
               packetManager.sendFurnitureMetadataPacket(baseEntity, mechanic);
            }

            IFurniturePacketManager.Companion.updateBaseEntity(baseEntity, mechanic);
            return;
         }
      }

   }

   private final FurnitureMechanic openHitboxMechanic(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      Iterable barriers = (Iterable)mechanic.getHitbox().getBarriers();
      Collection destination = (Collection)(new ObjectOpenHashSet((barriers instanceof Collection ? (Collection)barriers : null) != null ? (barriers instanceof Collection ? (Collection)barriers : null).size() : 10));

      for(Object item : barriers) {
         BarrierHitbox it = (BarrierHitbox)item;
         int value = 0;
         BlockLocation blockLoc = this.isSliding ? (BlockLocation)it : it.groundRotate(90.0F).add(0, 0, 0);
         destination.add(new InteractionHitbox(blockLoc.toVector3f(), 1.0F, 1.0F));
      }

      ObjectOpenHashSet hitboxes = (ObjectOpenHashSet)destination;
      FurnitureMechanic furnitureMechanic = mechanic.clone();
      int value2 = 0;
      FurnitureProperties furnitureProperties = this.openProperties;
      if (furnitureProperties == null) {
         furnitureProperties = furnitureMechanic.getProperties();
      }

      furnitureMechanic.setProperties$com_nexomc_core(furnitureProperties);
      furnitureMechanic.setHitbox$com_nexomc_core(new FurnitureHitbox(new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), hitboxes, new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet()));
      return furnitureMechanic;
   }

   private final String component1() {
      return this.openSound;
   }

   private final String component2() {
      return this.closeSound;
   }

   private final boolean component3() {
      return this.toggleHitboxOnOpen;
   }

   private final boolean component4() {
      return this.delayHitboxToggleDelay;
   }

   private final boolean component5() {
      return this.isSliding;
   }

   public final long component6_UwyO8pc/* $FF was: component6-UwyO8pc*/() {
      return this.automaticCloseDelay;
   }

   @Nullable
   public final FurnitureProperties component7() {
      return this.openProperties;
   }

   @NotNull
   public final FurnitureDoor copy_x2RqbK4/* $FF was: copy-x2RqbK4*/(@Nullable String openSound, @Nullable String closeSound, boolean toggleHitboxOnOpen, boolean delayHitboxToggleDelay, boolean isSliding, long automaticCloseDelay, @Nullable FurnitureProperties openProperties) {
      return new FurnitureDoor(openSound, closeSound, toggleHitboxOnOpen, delayHitboxToggleDelay, isSliding, automaticCloseDelay, openProperties);
   }

   @NotNull
   public String toString() {
      String str = this.openSound;
      return "FurnitureDoor(openSound=" + str + ", closeSound=" + this.closeSound + ", toggleHitboxOnOpen=" + this.toggleHitboxOnOpen + ", delayHitboxToggleDelay=" + this.delayHitboxToggleDelay + ", isSliding=" + this.isSliding + ", automaticCloseDelay=" + Duration.toString_impl(this.automaticCloseDelay) + ", openProperties=" + this.openProperties + ")";
   }

   public int hashCode() {
      int result = this.openSound == null ? 0 : this.openSound.hashCode();
      result = result * 31 + (this.closeSound == null ? 0 : this.closeSound.hashCode());
      result = result * 31 + Boolean.hashCode(this.toggleHitboxOnOpen);
      result = result * 31 + Boolean.hashCode(this.delayHitboxToggleDelay);
      result = result * 31 + Boolean.hashCode(this.isSliding);
      result = result * 31 + Duration.hashCode_impl(this.automaticCloseDelay);
      result = result * 31 + (this.openProperties == null ? 0 : this.openProperties.hashCode());
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FurnitureDoor)) {
         return false;
      } else {
         FurnitureDoor furnitureDoor = (FurnitureDoor)other;
         if (!Intrinsics.areEqual(this.openSound, furnitureDoor.openSound)) {
            return false;
         } else if (!Intrinsics.areEqual(this.closeSound, furnitureDoor.closeSound)) {
            return false;
         } else if (this.toggleHitboxOnOpen != furnitureDoor.toggleHitboxOnOpen) {
            return false;
         } else if (this.delayHitboxToggleDelay != furnitureDoor.delayHitboxToggleDelay) {
            return false;
         } else if (this.isSliding != furnitureDoor.isSliding) {
            return false;
         } else if (!Duration.equals_impl0(this.automaticCloseDelay, furnitureDoor.automaticCloseDelay)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.openProperties, furnitureDoor.openProperties);
         }
      }
   }

   private static final Unit toggleState$lambda$2(ItemDisplay baseEntity, FurnitureDoor furnitureDoor, FurnitureMechanic mechanic) {
      if (Companion.isOpen(baseEntity)) {
         furnitureDoor.toggleState(baseEntity, mechanic);
      }

      return Unit.INSTANCE;
   }

   private static final Unit ensureHitboxState$lambda$0(Player player, IFurniturePacketManager packetManager, ItemDisplay baseEntity, FurnitureMechanic mechanic, FurnitureMechanic fakeMechanic) {
      if (player == null) {
         packetManager.removeBarrierHitboxPacket(baseEntity, mechanic);
         packetManager.sendHitboxEntityPacket(baseEntity, fakeMechanic);
      } else {
         packetManager.removeBarrierHitboxPacket(baseEntity, mechanic, player);
         packetManager.sendHitboxEntityPacket(baseEntity, fakeMechanic, player);
      }

      return Unit.INSTANCE;
   }

   static {
      NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:door_open_state");
      DOOR_OPEN_STATE = namespacedKey;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fR\u0016\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\r"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/door/FurnitureDoor$Companion;", "", "<init>", "()V", "DOOR_OPEN_STATE", "Lorg/bukkit/NamespacedKey;", "Lorg/jetbrains/annotations/Nullable;", "getDOOR_OPEN_STATE", "()Lorg/bukkit/NamespacedKey;", "isOpen", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getDOOR_OPEN_STATE() {
         return FurnitureDoor.DOOR_OPEN_STATE;
      }

      public final boolean isOpen(@NotNull ItemDisplay baseEntity) {
         Boolean flag = baseEntity.getPersistentDataContainer().get(this.getDOOR_OPEN_STATE(), PersistentDataType.BOOLEAN);
         return flag != null ? flag : false;
      }
   }
}
