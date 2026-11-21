package com.nexomc.nexo.nms;

import kotlin.Metadata;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001:\u0001\tJ\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH&¨\u0006\nÀ\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/nms/IPluginConverter;", "", "convertItemsAdder", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "convertOraxen", "entity", "Lorg/bukkit/entity/Entity;", "EmptyPluginConverter", "com.nexomc:core"}
)
public interface IPluginConverter {
   void convertItemsAdder(@NotNull ItemStack itemStack);

   void convertOraxen(@NotNull ItemStack itemStack);

   void convertOraxen(@NotNull Entity entity);

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\nH\u0016¨\u0006\u000b"},
      d2 = {"Lcom/nexomc/nexo/nms/IPluginConverter$EmptyPluginConverter;", "Lcom/nexomc/nexo/nms/IPluginConverter;", "<init>", "()V", "convertItemsAdder", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "convertOraxen", "entity", "Lorg/bukkit/entity/Entity;", "com.nexomc:core"}
   )
   public static final class EmptyPluginConverter implements IPluginConverter {
      public void convertItemsAdder(@NotNull ItemStack itemStack) {
      }

      public void convertOraxen(@NotNull ItemStack itemStack) {
      }

      public void convertOraxen(@NotNull Entity entity) {
      }
   }
}
