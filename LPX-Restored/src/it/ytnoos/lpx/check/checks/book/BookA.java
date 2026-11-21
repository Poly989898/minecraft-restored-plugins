package it.ytnoos.lpx.check.checks.book;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;

public class BookA extends PlayerCheck<WrapperPlayClientEditBook> {
   private int maxSize;

   public BookA(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.maxSize = this.getInt("max", 15);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientEditBook wrapperPlayClientEditBook) {
      String string = wrapperPlayClientEditBook.getTitle();
      if (string != null) {
         int value = string.length();
         if (value > this.maxSize) {
            this.raiseViolation(wrapperPlayClientEditBook, new FlagInfo[]{FlagInfo.of("title", string), FlagInfo.of("length", value), FlagInfo.of("max", this.maxSize)});
            return CheckResult.DENY;
         }
      }

      return CheckResult.ALLOW;
   }

}
