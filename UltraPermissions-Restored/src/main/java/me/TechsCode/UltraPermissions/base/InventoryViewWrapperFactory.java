package me.TechsCode.UltraPermissions.base;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class InventoryViewWrapperFactory {
   private static final Class<?> inventoryViewClass;
   private static final HashMap<String, MethodHandle> inventoryViewGetterMethodHandles = new HashMap<>();
   private static final HashMap<String, MethodHandle> inventoryViewMethodHandles = new HashMap<>();

   public InventoryViewWrapperFactory() {
   }

   public static InventoryViewWrapperFactory.InventoryViewWrapper getInventoryViewWrapper(Object obj, @NotNull String text, Object... objs) {
      if (obj == null) {
         throw new NullPointerException("WrapperFactory: InventoryViewProvider is null, cannot invoke method " + text);
      }

      String cacheKey = obj.getClass().getName() + text;
      MethodHandle ifAbsent = inventoryViewGetterMethodHandles.computeIfAbsent(cacheKey, key -> {
         try {
            Class clazz = obj.getClass();
            Method method = clazz.getMethod(text, Arrays.stream(objs).map(Object::getClass).toArray(Class[]::new));
            Lookup lookup = MethodHandles.lookup();
            MethodHandle methodHandle = lookup.unreflect(method);
            methodHandle.asFixedArity();
            return methodHandle;
         } catch (Throwable exception) {
            throw new RuntimeException(exception);
         }
      });
      Object inventoryView = null;

      try {
         Object[] arguments = Stream.concat(Stream.of(obj), Stream.of(objs)).toArray(Object[]::new);
         inventoryView = ifAbsent.invokeWithArguments(arguments);
      } catch (Throwable exception) {
         throw new RuntimeException(exception);
      }

      if (inventoryView != null && !inventoryViewClass.isAssignableFrom(inventoryView.getClass())) {
         throw new RuntimeException("InventoryView Wrong Class: " + inventoryView.getClass().getName());
      } else {
         return new InventoryViewWrapperFactory.InventoryViewWrapper(inventoryView);
      }
   }

   @NotNull
   public static InventoryViewWrapperFactory.InventoryViewWrapper getOpenInventory(@NotNull HumanEntity humanEntity) {
      return getInventoryViewWrapper(humanEntity, "getOpenInventory");
   }

   @NotNull
   public static InventoryViewWrapperFactory.InventoryViewWrapper getView(@NotNull InventoryEvent event) {
      return getInventoryViewWrapper(event, "getView");
   }

   static {
      try {
         inventoryViewClass = Class.forName("org.bukkit.inventory.InventoryView");
      } catch (ClassNotFoundException exception) {
         throw new RuntimeException(exception);
      }
   }

   public static class InventoryViewWrapper {
      private final Object inventoryView;

      public InventoryViewWrapper(Object obj) {
         this.inventoryView = obj;
      }

      public <T> T invokeMethod(@NotNull String text, @NotNull Class<T> type, Object... objs) {
         return this.invokeMethod(text, "", type, objs);
      }

      public <T> T invokeMethod(@NotNull String text, @NotNull String memberName, @NotNull Class<T> type, Object... objs) {
         if (this.inventoryView == null) {
            throw new NullPointerException("Wrapper: InventoryView is null, cannot invoke method " + text);
         }

         MethodHandle ifAbsent = InventoryViewWrapperFactory.inventoryViewMethodHandles
            .computeIfAbsent(this.inventoryView.getClass().getName() + text + memberName, key -> {
               try {
                  Class clazz = this.inventoryView.getClass();
                  Lookup lookup = MethodHandles.lookup();
                  Lookup privateLookup = MethodHandles.privateLookupIn(clazz, lookup);
                  Method method = clazz.getMethod(text, Arrays.stream(objs).map(Object::getClass).toArray(Class[]::new));
                  MethodHandle methodHandle = privateLookup.unreflect(method);
                  methodHandle.asFixedArity();
                  return methodHandle;
               } catch (Throwable exception) {
                  throw new RuntimeException(exception);
               }
            });

         try {
            Object[] arguments = Stream.concat(Stream.of(this.inventoryView), Stream.of(objs)).toArray(Object[]::new);
            return (T)ifAbsent.invokeWithArguments(arguments);
         } catch (Throwable exception) {
            throw new RuntimeException(exception);
         }
      }

      @NotNull
      public Inventory getTopInventory() {
         return this.invokeMethod("getTopInventory", Inventory.class);
      }

      @NotNull
      public HumanEntity getPlayer() {
         return this.invokeMethod("getPlayer", HumanEntity.class);
      }
   }
}
