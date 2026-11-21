package io.lumine.mythic.bukkit.utils.config.properties.types;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.menu.EditableIcon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.hotbar.EditableHotbarMenuBuilder;
import io.lumine.mythic.bukkit.utils.menu.hotbar.HotbarMenuBuilder;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class HotbarMenuProp<T> extends PropertyType<EditableHotbarMenuBuilder<T>> {
   private HotbarMenuBuilder<T> defaultValue;
   private StringProp SCHEMA;
   private NodeListProp ICON_NODES;

   public HotbarMenuProp(LuminePlugin plugin, Object file, String node, HotbarMenuBuilder<T> defaultValue) {
      super(plugin, file, node);
      this.SCHEMA = new StringProp(plugin, file, node + ".Schema", null);
      this.ICON_NODES = new NodeListProp(plugin, file, node + ".Icons");
      this.defaultValue = defaultValue;
   }

   protected EditableHotbarMenuBuilder<T> compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.getDefault();
      } else {
         Map<String, EditableIcon<T>> icons = new HashMap<>();
         String schema = this.SCHEMA.get(section);
         Map<String, List<Integer>> schemaMap = new HashMap<>();
         List<Integer> schemaFreeIcons = Lists.newArrayList();
         if (schema != null) {
            int i = 0;

            for (String key : schema.split(" ")) {
               if (key.equals("~")) {
                  schemaFreeIcons.add(i);
               } else {
                  if (!schemaMap.containsKey(key)) {
                     schemaMap.put(key, new ArrayList<>());
                  }

                  schemaMap.get(key).add(i);
               }

               if (++i >= 9) {
                  break;
               }
            }
         }

         if (!schemaMap.isEmpty()) {
            for (String key : this.ICON_NODES.get(section)) {
               IconProp<T> iconProp = new IconProp<>(super.plugin, super.file, node + ".Icons." + key);
               ConfigurationSectionProp configProp = new ConfigurationSectionProp(super.plugin, super.scope, node + ".Icons." + key);
               StringProp slotsProp = new StringProp(super.plugin, super.file, node + ".Icons." + key + ".Mapping");
               String strSlots = slotsProp.get(section);
               if (schemaMap.containsKey(strSlots)) {
                  List<Integer> slots = schemaMap.get(strSlots);
                  IconBuilder<T> iconBuilder = iconProp.get(section);
                  iconProp.get(section);
                  icons.put(key, new EditableIcon<>(iconBuilder, slots, configProp.get(section)));
               }
            }
         } else {
            for (String keyx : this.ICON_NODES.get(section)) {
               IconProp<T> iconProp = new IconProp<>(super.plugin, super.file, node + ".Icons." + keyx);
               ConfigurationSectionProp configProp = new ConfigurationSectionProp(super.plugin, super.scope, node + ".Icons." + keyx);
               StringProp slotsProp = new StringProp(super.plugin, super.file, node + ".Icons." + keyx + ".Slots");
               String strSlots = slotsProp.get(section);
               List<Integer> slots = new ArrayList<>();
               String[] split = strSlots.split(",");

               for (String s : split) {
                  try {
                     int i = Integer.valueOf(s);
                     slots.add(i);
                  } catch (Error | Exception e) {
                     e.printStackTrace();
                  }
               }

               IconBuilder<T> iconBuilder = iconProp.get(section);
               iconProp.get(section);
               icons.put(keyx, new EditableIcon<>(iconBuilder, slots, configProp.get(section)));
            }
         }

         return new EditableHotbarMenuBuilder<>(HotbarMenuBuilder.create(), icons, schemaFreeIcons);
      }
   }

   protected EditableHotbarMenuBuilder<T> getDefaultValue() {
      return null;
   }

   public void set(String holder, EditableHotbarMenuBuilder<T> value) {
      if (this.config != null) {
         ;
      }
   }

   private EditableHotbarMenuBuilder getDefault() {
      return null;
   }
}
