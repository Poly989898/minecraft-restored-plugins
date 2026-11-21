package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.NBTDecodeEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.player.BukkitProtocolPlayer;
import it.ytnoos.lpx.utils.string.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ItemE extends PlayerCheck<NBTDecodeEvent> {
   private static final String[] DEFAULT_OCCURRENCES = new String[]{"translate,3", "with,3", "extra,15", "{,15"};
   private boolean clear;
   private Map<String, Integer> occurrences;

   public ItemE(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.clear = this.getBoolean("clear", true);
      this.occurrences = new HashMap<>();

      for (Object var12_o : this.getStringList("occurrences", DEFAULT_OCCURRENCES)) { String string = (String) var12_o;
         String[] strings = string.split(",");
         int value = strings.length;
         if (value < 2) {
            ((BukkitProtocolPlayer)this.player).getLpxBukkit().getLogger().warning(() -> string + " is not a valid ItemE parameter!");
         } else {
            try {
               this.occurrences.put(strings[0], Integer.parseInt(strings[1]));
            } catch (NumberFormatException numberFormatException) {
               ((BukkitProtocolPlayer)this.player).getLpxBukkit().getLogger().severe("Second parameter for ItemE must be a number!");
            }
         }
      }
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, NBTDecodeEvent nbtDecodeEvent) {
      if (!(nbtDecodeEvent.getNbt() instanceof NBTCompound)) {
         return CheckResult.ALLOW;
      }

      NBTList nbtList = ((NBTCompound)nbtDecodeEvent.getNbt()).getStringListTagOrNull("pages");
      if (nbtList == null) {
         return CheckResult.ALLOW;
      }

      for (int value = 0; value < nbtList.size(); value++) {
         NBTString nbtString = (NBTString)nbtList.getTag(value);
         String string = nbtString.getValue();

         for (Entry entry : this.occurrences.entrySet()) {
            int value2 = StringUtils.countMatches(string, (CharSequence)entry.getKey());
            if (value2 > (Integer)entry.getValue()) {
               if (this.clear) {
                  nbtString.setValue("");
               }

               this.raiseViolation(
                  nbtDecodeEvent,
                  new FlagInfo[]{
                     FlagInfo.of("pages", nbtList.size()),
                     FlagInfo.of("page-index", value),
                     FlagInfo.of("page-length", string.getBytes().length),
                     FlagInfo.of("occurrence", entry.getKey()),
                     FlagInfo.of("matches", value2)
                  }
               );
               return CheckResult.DENY;
            }
         }
      }

      return CheckResult.ALLOW;
   }

   @Override
   public boolean isPreVia() {
      return true;
   }
}
