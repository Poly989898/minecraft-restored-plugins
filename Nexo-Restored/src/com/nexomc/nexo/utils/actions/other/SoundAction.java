package com.nexomc.nexo.utils.actions.other;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.libs.actions.actions.Action;
import com.nexomc.libs.actions.actions.ActionMeta;
import com.nexomc.libs.actions.actions.Context;
import com.nexomc.nexo.utils.UtilsKt;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.util.Index;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00142\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0014B\u0017\u0012\u000e\u0010\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u001e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00022\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00020\u0013H\u0016R\u0013\u0010\u0007\u001a\u00070\b¢\u0006\u0002\b\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\n\u001a\u00070\u000b¢\u0006\u0002\b\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\f\u001a\u00070\u000b¢\u0006\u0002\b\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\r\u001a\u00070\u000e¢\u0006\u0002\b\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/nexo/utils/actions/other/SoundAction;", "Lcom/nexomc/libs/actions/actions/Action;", "Lorg/bukkit/entity/Player;", "meta", "Lcom/nexomc/libs/actions/actions/ActionMeta;", "<init>", "(Lcom/nexomc/libs/actions/actions/ActionMeta;)V", "source", "Lnet/kyori/adventure/sound/Sound$Source;", "Lorg/jetbrains/annotations/NotNull;", "volume", "", "pitch", "self", "", "run", "", "player", "context", "Lcom/nexomc/libs/actions/actions/Context;", "Companion", "com.nexomc:core"}
)
public final class SoundAction extends Action<Player> {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Sound.Source source;
   private final float volume;
   private final float pitch;
   private final boolean self;
   @NotNull
   public static final String IDENTIFIER = "sound";

   public SoundAction(@NotNull ActionMeta<Player> meta) {
      super(meta);
      Object property = meta.getProperty("source", Source.MASTER, SoundAction::source$lambda$0);
      this.source = (Sound.Source)property;
      property = meta.getProperty("volume", 1.0F, SoundAction::volume$lambda$0);
      this.volume = ((Number)property).floatValue();
      property = meta.getProperty("pitch", 1.0F, SoundAction::pitch$lambda$0);
      this.pitch = ((Number)property).floatValue();
      property = meta.getProperty("self", true, SoundAction::self$lambda$0);
      this.self = (Boolean)property;
   }

   public void run(@NotNull Player player, @NotNull Context<Player> context) {
      String parsedData = this.getMeta().getParsedData(player, context);
      String parsed = parsedData;
      SoundAction soundAction = this;

      Object obj;
      try {
         SoundAction soundAction2 = soundAction;
         int value = 0;
         Sound sound2 = Sound.sound(Key.key(parsed), soundAction2.source, soundAction2.volume, soundAction2.pitch);
         Sound sound = sound2;
         if (soundAction2.self) {
            player.playSound(sound);
         } else {
            player.getWorld().playSound(sound, player.getX(), player.getY(), player.getZ());
         }

         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      UtilsKt.printOnFailure(obj, false);
   }

   private static final Sound.Source source$lambda$0(String it) {
      Index index = Source.NAMES;
      String str = it.toLowerCase(Locale.ROOT);
      return (Sound.Source)index.value(str);
   }

   private static final Float volume$lambda$0(String it) {
      return StringsKt.toFloatOrNull(it);
   }

   private static final Float pitch$lambda$0(String it) {
      return StringsKt.toFloatOrNull(it);
   }

   private static final Boolean self$lambda$0(String it) {
      return Boolean.parseBoolean(it);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/utils/actions/other/SoundAction$Companion;", "", "<init>", "()V", "IDENTIFIER", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
