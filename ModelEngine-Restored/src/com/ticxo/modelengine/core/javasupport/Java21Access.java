package com.ticxo.modelengine.core.javasupport;

import com.ticxo.modelengine.api.utils.CompatibilityManager;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.core.ModelEngine;
import lombok.Getter;

@Getter
public enum Java21Access implements ReflectionUtils.MethodEnum {
   registerCompatibility(CompatibilityManager.class, ModelEngine.class),
   createNMSHandler(String.class),
   getMythicPackModelFiles();

   private static Class<?> HELPER;
   private final Class<?>[] parameterClasses;

   private static Class<?> getHelper() {
      if (!MiscUtils.isJavaVersionOrHigher(21)) {
         throw new RuntimeException("Cannot get Java 21 Helper: Server is not using Java 21+!");
      }

      if (HELPER == null) {
         try {
            HELPER = Class.forName("com.ticxo.modelengine.core21.Java21Helper");
         } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
         }
      }

      return HELPER;
   }

   Java21Access(Class<?>... parameterClasses) {
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
      return !MiscUtils.isJavaVersionOrHigher(21) ? null : ReflectionUtils.call(null, this, parameters);
   }
}
