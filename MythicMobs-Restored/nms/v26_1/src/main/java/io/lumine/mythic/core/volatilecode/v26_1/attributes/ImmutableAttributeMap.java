package io.lumine.mythic.core.volatilecode.v26_1.attributes;

import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.adapters.attributes.BukkitAttributable;
import io.lumine.mythic.bukkit.adapters.attributes.ImmutableAttributeInstance;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ImmutableAttributeMap(AttributeSupplier supplier) implements BukkitAttributable {
   @Nullable
   @Override
   public ImmutableAttributeInstance getAttribute(@NotNull BukkitAttribute attribute) {
      Holder<Attribute> nmsAttribute = CraftAttribute.bukkitToMinecraftHolder(attribute.getBukkitAttribute());
      if (!this.supplier.hasAttribute(nmsAttribute)) {
         return null;
      } else {
         try {
            Method method = this.supplier.getClass().getDeclaredMethod("supplier", Holder.class);
            method.setAccessible(true);
            AttributeInstance value = (AttributeInstance)method.invoke(this.supplier, nmsAttribute);
            return new ImmutableAttributeInstanceImpl(value, attribute.getBukkitAttribute());
         } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
         }
      }
   }
}
