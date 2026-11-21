package com.nexomc.nexo.utils.actions;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.libs.actions.actions.Action;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.UtilsKt;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB+\b\u0002\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0012\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00060\u0003¢\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0007J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\f\u001a\u00020\u0007R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00060\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/utils/actions/ClickAction;", "", "conditions", "", "", "actions", "Lcom/nexomc/libs/actions/actions/Action;", "Lorg/bukkit/entity/Player;", "<init>", "(Ljava/util/List;Ljava/util/List;)V", "canRun", "", "player", "performActions", "", "Companion", "com.nexomc:core"}
)
public final class ClickAction {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final List<String> conditions;
   @NotNull
   private final List<Action<Player>> actions;
   @NotNull
   private static final SpelExpressionParser PARSER = new SpelExpressionParser();

   private ClickAction(List<String> conditions, List<Action<Player>> actions) {
      this.conditions = conditions;
      this.actions = actions;
   }

   public final boolean canRun(@Nullable Player player) {
      if (this.conditions.isEmpty()) {
         return true;
      } else if (this.actions.isEmpty()) {
         return false;
      } else {
         StandardEvaluationContext context = new StandardEvaluationContext(player);
         context.setVariable("player", player);
         context.setVariable("server", Bukkit.getServer());
         Iterable iterable = (Iterable)this.conditions;
         boolean flag;
         if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
            flag = true;
         } else {
            for(Object element : iterable) {
               String condition = (String)element;
               int value = 0;
               ClickAction clickAction = this;

               Object obj2;
               try {
                  ClickAction clickAction2 = clickAction;
                  int value2 = 0;
                  Boolean flag2 = PARSER.parseExpression(condition).getValue((EvaluationContext)context, Boolean.TYPE);
                  if (flag2 == null) {
                     return false;
                  }

                  obj2 = ResultShim.ok(flag2);
               } catch (Throwable e) {
                  obj2 = ResultShim.ok(ResultKt.createFailure(e));
               }

               Object obj = UtilsKt.printOnFailure(obj2, true);
               Boolean flag3 = true;
               if (!(Boolean)(ResultShim.isFailure(obj) ? flag3 : obj)) {
                  flag = false;
                  return flag;
               }
            }

            flag = true;
         }

         return flag;
      }
   }

   public final void performActions(@NotNull Player player) {
      NexoPlugin.Companion.instance().clickActionManager().run(player, this.actions, false);
   }

   @JvmStatic
   @Nullable
   public static final ClickAction from(@NotNull ConfigurationSection config) {
      return Companion.from(config);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\tH\u0007b\u0002\b\nR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"},
      d2 = {"Lcom/nexomc/nexo/utils/actions/ClickAction$Companion;", "", "<init>", "()V", "PARSER", "Lorg/springframework/expression/spel/standard/SpelExpressionParser;", "from", "Lcom/nexomc/nexo/utils/actions/ClickAction;", "config", "Lorg/bukkit/configuration/ConfigurationSection;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @Nullable
      public final ClickAction from(@NotNull ConfigurationSection config) {
         Iterable iterable = (Iterable)NexoPlugin.Companion.instance().clickActionManager().parse(Player.class, (Collection)NexoYamlKt.stringListOrSingle(config, "action", (String)null));
         ClickAction clickAction;
         if (CollectionsKt.count(iterable) > 0) {
            List it = (List)iterable;
            int value = 0;
            List list = NexoYamlKt.stringListOrSingle(config, "condition", (String)null);
            clickAction = new ClickAction(list, it);
         } else {
            clickAction = null;
         }

         return clickAction;
      }
   }
}
