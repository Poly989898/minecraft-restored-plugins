package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.annotation.HasCategory;
import net.Indyuce.mmoitems.stat.data.SoulboundData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@HasCategory(cat = "soulbound")
public class Soulbound extends ItemStat<RandomStatData<SoulboundData>, SoulboundData> implements InternalStat, ItemRestriction {
   public Soulbound() {
      super("SOULBOUND", Material.ENDER_EYE, "Soulbound", new String[0], new String[0]);
   }

   @Nullable
   @Override
   public RandomStatData<SoulboundData> whenInitialized(Object var1) {
      throw new NotImplementedException();
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      throw new NotImplementedException();
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      throw new NotImplementedException();
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<RandomStatData<SoulboundData>> var2) {
      throw new NotImplementedException();
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull SoulboundData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
      String var3 = Message.SOULBOUND_ITEM_LORE.getFormatted().replace("#player#", var2.getName()).replace("#level#", MMOUtils.intToRoman(var2.getLevel()));
      var1.getLore().insert("soulbound", var3.split(Pattern.quote("//")));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull SoulboundData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), var1.toJson().toString()));
      return var2;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      SoulboundData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public SoulboundData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         try {
            return new SoulboundData(new JsonParser().parse((String)var2.getValue()).getAsJsonObject());
         } catch (JsonSyntaxException | IllegalStateException var4) {
         }
      }

      return null;
   }

   @NotNull
   public SoulboundData getClearStatData() {
      return new SoulboundData(UUID.fromString("df930b7b-a84d-4f76-90ac-33be6a5b6c88"), "gunging", 0);
   }

   @Override
   public boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3) {
      if (var2.hasTag(ItemStats.SOULBOUND.getNBTPath())
         && !var2.getString(ItemStats.SOULBOUND.getNBTPath()).contains(var1.getPlayer().getUniqueId().toString())
         && !var1.getPlayer().hasPermission("mmoitems.bypass.soulbound")) {
         if (var3) {
            int var4 = new JsonParser().parse(var2.getString(ItemStats.SOULBOUND.getNBTPath())).getAsJsonObject().get("Level").getAsInt();
            Message.SOULBOUND_RESTRICTION.format(ChatColor.RED).send(var1.getPlayer());
            var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
            var1.getPlayer().damage(MMOItems.plugin.getLanguage().soulboundBaseDamage + var4 * MMOItems.plugin.getLanguage().soulboundPerLvlDamage);
         }

         return false;
      } else {
         return true;
      }
   }
}
