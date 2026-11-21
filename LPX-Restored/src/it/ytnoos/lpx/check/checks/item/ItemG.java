package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.simple.NBTDecodeEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import java.util.Map.Entry;

public class ItemG extends PlayerCheck<NBTDecodeEvent> {
   private static final int D = 200;
   private int maxTranslate;
   private int maxKeybind;
   private boolean legacyClient;

   public ItemG(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.maxTranslate = this.getConfig().getInt(this.getPath("max-translate"), 1);
      this.maxKeybind = this.getConfig().getInt(this.getPath("max-keybind"), 1);
      this.legacyClient = this.player.getUser().getClientVersion().isOlderThan(ClientVersion.V_1_16)
         && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_16);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, NBTDecodeEvent nbtDecodeEvent) {
      return protocolPlayer.usesComponents() ? CheckResult.ALLOW : this.checkNbt("", nbtDecodeEvent.getNbt(), nbtDecodeEvent);
   }

   private CheckResult checkNbt(String string, NBT nbt, NBTDecodeEvent nbtDecodeEvent) {
      if (nbt instanceof NBTString nbtString) {
         return this.checkString(string, nbtString, nbtDecodeEvent);
      } else if (nbt instanceof NBTList nbtList) {
         for (Object var21_o : nbtList.getTags()) { NBT nbt2 = (NBT) var21_o;
            CheckResult checkResult = this.checkNbt(string, nbt2, nbtDecodeEvent);
            if (checkResult == CheckResult.DENY) {
               return checkResult;
            }
         }

         return CheckResult.ALLOW;
      } else if (nbt instanceof NBTCompound nbtCompound) {
         for (Entry entry : nbtCompound.getTags().entrySet()) {
            CheckResult checkResult2 = this.checkNbt((String)entry.getKey(), (NBT)entry.getValue(), nbtDecodeEvent);
            if (checkResult2 == CheckResult.DENY) {
               return checkResult2;
            }
         }

         return CheckResult.ALLOW;
      } else {
         return CheckResult.ALLOW;
      }
   }

   private CheckResult checkString(String string, NBTString nbtString, NBTDecodeEvent nbtDecodeEvent) {
      String string2 = nbtString.getValue();
      if (string2.isEmpty() || string2.charAt(0) == '"') {
         return CheckResult.ALLOW;
      }

      if (string2.contains("{") && string2.contains("}")) {
         int value = this.countKey(string2, "translate", this.maxTranslate + 1);
         if (value == -1) {
            this.raiseViolation(nbtDecodeEvent, new FlagInfo[]{FlagInfo.of("identifier", string), FlagInfo.of("visited", "translate")});
            return CheckResult.DENY;
         }

         if (value > this.maxTranslate) {
            int value2 = this.countKey(string2, "with", this.maxTranslate + 1);
            if (value2 == -1) {
               this.raiseViolation(nbtDecodeEvent, new FlagInfo[]{FlagInfo.of("identifier", string), FlagInfo.of("visited", "with")});
               return CheckResult.DENY;
            }

            if (value2 > this.maxTranslate) {
               this.raiseViolation(nbtDecodeEvent, new FlagInfo[]{FlagInfo.of("identifier", string), FlagInfo.of("translate", value), FlagInfo.of("with", value2)});
               return CheckResult.DENY;
            }
         }

         int value3 = this.countKey(string2, "keybind", this.maxKeybind + 1);
         if (value3 == -1) {
            this.raiseViolation(nbtDecodeEvent, new FlagInfo[]{FlagInfo.of("identifier", string), FlagInfo.of("visited", "keybind")});
            return CheckResult.DENY;
         } else if (value3 > this.maxKeybind) {
            this.raiseViolation(nbtDecodeEvent, new FlagInfo[]{FlagInfo.of("identifier", string), FlagInfo.of("keybind", value3)});
            return CheckResult.DENY;
         } else {
            return CheckResult.ALLOW;
         }
      } else {
         return CheckResult.ALLOW;
      }
   }

   private int countKey(String string, String string2, int value) {
      int value2 = 0;
      int value3 = 0;
      int value4 = 0;
      int value5 = string2.length();

      while ((value3 = string.indexOf(string2, value3)) != -1) {
         if (++value4 > 200) {
            return -1;
         }

         char ch = value3 > 0 ? string.charAt(value3 - 1) : '{';
         char ch2 = value3 + value5 < string.length() ? string.charAt(value3 + value5) : ' ';
         boolean flag = ch == '"' && (ch2 == '"' || ch2 == '\\');
         if (!flag && this.legacyClient) {
            flag = ch == '\'' && ch2 == '\'' || isSeparator(ch) && ch2 == ':';
         }

         if (flag) {
            if (++value2 >= value) {
               return value2;
            }
         }

         value3 += value5;
      }

      return value2;
   }

   private static boolean isSeparator(int value) {
      return value == 123 || value == 44 || value == 32 || value == 10 || value == 9;
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
