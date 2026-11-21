package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.NBTDecodeEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class ItemC extends PlayerCheck<NBTDecodeEvent> {
   private boolean stripDisallowed;

   public ItemC(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   private static String stripDisallowed(String string) {
      StringBuilder stringBuilder = new StringBuilder();

      for (int value = 0; value < string.length(); value++) {
         char ch = string.charAt(value);
         if (isValidChar(ch)) {
            stringBuilder.append(ch);
         }
      }

      return stringBuilder.toString();
   }

   private static boolean isValidChar(int value) {
      if (value < 32) {
         return false;
      }

      return switch (Character.getType((char)value)) {
         case 0, 15, 16, 18, 19 -> false;
         default -> true;
      };
   }

   private static boolean isValid(String string) {
      for (int value = 0; value < string.length(); value++) {
         if (!isValidChar(string.charAt(value))) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void loadOptions() {
      this.stripDisallowed = this.getBoolean("remove-ascii", true);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, NBTDecodeEvent nbtDecodeEvent) {
      NBT nbt = nbtDecodeEvent.getNbt();
      if (!(nbt instanceof NBTCompound)) {
         return CheckResult.ALLOW;
      }

      NBTList nbtList = ((NBTCompound)nbt).getStringListTagOrNull("pages");
      if (nbtList == null) {
         return CheckResult.ALLOW;
      }

      boolean flag = false;

      for (int value = 0; value < nbtList.size(); value++) {
         NBTString nbtString = (NBTString)nbtList.getTag(value);
         String string = nbtString.getValue();
         if (!isValid(nbtString.getValue())) {
            if (this.raiseViolation(nbtDecodeEvent, new FlagInfo[]{FlagInfo.of("pages", nbtList.size()), FlagInfo.of("page-index", value), FlagInfo.of("page-length", string.length())})
               )
             {
               return CheckResult.DENY;
            }

            if (this.stripDisallowed) {
               nbtString.setValue(stripDisallowed(string));
               flag = true;
            }
         }
      }

      return flag ? CheckResult.CHANGE : CheckResult.ALLOW;
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
