package com.nexomc.libs.actions.actions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.nexomc.libs.actions.actions.implementations.DataAction;
import com.nexomc.libs.actions.components.ComponentParser;
import com.nexomc.libs.actions.placeholders.PlaceholderManager;
import com.nexomc.libs.actions.tasks.DefaultTaskProcessor;
import com.nexomc.libs.actions.tasks.TaskProcessor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ActionManager {
   protected final PlaceholderManager placeholderManager = new PlaceholderManager();
   protected final ComponentParser componentParser;
   protected final ActionParser actionParser;
   protected final SplittableRandom random;
   protected final Table<String, Class<?>, Action.Supplier<?>> actions;
   protected final TaskProcessor taskProcessor;
   protected final double maxChance;

   public ActionManager(@Nullable TaskProcessor taskProcessor, double maxChance) {
      this.componentParser = new ComponentParser(this.placeholderManager);
      this.actionParser = new ActionParser(this);
      this.random = new SplittableRandom();
      this.actions = HashBasedTable.create();
      this.taskProcessor = (TaskProcessor)(taskProcessor == null ? new DefaultTaskProcessor() : taskProcessor);
      this.maxChance = maxChance + (double)1.0F;
   }

   protected final <T> @Nullable Action<T> createAction(@NotNull Class<T> clazz, @NotNull String id, @NotNull ActionMeta<T> meta) {
      Action.Supplier<?> supplier = (Action.Supplier)this.actions.get(id.toLowerCase(), clazz);
      return supplier == null ? null : supplier.run(meta);
   }

   public @NotNull PlaceholderManager getPlaceholderManager() {
      return this.placeholderManager;
   }

   public @NotNull ComponentParser getComponentParser() {
      return this.componentParser;
   }

   public <T> boolean isRegistered(@NotNull Class<T> clazz, @NotNull String id) {
      return this.actions.get(id.toLowerCase(), clazz) != null;
   }

   public <T> void register(@NotNull Class<T> clazz, @NotNull String id, Action.@NotNull Supplier<T> action) {
      this.actions.put(id.toLowerCase(), clazz, action);
   }

   public <T> void registerDefaults(@NotNull Class<T> clazz) {
      this.register(clazz, "data", DataAction::new);
   }

   public <T> @NotNull List<Action<T>> parse(@NotNull Class<T> clazz, @NotNull Collection<String> actions) {
      return (List)actions.stream().map((it) -> this.actionParser.parseAction(clazz, it)).filter(Objects::nonNull).collect(Collectors.toList());
   }

   public <T> void run(@NotNull T t, @NotNull List<Action<T>> actions, boolean async, @NotNull Map<String, Object> data) {
      Context<T> context = new Context<T>(actions, data);

      for(Action<T> action : context) {
         ActionMeta<T> meta = action.getMeta();
         if (!meta.hasChance() || !(this.random.nextDouble((double)0.0F, this.maxChance) > meta.getChance())) {
            Runnable task = () -> action.run(t, context);
            if (meta.hasDelay()) {
               if (async) {
                  this.taskProcessor.runAsync(task, meta.getDelay());
               } else {
                  this.taskProcessor.runSync(task, meta.getDelay());
               }
            } else if (async) {
               this.taskProcessor.runAsync(task);
            } else {
               this.taskProcessor.runSync(task);
            }
         }
      }

   }

   public <T> void run(@NotNull T t, @NotNull List<Action<T>> actions, boolean async) {
      this.run(t, actions, async, new HashMap());
   }
}
