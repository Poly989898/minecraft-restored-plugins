package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class IconProp<T> extends PropertyType<IconBuilder<T>> {
   private IconBuilder<T> defaultValue;
   private ItemProp ITEM;
   private BooleanProp HIDE_FLAGS;
   private StringProp CLICK_COMMAND;
   private BooleanProp CLICK_COMMAND_AS_PLAYER;
   private BooleanProp CLICK_COMMAND_AS_OP;
   private StringProp CLICK_SOUND;
   private DoubleProp CLICK_VOLUME;
   private DoubleProp CLICK_PITCH;
   private BooleanProp CLICK_CLOSE;

   public IconProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.ITEM = new ItemProp(plugin, file, node);
      this.HIDE_FLAGS = new BooleanProp(plugin, file, node + ".HideFlags", true);
      this.CLICK_COMMAND = new StringProp(plugin, file, node + ".Click.Command", null);
      this.CLICK_COMMAND_AS_PLAYER = new BooleanProp(plugin, file, node + ".Click.CommandAsPlayer", false);
      this.CLICK_COMMAND_AS_OP = new BooleanProp(plugin, file, node + ".Click.CommandASOp", false);
      this.CLICK_SOUND = new StringProp(plugin, file, node + ".Click.Sound", null);
      this.CLICK_VOLUME = new DoubleProp(plugin, file, node + ".Click.Volume", 1.0);
      this.CLICK_PITCH = new DoubleProp(plugin, file, node + ".Click.Pitch", 1.0);
      this.CLICK_CLOSE = new BooleanProp(plugin, file, node + ".Click.ThenClose", false);
      this.defaultValue = IconBuilder.<T>create().name("UNSET").material(Material.PAPER);
   }

   public IconProp(LuminePlugin plugin, Object file, String node, IconBuilder<T> defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected IconBuilder<T> compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else {
         ItemStack itemIcon = this.ITEM.get(section);
         boolean hideFlags = this.HIDE_FLAGS.get(section);
         String clickCommand = this.CLICK_COMMAND.get(section);
         boolean clickAsPlayer = this.CLICK_COMMAND_AS_PLAYER.get(section);
         boolean clickAsOp = this.CLICK_COMMAND_AS_OP.get(section);
         String clickSound = this.CLICK_SOUND.get(section);
         double clickVolume = this.CLICK_VOLUME.get(section);
         double clickPitch = this.CLICK_PITCH.get(section);
         boolean clickThenClose = this.CLICK_CLOSE.get(section);
         IconBuilder<T> builder = IconBuilder.create();
         builder.itemStack(itemIcon);
         if (hideFlags) {
            builder.hideFlags();
         } else {
            builder.hideFlags(false);
         }

         if (clickCommand != null) {
            builder.click((impl, player) -> {
               if (clickSound != null) {
                  player.playSound(player.getLocation(), clickSound, (float)clickVolume, (float)clickPitch);
               }

               String command = clickCommand.replace("<player.name>", player.getName());
               command = command.replace("<player.uuid>", player.getUniqueId().toString());
               if (clickAsPlayer) {
                  if (clickAsOp) {
                     boolean op = player.isOp();
                     player.setOp(true);

                     try {
                        player.performCommand(command);
                     } finally {
                        player.setOp(op);
                     }
                  } else {
                     player.performCommand(command);
                  }
               } else {
                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
               }

               if (clickThenClose) {
                  player.closeInventory();
               }
            });
         }

         return builder;
      }
   }

   public void set(String holder, IconBuilder<T> value) {
      if (this.config != null) {
         ;
      }
   }

   public IconBuilder<T> getDefaultValue() {
      return this.defaultValue;
   }
}
