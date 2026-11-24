package net.Indyuce.mmoitems.api.interaction;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.List;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.SkullTextureData;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class ItemSkin extends UseItem {
   public static final String SKIN_ID_TAG = "MMOITEMS_SKIN_ID";
   public static final String SKIN_TYPE_TAG = "MMOITEMS_SKIN_TYPE";

   @Deprecated
   public ItemSkin(Player player, NBTItem nbtItem) {
      super(player, nbtItem);
   }

   public ItemSkin(PlayerData playerData, NBTItem nbtItem) {
      super(playerData, nbtItem);
   }

   public ItemSkin.ApplyResult applyOntoItem(NBTItem nbtItem, Type type) {
      if (type == Type.SKIN) {
         return new ItemSkin.ApplyResult(ItemSkin.ResultType.NONE);
      }

      if (MMOItems.plugin.getConfig().getBoolean("locked-skins") && MMOUtils.isNonEmpty(nbtItem.getString("MMOITEMS_SKIN_ID"))) {
         this.player.playSound(this.player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         Message.SKIN_REJECTED.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(nbtItem.getItem())).send(this.player);
         return new ItemSkin.ApplyResult(ItemSkin.ResultType.NONE);
      }

      if (this.mmoitem.hasData(ItemStats.COMPATIBLE_TYPES)) {
         List<String> texts = ((StringListData)this.mmoitem.getData(ItemStats.COMPATIBLE_TYPES)).getList();
         if (texts.size() > 0 && texts.stream().noneMatch(var1x -> var1x.equalsIgnoreCase(type.getId()))) {
            this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            Message.SKIN_INCOMPATIBLE.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(nbtItem.getItem())).send(this.player);
            return new ItemSkin.ApplyResult(ItemSkin.ResultType.NONE);
         }
      }

      if (this.mmoitem.hasData(ItemStats.COMPATIBLE_IDS)) {
         List<String> texts = ((StringListData)this.mmoitem.getData(ItemStats.COMPATIBLE_IDS)).getList();
         String mMOITEMSITEMID = nbtItem.getString("MMOITEMS_ITEM_ID");
         if (texts.size() > 0 && texts.stream().noneMatch(var1x -> var1x.equalsIgnoreCase(mMOITEMSITEMID))) {
            this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            Message.SKIN_INCOMPATIBLE.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(nbtItem.getItem())).send(this.player);
            return new ItemSkin.ApplyResult(ItemSkin.ResultType.NONE);
         }
      }

      if (this.mmoitem.hasData(ItemStats.COMPATIBLE_MATERIALS)) {
         List<String> texts = ((StringListData)this.mmoitem.getData(ItemStats.COMPATIBLE_MATERIALS)).getList();
         if (texts.size() > 0 && texts.stream().noneMatch(var1x -> var1x.equalsIgnoreCase(nbtItem.getItem().getType().name()))) {
            this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            Message.SKIN_INCOMPATIBLE.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(nbtItem.getItem())).send(this.player);
            return new ItemSkin.ApplyResult(ItemSkin.ResultType.NONE);
         }
      }

      double stat = this.getNBTItem().getStat(ItemStats.SUCCESS_RATE.getId());
      if (stat != 0.0 && RANDOM.nextDouble() < 1.0 - stat / 100.0) {
         this.player.playSound(this.player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
         Message.SKIN_BROKE.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(nbtItem.getItem())).send(this.player);
         return new ItemSkin.ApplyResult(ItemSkin.ResultType.FAILURE);
      } else {
         ItemStack item = applySkin(nbtItem, this.mmoitem);
         this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
         Message.SKIN_APPLIED.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(nbtItem.getItem())).send(this.player);
         return new ItemSkin.ApplyResult(item);
      }
   }

   @Deprecated
   @NotNull
   public static ItemStack applySkin(@NotNull NBTItem nbtItem, @NotNull VolatileMMOItem volatileMMOItem) {
      NBTItem nBT = volatileMMOItem.getNBT();
      String text = MMOUtils.requireNonEmptyElse(volatileMMOItem.getNBT().getString("MMOITEMS_SKIN_ID"), nBT.getString("MMOITEMS_ITEM_ID"));
      String text2 = MMOUtils.requireNonEmptyElse(volatileMMOItem.getNBT().getString("MMOITEMS_SKIN_TYPE"), nBT.getString("MMOITEMS_ITEM_TYPE"));
      nbtItem.addTag(new ItemTag[]{new ItemTag("MMOITEMS_SKIN_ID", text)});
      nbtItem.addTag(new ItemTag[]{new ItemTag("MMOITEMS_SKIN_TYPE", text2)});
      if (!nBT.getString("MMOITEMS_ITEM_PARTICLES").isEmpty()) {
         nbtItem.addTag(new ItemTag[]{new ItemTag("MMOITEMS_ITEM_PARTICLES", nBT.getString("MMOITEMS_ITEM_PARTICLES"))});
      }

      ItemStack item = nbtItem.toItem();
      if (item.getType() != nBT.getItem().getType()) {
         item.setType(nBT.getItem().getType());
      }

      ItemMeta itemMeta = item.getItemMeta();
      ItemMeta itemMeta2 = nBT.getItem().getItemMeta();
      if (itemMeta2 != null && itemMeta != null) {
         if (itemMeta2.hasCustomModelData()) {
            itemMeta.setCustomModelData(itemMeta2.getCustomModelData());
         }

         if (itemMeta2.isUnbreakable()) {
            itemMeta.setUnbreakable(true);
            if (itemMeta instanceof Damageable && itemMeta2 instanceof Damageable) {
               ((Damageable)itemMeta).setDamage(((Damageable)itemMeta2).getDamage());
            }
         }

         if (itemMeta2 instanceof LeatherArmorMeta && itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta)itemMeta).setColor(((LeatherArmorMeta)itemMeta2).getColor());
         }

         if (itemMeta2 instanceof ArmorMeta && itemMeta instanceof ArmorMeta) {
            ((ArmorMeta)itemMeta).setTrim(((ArmorMeta)itemMeta2).getTrim());
            if (itemMeta2.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
               itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
            } else {
               itemMeta.removeItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
            }
         }

         if (volatileMMOItem.hasData(ItemStats.SKULL_TEXTURE) && item.getType() == Material.PLAYER_HEAD && nBT.getItem().getType() == Material.PLAYER_HEAD) {
            MythicLib.plugin.getVersion().getWrapper().setProfile((SkullMeta)itemMeta, ((SkullTextureData)volatileMMOItem.getData(ItemStats.SKULL_TEXTURE)).getGameProfile());
         }

         item.setItemMeta(itemMeta);
      }

      return item;
   }

   public static class ApplyResult {
      private final ItemSkin.ResultType type;
      private final ItemStack result;

      public ApplyResult(ItemSkin.ResultType resultType) {
         this(null, resultType);
      }

      public ApplyResult(ItemStack item) {
         this(item, ItemSkin.ResultType.SUCCESS);
      }

      public ApplyResult(ItemStack item, ItemSkin.ResultType resultType) {
         this.type = resultType;
         this.result = item;
      }

      public ItemSkin.ResultType getType() {
         return this.type;
      }

      public ItemStack getResult() {
         return this.result;
      }
   }

   public enum ResultType {
      FAILURE,
      NONE,
      SUCCESS;
   }
}
