package net.Indyuce.mmocore.api.player.attribute;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.api.InstanceModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.UUID;
import net.Indyuce.mmocore.api.player.PlayerData;

public class AttributeModifier extends InstanceModifier {
   private final String attribute;

   public AttributeModifier(String key, String attribute, double value) {
      super(key, value);
      this.attribute = attribute;
   }

   public AttributeModifier(String key, String attribute, double value, ModifierType type) {
      this(key, attribute, value, type, EquipmentSlot.OTHER, ModifierSource.OTHER);
   }

   public AttributeModifier(String key, String attribute, double value, ModifierType type, EquipmentSlot slot, ModifierSource source) {
      this(UUID.randomUUID(), key, attribute, value, type, slot, source);
   }

   public AttributeModifier(UUID uniqueId, String key, String attribute, double value, ModifierType type, EquipmentSlot slot, ModifierSource source) {
      super(uniqueId, key, slot, source, value, type);
      this.attribute = attribute;
   }

   public AttributeModifier(String key, String attribute, String str) {
      super(key, EquipmentSlot.OTHER, ModifierSource.OTHER, str);
      this.attribute = attribute;
   }

   public AttributeModifier(ConfigObject object) {
      super(object);
      this.attribute = object.getString("attribute");
   }

   public String getAttribute() {
      return this.attribute;
   }

   public AttributeModifier multiply(double coef) {
      return new AttributeModifier(this.getUniqueId(), this.getKey(), this.attribute, this.value * coef, this.type, this.getSlot(), this.getSource());
   }

   public void register(MMOPlayerData mmoPlayerData) {
      PlayerData playerData = PlayerData.get(mmoPlayerData);
      playerData.getAttributes().getInstance(this.attribute).addModifier(this);
   }

   public void unregister(MMOPlayerData mmoPlayerData) {
      PlayerData playerData = PlayerData.get(mmoPlayerData);
      playerData.getAttributes().getInstance(this.attribute).removeModifier(this.getKey());
   }
}
