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
   public ItemSkin(Player var1, NBTItem var2) {
      super(var1, var2);
   }

   public ItemSkin(PlayerData var1, NBTItem var2) {
      super(var1, var2);
   }

   public ItemSkin.ApplyResult applyOntoItem(NBTItem var1, Type var2) {
      if (var2 == Type.SKIN) {
         return new ItemSkin.ApplyResult(ItemSkin.ResultType.NONE);
      }

      if (MMOItems.plugin.getConfig().getBoolean("locked-skins") && MMOUtils.isNonEmpty(var1.getString("MMOITEMS_SKIN_ID"))) {
         this.player.playSound(this.player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         Message.SKIN_REJECTED.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(var1.getItem())).send(this.player);
         return new ItemSkin.ApplyResult(ItemSkin.ResultType.NONE);
      }

      if (this.mmoitem.hasData(ItemStats.COMPATIBLE_TYPES)) {
         List<String> var3 = ((StringListData)this.mmoitem.getData(ItemStats.COMPATIBLE_TYPES)).getList();
         if (var3.size() > 0 && var3.stream().noneMatch(var1x -> var1x.equalsIgnoreCase(var2.getId()))) {
            this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            Message.SKIN_INCOMPATIBLE.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(var1.getItem())).send(this.player);
            return new ItemSkin.ApplyResult(ItemSkin.ResultType.NONE);
         }
      }

      if (this.mmoitem.hasData(ItemStats.COMPATIBLE_IDS)) {
         List<String> var6 = ((StringListData)this.mmoitem.getData(ItemStats.COMPATIBLE_IDS)).getList();
         String var4 = var1.getString("MMOITEMS_ITEM_ID");
         if (var6.size() > 0 && var6.stream().noneMatch(var1x -> var1x.equalsIgnoreCase(var4))) {
            this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            Message.SKIN_INCOMPATIBLE.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(var1.getItem())).send(this.player);
            return new ItemSkin.ApplyResult(ItemSkin.ResultType.NONE);
         }
      }

      if (this.mmoitem.hasData(ItemStats.COMPATIBLE_MATERIALS)) {
         List<String> var7 = ((StringListData)this.mmoitem.getData(ItemStats.COMPATIBLE_MATERIALS)).getList();
         if (var7.size() > 0 && var7.stream().noneMatch(var1x -> var1x.equalsIgnoreCase(var1.getItem().getType().name()))) {
            this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            Message.SKIN_INCOMPATIBLE.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(var1.getItem())).send(this.player);
            return new ItemSkin.ApplyResult(ItemSkin.ResultType.NONE);
         }
      }

      double var8 = this.getNBTItem().getStat(ItemStats.SUCCESS_RATE.getId());
      if (var8 != 0.0 && RANDOM.nextDouble() < 1.0 - var8 / 100.0) {
         this.player.playSound(this.player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
         Message.SKIN_BROKE.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(var1.getItem())).send(this.player);
         return new ItemSkin.ApplyResult(ItemSkin.ResultType.FAILURE);
      } else {
         ItemStack var5 = applySkin(var1, this.mmoitem);
         this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
         Message.SKIN_APPLIED.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var1.getItem())).send(this.player);
         return new ItemSkin.ApplyResult(var5);
      }
   }

   @Deprecated
   @NotNull
   public static ItemStack applySkin(@NotNull NBTItem var0, @NotNull VolatileMMOItem var1) {
      NBTItem var2 = var1.getNBT();
      String var3 = MMOUtils.requireNonEmptyElse(var1.getNBT().getString("MMOITEMS_SKIN_ID"), var2.getString("MMOITEMS_ITEM_ID"));
      String var4 = MMOUtils.requireNonEmptyElse(var1.getNBT().getString("MMOITEMS_SKIN_TYPE"), var2.getString("MMOITEMS_ITEM_TYPE"));
      var0.addTag(new ItemTag[]{new ItemTag("MMOITEMS_SKIN_ID", var3)});
      var0.addTag(new ItemTag[]{new ItemTag("MMOITEMS_SKIN_TYPE", var4)});
      if (!var2.getString("MMOITEMS_ITEM_PARTICLES").isEmpty()) {
         var0.addTag(new ItemTag[]{new ItemTag("MMOITEMS_ITEM_PARTICLES", var2.getString("MMOITEMS_ITEM_PARTICLES"))});
      }

      ItemStack var5 = var0.toItem();
      if (var5.getType() != var2.getItem().getType()) {
         var5.setType(var2.getItem().getType());
      }

      ItemMeta var6 = var5.getItemMeta();
      ItemMeta var7 = var2.getItem().getItemMeta();
      if (var7 != null && var6 != null) {
         if (var7.hasCustomModelData()) {
            var6.setCustomModelData(var7.getCustomModelData());
         }

         if (var7.isUnbreakable()) {
            var6.setUnbreakable(true);
            if (var6 instanceof Damageable && var7 instanceof Damageable) {
               ((Damageable)var6).setDamage(((Damageable)var7).getDamage());
            }
         }

         if (var7 instanceof LeatherArmorMeta && var6 instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta)var6).setColor(((LeatherArmorMeta)var7).getColor());
         }

         if (var7 instanceof ArmorMeta && var6 instanceof ArmorMeta) {
            ((ArmorMeta)var6).setTrim(((ArmorMeta)var7).getTrim());
            if (var7.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
               var6.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
            } else {
               var6.removeItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
            }
         }

         if (var1.hasData(ItemStats.SKULL_TEXTURE) && var5.getType() == Material.PLAYER_HEAD && var2.getItem().getType() == Material.PLAYER_HEAD) {
            MythicLib.plugin.getVersion().getWrapper().setProfile((SkullMeta)var6, ((SkullTextureData)var1.getData(ItemStats.SKULL_TEXTURE)).getGameProfile());
         }

         var5.setItemMeta(var6);
      }

      return var5;
   }

   public static class ApplyResult {
      private final ItemSkin.ResultType type;
      private final ItemStack result;

      public ApplyResult(ItemSkin.ResultType var1) {
         this(null, var1);
      }

      public ApplyResult(ItemStack var1) {
         this(var1, ItemSkin.ResultType.SUCCESS);
      }

      public ApplyResult(ItemStack var1, ItemSkin.ResultType var2) {
         this.type = var2;
         this.result = var1;
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
