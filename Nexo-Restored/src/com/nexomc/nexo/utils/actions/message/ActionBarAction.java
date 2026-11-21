package com.nexomc.nexo.utils.actions.message;

import com.nexomc.libs.actions.actions.Action;
import com.nexomc.libs.actions.actions.ActionMeta;
import com.nexomc.libs.actions.actions.Context;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \f2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\fB\u0017\u0012\u000e\u0010\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u001e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00022\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00020\u000bH\u0016¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/utils/actions/message/ActionBarAction;", "Lcom/nexomc/libs/actions/actions/Action;", "Lorg/bukkit/entity/Player;", "meta", "Lcom/nexomc/libs/actions/actions/ActionMeta;", "<init>", "(Lcom/nexomc/libs/actions/actions/ActionMeta;)V", "run", "", "player", "context", "Lcom/nexomc/libs/actions/actions/Context;", "Companion", "com.nexomc:core"}
)
public final class ActionBarAction extends Action<Player> {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   public static final String IDENTIFIER = "actionbar";

   public ActionBarAction(@NotNull ActionMeta<Player> meta) {
      super(meta);
   }

   public void run(@NotNull Player player, @NotNull Context<Player> context) {
      String parsedData = this.getMeta().getParsedData(player, context);
      player.sendActionBar(AdventureUtilsKt.deserialize(parsedData));
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/utils/actions/message/ActionBarAction$Companion;", "", "<init>", "()V", "IDENTIFIER", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
