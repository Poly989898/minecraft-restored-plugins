package io.lumine.mythic.lib.api.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.api.ModifiedInstance;
import io.lumine.mythic.lib.api.stat.handler.StatHandler;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.util.Closeable;
import io.lumine.mythic.lib.util.Lazy;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatInstance extends ModifiedInstance<StatModifier> {
   @NotNull
   private final StatMap map;
   @NotNull
   private final String stat;
   @NotNull
   private final Lazy<Optional<StatHandler>> cachedHandler;

   public StatInstance(@NotNull StatMap map, @NotNull String stat) {
      this.map = map;
      this.stat = stat;
      this.cachedHandler = Lazy.persistent(() -> MythicLib.plugin.getStats().getHandler(stat));
   }

   @NotNull
   public StatMap getMap() {
      return this.map;
   }

   @NotNull
   public String getStat() {
      return this.stat;
   }

   public double getBase() {
      return this.hasHandler() ? this.cachedHandler.get().get().getBaseValue(this) : 0.0;
   }

   public double getTotal() {
      return this.getFilteredTotal(EquipmentSlot.MAIN_HAND::isCompatible, mod -> mod);
   }

   public double getFinal() {
      return this.hasHandler() ? this.cachedHandler.get().get().getFinalValue(this) : this.getTotal();
   }

   @NotNull
   public String formatFinal() {
      return this.format(this.getFinal());
   }

   @NotNull
   public String format(double value) {
      return (this.hasHandler() ? this.cachedHandler.get().get().getDecimalFormat() : MythicLib.plugin.getMMOConfig().decimal).format(value);
   }

   public double getFilteredTotal(Predicate<StatModifier> filter) {
      return this.getFilteredTotal(filter, mod -> mod);
   }

   public double getTotal(Function<StatModifier, StatModifier> modification) {
      return this.getFilteredTotal(EquipmentSlot.MAIN_HAND::isCompatible, modification);
   }

   public double getFilteredTotal(Predicate<StatModifier> filter, Function<StatModifier, StatModifier> modification) {
      double total = this.getFilteredTotal(this.getBase(), filter, modification);
      return this.hasHandler() ? this.cachedHandler.get().get().clampValue(total) : total;
   }

   private boolean hasHandler() {
      return !this.map.getData().hasFullySynchronized() ? false : this.cachedHandler.get().isPresent();
   }

   public void registerModifier(@NotNull StatModifier modifier) {
      StatInstance.ModifierPacket packet = new StatInstance.ModifierPacket();
      packet.addModifier(modifier);
      packet.update();
   }

   @Override
   public void removeIf(@NotNull Predicate<String> condition) {
      StatInstance.ModifierPacket packet = new StatInstance.ModifierPacket();
      packet.removeIf(condition);
      packet.update();
   }

   @Override
   public void removeModifier(@NotNull UUID uniqueId) {
      StatInstance.ModifierPacket packet = new StatInstance.ModifierPacket();
      packet.remove(uniqueId);
      packet.update();
   }

   @NotNull
   public StatInstance.ModifierPacket newPacket() {
      return new StatInstance.ModifierPacket();
   }

   public void update() {
      if (this.hasHandler()) {
         this.cachedHandler.get().get().runUpdate(this);
      }
   }

   public void flushCache() {
      this.cachedHandler.flush();
   }

   @Deprecated
   public void addModifier(@NotNull StatModifier modifier) {
      this.removeIf(modifier.getKey()::equals);
      this.registerModifier(modifier);
   }

   @Deprecated
   @Nullable
   public StatHandler findHandler() {
      return this.hasHandler() ? this.cachedHandler.get().get() : null;
   }

   @Deprecated
   @Override
   public void remove(String key) {
      this.removeIf(key::equals);
   }

   public class ModifierPacket {
      private boolean updateRequired;

      public void addModifier(StatModifier modifier) {
         StatModifier current = StatInstance.this.modifiers.put(modifier.getUniqueId(), modifier);
         if (current != null && current instanceof Closeable) {
            ((Closeable)current).close();
         }

         this.updateRequired = true;
      }

      public void remove(@NotNull UUID uniqueId) {
         StatModifier mod = StatInstance.this.modifiers.remove(uniqueId);
         if (mod != null) {
            if (mod instanceof Closeable) {
               ((Closeable)mod).close();
            }

            this.updateRequired = true;
         }
      }

      @Deprecated
      public void remove(@NotNull String key) {
         this.removeIf(str -> str.equals(key));
      }

      public void removeIf(@NotNull Predicate<String> condition) {
         Iterator<StatModifier> iterator = StatInstance.this.modifiers.values().iterator();

         while (iterator.hasNext()) {
            StatModifier modifier = iterator.next();
            if (condition.test(modifier.getKey())) {
               if (modifier instanceof Closeable) {
                  ((Closeable)modifier).close();
               }

               iterator.remove();
               this.updateRequired = true;
            }
         }
      }

      public void update() {
         if (this.updateRequired) {
            StatInstance.this.update();
         }
      }

      @Deprecated
      public void runUpdate() {
         this.update();
      }
   }
}
