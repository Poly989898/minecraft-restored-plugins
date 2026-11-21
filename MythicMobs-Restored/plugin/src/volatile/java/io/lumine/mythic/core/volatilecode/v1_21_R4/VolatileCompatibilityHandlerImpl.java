package io.lumine.mythic.core.volatilecode.v1_21_R4;

import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileCompatibilityHandler;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.Holder.c;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.animal.CowVariant;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftCow;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Wolf.Variant;

public class VolatileCompatibilityHandlerImpl implements VolatileCompatibilityHandler {
   public VolatileCompatibilityHandlerImpl(VolatileCodeHandler handler) {
   }

   @Override
   public Set<String> getWolfVariantValues(Wolf wolf) {
      Variant variant = wolf.getVariant();
      return VolatileCompatibilityHandler.variantValues(variant.toString(), variant.getKey());
   }

   @Override
   public Set<String> getCowVariantValues(Cow cow) {
      Holder<CowVariant> variant = ((CraftCow)cow).getHandle().p();
      MinecraftKey key = variant.e().<MinecraftKey>map(ResourceKey::a).orElse(null);
      return key == null
         ? Set.of()
         : VolatileCompatibilityHandler.variantValues(((CowVariant)variant.a()).toString(), NamespacedKey.fromString(key.toString()));
   }

   @Override
   public Set<String> getChickenVariantValues(Chicken chicken) {
      org.bukkit.entity.Chicken.Variant variant = chicken.getVariant();
      return VolatileCompatibilityHandler.variantValues(variant.toString(), variant.getKey());
   }

   @Override
   public Set<String> getPigVariantValues(Pig pig) {
      org.bukkit.entity.Pig.Variant variant = pig.getVariant();
      return VolatileCompatibilityHandler.variantValues(variant.toString(), variant.getKey());
   }

   @Override
   public void setCowVariant(Cow cow, String key) {
      key = VolatileCompatibilityHandler.normalizeRegistryKey(key);
      MinecraftKey location = MinecraftKey.c(key);
      if (location != null) {
         ResourceKey<CowVariant> variantKey = ResourceKey.a(Registries.aM, location);
         IRegistry<CowVariant> holderGetter = MinecraftServer.getServer().ba().f(Registries.aM);
         c<CowVariant> holder = (c<CowVariant>)holderGetter.a(variantKey).orElse(null);
         if (holder != null) {
            ((CraftCow)cow).getHandle().j(holder);
         }
      }
   }
}
