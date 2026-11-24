package io.lumine.mythic.lib.gui;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.explorer.ChatInput;
import io.lumine.mythic.lib.api.explorer.ItemBuilder;
import io.lumine.mythic.lib.api.stat.handler.AttributeStatHandler;
import io.lumine.mythic.lib.util.ReflectionUtils;
import io.lumine.mythic.lib.version.VersionUtils;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttributeExplorer extends PluginInventory {
   private final Player target;
   private final boolean legacy;
   private Attribute explored;
   private List<AttributeModifier> modifiers;
   private int modifierOffset;
   private int attributeOffset;
   private static final int[] ATTRIBUTE_SLOTS = new int[]{37, 38, 39, 40, 41, 42, 43, 46, 47, 48, 49, 50, 51, 52};
   private static final int[] MODIFIER_SLOTS = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
   private final List<AttributeStatHandler> attributes;
   public static final DecimalFormat FORMAT = new DecimalFormat("0.#####");
   private static final Method AttributeModifier_getUniqueId = ReflectionUtils.getDeclaredMethod(AttributeModifier.class, "getUniqueId");
   private static final NamespacedKey ATTRIBUTE_KEY = new NamespacedKey(MythicLib.plugin, "attribute");
   private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(MythicLib.plugin, "modifier");

   public AttributeExplorer(Player player, Player target) {
      super(player);
      Validate.notNull(target, "Target cannot be null");
      this.target = target;
      this.legacy = MythicLib.plugin.getVersion().isUnder(1, 21);
      this.attributes = MythicLib.plugin
         .getStats()
         .getHandlers()
         .stream()
         .filter(handler -> handler instanceof AttributeStatHandler)
         .map(handler -> (AttributeStatHandler)handler)
         .collect(Collectors.toList());
   }

   public Attribute getExplored() {
      return this.explored;
   }

   public Player getTarget() {
      return this.target;
   }

   public boolean isLegacy() {
      return this.legacy;
   }

   @Override
   public Inventory getInventory() {
      Inventory inv = Bukkit.createInventory(
         this, 54, "Attributes of " + this.target.getName() + (this.explored == null ? "" : " (" + this.getName(this.explored) + ")")
      );
      inv.setItem(4, new ItemBuilder(Material.WHITE_BED, "&6Refresh &8(Click)"));
      int j = 0;

      while (j < Math.min(ATTRIBUTE_SLOTS.length, this.attributes.size() - this.attributeOffset)) {
         AttributeStatHandler handler = this.attributes.get(this.attributeOffset + j);
         AttributeInstance ins = this.target.getAttribute(handler.getAttribute());
         if (ins != null) {
            ItemStack item = new ItemStack(handler.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + this.getName(handler.getAttribute()));
            meta.addItemFlags(ItemFlag.values());
            if (MythicLib.plugin.getVersion().isAbove(1, 20, 5)) {
               VersionUtils.addEmptyAttributeModifier(meta);
            }

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + handler.getDescription());
            lore.add("");
            lore.add(ChatColor.GRAY + "Total Value: " + ChatColor.GOLD + ChatColor.BOLD + FORMAT.format(ins.getValue()));
            lore.add(ChatColor.GRAY + "▸" + " Base Value: " + ChatColor.GOLD + FORMAT.format(ins.getBaseValue()));
            lore.add(ChatColor.GRAY + "▸" + " Due to Modifiers: " + ChatColor.GOLD + FORMAT.format(ins.getValue() - ins.getBaseValue()));
            lore.add(ChatColor.GRAY + "▸" + " Default Value: " + ChatColor.GOLD + FORMAT.format(ins.getDefaultValue()));
            lore.add("");
            lore.add(ChatColor.GRAY + "Modifier Count: " + ChatColor.GOLD + ins.getModifiers().size());
            lore.add("");
            lore.add(ChatColor.YELLOW + "▸" + " Left click to explore.");
            lore.add(ChatColor.YELLOW + "▸" + " Right click to set the base value.");
            lore.add(ChatColor.YELLOW + "▸" + " Shift click to reset base value.");
            meta.getPersistentDataContainer().set(ATTRIBUTE_KEY, PersistentDataType.STRING, handler.getAttribute().name());
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(ATTRIBUTE_SLOTS[j++], item);
         }
      }

      ItemStack fillAttribute = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE, "&cNo Attribute");

      while (j < ATTRIBUTE_SLOTS.length) {
         inv.setItem(ATTRIBUTE_SLOTS[j++], fillAttribute);
      }

      if (this.attributeOffset + ATTRIBUTE_SLOTS.length < this.attributes.size()) {
         inv.setItem(53, new ItemBuilder(Material.ARROW, "&6Next Attributes"));
      }

      if (this.attributeOffset > 0) {
         inv.setItem(45, new ItemBuilder(Material.ARROW, "&6Previous Attributes"));
      }

      if (this.explored != null) {
         inv.setItem(1, new ItemBuilder(Material.WRITABLE_BOOK, "&6New Modifier.."));
         inv.setItem(7, new ItemBuilder(Material.BARRIER, "&6⇨ Back"));
         j = 0;

         while (j < Math.min(MODIFIER_SLOTS.length, this.modifiers.size() - this.modifierOffset)) {
            AttributeModifier modifier = this.modifiers.get(this.modifierOffset + j);
            ItemStack item = new ItemStack(Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Modifier n" + (j + 1));
            List<String> lore = new ArrayList<>();
            lore.add("");
            if (!this.legacy) {
               lore.add(ChatColor.GRAY + "Key: " + ChatColor.GOLD + modifier.getKey());
            } else {
               lore.add(ChatColor.GRAY + "UUID: " + ChatColor.GOLD + ReflectionUtils.invoke(AttributeModifier_getUniqueId, modifier));
               lore.add(ChatColor.GRAY + "Name: " + ChatColor.GOLD + modifier.getName());
            }

            lore.add(ChatColor.GRAY + "Amount: " + ChatColor.GOLD + modifier.getAmount());
            lore.add(ChatColor.GRAY + "Operation: " + ChatColor.GOLD + modifier.getOperation());
            if (!this.legacy) {
               lore.add(ChatColor.GRAY + "Slot Group: " + ChatColor.GOLD + modifier.getSlotGroup());
            } else {
               lore.add(ChatColor.GRAY + "Slot: " + ChatColor.GOLD + modifier.getSlot());
            }

            lore.add("");
            lore.add(ChatColor.YELLOW + "▸" + " Right click to remove.");
            meta.getPersistentDataContainer()
               .set(
                  MODIFIER_KEY,
                  PersistentDataType.STRING,
                  !this.legacy ? modifier.getKey().toString() : ReflectionUtils.invoke(AttributeModifier_getUniqueId, modifier).toString()
               );
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(MODIFIER_SLOTS[j++], item);
         }

         ItemStack fillModifier = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE, "&cNo Modifier");

         while (j < MODIFIER_SLOTS.length) {
            inv.setItem(MODIFIER_SLOTS[j++], fillModifier);
         }

         if (this.modifierOffset + MODIFIER_SLOTS.length < this.modifiers.size()) {
            inv.setItem(26, new ItemBuilder(Material.ARROW, "&6Next Page"));
         }

         if (this.modifierOffset > 0) {
            inv.setItem(18, new ItemBuilder(Material.ARROW, "&6Previous Page"));
         }
      }

      return inv;
   }

   public void setExplored(Attribute attribute) {
      this.explored = attribute;
      this.modifiers = this.explored != null ? new ArrayList<>(this.target.getAttribute(this.explored).getModifiers()) : null;
   }

   private String getName(Attribute attribute) {
      return UtilityMethods.caseOnWords(attribute.name().replace("GENERIC_", "").replace("PLAYER_", "").toLowerCase().replace("_", " "));
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      event.setCancelled(true);
      if (event.getInventory().equals(event.getClickedInventory())) {
         ItemStack item = event.getCurrentItem();
         if (UtilityMethods.isMetaItem(item)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Refresh " + ChatColor.DARK_GRAY + "(Click)")) {
               this.setExplored(this.explored);
               this.open();
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "⇨" + " Back")) {
               this.setExplored(null);
               this.open();
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Next Page")) {
               this.modifierOffset = this.modifierOffset + MODIFIER_SLOTS.length;
               this.open();
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Previous Page")) {
               this.modifierOffset = this.modifierOffset - MODIFIER_SLOTS.length;
               this.open();
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Next Attributes")) {
               this.attributeOffset = this.attributeOffset + ATTRIBUTE_SLOTS.length;
               this.open();
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Previous Attributes")) {
               this.attributeOffset = this.attributeOffset - ATTRIBUTE_SLOTS.length;
               this.open();
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "New Modifier..")) {
               new AttributeCreator(this).open();
            } else {
               String tag = (String)item.getItemMeta().getPersistentDataContainer().get(MODIFIER_KEY, PersistentDataType.STRING);
               if (tag != null && event.getAction() == InventoryAction.PICKUP_HALF) {
                  AttributeModifier mod = !this.legacy
                     ? this.getPlayer().getAttribute(this.explored).getModifier(NamespacedKey.fromString(tag))
                     : getModifier(this.getPlayer().getAttribute(this.explored), UUID.fromString(tag));
                  Validate.notNull(mod, "Could not find attribute modifier with tag '" + tag + "'");
                  this.target.getAttribute(this.explored).removeModifier(mod);
                  this.getPlayer().sendMessage(ChatColor.YELLOW + "> Modifier successfully removed.");
                  this.setExplored(this.explored);
                  this.open();
               } else {
                  tag = (String)item.getItemMeta().getPersistentDataContainer().get(ATTRIBUTE_KEY, PersistentDataType.STRING);
                  if (tag != null) {
                     Attribute attribute = Attribute.valueOf(tag);
                     if (event.getAction() == InventoryAction.PICKUP_ALL) {
                        this.setExplored(attribute);
                        this.open();
                     } else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        this.target.getAttribute(attribute).setBaseValue(this.target.getAttribute(attribute).getDefaultValue());
                        this.getPlayer()
                           .sendMessage(ChatColor.YELLOW + "> Base value of " + ChatColor.GOLD + attribute.name() + ChatColor.YELLOW + " successfully reset.");
                        this.open();
                     } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                        this.getPlayer().closeInventory();
                        this.getPlayer().sendMessage(ChatColor.YELLOW + "> Write in the chat the value you want.");
                        new ChatInput(this.getPlayer(), output -> {
                           if (output == null) {
                              this.open();
                              return true;
                           }

                           double d;
                           try {
                              d = Double.parseDouble(output);
                           } catch (NumberFormatException exception) {
                              this.getPlayer().sendMessage(ChatColor.RED + "> " + output + " is not a valid number. Type 'cancel' to cancel.");
                              return false;
                           }

                           this.getPlayer().sendMessage(ChatColor.YELLOW + "> Base value set to " + ChatColor.GOLD + FORMAT.format(d) + ChatColor.YELLOW + ".");
                           this.target.getAttribute(attribute).setBaseValue(d);
                           this.open();
                           return true;
                        });
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   private static AttributeModifier getModifier(@NotNull AttributeInstance instance, @NotNull UUID uuid) {
      for (AttributeModifier modifier : instance.getModifiers()) {
         if (modifier.getUniqueId().equals(uuid)) {
            return modifier;
         }
      }

      return null;
   }
}
