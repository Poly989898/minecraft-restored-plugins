package io.lumine.mythic.bukkit.commands.items;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.utils.jnbt.ByteArrayTag;
import io.lumine.mythic.core.utils.jnbt.ByteTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.DoubleTag;
import io.lumine.mythic.core.utils.jnbt.FloatTag;
import io.lumine.mythic.core.utils.jnbt.IntArrayTag;
import io.lumine.mythic.core.utils.jnbt.IntTag;
import io.lumine.mythic.core.utils.jnbt.ListTag;
import io.lumine.mythic.core.utils.jnbt.LongTag;
import io.lumine.mythic.core.utils.jnbt.ShortTag;
import io.lumine.mythic.core.utils.jnbt.StringTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ImportCommand extends Command<MythicBukkit> {
   public ImportCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 1) {
         CommandHelper.sendError(sender, "You must specify a name for this item.");
         return true;
      } else {
         String name = args[0];
         String fileName = name + ".yml";
         if (args.length > 1) {
            fileName = args[1];
         }

         Player player = (Player)sender;
         ItemStack item = player.getInventory().getItemInHand();
         if (item != null && item.getType() != Material.AIR) {
            File basePath = this.plugin.getDataFolder();
            File itemsFolder = new File(basePath, "items");
            if (!itemsFolder.exists() || !itemsFolder.isDirectory()) {
               itemsFolder = new File(basePath, "Items");
               if (!itemsFolder.exists() || !itemsFolder.isDirectory()) {
                  itemsFolder = new File(basePath, "items");
                  itemsFolder.mkdir();
               }
            }

            if (!fileName.endsWith(".yml")) {
               fileName = fileName + ".yml";
            }

            File file = new File(itemsFolder, fileName);
            if (!file.exists()) {
               try {
                  if (!file.createNewFile()) {
                     CommandHelper.sendError(sender, "Failed to create new file for Item Import.");
                     return true;
                  }
               } catch (IOException e) {
                  CommandHelper.sendError(sender, "Failed to create new file for Item Import.");
                  e.printStackTrace();
               }
            }

            MythicConfig mc = new MythicConfigImpl(name, file);
            mc.load();
            mc.set("Material", item.getType().toString());
            if (item.hasData(DataComponentTypes.ITEM_MODEL)) {
               mc.set("Model", (item.getData(DataComponentTypes.ITEM_MODEL)).toString());
            }

            if (item.hasData(DataComponentTypes.CUSTOM_MODEL_DATA)) {
               CustomModelData data = item.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
               if (data.floats().size() == 1 && data.flags().isEmpty() && data.strings().isEmpty() && data.colors().isEmpty()) {
                  Float value = data.floats().getFirst();
                  if (isWholeNumber(value)) {
                     mc.set("CustomModelData", value.intValue());
                  } else {
                     mc.set("CustomModelData", List.of("float/" + formatFloat(value)));
                  }
               } else {
                  ArrayList<String> entries = new ArrayList<>();

                  for (Float value : data.floats()) {
                     entries.add("float/" + formatFloat(value));
                  }

                  for (String value : data.strings()) {
                     entries.add("string/" + value);
                  }

                  for (Boolean value : data.flags()) {
                     entries.add("boolean/" + value);
                  }

                  for (Color value : data.colors()) {
                     entries.add("color/" + formatColor(value));
                  }

                  mc.set("CustomModelData", entries);
               }
            }

            if (item.hasData(DataComponentTypes.CUSTOM_NAME)) {
               Component component = item.getData(DataComponentTypes.CUSTOM_NAME);
               mc.set("Display", Text.toReadable(component));
            }

            if (item.hasData(DataComponentTypes.LORE)) {
               List<String> lines = Lists.newArrayList();
               ItemLore data = item.getData(DataComponentTypes.LORE);

               for (Component line : data.lines()) {
                  lines.add(Text.toReadable(line));
               }

               mc.set("Lore", lines);
            }

            if (item.hasData(DataComponentTypes.DAMAGE)) {
               mc.set("Damage", item.getData(DataComponentTypes.DAMAGE));
            }

            if (item.hasData(DataComponentTypes.ENCHANTMENTS)) {
               List<String> lines = Lists.newArrayList();
               ItemEnchantments data = item.getData(DataComponentTypes.ENCHANTMENTS);

               for (Entry<Enchantment, Integer> entry : data.enchantments().entrySet()) {
                  String key = entry.getKey().getKey().toString();
                  Integer level = entry.getValue();
                  lines.add(key + ": " + level);
               }

               mc.set("Enchantments", lines);
            }

            CompoundTag nbt = this.getPlugin().getVolatileCodeHandler().getItemHandler().getNBTData(item);
            if (nbt != null && !nbt.getValue().isEmpty()) {
               importNBT(mc, "NBT", nbt);
            }

            mc.save();
            MythicItem mi = new MythicItem(this.getPlugin().getPackManager().getBasePack(), file, name, mc);
            this.getPlugin().getItemManager().registerItem(name, mi);
            CommandHelper.sendSuccess(sender, "Item '" + name + "' imported successfully to file " + fileName);
            return true;
         } else {
            CommandHelper.sendError(sender, "You must be holding an item to import.");
            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.items.import";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "import";
   }

   private static void importNBT(MythicConfig mc, String parentKey, CompoundTag compoundTag) {
      for (Entry<String, Tag> entry : compoundTag.getValue().entrySet()) {
         String key = entry.getKey();
         Tag tag = entry.getValue();
         String path = parentKey != null && !parentKey.isEmpty() ? parentKey + "." + key : key;
         writeTag(mc, path, tag);
      }
   }

   private static void writeTag(MythicConfig mc, String path, Tag tag) {
      if (tag instanceof CompoundTag ct) {
         importNBT(mc, path, ct);
      } else if (tag instanceof ListTag lt) {
         writeList(mc, path, lt);
      } else {
         if (tag instanceof StringTag st) {
            mc.set(path, st.getValue());
         } else if (tag instanceof IntTag it) {
            mc.set(path, "int/" + it.getValue());
         } else if (tag instanceof LongTag ltg) {
            mc.set(path, "long/" + ltg.getValue());
         } else if (tag instanceof ShortTag sht) {
            mc.set(path, "short/" + sht.getValue());
         } else if (tag instanceof ByteTag bt) {
            mc.set(path, "byte/" + bt.getValue());
         } else if (tag instanceof FloatTag ft) {
            mc.set(path, "float/" + ft.getValue());
         } else if (tag instanceof DoubleTag dt) {
            mc.set(path, "double/" + dt.getValue());
         } else if (tag instanceof IntArrayTag iat) {
            int[] arr = iat.getValue();
            List<String> out = Arrays.stream(arr).mapToObj(i -> "int/" + i).collect(Collectors.toList());
            mc.set(path, out);
         } else if (tag instanceof ByteArrayTag bat) {
            byte[] arr = bat.getValue();
            List<String> out = new ArrayList<>(arr.length);

            for (byte b : arr) {
               out.add("byte/" + b);
            }

            mc.set(path, out);
         } else {
            mc.set(path, String.valueOf(tag.getValue()));
         }
      }
   }

   private static void writeList(MythicConfig mc, String path, ListTag list) {
      List<Tag> values = list.getValue();
      if (values.isEmpty()) {
         mc.set(path, Collections.emptyList());
      } else if (values.get(0) instanceof CompoundTag) {
         for (int i = 0; i < values.size(); i++) {
            Tag t = values.get(i);
            if (!(t instanceof CompoundTag ct)) {
               mc.set(path, values.stream().map(ImportCommand::scalarFor).collect(Collectors.toList()));
               return;
            }

            importNBT(mc, path + "." + i, ct);
         }
      } else {
         List<Object> out = new ArrayList<>(values.size());

         for (Tag t : values) {
            out.add(scalarFor(t));
         }

         mc.set(path, out);
      }
   }

   private static Object scalarFor(Tag t) {
      if (t instanceof StringTag st) {
         return st.getValue();
      } else if (t instanceof IntTag it) {
         return "int/" + it.getValue();
      } else if (t instanceof LongTag ltg) {
         return "long/" + ltg.getValue();
      } else if (t instanceof ShortTag sht) {
         return "short/" + sht.getValue();
      } else if (t instanceof ByteTag bt) {
         return "byte/" + bt.getValue();
      } else if (t instanceof FloatTag ft) {
         return "float/" + ft.getValue();
      } else if (t instanceof DoubleTag dt) {
         return "double/" + dt.getValue();
      } else if (t instanceof ByteArrayTag) {
         return "<byte-array>";
      } else if (t instanceof IntArrayTag) {
         return "<int-array>";
      } else if (t instanceof CompoundTag) {
         return "<compound>";
      } else {
         return t instanceof ListTag ? "<list>" : String.valueOf(t.getValue());
      }
   }

   private static boolean isWholeNumber(float value) {
      return value == Math.rint(value) && value <= 2.1474836E9F && value >= -2.1474836E9F;
   }

   private static String formatFloat(float value) {
      return isWholeNumber(value) ? Integer.toString((int)value) : Float.toString(value);
   }

   private static String formatColor(Color color) {
      return String.format(Locale.ROOT, "#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
   }
}
