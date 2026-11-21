package litebans;

import java.util.concurrent.CopyOnWriteArrayList;

public final class LiteHelper12 implements LiteAInterface {
   final String text2;
   final CopyOnWriteArrayList copyOnWriteArrayList;

   LiteHelper12(String text, CopyOnWriteArrayList copyOnWriteArrayList) {
      this.text2 = text;
      this.copyOnWriteArrayList = copyOnWriteArrayList;
   }

   @Override
   public boolean checkUserRecord(UserRecord userRecord, SyncMessageType syncMessageType, String text) {
      if (syncMessageType == SyncMessageType.PONG && Intrinsics.areEqual(text, this.text2) && userRecord != null) {
         this.copyOnWriteArrayList.add(userRecord);
      }

      return true;
   }
}
