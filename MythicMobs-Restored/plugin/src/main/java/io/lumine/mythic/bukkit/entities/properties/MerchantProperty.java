package io.lumine.mythic.bukkit.entities.properties;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.serialize.InventorySerialization;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.droppables.CustomDrop;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

public class MerchantProperty implements EntityPropertySet {
   private final MythicConfig mc;
   private int level;
   private boolean hasTrades = true;
   private boolean hasDynamicTrades = false;
   private List<MerchantProperty.ConfigTrade> trades;

   public MerchantProperty(MythicConfig mc) {
      this.mc = mc;
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_14)) {
         this.level = mc.getInteger("Options.TradingLevel", 1);
         this.level = mc.getInteger("Options.Level", this.level);
      }

      this.hasTrades = mc.getBoolean("Options.HasTrades", true);
      if (mc.isSet("DynamicTrades")) {
         this.hasDynamicTrades = true;
      }

      if (mc.isSet("Trades")) {
         this.trades = new ArrayList<>();

         for (MythicConfig tradeConf : mc.getNestedConfigs("Trades").values()) {
            try {
               String strResult = tradeConf.getString("Result", "STONE");
               String strItem1 = tradeConf.getString("Item1", "5 EMERALD");
               String strItem2 = tradeConf.getString("Item2", null);
               PlaceholderInt maxUses = PlaceholderInt.of(tradeConf.getString("MaxUses", "10000"));
               String[] spResult = strResult.split(" ");
               String result;
               int resultAmount;
               if (spResult.length == 1) {
                  result = spResult[0];
                  resultAmount = 1;
               } else if (spResult.length > 1) {
                  result = spResult[1];

                  try {
                     resultAmount = Integer.parseInt(spResult[0]);
                  } catch (NumberFormatException e) {
                     resultAmount = 0;
                  }
               } else {
                  result = "STONE";
                  resultAmount = 0;
               }

               MerchantProperty.ConfigTrade recipe = new MerchantProperty.ConfigTrade(result, resultAmount, maxUses);
               if (strItem1 != null) {
                  String[] spItem = strItem1.split(" ");
                  String configItem = null;
                  int configItemAmount = 0;
                  if (spItem.length == 1) {
                     configItem = spItem[0];
                     configItemAmount = 1;
                  } else if (spItem.length > 1) {
                     configItem = spItem[1];

                     try {
                        configItemAmount = Integer.parseInt(spItem[0]);
                     } catch (NumberFormatException ex) {
                        configItemAmount = 0;
                     }
                  }

                  recipe.setConfigItem1(configItem);
                  recipe.setAmount1(configItemAmount);
               }

               if (strItem2 != null) {
                  String[] spItem = strItem2.split(" ");
                  String configItem = null;
                  int configItemAmount = 0;
                  if (spItem.length == 1) {
                     configItem = spItem[0];
                     configItemAmount = 1;
                  } else if (spItem.length > 1) {
                     configItem = spItem[1];

                     try {
                        configItemAmount = Integer.parseInt(spItem[0]);
                     } catch (NumberFormatException ex2) {
                        configItemAmount = 0;
                     }
                  }

                  recipe.setConfigItem2(configItem);
                  recipe.setAmount2(configItemAmount);
               }

               this.trades.add(recipe);
            } catch (Throwable ex3) {
               MythicLogger.errorGenericConfig("A villager trade with result " + tradeConf.getString("Result", "STONE") + " is configured incorrectly.");
            }
         }
      }
   }

   public boolean hasTrades() {
      return this.trades != null && !this.trades.isEmpty() || this.hasDynamicTrades;
   }

   @Override
   public Entity applyProperties(Entity entity) {
      if (entity instanceof Merchant merchant) {
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_14) && entity instanceof Villager villager) {
            villager.setVillagerLevel(this.level);
         }

         if (this.hasTrades()) {
            List<MerchantRecipe> trades = Lists.newArrayList();
            if (this.trades != null) {
               for (MerchantProperty.ConfigTrade trade : this.trades) {
                  try {
                     trades.add(trade.toMerchantRecipe());
                  } catch (Throwable e) {
                     MythicLogger.errorGenericConfig("Failed to process Villager Trade with result " + trade.getConfigResult());
                  }
               }
            }

            if (this.hasDynamicTrades) {
               for (MythicConfig tradeConf : this.mc.getNestedConfigs("DynamicTrades").values()) {
                  MerchantRecipe recipe = this.getDynamicRecipe(entity, tradeConf);
                  if (recipe != null) {
                     trades.add(recipe);
                  }
               }
            }

            merchant.setRecipes(trades);
         } else {
            merchant.setRecipes(Collections.emptyList());
         }

         return entity;
      } else {
         return entity;
      }
   }

   private MerchantRecipe getDynamicRecipe(Entity entity, MythicConfig tradeConf) {
      try {
         String strResult = tradeConf.getString("Result", "STONE");
         String strItem1 = tradeConf.getString("Item1", "5 EMERALD");
         String strItem2 = tradeConf.getString("Item2", null);
         PlaceholderInt maxUses = tradeConf.getPlaceholderInt("MaxUses", "10000");
         String[] spResult = strResult.split(" ");
         ItemStack result = spResult.length == 1 ? getItemFromTable(entity, spResult[0]) : getItemFromTable(entity, spResult[1]);
         if (result.getType() != Material.PLAYER_HEAD) {
            result = InventorySerialization.decodeItemStack(InventorySerialization.encodeItemStackToString(result));
         }

         if (spResult.length == 2) {
            int amount;
            try {
               amount = Integer.parseInt(spResult[0]);
            } catch (NumberFormatException e) {
               amount = 0;
            }

            result.setAmount(amount);
         }

         MerchantRecipe recipe = new MerchantRecipe(result, maxUses.get());
         if (strItem1 != null) {
            String[] spItem1 = strItem1.split(" ");
            ItemStack item1 = spItem1.length == 1 ? getItemFromTable(entity, spItem1[0]) : getItemFromTable(entity, spItem1[1]);
            item1 = InventorySerialization.decodeItemStack(InventorySerialization.encodeItemStackToString(item1));
            if (spItem1.length == 2) {
               int amount;
               try {
                  amount = Integer.parseInt(spItem1[0]);
               } catch (NumberFormatException ex) {
                  amount = 0;
               }

               item1.setAmount(amount);
            }

            recipe.addIngredient(item1);
         }

         if (strItem2 != null) {
            String[] spItem2 = strItem2.split(" ");
            ItemStack item2 = spItem2.length == 1 ? getItemFromTable(entity, spItem2[0]) : getItemFromTable(entity, spItem2[1]);
            item2 = InventorySerialization.decodeItemStack(InventorySerialization.encodeItemStackToString(item2));
            if (spItem2.length == 2) {
               int amount;
               try {
                  amount = Integer.parseInt(spItem2[0]);
               } catch (NumberFormatException ex2) {
                  amount = 0;
               }

               item2.setAmount(amount);
            }

            recipe.addIngredient(item2);
         }

         return recipe;
      } catch (Exception ex3) {
         MythicLogger.errorGenericConfig("A villager trade with result " + tradeConf.getString("Result", "STONE") + " is configured incorrectly.");
         ex3.printStackTrace();
         return null;
      }
   }

   public static ItemStack getItemFromTable(Entity entity, String itemString) {
      Optional<DropTable> maybeTable = MythicBukkit.inst().getDropManager().getDropTable(itemString);
      DropTable table;
      if (maybeTable.isPresent()) {
         table = maybeTable.get();
      } else {
         if (itemString.startsWith("\"")) {
            try {
               itemString = itemString.substring(1, itemString.length() - 1);
            } catch (Exception e) {
               e.printStackTrace();
            }

            itemString = SkillString.parseMessageSpecialChars(itemString);
         }

         List<String> items = Lists.newArrayList();

         for (String s : itemString.split(",")) {
            items.add(s);
         }

         table = new DropTable("DropItemMechanic", "DropItemMechanic", items);
      }

      AbstractEntity aEntity = BukkitAdapter.adapt(entity);
      DropMetadata meta = new DropMetadataImpl(new GenericCaster(aEntity), aEntity);
      ItemStack ret = new ItemStack(Material.AIR);

      for (Drop type : table.generate(meta).getDrops()) {
         double amount = type.getAmount();
         IDrop drop;
         if (type instanceof CustomDrop) {
            if (((CustomDrop)type).getDrop().isEmpty()) {
               continue;
            }

            drop = ((CustomDrop)type).getDrop().get();
         } else {
            drop = type;
         }

         if (drop instanceof IItemDrop itemDrop) {
            ret = BukkitAdapter.adapt(itemDrop.getDrop(meta, amount));
            break;
         }
      }

      return ret;
   }

   private class ConfigTrade {
      private final String configResult;
      private final int amountResult;
      private final PlaceholderInt maxUses;
      private String configItem1 = null;
      private int amount1 = 1;
      private String configItem2 = null;
      private int amount2 = 1;

      public MerchantRecipe toMerchantRecipe() {
         ItemStack itemResult = BukkitAdapter.adapt(MythicBukkit.inst().getBootstrap().createItem(this.configResult));
         itemResult.setAmount(this.amountResult);
         MerchantRecipe recipe = new MerchantRecipe(itemResult, this.maxUses.get());
         if (this.configItem1 != null) {
            ItemStack item1 = BukkitAdapter.adapt(MythicBukkit.inst().getBootstrap().createItem(this.configItem1));
            item1 = InventorySerialization.decodeItemStack(InventorySerialization.encodeItemStackToString(item1));
            if (this.amount1 > 1) {
               item1.setAmount(this.amount1);
            }

            recipe.addIngredient(item1);
         }

         if (this.configItem2 != null) {
            ItemStack item2 = BukkitAdapter.adapt(MythicBukkit.inst().getBootstrap().createItem(this.configItem2));
            item2 = InventorySerialization.decodeItemStack(InventorySerialization.encodeItemStackToString(item2));
            if (this.amount2 > 1) {
               item2.setAmount(this.amount2);
            }

            recipe.addIngredient(item2);
         }

         return recipe;
      }

      @Generated
      public ConfigTrade(final String configResult, final int amountResult, final PlaceholderInt maxUses) {
         this.configResult = configResult;
         this.amountResult = amountResult;
         this.maxUses = maxUses;
      }

      @Generated
      public String getConfigResult() {
         return this.configResult;
      }

      @Generated
      public int getAmountResult() {
         return this.amountResult;
      }

      @Generated
      public PlaceholderInt getMaxUses() {
         return this.maxUses;
      }

      @Generated
      public String getConfigItem1() {
         return this.configItem1;
      }

      @Generated
      public int getAmount1() {
         return this.amount1;
      }

      @Generated
      public String getConfigItem2() {
         return this.configItem2;
      }

      @Generated
      public int getAmount2() {
         return this.amount2;
      }

      @Generated
      public void setConfigItem1(String configItem1) {
         this.configItem1 = configItem1;
      }

      @Generated
      public void setAmount1(int amount1) {
         this.amount1 = amount1;
      }

      @Generated
      public void setConfigItem2(String configItem2) {
         this.configItem2 = configItem2;
      }

      @Generated
      public void setAmount2(int amount2) {
         this.amount2 = amount2;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof MerchantProperty.ConfigTrade other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.getAmountResult() != other.getAmountResult()) {
            return false;
         } else if (this.getAmount1() != other.getAmount1()) {
            return false;
         } else if (this.getAmount2() != other.getAmount2()) {
            return false;
         } else {
            Object this$configResult = this.getConfigResult();
            Object other$configResult = other.getConfigResult();
            if (this$configResult == null ? other$configResult == null : this$configResult.equals(other$configResult)) {
               Object this$maxUses = this.getMaxUses();
               Object other$maxUses = other.getMaxUses();
               if (this$maxUses == null ? other$maxUses == null : this$maxUses.equals(other$maxUses)) {
                  Object this$configItem1 = this.getConfigItem1();
                  Object other$configItem1 = other.getConfigItem1();
                  if (this$configItem1 == null ? other$configItem1 == null : this$configItem1.equals(other$configItem1)) {
                     Object this$configItem2 = this.getConfigItem2();
                     Object other$configItem2 = other.getConfigItem2();
                     return this$configItem2 == null ? other$configItem2 == null : this$configItem2.equals(other$configItem2);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof MerchantProperty.ConfigTrade;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + this.getAmountResult();
         result = result * 59 + this.getAmount1();
         result = result * 59 + this.getAmount2();
         Object $configResult = this.getConfigResult();
         result = result * 59 + ($configResult == null ? 43 : $configResult.hashCode());
         Object $maxUses = this.getMaxUses();
         result = result * 59 + ($maxUses == null ? 43 : $maxUses.hashCode());
         Object $configItem1 = this.getConfigItem1();
         result = result * 59 + ($configItem1 == null ? 43 : $configItem1.hashCode());
         Object $configItem2 = this.getConfigItem2();
         return result * 59 + ($configItem2 == null ? 43 : $configItem2.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "MerchantProperty.ConfigTrade(configResult="
            + this.getConfigResult()
            + ", amountResult="
            + this.getAmountResult()
            + ", maxUses="
            + this.getMaxUses()
            + ", configItem1="
            + this.getConfigItem1()
            + ", amount1="
            + this.getAmount1()
            + ", configItem2="
            + this.getConfigItem2()
            + ", amount2="
            + this.getAmount2()
            + ")";
      }
   }
}
