package io.lumine.mythic.core.volatilecode;

import java.lang.reflect.Method;

public final class FoliaEntityTicking {
   private static volatile Method getCurrentWorldData;
   private static Method hasEntityTickingEntity;
   private static Method addEntityTickingEntity;
   private static Method removeEntityTickingEntity;

   private FoliaEntityTicking() {
   }

   public static boolean setTicking(Object level, Object entity, Class<?> entityType, boolean ticking) {
      try {
         if (getCurrentWorldData == null) {
            Method worldData = level.getClass().getMethod("getCurrentWorldData");
            Class<?> type = worldData.getReturnType();
            hasEntityTickingEntity = type.getMethod("hasEntityTickingEntity", entityType);
            addEntityTickingEntity = type.getMethod("addEntityTickingEntity", entityType);
            removeEntityTickingEntity = type.getMethod("removeEntityTickingEntity", entityType);
            getCurrentWorldData = worldData;
         }

         Object data = getCurrentWorldData.invoke(level);
         if (ticking) {
            addEntityTickingEntity.invoke(data, entity);
            return true;
         } else if (!(Boolean)hasEntityTickingEntity.invoke(data, entity)) {
            return false;
         } else {
            removeEntityTickingEntity.invoke(data, entity);
            return true;
         }
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }
}
