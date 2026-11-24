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
   public RandomStatData<SoulboundData> whenInitialized(Object obj) {
      throw new NotImplementedException();
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      throw new NotImplementedException();
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      throw new NotImplementedException();
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomStatData<SoulboundData>> optional) {
      throw new NotImplementedException();
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull SoulboundData soulboundData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(soulboundData));
      String text = Message.SOULBOUND_ITEM_LORE.getFormatted().replace("#player#", soulboundData.getName()).replace("#level#", MMOUtils.intToRoman(soulboundData.getLevel()));
      itemStackBuilder.getLore().insert("soulbound", text.split(Pattern.quote("//")));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull SoulboundData soulboundData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), soulboundData.toJson().toString()));
      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      SoulboundData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public SoulboundData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         try {
            return new SoulboundData(new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonObject());
         } catch (JsonSyntaxException | IllegalStateException exception) {
         }
      }

      return null;
   }

   @NotNull
   public SoulboundData getClearStatData() {
      return new SoulboundData(UUID.fromString("df930b7b-a84d-4f76-90ac-33be6a5b6c88"), "gunging", 0);
   }

   @Override
   public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
      if (nbtItem.hasTag(ItemStats.SOULBOUND.getNBTPath())
         && !nbtItem.getString(ItemStats.SOULBOUND.getNBTPath()).contains(rpgPlayer.getPlayer().getUniqueId().toString())
         && !rpgPlayer.getPlayer().hasPermission("mmoitems.bypass.soulbound")) {
         if (flag) {
            int asInt = new JsonParser().parse(nbtItem.getString(ItemStats.SOULBOUND.getNBTPath())).getAsJsonObject().get("Level").getAsInt();
            Message.SOULBOUND_RESTRICTION.format(ChatColor.RED).send(rpgPlayer.getPlayer());
            rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
            rpgPlayer.getPlayer().damage(MMOItems.plugin.getLanguage().soulboundBaseDamage + asInt * MMOItems.plugin.getLanguage().soulboundPerLvlDamage);
         }

         return false;
      } else {
         return true;
      }
   }
}
