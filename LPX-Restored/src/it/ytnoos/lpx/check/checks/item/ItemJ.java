package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.FireworkExplosion;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemFireworks;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTIntArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import java.util.List;

public class ItemJ extends PlayerCheck<ItemStackDecodeEvent> {
   private int maxList;
   private int maxArray;

   public ItemJ(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.maxList = this.getConfig().getInt(this.getPath("max-list"), 12);
      this.maxArray = this.getConfig().getInt(this.getPath("max-array"), 12);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, ItemStackDecodeEvent itemStackDecodeEvent) {
      ItemStack itemStack = itemStackDecodeEvent.getItemStack();
      return protocolPlayer.usesComponents() ? this.checkFireworkComponents(itemStack, itemStackDecodeEvent) : this.checkFireworkNbt(itemStack, itemStackDecodeEvent);
   }

   private CheckResult checkFireworkComponents(ItemStack itemStack, ItemStackDecodeEvent itemStackDecodeEvent) {
      FireworkExplosion fireworkExplosion = (FireworkExplosion)itemStack.getComponentOr(ComponentTypes.FIREWORK_EXPLOSION, null);
      if (fireworkExplosion != null) {
         return this.checkExplosion(fireworkExplosion, itemStackDecodeEvent);
      }

      ItemFireworks itemFireworks = (ItemFireworks)itemStack.getComponentOr(ComponentTypes.FIREWORKS, null);
      if (itemFireworks == null) {
         return CheckResult.ALLOW;
      }

      List list = itemFireworks.getExplosions();
      if (list.size() > this.maxList) {
         this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("explosions", list.size())});
         return CheckResult.DENY;
      }

      for (Object var16_o : list) { FireworkExplosion fireworkExplosion2 = (FireworkExplosion) var16_o;
         CheckResult checkResult = this.checkExplosion(fireworkExplosion2, itemStackDecodeEvent);
         if (checkResult != CheckResult.ALLOW) {
            return checkResult;
         }
      }

      return CheckResult.ALLOW;
   }

   private CheckResult checkExplosion(FireworkExplosion fireworkExplosion, ItemStackDecodeEvent itemStackDecodeEvent) {
      if (fireworkExplosion.getColors().size() > this.maxArray) {
         this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("colors", fireworkExplosion.getColors().size())});
         return CheckResult.DENY;
      } else if (fireworkExplosion.getFadeColors().size() > this.maxArray) {
         this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("fade-colors", fireworkExplosion.getFadeColors().size())});
         return CheckResult.DENY;
      } else {
         return CheckResult.ALLOW;
      }
   }

   private CheckResult checkFireworkNbt(ItemStack itemStack, ItemStackDecodeEvent itemStackDecodeEvent) {
      NBTCompound nbtCompound = itemStack.getNBT();
      if (nbtCompound == null) {
         return CheckResult.ALLOW;
      }

      NBTCompound nbtCompound2 = nbtCompound.getCompoundTagOrNull("Explosion");
      if (nbtCompound2 != null) {
         return this.checkLegacyExplosion(nbtCompound2, itemStackDecodeEvent);
      }

      NBTCompound nbtCompound3 = nbtCompound.getCompoundTagOrNull("Fireworks");
      if (nbtCompound3 == null) {
         return CheckResult.ALLOW;
      }

      NBTList nbtList = nbtCompound3.getCompoundListTagOrNull("Explosions");
      if (nbtList == null) {
         return CheckResult.ALLOW;
      }

      if (nbtList.size() > this.maxList) {
         this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("explosions", nbtList.size())});
         return CheckResult.DENY;
      }

      for (Object var17_o : nbtList.getTags()) { NBTCompound nbtCompound4 = (NBTCompound) var17_o;
         CheckResult checkResult = this.checkLegacyExplosion(nbtCompound4, itemStackDecodeEvent);
         if (checkResult != CheckResult.ALLOW) {
            return checkResult;
         }
      }

      return CheckResult.ALLOW;
   }

   private CheckResult checkLegacyExplosion(NBTCompound nbtCompound, ItemStackDecodeEvent itemStackDecodeEvent) {
      CheckResult checkResult = this.checkColors(nbtCompound, "Colors", itemStackDecodeEvent);
      return checkResult != CheckResult.ALLOW ? checkResult : this.checkColors(nbtCompound, "FadeColors", itemStackDecodeEvent);
   }

   private CheckResult checkColors(NBTCompound nbtCompound, String string, ItemStackDecodeEvent itemStackDecodeEvent) {
      if (nbtCompound.getTagOrNull(string) instanceof NBTIntArray nbtIntArray) {
         int value = nbtIntArray.getValue().length;
         if (value > this.maxArray) {
            this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of(string.toLowerCase(), value)});
            return CheckResult.DENY;
         }
      }

      return CheckResult.ALLOW;
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
