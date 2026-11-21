package io.lumine.mythic.bukkit.utils.gson;

import com.google.gson.JsonElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface GsonSerializable {
   @Nonnull
   static <T extends GsonSerializable> T deserialize(@Nonnull Class<T> clazz, @Nonnull JsonElement element) {
      Method deserializeMethod = getDeserializeMethod(clazz);
      if (deserializeMethod == null) {
         throw new IllegalStateException("Class does not have a deserialize method accessible.");
      } else {
         try {
            return (T)deserializeMethod.invoke(null, element);
         } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
         }
      }
   }

   @Nonnull
   static GsonSerializable deserializeRaw(@Nonnull Class<?> clazz, @Nonnull JsonElement element) {
      Class<? extends GsonSerializable> typeCastedClass = clazz.asSubclass(GsonSerializable.class);
      return deserialize(typeCastedClass, element);
   }

   @Nullable
   static Method getDeserializeMethod(@Nonnull Class<?> clazz) {
      if (!GsonSerializable.class.isAssignableFrom(clazz)) {
         return null;
      } else {
         Method deserializeMethod;
         try {
            deserializeMethod = clazz.getDeclaredMethod("deserialize", JsonElement.class);
            deserializeMethod.setAccessible(true);
         } catch (Exception e) {
            return null;
         }

         return !Modifier.isStatic(deserializeMethod.getModifiers()) ? null : deserializeMethod;
      }
   }

   @Nonnull
   JsonElement serialize();
}
