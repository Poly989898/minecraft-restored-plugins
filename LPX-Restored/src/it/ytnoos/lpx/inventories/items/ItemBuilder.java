package it.ytnoos.lpx.inventories.items;

import com.google.common.collect.Lists;
import it.ytnoos.lpx.ClassKeyDerivation;
import it.ytnoos.lpx.utils.string.MessageFormatter;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
   private final ItemStack itemStack;
   private static final long a = ClassKeyDerivation.a(-5250232644462480517L, 4648352961733344520L, MethodHandles.lookup().lookupClass()).a(211145204153448L);

   public ItemBuilder(Material material) {
      this(material, 1);
   }

   public ItemBuilder(ItemStack itemStack2) {
      this.itemStack = itemStack2;
   }

   public ItemBuilder(Material material, int value) {
      this.itemStack = new ItemStack(material, value);
   }

   public static ItemBuilder of(Material material) {
      return new ItemBuilder(material);
   }

   public ItemBuilder hideAttributes() {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      if (itemMeta != null) {
         itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
      }

      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public ItemBuilder amount(int value) {
      this.itemStack.setAmount(value);
      return this;
   }

   public ItemBuilder name(String string) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      if (itemMeta != null) {
         itemMeta.setDisplayName(MessageFormatter.toLegacy(string));
      }

      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int value) {
      this.itemStack.addUnsafeEnchantment(enchantment, value);
      return this;
   }

   public ItemBuilder removeEnchantment(Enchantment enchantment) {
      this.itemStack.removeEnchantment(enchantment);
      return this;
   }

   public ItemBuilder skullOwner(String string, long longValue) {
      long longValue2 = longValue;
      longValue2 = a ^ longValue2;
      SkullMeta skullMeta = (SkullMeta)this.itemStack.getItemMeta();
      if (skullMeta != null) {
         skullMeta.setOwner(string);
      }

      this.itemStack.setItemMeta(skullMeta);
      if (1L <= longValue2) {
      }

      return this;
   }

   public ItemBuilder enchant(long longValue, Enchantment enchantment, int value) {
      long longValue2 = longValue;
      longValue2 = a ^ longValue2;
      if (value == 0) {
         return this;
      }

      ItemMeta itemMeta = this.itemStack.getItemMeta();
      if (1455275723581818655L >= longValue2) {
      }

      if (itemMeta != null) {
         itemMeta.addEnchant(enchantment, value, true);
         this.itemStack.setItemMeta(itemMeta);
      }

      return this;
   }

   public ItemBuilder maxDurability() {
      this.itemStack.setDurability((short)32767);
      return this;
   }

   public ItemBuilder lore(String[] strings) {
      return this.lore(Lists.newArrayList(strings));
   }

   public ItemBuilder lore(List<String> list) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      list.add(0, "");
      if (itemMeta != null) {
         itemMeta.setLore(list.stream().map(MessageFormatter::toLegacy).collect(Collectors.toList()));
      }

      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public ItemStack toItemStack() {
      return this.itemStack;
   }

}
