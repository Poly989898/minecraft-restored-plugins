package io.lumine.mythic.core.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.items.ItemManager;
import io.lumine.mythic.api.items.ItemSupplier;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.properties.types.NodeListProp;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.lib.lang3.text.WordUtils;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.config.IOHandler;
import io.lumine.mythic.core.config.IOLoader;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.constants.ItemKeys;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.packs.PackExecutor;
import io.lumine.mythic.core.utils.TemplateUtil;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTagBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemExecutor extends ReloadableModule<MythicBukkit> implements ItemManager, Listener {
   private List<File> itemFiles;
   private IOLoader<MythicBukkit> defaultItems;
   private List<IOLoader<MythicBukkit>> itemLoaders;
   private Map<String, MythicItem> items = Maps.newConcurrentMap();
   private Map<String, ItemGroup> groups = Maps.newConcurrentMap();
   private Map<String, ItemSupplier> itemSuppliers = new ConcurrentHashMap<>();

   public ItemExecutor(MythicBukkit plugin) {
      super(plugin, false);
   }

   public void load(MythicBukkit plugin) {
      long startMillis = System.currentTimeMillis();
      this.loadPredefinedItemGroups(plugin);
      this.loadItems(plugin);
      this.loadItemGroups(plugin);
      long endMillis = System.currentTimeMillis();
      MythicLogger.log("All items loaded after " + (endMillis - startMillis) + "ms!");
   }

   @Override
   public void unload() {
      this.itemFiles = null;
      this.defaultItems = null;
      this.itemLoaders = null;
      this.groups.clear();
      this.items.clear();
   }

   private void loadItems(MythicBukkit plugin) {
      MythicLogger.log("Loading Items...");
      this.defaultItems = new IOLoader<>(MythicBukkit.inst(), "ExampleItems.yml", "Items");
      this.itemFiles = IOHandler.getAllFiles(this.defaultItems.getFile().getParent());
      this.items.clear();
      ArrayList<TemplateUtil.TemplateItem> itemsUsingTemplates = Lists.newArrayList();

      for (PackExecutor.ConfigFile configFile : plugin.getPackManager().getConfigFiles(ConfigFileType.ITEM)) {
         MythicConfigImpl fileConfig = new MythicConfigImpl(configFile.file());

         for (String node : NodeListProp.getNodes(configFile.file(), "")) {
            try {
               MythicConfig config = fileConfig.getNestedConfig(node);
               if (!configFile.file().getParentFile().getName().equals("mono")
                  || (!config.isSet("Type") || MythicEntityType.get(config.getString("Type")) == null) && (!config.isSet("Skills") || config.isSet("Id"))) {
                  String maybeTemplate = config.getString("Template");
                  if (maybeTemplate == null) {
                     maybeTemplate = config.getString("Templates");
                  }

                  if (maybeTemplate != null) {
                     itemsUsingTemplates.add(new TemplateUtil.TemplateItem(configFile.file(), configFile.pack(), node, config, maybeTemplate));
                  } else {
                     MythicItem mi = new MythicItem(configFile.pack(), configFile.file(), node, config);
                     configFile.pack().addItem();
                     this.registerItem(node, mi);
                  }
               }
            } catch (Error | Exception e) {
               MythicLogger.error("Error loading item '" + node + "'. Enable debugging for a stack trace.");
               if (this.getPlugin().getConfiguration().getDebugLevel() > 0) {
                  e.printStackTrace();
               }
            }
         }
      }

      TemplateUtil.handleTemplates(itemsUsingTemplates, this.items);
   }

   private void loadPredefinedItemGroups(MythicBukkit plugin) {
      this.groups.put("OTHER", new ItemGroup("OTHER", "Other", ItemFactory.of(Material.IRON_SWORD)));
   }

   private void loadItemGroups(MythicBukkit plugin) {
      MythicLogger.log("Loading Item Groups...");

      for (MythicItem item : this.items.values()) {
         String group = item.getGroup();
         if (group == null) {
            group = "OTHER";
         } else {
            group = group.toUpperCase(Locale.ROOT);
         }

         if (!this.groups.containsKey(group)) {
            this.groups.put(group, new ItemGroup(group, WordUtils.capitalizeFully(group), ItemFactory.of(item.getMaterial()).model(item.getCustomModelData())));
         }
      }
   }

   @Override
   public Collection<MythicItem> getItems() {
      return Collections.unmodifiableCollection(this.items.values());
   }

   @Override
   public Collection<String> getItemNames() {
      return Collections.unmodifiableCollection(this.items.keySet());
   }

   public Collection<ItemGroup> getItemGroups() {
      return this.groups.values();
   }

   public Collection<String> getItemGroupNames() {
      return this.groups.keySet();
   }

   @Override
   public Optional<MythicItem> getItem(String name) {
      return Optional.ofNullable(this.items.getOrDefault(name, null));
   }

   @Override
   public String getMythicTypeFromItem(ItemStack item) {
      return !this.isMythicItem(item) ? null : item.getItemMeta().getPersistentDataContainer().get(ItemKeys.TYPE, PersistentDataType.STRING);
   }

   @Override
   public boolean isMythicItem(ItemStack item) {
      if (item == null) {
         return false;
      } else {
         CompoundTag nbt = this.getPlugin().getVolatileCodeHandler().getItemHandler().getNBTData(item);
         if (nbt.containsKey("MYTHIC_TYPE")) {
            String mythicType = nbt.getString("MYTHIC_TYPE");
            boolean nbtPreventA = nbt.containsKey("PreventAnvil");
            boolean nbtPreventC = nbt.containsKey("PreventCrafting");
            boolean nbtPreventE = nbt.containsKey("PreventEnchanting");
            boolean nbtPreventS = nbt.containsKey("PreventSmithing");
            CompoundTagBuilder builder = nbt.createBuilder()
               .remove("MYTHIC_TYPE")
               .remove("PreventAnvil")
               .remove("PreventCrafting")
               .remove("PreventEnchanting")
               .remove("PreventSmithing");
            this.getPlugin().getVolatileCodeHandler().getItemHandler().setNBTData(item, builder.build());
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(ItemKeys.TYPE, PersistentDataType.STRING, mythicType);
            if (nbtPreventA) {
               pdc.set(ItemKeys.PREVENT_REPAIRING_WITH, PersistentDataType.BYTE, (byte)1);
            }

            if (nbtPreventC) {
               pdc.set(ItemKeys.PREVENT_CRAFTING_WITH, PersistentDataType.BYTE, (byte)1);
            }

            if (nbtPreventE) {
               pdc.set(ItemKeys.PREVENT_ENCHANTING, PersistentDataType.BYTE, (byte)1);
            }

            if (nbtPreventS) {
               pdc.set(ItemKeys.PREVENT_SMITHING_WITH, PersistentDataType.BYTE, (byte)1);
            }

            item.setItemMeta(meta);
         }

         return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(ItemKeys.TYPE);
      }
   }

   @Override
   public void registerItemSupplier(ItemSupplier supplier) {
      this.itemSuppliers.put(supplier.getNamespace().toLowerCase(Locale.ROOT), supplier);
   }

   public boolean isMMOItem(ItemStack item) {
      return this.getPlugin().getVolatileCodeHandler().getItemHandler().getNBTData(item).containsKey("MMOITEMS_ITEM_ID");
   }

   public boolean hasCustomDurability(ItemStack item) {
      return this.getPlugin().getVolatileCodeHandler().getItemHandler().getNBTData(item).containsKey("MYTHIC_MAX_CUSTOM_DURABILITY");
   }

   public int getMaxCustomDurability(ItemStack item) {
      return this.getPlugin().getVolatileCodeHandler().getItemHandler().getNBTData(item).getInt("MYTHIC_MAX_CUSTOM_DURABILITY");
   }

   public int getCurrentCustomDurability(ItemStack item) {
      return this.getPlugin().getVolatileCodeHandler().getItemHandler().getNBTData(item).getInt("MYTHIC_CURRENT_CUSTOM_DURABILITY");
   }

   public boolean getEnchantingDisabled(ItemStack item) {
      return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(ItemKeys.PREVENT_ENCHANTING);
   }

   public boolean getAnvilDisabled(ItemStack item) {
      return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(ItemKeys.PREVENT_REPAIRING_WITH);
   }

   public boolean getCraftingDisabled(ItemStack item) {
      return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(ItemKeys.PREVENT_CRAFTING_WITH);
   }

   public boolean getSmithingDisabled(ItemStack item) {
      return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(ItemKeys.PREVENT_SMITHING_WITH);
   }

   public ItemStack getItemStack(String name) {
      return this.getItemStack(name, 1);
   }

   public ItemStack getItemStack(String name, int amount) {
      Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(name);
      if (maybeItem.isPresent()) {
         return BukkitAdapter.adapt(maybeItem.get().generateItemStack(amount));
      } else {
         try {
            return new ItemStack(Material.valueOf(name.toUpperCase(Locale.ROOT)));
         } catch (Exception e) {
            return null;
         }
      }
   }

   public boolean registerItem(String internalName, MythicItem item) {
      if (this.items.containsKey(internalName)) {
         return false;
      } else {
         this.items.put(internalName, item);
         return true;
      }
   }

   public void deleteItem(MythicItem item) {
      MythicLogger.log("Deleting MythicItem: {0}", item.getInternalName());
      this.items.remove(item.getInternalName());
      item.getConfig().deleteNodeAndSave();
   }

   @Override
   public Collection<MythicItem> filterItems(Collection<MythicItem> items, String ffilter) {
      String filter = ffilter.toUpperCase(Locale.ROOT);
      items.removeIf(mi -> {
         boolean remove = true;
         if (mi.getInternalName().toUpperCase(Locale.ROOT).contains(filter)) {
            remove = false;
         } else if (mi.getDisplayName() != null && mi.getDisplayName().toUpperCase(Locale.ROOT).contains(filter)) {
            remove = false;
         }

         return remove;
      });
      return items;
   }
}
