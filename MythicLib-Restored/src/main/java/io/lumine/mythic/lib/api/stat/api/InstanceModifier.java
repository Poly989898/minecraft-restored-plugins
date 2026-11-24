package io.lumine.mythic.lib.api.stat.api;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.player.modifier.PlayerModifier;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.UUID;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public abstract class InstanceModifier extends PlayerModifier {
   protected final double value;
   protected final ModifierType type;

   public InstanceModifier(@NotNull String key, double value) {
      this(key, EquipmentSlot.OTHER, ModifierSource.OTHER, value, ModifierType.FLAT);
   }

   public InstanceModifier(@NotNull String key, EquipmentSlot slot, ModifierSource source, double value, ModifierType type) {
      this(UUID.randomUUID(), key, slot, source, value, type);
   }

   public InstanceModifier(@NotNull UUID uniqueId, @NotNull String key, EquipmentSlot slot, ModifierSource source, double value, ModifierType type) {
      super(uniqueId, key, slot, source);
      this.value = value;
      this.type = type;
   }

   public InstanceModifier(String key, EquipmentSlot slot, ModifierSource source, String str) {
      super(key, slot, source);
      Validate.notNull(str, "String cannot be null");
      Validate.notEmpty(str, "String cannot be empty");
      this.type = str.toCharArray()[str.length() - 1] == '%' ? ModifierType.RELATIVE : ModifierType.FLAT;
      this.value = Double.parseDouble(this.type == ModifierType.RELATIVE ? str.substring(0, str.length() - 1) : str);
   }

   public InstanceModifier(ConfigObject object) {
      super(object.getString("key"), EquipmentSlot.OTHER, ModifierSource.OTHER);
      this.value = object.getDouble("value");
      this.type = object.getBoolean("multiplicative", false) ? ModifierType.RELATIVE : ModifierType.FLAT;
   }

   @NotNull
   public ModifierType getType() {
      return this.type;
   }

   public double getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return MythicLib.plugin.getMMOConfig().decimal.format(this.value) + (this.type == ModifierType.RELATIVE ? "%" : "");
   }
}
