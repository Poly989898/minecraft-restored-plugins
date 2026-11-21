package io.lumine.mythic.bukkit.utils.config.properties.types;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.menu.EditableIcon;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.MenuBuilder;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class MenuProp<T> extends PropertyType<EditableMenuBuilder<T>> {
   private MenuBuilder<T> defaultValue;
   private LangProp DISPLAY;
   private StringListProp SCHEMA;
   private NodeListProp ICON_NODES;
   private IntProp SIZE;
   private BooleanProp STATIC;
   private IntProp UPDATE_INTERVAL;

   public MenuProp(LuminePlugin plugin, Object file, String node, MenuBuilder<T> defaultValue) {
      super(plugin, file, node);
      this.DISPLAY = new LangProp(plugin, file, node + ".Display", "Menu");
      this.SCHEMA = new StringListProp(plugin, file, node + ".Schema");
      this.ICON_NODES = new NodeListProp(plugin, file, node + ".Icons");
      this.SIZE = new IntProp(plugin, file, node + ".Size", -1);
      this.STATIC = new BooleanProp(plugin, file, node + ".Static", false);
      this.UPDATE_INTERVAL = new IntProp(plugin, file, node + ".UpdateInterval", 1);
      this.defaultValue = defaultValue;
   }

   protected EditableMenuBuilder<T> compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.getDefault();
      } else {
         String display = this.DISPLAY.get(section);
         int size = this.SIZE.get(section);
         Map<String, EditableIcon<T>> icons = new HashMap<>();
         List<String> schema = this.SCHEMA.get(section);
         Map<String, List<Integer>> schemaMap = Maps.newHashMap();
         List<Integer> schemaDataIcons = Lists.newArrayList();
         List<Integer> schemaSecondaryDataIcons = Lists.newArrayList();
         List<Integer> schemaFreeIcons = Lists.newArrayList();
         if (schema != null && !schema.isEmpty()) {
            int lineNumber = 0;

            for (String line : schema) {
               int i = 0;

               for (String key : line.split(" ")) {
                  if (key.equals("$")) {
                     schemaDataIcons.add(lineNumber * 9 + i);
                  } else if (key.equals("%")) {
                     schemaSecondaryDataIcons.add(lineNumber * 9 + i);
                  } else if (key.equals("~")) {
                     schemaFreeIcons.add(lineNumber * 9 + i);
                  } else {
                     if (!schemaMap.containsKey(key)) {
                        schemaMap.put(key, new ArrayList<>());
                     }

                     schemaMap.get(key).add(lineNumber * 9 + i);
                  }

                  if (++i >= 9) {
                     break;
                  }
               }

               if (++lineNumber > 6) {
                  break;
               }
            }
         }

         if (!schemaMap.isEmpty()) {
            for (String key : this.ICON_NODES.get(section)) {
               IconProp<T> iconProp = new IconProp<>(super.plugin, super.scope, node + ".Icons." + key);
               ConfigurationSectionProp configProp = new ConfigurationSectionProp(super.plugin, super.scope, node + ".Icons." + key);
               StringProp slotsProp = new StringProp(super.plugin, super.scope, node + ".Icons." + key + ".Mapping");
               String strSlots = slotsProp.get(section);
               List<Integer> slots = schemaMap.get(strSlots);
               IconBuilder<T> iconBuilder = iconProp.get(section);
               iconBuilder.hideFlags();
               icons.put(key, new EditableIcon<>(iconBuilder, slots, configProp.get(section)));
            }
         } else {
            for (String key : this.ICON_NODES.get(section)) {
               IconProp<T> iconProp = new IconProp<>(super.plugin, super.scope, node + ".Icons." + key);
               ConfigurationSectionProp configProp = new ConfigurationSectionProp(super.plugin, super.scope, node + ".Icons." + key);
               StringProp slotsProp = new StringProp(super.plugin, super.scope, node + ".Icons." + key + ".Slots");
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
               icons.put(key, new EditableIcon<>(iconBuilder, slots, configProp.get(section)));
            }
         }

         MenuBuilder<T> menuBuilder = MenuBuilder.<T>create().title(display);
         if (size > 0) {
            menuBuilder.size(size);
         }

         menuBuilder.setStatic(this.STATIC.get(section));
         menuBuilder.setUpdateInterval(this.UPDATE_INTERVAL.get(section));
         return new EditableMenuBuilder<>(section, menuBuilder, icons, schemaDataIcons, schemaSecondaryDataIcons, schemaFreeIcons);
      }
   }

   protected EditableMenuBuilder<T> getDefaultValue() {
      return null;
   }

   public void set(String holder, EditableMenuBuilder<T> value) {
      if (this.config != null) {
         ;
      }
   }

   private EditableMenuBuilder getDefault() {
      return null;
   }
}
