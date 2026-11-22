package litebans;

import java.util.concurrent.CopyOnWriteArrayList;

public final class LiteHelper12 implements LiteAInterface {
   final String expectedText;
   final CopyOnWriteArrayList copyOnWriteArrayList;

   LiteHelper12(String text, CopyOnWriteArrayList copyOnWriteArrayList) {
      this.expectedText = text;
      this.copyOnWriteArrayList = copyOnWriteArrayList;
   }

   @Override
   public boolean checkUserRecord(UserRecord userRecord, SyncMessageType syncMessageType, String text) {
      if (syncMessageType == SyncMessageType.PONG && Intrinsics.areEqual(text, this.expectedText) && userRecord != null) {
         this.copyOnWriteArrayList.add(userRecord);
      }

      return true;
   }
}
