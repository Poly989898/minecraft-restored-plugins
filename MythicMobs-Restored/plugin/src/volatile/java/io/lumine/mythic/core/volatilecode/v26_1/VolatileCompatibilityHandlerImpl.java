package io.lumine.mythic.core.volatilecode.v26_1;

import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileCompatibilityHandler;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.animal.cow.CowVariant;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftCow;
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
      Holder<CowVariant> variant = ((CraftCow)cow).getHandle().getVariant();
      Identifier key = variant.unwrapKey().<Identifier>map(ResourceKey::identifier).orElse(null);
      return key == null
         ? Set.of()
         : VolatileCompatibilityHandler.variantValues(((CowVariant)variant.value()).toString(), NamespacedKey.fromString(key.toString()));
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
      Identifier location = Identifier.tryParse(key);
      if (location != null) {
         ResourceKey<CowVariant> variantKey = ResourceKey.create(Registries.COW_VARIANT, location);
         Registry<CowVariant> holderGetter = MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.COW_VARIANT);
         Reference<CowVariant> holder = (Reference<CowVariant>)holderGetter.get(variantKey).orElse(null);
         if (holder != null) {
            ((CraftCow)cow).getHandle().setVariant(holder);
         }
      }
   }
}
