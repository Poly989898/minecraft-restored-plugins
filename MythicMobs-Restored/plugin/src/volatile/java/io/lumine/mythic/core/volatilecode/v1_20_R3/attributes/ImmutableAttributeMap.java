package io.lumine.mythic.core.volatilecode.v1_20_R3.attributes;

import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.adapters.attributes.BukkitAttributable;
import io.lumine.mythic.bukkit.adapters.attributes.ImmutableAttributeInstance;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import org.bukkit.craftbukkit.v1_20_R3.attribute.CraftAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ImmutableAttributeMap(AttributeProvider supplier) implements BukkitAttributable {
   @Nullable
   @Override
   public ImmutableAttributeInstance getAttribute(@NotNull BukkitAttribute attribute) {
      AttributeBase nmsAttribute = CraftAttribute.bukkitToMinecraft(attribute.getBukkitAttribute());
      if (!this.supplier.c(nmsAttribute)) {
         return null;
      } else {
         try {
            Method method = this.supplier.getClass().getDeclaredMethod("d", AttributeBase.class);
            method.setAccessible(true);
            AttributeModifiable value = (AttributeModifiable)method.invoke(this.supplier, nmsAttribute);
            return new ImmutableAttributeInstanceImpl(value, attribute.getBukkitAttribute());
         } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var5) {
            return null;
         }
      }
   }
}
