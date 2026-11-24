package io.lumine.mythic.lib.api.stat.modifier;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.api.InstanceModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class StatModifier extends InstanceModifier {
   private final String stat;

   public StatModifier(@NotNull String key, @NotNull String stat, double value) {
      this(key, stat, value, ModifierType.FLAT, EquipmentSlot.OTHER, ModifierSource.OTHER);
   }

   public StatModifier(@NotNull String key, @NotNull String stat, double value, @NotNull ModifierType type) {
      this(key, stat, value, type, EquipmentSlot.OTHER, ModifierSource.OTHER);
   }

   public StatModifier(
      @NotNull String key, @NotNull String stat, double value, @NotNull ModifierType type, @NotNull EquipmentSlot slot, @NotNull ModifierSource source
   ) {
      this(UUID.randomUUID(), key, stat, value, type, slot, source);
   }

   public StatModifier(
      @NotNull UUID uniqueId,
      @NotNull String key,
      @NotNull String stat,
      double value,
      @NotNull ModifierType type,
      @NotNull EquipmentSlot slot,
      @NotNull ModifierSource source
   ) {
      super(uniqueId, key, slot, source, value, type);
      this.stat = stat;
   }

   public StatModifier(String key, String stat, String str) {
      super(key, EquipmentSlot.OTHER, ModifierSource.OTHER, str);
      this.stat = stat;
   }

   public StatModifier(ConfigObject object) {
      super(object);
      this.stat = object.getString("stat");
   }

   @NotNull
   public String getStat() {
      return this.stat;
   }

   @NotNull
   public StatModifier add(double offset) {
      return new StatModifier(this.getUniqueId(), this.getKey(), this.stat, this.value + offset, this.type, this.getSlot(), this.getSource());
   }

   @NotNull
   public StatModifier multiply(double coef) {
      return new StatModifier(this.getUniqueId(), this.getKey(), this.stat, this.value * coef, this.type, this.getSlot(), this.getSource());
   }

   @Override
   public void register(MMOPlayerData playerData) {
      playerData.getStatMap().getInstance(this.stat).registerModifier(this);
   }

   @Override
   public void unregister(MMOPlayerData playerData) {
      playerData.getStatMap().getInstance(this.stat).removeModifier(this.getUniqueId());
   }
}
