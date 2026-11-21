package io.lumine.mythic.bukkit.entities;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.entities.properties.TameableEntityType;
import io.lumine.mythic.bukkit.entities.properties.TameableProperty;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ZombieNautilus;
import org.bukkit.entity.ZombieNautilus.Variant;
import org.bukkit.inventory.ArmoredSaddledMountInventory;
import org.bukkit.inventory.ItemStack;

public class BukkitZombieNautilus extends BukkitEntityType implements TameableEntityType {
   private AgeableProperty ageableProperty;
   @Getter
   private TameableProperty tameableProperty;
   private boolean saddled;
   private boolean tamed;
   private NamespacedKey variant;

   @Override
   public EntityType getBukkitType() {
      return EntityType.ZOMBIE_NAUTILUS;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.tameableProperty = new TameableProperty(mc, Sets.immutableEnumSet(Material.CACTUS, new Material[0]));
      this.saddled = mc.getBoolean("Options.Saddled", false);
      this.tamed = mc.getBoolean("Options.Tamed", false);
      String variant = mc.getNamespacedString(new String[]{"Variant", "Options.Variant"}, null);
      if (variant != null) {
         this.variant = NamespacedKey.fromString(variant);
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      ZombieNautilus mob = (ZombieNautilus)entity;
      this.ageableProperty.applyProperties(entity);
      ArmoredSaddledMountInventory inventory = mob.getInventory();
      if (this.saddled) {
         inventory.setSaddle(new ItemStack(Material.SADDLE));
      }

      if (this.tamed) {
         mob.setTamed(true);
      }

      if (this.variant != null) {
         Variant variant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ZOMBIE_NAUTILUS_VARIANT).get(this.variant);
         if (variant != null) {
            mob.setVariant(variant);
         }
      }

      return mob;
   }
}
