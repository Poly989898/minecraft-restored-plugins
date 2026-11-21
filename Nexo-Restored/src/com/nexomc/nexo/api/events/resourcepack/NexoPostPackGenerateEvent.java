package com.nexomc.nexo.api.events.resourcepack;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.api.NexoPack;
import com.nexomc.nexo.pack.creative.NexoPackReader;
import com.nexomc.nexo.utils.UtilsKt;
import java.io.File;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.pack.PackFormat;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u0003J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\nJ\u0016\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\b\u0010\u0010\u001a\u00020\u0011H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/api/events/resourcepack/NexoPostPackGenerateEvent;", "Lorg/bukkit/event/Event;", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "getResourcePack", "()Lteam/unnamed/creative/ResourcePack;", "addResourcePack", "", "Ljava/io/File;", "addUnknownFile", "path", "", "data", "", "getHandlers", "Lorg/bukkit/event/HandlerList;", "Companion", "com.nexomc:core"}
)
public final class NexoPostPackGenerateEvent extends Event {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ResourcePack resourcePack;
   @NotNull
   private static final HandlerList handlerList = new HandlerList();

   public NexoPostPackGenerateEvent(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
   }

   @NotNull
   public final ResourcePack getResourcePack() {
      return this.resourcePack;
   }

   public final boolean addResourcePack(@NotNull ResourcePack resourcePack) {
      NexoPostPackGenerateEvent nexoPostPackGenerateEvent = this;

      Object obj2;
      try {
         NexoPostPackGenerateEvent nexoPostPackGenerateEvent2 = nexoPostPackGenerateEvent;
         int value = 0;
         NexoPack.mergePack(nexoPostPackGenerateEvent2.resourcePack, resourcePack);
         obj2 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = UtilsKt.printOnFailure(obj2, false);
      return (ResultShim.isFailure(obj) ? null : obj) != null;
   }

   public final boolean addResourcePack(@NotNull File resourcePack) {
      NexoPostPackGenerateEvent nexoPostPackGenerateEvent = this;

      Object obj2;
      try {
         NexoPostPackGenerateEvent nexoPostPackGenerateEvent2 = nexoPostPackGenerateEvent;
         int value = 0;
         NexoPack.mergePack(nexoPostPackGenerateEvent2.resourcePack, NexoPackReader.INSTANCE.readFile(resourcePack, (PackFormat)null));
         obj2 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = UtilsKt.printOnFailure(obj2, false);
      return (ResultShim.isFailure(obj) ? null : obj) != null;
   }

   public final boolean addUnknownFile(@NotNull String path, @NotNull byte[] data) {
      NexoPostPackGenerateEvent nexoPostPackGenerateEvent = this;

      Object obj2;
      try {
         NexoPostPackGenerateEvent nexoPostPackGenerateEvent2 = nexoPostPackGenerateEvent;
         int value = 0;
         nexoPostPackGenerateEvent2.resourcePack.unknownFile(path, Writable.bytes(data));
         obj2 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = UtilsKt.printOnFailure(obj2, false);
      return (ResultShim.isFailure(obj) ? null : obj) != null;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlerList;
   }

   @NotNull
   public static final HandlerList getHandlerList() {
      return Companion.getHandlerList();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R \u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004r\u0002\b\t¢\u0006\u000e\n\u0000\u0012\u0004\b\u0006\u0010\u0003\u001a\u0004\b\u0007\u0010\b¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/api/events/resourcepack/NexoPostPackGenerateEvent$Companion;", "", "<init>", "()V", "handlerList", "Lorg/bukkit/event/HandlerList;", "getHandlerList$annotations", "getHandlerList", "()Lorg/bukkit/event/HandlerList;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final HandlerList getHandlerList() {
         return NexoPostPackGenerateEvent.handlerList;
      }
   }
}
