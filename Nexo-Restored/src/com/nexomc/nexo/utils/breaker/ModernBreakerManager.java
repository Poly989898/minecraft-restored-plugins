package com.nexomc.nexo.utils.breaker;

import com.nexomc.nexo.mechanics.breakable.Breakable;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\n\u001a\u00020\u000bH\u0016J \u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0018\u0010\u0014\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0018\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u0007H\u0002J\u0010\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0002R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\t0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b"},
   d2 = {"Lcom/nexomc/nexo/utils/breaker/ModernBreakerManager;", "Lcom/nexomc/nexo/utils/breaker/BreakerManager;", "<init>", "()V", "modifierMap", "Ljava/util/concurrent/ConcurrentHashMap;", "Ljava/util/UUID;", "Lorg/bukkit/attribute/AttributeModifier;", "furnitureMap", "Lorg/bukkit/scheduler/BukkitTask;", "reload", "", "startBlockBreak", "player", "Lorg/bukkit/entity/Player;", "block", "Lorg/bukkit/block/Block;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "stopBlockBreak", "createBreakingModifier", "breakable", "Lcom/nexomc/nexo/mechanics/breakable/Breakable;", "addTransientModifier", "modifier", "removeTransientModifier", "Companion", "com.nexomc:core"}
)
public final class ModernBreakerManager implements BreakerManager {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ConcurrentHashMap<UUID, AttributeModifier> modifierMap = new ConcurrentHashMap();
   @NotNull
   private final ConcurrentHashMap<UUID, BukkitTask> furnitureMap = new ConcurrentHashMap();
   @NotNull
   private static final NamespacedKey KEY;

   public void reload() {
      Map map = (Map)this.modifierMap;
      int value = 0;

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value2 = 0;
         Player player = Bukkit.getPlayer((UUID)element.getKey());
         if (player != null) {
            Player p0 = player;
            int value3 = 0;
            this.removeTransientModifier(p0);
         }
      }

      ((ConcurrentHashMap)map).clear();
      map = (Map)this.furnitureMap;

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value4 = 0;
         ((BukkitTask)entry.getValue()).cancel();
      }

   }

   public void startBlockBreak(@NotNull Player player, @NotNull Block block, @NotNull CustomBlockMechanic mechanic) {
      this.removeTransientModifier(player);
      if (player.getGameMode() != GameMode.CREATIVE) {
         this.addTransientModifier(player, this.createBreakingModifier(player, mechanic.getBreakable()));
      }
   }

   public void stopBlockBreak(@NotNull Player player) {
      this.removeTransientModifier(player);
   }

   private final AttributeModifier createBreakingModifier(Player player, Breakable breakable) {
      return new AttributeModifier(KEY, 0.24 / breakable.getHardness() * breakable.toolSpeedMultiplier(player) - (double)1, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.HAND);
   }

   private final void addTransientModifier(Player player, AttributeModifier modifier) {
      this.removeTransientModifier(player);
      ((Map)this.modifierMap).put(player.getUniqueId(), modifier);
      Attribute attribute = Attribute.BLOCK_BREAK_SPEED;
      AttributeInstance attributeInstance = player.getAttribute(attribute);
      if (attributeInstance != null) {
         attributeInstance.addTransientModifier(modifier);
      }

   }

   private final void removeTransientModifier(Player player) {
      AttributeModifier attributeModifier = (AttributeModifier)this.modifierMap.get(player.getUniqueId());
      if (attributeModifier != null) {
         AttributeModifier modifier = attributeModifier;
         Attribute attribute = Attribute.BLOCK_BREAK_SPEED;
         AttributeInstance attributeInstance = player.getAttribute(attribute);
         if (attributeInstance != null) {
            AttributeInstance instance = attributeInstance;
            instance.removeModifier(modifier);
         }
      }
   }

   static {
      NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:custom_breaking_speed");
      KEY = namespacedKey;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0013\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"},
      d2 = {"Lcom/nexomc/nexo/utils/breaker/ModernBreakerManager$Companion;", "", "<init>", "()V", "KEY", "Lorg/bukkit/NamespacedKey;", "Lorg/jetbrains/annotations/Nullable;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
