package com.nexomc.libs.gui.builder.item;

import com.nexomc.libs.gui.components.exception.GuiException;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MapBuilder extends BaseItemBuilder<MapBuilder> {
   private static final Material MAP;

   MapBuilder() {
      super(new ItemStack(MAP));
   }

   MapBuilder(@NotNull ItemStack itemStack) {
      super(itemStack);
      if (itemStack.getType() != MAP) {
         throw new GuiException("MapBuilder requires the material to be a MAP!");
      }
   }

   @Contract("_ -> this")
   public @NotNull MapBuilder color(@Nullable Color color) {
      MapMeta mapMeta = (MapMeta)this.getMeta();
      mapMeta.setColor(color);
      this.setMeta(mapMeta);
      return this;
   }

   @Contract("_ -> this")
   public @NotNull MapBuilder locationName(@Nullable String name) {
      MapMeta mapMeta = (MapMeta)this.getMeta();
      mapMeta.setLocationName(name);
      this.setMeta(mapMeta);
      return this;
   }

   @Contract("_ -> this")
   public @NotNull MapBuilder scaling(boolean scaling) {
      MapMeta mapMeta = (MapMeta)this.getMeta();
      mapMeta.setScaling(scaling);
      this.setMeta(mapMeta);
      return this;
   }

   @Contract("_ -> this")
   public @NotNull MapBuilder view(@NotNull MapView view) {
      MapMeta mapMeta = (MapMeta)this.getMeta();
      mapMeta.setMapView(view);
      this.setMeta(mapMeta);
      return this;
   }

   static {
      MAP = Material.MAP;
   }
}
