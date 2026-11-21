package litebans;

import java.util.ArrayList;

public final class KotlinFunction1Impl4 implements Function1 {
   final ArrayList arrayList2;
   final SyncModule syncModule;

   public KotlinFunction1Impl4(ArrayList list, SyncModule syncModule) {
      this.arrayList2 = list;
      this.syncModule = syncModule;
   }

   public final void handleKotlinUtil(KotlinUtil26 kotlinUtil) {
      this.arrayList2.add(kotlinUtil.getBytes2());
   }

   @Override
   public Object computeObj(Object obj) {
      this.handleKotlinUtil((KotlinUtil26)obj);
      return Unit.UNIT;
   }
}
