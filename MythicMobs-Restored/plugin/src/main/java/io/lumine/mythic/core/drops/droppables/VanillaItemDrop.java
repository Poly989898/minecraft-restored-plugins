package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.numbers.RandomDouble;
import io.lumine.mythic.core.skills.SkillString;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class VanillaItemDrop extends ItemDrop implements IItemDrop {
   private final AbstractItemStack item;

   public VanillaItemDrop(String line, MythicLineConfig config, String item) {
      super(line, config);
      Material material = Material.matchMaterial(item.toUpperCase());
      if (material != null) {
         this.item = BukkitAdapter.adapt(material);
      } else {
         this.item = BukkitAdapter.adapt(new ItemStack(Material.PAPER));
      }

      String display = config.getString(new String[]{"name", "display", "n"}, null);
      int data = config.getInteger(new String[]{"data", "d"}, 0);
      int amount = config.getInteger(new String[]{"amount", "a"}, 1);
      int model = config.getInteger(new String[]{"model"}, 0);
      String lore = config.getString(new String[]{"lore", "l"}, null);
      String color = config.getString(new String[]{"color"}, null);
      String enchantments = config.getString(new String[]{"enchantments", "enchants", "ench", "e"}, null);
      String potionEffects = config.getString(new String[]{"potioneffects", "peffects", "potion", "pe"}, null);
      String skullOwner = config.getString(new String[]{"skullowner"}, null);
      String skullTexture = config.getString(new String[]{"skulltexture"}, null);
      if (display != null) {
         if (display.startsWith("\"")) {
            display = display.substring(1, display.length() - 1);
         }

         display = SkillString.parseMessageSpecialChars(display);
         this.item.setName(display);
      }

      if (data > 0) {
         this.item.data((byte)data);
      }

      if (model > 0) {
         this.item.modelData(model);
      }

      if (amount != 1) {
         this.item.amount(amount);
      }

      if (lore != null) {
         try {
            List<String> loreList = new ArrayList<>();

            for (String s : lore.split(",")) {
               String lores = s.substring(1, s.length() - 1);
               lores = SkillString.parseMessageSpecialChars(lores);
               loreList.add(lores);
            }

            this.item.setLore(loreList);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      if (color != null) {
         this.item.color(color);
      }

      if (enchantments != null) {
         try {
            for (String s : enchantments.split(",")) {
               try {
                  String[] split = s.split(":");
                  Enchantment ench = Enchantment.getByName(split[0].toUpperCase());
                  int level = Integer.parseInt(split[1]);
                  this.item.enchant(ench, level);
               } catch (Exception ex) {
                  Log.error("Badly configured enchantment");
               }
            }
         } catch (Exception ex2) {
            ex2.printStackTrace();
         }
      }

      if (potionEffects != null) {
         try {
            for (String s : potionEffects.split(",")) {
               try {
                  String[] split = s.split(":");
                  PotionEffectType type = PotionEffectType.getByName(split[0].toUpperCase());
                  int duration = Integer.parseInt(split[1]);
                  int amplifier = 0;
                  if (split.length > 2) {
                     amplifier = Integer.parseInt(split[2]);
                  }

                  this.item.potionEffect(type, duration, amplifier);
               } catch (Exception ex3) {
                  Log.error("Badly configured enchantment " + s);
               }
            }
         } catch (Exception ex4) {
            ex4.printStackTrace();
         }
      }

      if (skullOwner != null) {
         this.item.skullOwner(skullOwner);
      }

      if (skullTexture != null) {
         this.item.skullTexture(skullTexture);
      }
   }

   public VanillaItemDrop(String line, MythicLineConfig config, AbstractItemStack item) {
      super(line, config);
      this.item = item;
   }

   public VanillaItemDrop(String line, MythicLineConfig config, AbstractItemStack item, RandomDouble amount) {
      super(line, config, amount);
      this.item = item;
   }

   @Override
   public AbstractItemStack getDrop(DropMetadata meta, double amount) {
      amount = (int)(this.item.getAmount() * amount);
      int finalAmount = this.rollBonuses(meta, Numbers.floor(amount));
      return this.item.copy().amount(finalAmount);
   }

   @Override
   public String toString() {
      return "ItemDrop{" + this.item.toString() + "}";
   }
}
