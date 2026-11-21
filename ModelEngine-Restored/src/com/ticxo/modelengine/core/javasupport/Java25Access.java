package com.ticxo.modelengine.core.javasupport;

import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import lombok.Getter;

@Getter
public enum Java25Access implements ReflectionUtils.MethodEnum {
   createNMSHandler(String.class);

   private static Class<?> HELPER;
   private final Class<?>[] parameterClasses;

   private static Class<?> getHelper() {
      if (!MiscUtils.isJavaVersionOrHigher(25)) {
         throw new RuntimeException("Cannot get Java 25 Helper: Server is not using Java 25+!");
      }

      if (HELPER == null) {
         try {
            HELPER = Class.forName("com.ticxo.modelengine.core25.Java25Helper");
         } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
         }
      }

      return HELPER;
   }

   Java25Access(Class<?>... parameterClasses) {
      this.parameterClasses = parameterClasses;
   }

   @Override
   public Class<?> getTarget() {
      return getHelper();
   }

   @Override
   public String getObfuscated() {
      return this.toString();
   }

   @Override
   public String getMapped() {
      return this.toString();
   }

   public <T> T call(Object... parameters) {
      return !MiscUtils.isJavaVersionOrHigher(25) ? null : ReflectionUtils.call(null, this, parameters);
   }
}
