package it.ytnoos.lpx.check;

import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.PersonalCheckOptions;

public class PendingPunishment {
   private final String name;
   private final PersonalCheckOptions checkOptions;
   private final long timestamp;
   private final FlagInfo[] info;

   public PendingPunishment(String string, PersonalCheckOptions personalCheckOptions, long longValue, FlagInfo[] flagInfos) {
      this.name = string;
      this.checkOptions = personalCheckOptions;
      this.timestamp = longValue;
      this.info = flagInfos;
   }

   public String getName() {
      return this.name;
   }

   public PersonalCheckOptions getOptions() {
      return this.checkOptions;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   public FlagInfo[] getInfo() {
      return this.info;
   }

}
