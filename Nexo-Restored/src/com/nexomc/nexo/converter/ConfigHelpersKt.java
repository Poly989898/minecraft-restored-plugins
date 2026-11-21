package com.nexomc.nexo.converter;

import com.nexomc.nexo.decomp.ResultShim;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000>\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a7\u0010\u0000\u001a\u0004\u0018\u0001H\u0001\"\b\b\u0000\u0010\u0001*\u00020\u0002\"\u0004\b\u0001\u0010\u0003*\u0002H\u00012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00030\u0005H\u0000¢\u0006\u0002\u0010\u0006\u001a7\u0010\u0007\u001a\u0004\u0018\u0001H\u0003\"\b\b\u0000\u0010\u0001*\u00020\u0002\"\u0004\b\u0001\u0010\u0003*\u0002H\u00012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00030\u0005H\u0000¢\u0006\u0002\u0010\b\u001a%\u0010\t\u001a\u0004\u0018\u00010\u0002\"\b\b\u0000\u0010\u0001*\u00020\u0002*\u0002H\u00012\u0006\u0010\n\u001a\u00020\u000bH\u0000¢\u0006\u0002\u0010\f\u001a%\u0010\t\u001a\u0004\u0018\u00010\u0002\"\b\b\u0000\u0010\u0001*\u00020\u0002*\u0002H\u00012\u0006\u0010\r\u001a\u00020\u0002H\u0000¢\u0006\u0002\u0010\u000e\u001a-\u0010\u000f\u001a\n \u0010*\u0004\u0018\u00010\u00020\u0002\"\b\b\u0000\u0010\u0001*\u00020\u0002*\u0002H\u00012\b\u0010\n\u001a\u0004\u0018\u00010\u0011H\u0000¢\u0006\u0002\u0010\u0012\u001a\f\u0010\u001b\u001a\u00020\u000b*\u00020\u001cH\u0000\"2\u0010\u0013\u001a\u0012\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u000b0\u000b\u0018\u00010\u0014\"\b\b\u0000\u0010\u0001*\u00020\u0002*\u0002H\u00018@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016\"R\u0010\u0017\u001a2\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u000b0\u000b\u0018\u0001 \u0010*\u0017\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u000b0\u000b\u0018\u00010\u0014¢\u0006\u0002\b\u00190\u0018¢\u0006\u0002\b\u0019\"\b\b\u0000\u0010\u0001*\u00020\u0002*\u0002H\u00018@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u0016¨\u0006\u001d"},
   d2 = {"ifExists", "N", "Lorg/spongepowered/configurate/ConfigurationNode;", "R", "block", "Lkotlin/Function1;", "(Lorg/spongepowered/configurate/ConfigurationNode;Lkotlin/jvm/functions/Function1;)Lorg/spongepowered/configurate/ConfigurationNode;", "onExists", "(Lorg/spongepowered/configurate/ConfigurationNode;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "renameNode", "key", "", "(Lorg/spongepowered/configurate/ConfigurationNode;Ljava/lang/String;)Lorg/spongepowered/configurate/ConfigurationNode;", "node", "(Lorg/spongepowered/configurate/ConfigurationNode;Lorg/spongepowered/configurate/ConfigurationNode;)Lorg/spongepowered/configurate/ConfigurationNode;", "removeChildNode", "kotlin.jvm.PlatformType", "", "(Lorg/spongepowered/configurate/ConfigurationNode;Ljava/lang/Object;)Lorg/spongepowered/configurate/ConfigurationNode;", "stringList", "", "getStringList", "(Lorg/spongepowered/configurate/ConfigurationNode;)Ljava/util/List;", "stringListOrNull", "", "Lorg/checkerframework/checker/nullness/qual/Nullable;", "getStringListOrNull", "fineString", "Lorg/bukkit/Location;", "com.nexomc:core"}
)
public final class ConfigHelpersKt {
   @Nullable
   public static final <N extends ConfigurationNode, R> N ifExists(@NotNull N n, @NotNull Function1<? super N, ? extends R> block) {
      int value = 0;
      ConfigurationNode configurationNode = !n.virtual() ? n : null;
      if (configurationNode != null) {
         ConfigurationNode it = configurationNode;
         value = 0;
         block.invoke((N)it);
         configurationNode = it;
      } else {
         configurationNode = null;
      }

      return (N)configurationNode;
   }

   @Nullable
   public static final <N extends ConfigurationNode, R> R onExists(@NotNull N n, @NotNull Function1<? super N, ? extends R> block) {
      int value = 0;
      ConfigurationNode configurationNode = !n.virtual() ? n : null;
      Object obj;
      if (configurationNode != null) {
         ConfigurationNode it = configurationNode;
         value = 0;
         obj = block.invoke((N)it);
      } else {
         obj = null;
      }

      return (R)obj;
   }

   @Nullable
   public static final <N extends ConfigurationNode> ConfigurationNode renameNode(@NotNull N n, @NotNull String key) {
      ConfigurationNode parent = n.parent();
      if (parent != null) {
         ConfigurationNode configurationNode = null;
         parent = parent.node(new Object[]{key});
         if (parent != null) {
            parent = parent.mergeFrom(n);
            if (parent != null) {
               ConfigurationNode configurationNode2 = parent;
               int value = 0;
               ConfigurationNode configurationNode3 = n.parent();
               configurationNode3.removeChild(n.key());
               parent = configurationNode2;
               return parent;
            }
         }
      }

      parent = null;
      return parent;
   }

   @Nullable
   public static final <N extends ConfigurationNode> ConfigurationNode renameNode(@NotNull N n, @NotNull ConfigurationNode node) {
      ConfigurationNode configurationNode = node.mergeFrom(n);
      if (configurationNode != null) {
         ConfigurationNode configurationNode2 = configurationNode;
         int value = 0;
         configurationNode = n.parent();
         configurationNode.removeChild(n.key());
         configurationNode = configurationNode2;
      } else {
         configurationNode = null;
      }

      return configurationNode;
   }

   public static final <N extends ConfigurationNode> ConfigurationNode removeChildNode(@NotNull N n, @Nullable Object key) {
      ConfigurationNode configurationNode = n.node(key).copy();
      int value = 0;
      n.removeChild(key);
      return configurationNode;
   }

   @Nullable
   public static final <N extends ConfigurationNode> List<String> getStringList(@NotNull N n) {
      ConfigurationNode configurationNode = n;

      Object obj;
      Object result1;
      Object result2;
      try {
         int value = 0;
         result2 = ResultShim.ok(configurationNode.getList(String.class));
      } catch (Throwable e) {
         result2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj2 = result2;
      List emptyList = CollectionsKt.emptyList();
      return (List)(ResultShim.isFailure(obj2) ? emptyList : obj2);
   }

   @Nullable
   public static final <N extends ConfigurationNode> List<String> getStringListOrNull(@NotNull N n) {
      ConfigurationNode configurationNode = n;

      Object obj;
      try {
         List it;
         boolean flag;
         block: {
            int value = 0;
            it = configurationNode.getList(String.class);
            int value2 = 0;
            if (!configurationNode.virtual()) {
               Collection collection = (Collection)it;
               if (collection != null && !collection.isEmpty()) {
                  flag = false;
                  break block;
               }
            }

            flag = true;
         }

         obj = ResultShim.ok(!flag ? it : null);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (List)(ResultShim.isFailure(obj) ? null : obj);
   }

   @NotNull
   public static final String fineString(@NotNull Location location) {
      double x = location.getX();
      return x + "," + location.getY() + "," + location.getZ() + " | " + location.getWorld().getName();
   }
}
