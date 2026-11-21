package io.lumine.mythic.core.volatilecode.v1_21_R3.attributes;

import io.lumine.mythic.bukkit.adapters.attributes.ImmutableAttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_21_R3.attribute.CraftAttributeInstance;

public class ImmutableAttributeInstanceImpl extends CraftAttributeInstance implements ImmutableAttributeInstance {
   public ImmutableAttributeInstanceImpl(AttributeModifiable handle, Attribute attribute) {
      super(handle, attribute);
   }

   public void setBaseValue(double d) {
      throw new UnsupportedOperationException("Cannot modify default attributes");
   }

   public void addModifier(AttributeModifier modifier) {
      throw new UnsupportedOperationException("Cannot modify default attributes");
   }

   public void removeModifier(AttributeModifier modifier) {
      throw new UnsupportedOperationException("Cannot modify default attributes");
   }
}
