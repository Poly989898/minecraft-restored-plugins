package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.entities.properties.MerchantProperty;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

@MythicMechanic(
   author = "Seyarada",
   name = "addTrade",
   aliases = {"setTrade", "removeTrade", "replaceTrade"},
   description = "Changes the trades of a villager"
)
public class AddTradeMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "action",
      aliases = {"mode", "m"},
      defValue = "ADD",
      description = "The type of trade action to perform, valid values are ADD/REMOVE/REPLACE"
   )
   final String action;
   @MythicField(
      name = "slot",
      aliases = {"s", "index"},
      defValue = "0",
      description = "The slot to modify the trade in."
   )
   final PlaceholderInt slot;
   @MythicField(
      name = "ingredient",
      aliases = {"item", "ingredient1", "item1", "i", "i1"},
      defValue = "STONE",
      description = "The item in the first trade slot."
   )
   final String ingredient1;
   @MythicField(
      name = "ingredient2",
      aliases = {"item2", "i2"},
      description = "The item in the second trade slot."
   )
   final String ingredient2;
   @MythicField(
      name = "result",
      aliases = {"r"},
      defValue = "STONE",
      description = "The resulting item from the trade."
   )
   final String result;
   @MythicField(
      name = "maxuses",
      aliases = {"uses", "u"},
      defValue = "Integer.MAX_VALUE",
      description = "Max uses for the trade"
   )
   final PlaceholderInt maxUses;
   @MythicField(
      name = "expReward",
      aliases = {"experienceReward", "exp", "dropExp"},
      description = "Whether or not to reward EXP from the trade.",
      defValue = "false"
   )
   final boolean expReward;
   @MythicField(
      name = "villagerExperience",
      aliases = {"villExp", "vexp"},
      defValue = "0",
      description = "The amount of experience to give to the villager upon successful trade."
   )
   final PlaceholderInt villagerExperience;
   @MythicField(
      name = "priceMultiplier",
      aliases = {"multiplier"},
      defValue = "0",
      description = "The multiplier to apply to the price of the trade."
   )
   final PlaceholderFloat priceMultiplier;
   @MythicField(
      name = "demand",
      aliases = {"d"},
      defValue = "1",
      description = "The demand level of the trade."
   )
   final PlaceholderInt demand;
   @MythicField(
      name = "specialPrice",
      aliases = {"special"},
      defValue = "1",
      description = "The special price of the trade."
   )
   final PlaceholderInt specialPrice;
   @MythicField(
      name = "ignoreDiscounts",
      aliases = {"discounts"},
      defValue = "false",
      description = "Whether or not to ignore discounts for the trade."
   )
   final boolean ignoreDiscounts;

   public AddTradeMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.action = mlc.getString(new String[]{"action", "mode", "m"}, "ADD");
      this.slot = mlc.getPlaceholderInteger(new String[]{"slot", "s", "index"}, 0);
      this.ingredient1 = mlc.getString(new String[]{"ingredient", "item", "ingredient1", "item1", "i", "i1"}, "STONE");
      this.ingredient2 = mlc.getString(new String[]{"ingredient2", "item2", "i2"}, null);
      this.result = mlc.getString(new String[]{"result", "r"}, "STONE");
      this.maxUses = mlc.getPlaceholderInteger(new String[]{"maxUses", "uses", "u"}, Integer.MAX_VALUE);
      this.expReward = mlc.getBoolean(new String[]{"experienceReward", "expReward", "exp", "dropExp"}, false);
      this.villagerExperience = mlc.getPlaceholderInteger(new String[]{"villagerExp", "villExp", "vexp"}, 0);
      this.priceMultiplier = mlc.getPlaceholderFloat(new String[]{"priceMultiplier", "multiplier"}, 0.0F);
      this.demand = mlc.getPlaceholderInteger(new String[]{"demand", "d"}, 1);
      this.specialPrice = mlc.getPlaceholderInteger(new String[]{"specialPrice", "special"}, 1);
      this.ignoreDiscounts = mlc.getBoolean(new String[]{"ignoreDiscounts", "discounts"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.getBukkitEntity() instanceof Merchant merchant) {
         Schedulers.runOnRegion(target.getBukkitEntity(), () -> {
            if (merchant instanceof Villager villager && villager.getProfession() == Profession.NONE) {
               villager.setProfession(Profession.NITWIT);
            }

            int slotIndex = this.slot.get(data);
            String toUpperCase = this.action.toUpperCase();
            switch (toUpperCase) {
               case "REMOVE": {
                  List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());
                  recipes.remove(slotIndex);
                  merchant.setRecipes(recipes);
                  break;
               }
               case "REPLACE":
                  merchant.setRecipe(slotIndex, this.createRecipe(data, target));
                  break;
               case "ADD": {
                  List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());
                  recipes.add(slotIndex, this.createRecipe(data, target));
                  merchant.setRecipes(recipes);
               }
            }
         });
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }

   private MerchantRecipe createRecipe(SkillMetadata data, AbstractEntity target) {
      MerchantRecipe recipe = new MerchantRecipe(
         this.getItemStack(target, this.result, data),
         0,
         this.maxUses.get(data),
         this.expReward,
         this.villagerExperience.get(data),
         this.priceMultiplier.get(data),
         this.demand.get(data),
         this.specialPrice.get(data),
         this.ignoreDiscounts
      );
      recipe.addIngredient(this.getItemStack(target, this.ingredient1, data));
      if (this.ingredient2 != null) {
         recipe.addIngredient(this.getItemStack(target, this.ingredient2, data));
      }

      return recipe;
   }

   private ItemStack getItemStack(AbstractEntity e, String stringItem, SkillMetadata data) {
      String[] split = stringItem.split(" ", 2);
      stringItem = split[0];
      AtomicInteger amount = new AtomicInteger(1);
      if (split.length > 1) {
         PlaceholderInt placeholderAmount = PlaceholderInt.of(split[1]);
         if (placeholderAmount != null) {
            amount.set(placeholderAmount.get(data));
         }
      }

      ItemStack itemStack = MerchantProperty.getItemFromTable(BukkitAdapter.adapt(e), stringItem);
      itemStack.setAmount(amount.get());
      return itemStack;
   }
}
