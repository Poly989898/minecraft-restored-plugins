package io.lumine.mythic.lib.gui;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.explorer.ChatInput;
import io.lumine.mythic.lib.api.explorer.ItemBuilder;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AttributeCreator extends PluginInventory {
   private final AttributeExplorer explorer;
   private final boolean legacy;
   private NamespacedKey namespacedKey;
   private double amount;
   private Operation operation;
   private EquipmentSlotGroup group;
   private UUID legacyUuid;
   private String legacyName;
   private EquipmentSlot legacySlot;

   public AttributeCreator(AttributeExplorer inv) {
      super(inv.getPlayer());
      this.explorer = inv;
      this.legacy = inv.isLegacy();
   }

   @Override
   public Inventory getInventory() {
      Inventory inv = Bukkit.createInventory(this, 36, "Attribute Creation..");
      if (!this.legacy) {
         inv.setItem(
            11,
            new ItemBuilder(Material.OAK_SIGN, "&6Name-Spaced Key")
               .setLore("", "&7Current Value: &6" + (this.namespacedKey == null ? "None" : this.namespacedKey))
         );
      } else {
         inv.setItem(
            10,
            new ItemBuilder(Material.COMMAND_BLOCK_MINECART, "&6UUID")
               .setLore("", "&7Current Value: &6" + (this.legacyUuid == null ? "None" : this.legacyUuid))
         );
         inv.setItem(11, new ItemBuilder(Material.OAK_SIGN, "&6Name").setLore("", "&7Current Value: &6" + (this.legacyName == null ? "None" : this.legacyName)));
      }

      inv.setItem(12, new ItemBuilder(Material.CHEST, "&6Amount").setLore("", "&7Current Value: &6" + this.amount));
      inv.setItem(
         13, new ItemBuilder(Material.REPEATER, "&6Operation").setLore("", "&7Current Value: &6" + (this.operation == null ? "None" : this.operation.name()))
      );
      if (!this.legacy) {
         inv.setItem(
            14,
            new ItemBuilder(Material.LEATHER_HELMET, "&6Slot Group").setLore("", "&7Current Value: &6" + (this.group == null ? "None" : this.group.toString()))
         );
      } else {
         inv.setItem(
            14,
            new ItemBuilder(Material.LEATHER_HELMET, "&6Slot").setLore("", "&7Current Value: &6" + (this.legacySlot == null ? "None" : this.legacySlot.name()))
         );
      }

      inv.setItem(30, new ItemBuilder(Material.BARRIER, "&6⇨ Back"));
      inv.setItem(32, new ItemBuilder(Material.GREEN_TERRACOTTA, "&aAdd Attribute!"));
      return inv;
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      event.setCancelled(true);
      if (event.getInventory().equals(event.getClickedInventory())) {
         ItemStack item = event.getCurrentItem();
         if (UtilityMethods.isMetaItem(item)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "⇨" + " Back")) {
               this.explorer.open();
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Add Attribute!")) {
               if (this.amount == 0.0) {
                  this.getPlayer().sendMessage(ChatColor.RED + "> You must specify a valid amount!");
                  return;
               }

               Operation operation = this.operation == null ? Operation.ADD_NUMBER : this.operation;
               AttributeModifier modifier;
               if (!this.legacy) {
                  NamespacedKey key = this.namespacedKey == null ? new NamespacedKey(MythicLib.plugin, UUID.randomUUID().toString()) : this.namespacedKey;
                  modifier = VersionUtils.attrMod(key, this.amount, operation);
               } else {
                  UUID uuid = this.legacyUuid == null ? UUID.randomUUID() : this.legacyUuid;
                  String name = this.legacyName == null ? "" : this.legacyName;
                  modifier = new AttributeModifier(uuid, name, this.amount, operation);
               }

               this.explorer.getTarget().getAttribute(this.explorer.getExplored()).addModifier(modifier);
               this.explorer.open();
               this.getPlayer().sendMessage(ChatColor.YELLOW + "> Attribute successfully added.");
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Name-Spaced Key")) {
               this.input("name-spaced key (eg `myplugin:iron_skin_pot`)", input -> {
                  this.namespacedKey = Objects.requireNonNull(NamespacedKey.fromString(input, MythicLib.plugin));
                  return this.namespacedKey.toString();
               });
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Slot Group")) {
               for (EquipmentSlot slot : EquipmentSlot.values()) {
                  this.getPlayer().sendMessage(ChatColor.YELLOW + "- " + slot.name().toLowerCase());
               }

               this.input("slot group", input -> {
                  this.group = EquipmentSlotGroup.getByName(UtilityMethods.enumName(input));
                  return this.group.toString();
               });
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Operation")) {
               for (Operation operation : Operation.values()) {
                  this.getPlayer().sendMessage(ChatColor.YELLOW + "- " + operation.name().toLowerCase());
               }

               this.input("operation", input -> {
                  this.operation = Operation.valueOf(UtilityMethods.enumName(input));
                  return this.operation.name();
               });
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Amount")) {
               this.input("amount", input -> {
                  this.amount = Double.parseDouble(input);
                  return AttributeExplorer.FORMAT.format(this.amount);
               });
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Slot")) {
               for (EquipmentSlot slot : EquipmentSlot.values()) {
                  this.getPlayer().sendMessage(ChatColor.YELLOW + "- " + slot.name());
               }

               this.input("slot", input -> {
                  this.legacySlot = EquipmentSlot.valueOf(UtilityMethods.enumName(input));
                  return this.legacySlot.name();
               });
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "UUID")) {
               this.input("UUID", input -> {
                  this.legacyUuid = UUID.fromString(input);
                  return this.legacyUuid.toString();
               });
            } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Name")) {
               this.input("name", input -> this.legacyName = input);
            }
         }
      }
   }

   private void input(@NotNull String inputChatMessageObject, @NotNull Function<String, String> inputHandler) {
      this.getPlayer().closeInventory();
      this.getPlayer().sendMessage(ChatColor.YELLOW + "Write in the chat the desired " + inputChatMessageObject + ".");
      new ChatInput(this.getPlayer(), input -> {
         try {
            String stringResult = inputHandler.apply(input);
            this.getPlayer().sendMessage(ChatColor.YELLOW + "Value set to " + ChatColor.GOLD + stringResult + ChatColor.YELLOW + ".");
            this.open();
            return true;
         } catch (Exception exception) {
            this.getPlayer().sendMessage(ChatColor.RED + "'" + input + "' is not a valid input. Type 'cancel' to cancel.");
            return false;
         }
      }, this::open);
   }
}
