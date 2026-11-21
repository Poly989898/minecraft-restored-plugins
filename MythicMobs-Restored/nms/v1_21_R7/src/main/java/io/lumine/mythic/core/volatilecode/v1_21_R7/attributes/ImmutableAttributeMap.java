package io.lumine.mythic.core.volatilecode.v1_21_R7.attributes;

import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.adapters.attributes.BukkitAttributable;
import io.lumine.mythic.bukkit.adapters.attributes.ImmutableAttributeInstance;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import org.bukkit.craftbukkit.v1_21_R7.attribute.CraftAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ImmutableAttributeMap(AttributeProvider supplier) implements BukkitAttributable {
   @Nullable
   @Override
   public ImmutableAttributeInstance getAttribute(@NotNull BukkitAttribute attribute) {
      Holder<AttributeBase> nmsAttribute = CraftAttribute.bukkitToMinecraftHolder(attribute.getBukkitAttribute());
      if (!this.supplier.c(nmsAttribute)) {
         return null;
      } else {
         try {
            Method method = this.supplier.getClass().getDeclaredMethod("d", Holder.class);
            method.setAccessible(true);
            AttributeModifiable value = (AttributeModifiable)method.invoke(this.supplier, nmsAttribute);
            return new ImmutableAttributeInstanceImpl(value, attribute.getBukkitAttribute());
         } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var5) {
            return null;
         }
      }
   }
}
