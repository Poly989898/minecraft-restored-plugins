package io.lumine.mythic.bukkit.entities.properties.display;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;

public class ItemDisplayProperty extends DisplayProperty {
   private PlaceholderString item;
   private ItemDisplayTransform transform;

   public ItemDisplayProperty(MythicConfig config) {
      super(config);
      this.item = config.getPlaceholderString("DisplayOptions.Item", "grass_block");
      this.transform = config.getEnum("DisplayOptions.Transform", ItemDisplayTransform.class, ItemDisplayTransform.NONE);
   }

   @Override
   public Entity applyProperties(Entity entity) {
      entity = super.applyProperties(entity);
      if (entity instanceof ItemDisplay itemDisplay) {
         AbstractItemStack itemStack = MythicBukkit.inst().getBootstrap().createItem(this.item.get());
         itemDisplay.setItemStack(BukkitAdapter.adapt(itemStack));
         itemDisplay.setItemDisplayTransform(this.transform);
      }

      return entity;
   }
}
