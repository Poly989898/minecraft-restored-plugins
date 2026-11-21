package litebans;

import java.util.WeakHashMap;

public final class PunishmentCacheLocal extends ThreadLocal {
   PunishmentCacheLocal() {
   }

   protected WeakHashMap obtainWeakHashMap() {
      return new WeakHashMap();
   }

   @Override
   public Object initialValue() {
      return this.obtainWeakHashMap();
   }
}
