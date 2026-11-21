package com.nexomc.nexo.configs;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SpreadBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b#\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0081\u0002\u0018\u0000 32\b\u0012\u0004\u0012\u00020\u00000\u0001:\u00013B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\n\u0010%\u001a\u00020\u0003H\u0096\u0080\u0004J)\u0010&\u001a\u00020'2\b\u0010(\u001a\u0004\u0018\u00010)2\u0012\u0010*\u001a\n\u0012\u0006\b\u0001\u0012\u00020,0+\"\u00020,¢\u0006\u0002\u0010-J\u000b\u0010.\u001a\u00070/¢\u0006\u0002\b0J\u001f\u00101\u001a\u00020'2\u0012\u0010*\u001a\n\u0012\u0006\b\u0001\u0012\u00020,0+\"\u00020,¢\u0006\u0002\u00102R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018j\u0002\b\u0019j\u0002\b\u001aj\u0002\b\u001bj\u0002\b\u001cj\u0002\b\u001dj\u0002\b\u001ej\u0002\b\u001fj\u0002\b j\u0002\b!j\u0002\b\"j\u0002\b#j\u0002\b$¨\u00064"},
   d2 = {"Lcom/nexomc/nexo/configs/Message;", "", "path", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "getPath", "()Ljava/lang/String;", "PREFIX", "NO_PERMISSION", "NOT_PLAYER", "COOLDOWN", "RELOAD", "PACK_REGENERATED", "UPDATING_CONFIG", "REMOVING_CONFIG", "UPDATED_ITEMS", "BAD_RECIPE", "ITEM_NOT_FOUND", "PLUGIN_HOOKS", "PLUGIN_UNHOOKS", "NOT_ENOUGH_SPACE", "EXIT_MENU", "NO_EMOJIS", "PLUGIN_UNLOADED", "COMMAND_HELP", "DEBUG_TOGGLE", "VERSION", "RECIPE_NO_BUILDER", "RECIPE_NO_RECIPE", "RECIPE_SAVE", "GIVE_PLAYER", "GIVE_PLAYERS", "DYE_SUCCESS", "DYE_WRONG_COLOR", "DYE_FAILED", "MECHANICS_JUKEBOX_NOW_PLAYING", "toString", "send", "", "sender", "Lorg/bukkit/command/CommandSender;", "placeholders", "", "Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "(Lorg/bukkit/command/CommandSender;[Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;)V", "toComponent", "Lnet/kyori/adventure/text/Component;", "Lorg/jetbrains/annotations/NotNull;", "log", "([Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;)V", "Companion", "com.nexomc:core"}
)
public enum Message {
   PREFIX("general.prefix"),
   NO_PERMISSION("general.no_permission"),
   NOT_PLAYER("general.not_player"),
   COOLDOWN("general.cooldown"),
   RELOAD("general.reload"),
   PACK_REGENERATED("general.pack_regenerated"),
   UPDATING_CONFIG("general.updating_config"),
   REMOVING_CONFIG("general.removing_config"),
   UPDATED_ITEMS("general.updated_items"),
   BAD_RECIPE("general.bad_recipe"),
   ITEM_NOT_FOUND("general.item_not_found"),
   PLUGIN_HOOKS("general.plugin_hooks"),
   PLUGIN_UNHOOKS("general.plugin_unhooks"),
   NOT_ENOUGH_SPACE("general.not_enough_space"),
   EXIT_MENU("general.exit_menu"),
   NO_EMOJIS("general.no_emojis"),
   PLUGIN_UNLOADED("logs.unloaded"),
   COMMAND_HELP("command.help"),
   DEBUG_TOGGLE("command.debug.toggle"),
   VERSION("command.version"),
   RECIPE_NO_BUILDER("command.recipe.no_builder"),
   RECIPE_NO_RECIPE("command.recipe.no_recipes"),
   RECIPE_SAVE("command.recipe.save"),
   GIVE_PLAYER("command.give.player"),
   GIVE_PLAYERS("command.give.players"),
   DYE_SUCCESS("command.dye.success"),
   DYE_WRONG_COLOR("command.dye.wrong_color"),
   DYE_FAILED("command.dye.failed"),
   MECHANICS_JUKEBOX_NOW_PLAYING("mechanics.jukebox_now_playing");

   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final String path;
   @NotNull
   private static TagResolver PREFIX_TAG_RESOLVER;

   private Message(String path) {
      this.path = path;
   }

   @NotNull
   public final String getPath() {
      return this.path;
   }

   @NotNull
   public String toString() {
      String string = NexoPlugin.Companion.instance().configsManager().getMessages().getString(this.path);
      return string;
   }

   public final void send(@Nullable CommandSender sender, @NotNull TagResolver... placeholders) {
      if (sender != null) {
         String str = NexoYamlKt.stringOrNull((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMessages(), this.path);
         if (str != null) {
            String lang = str;
            SpreadBuilder spreadBuilder = new SpreadBuilder(3);
            spreadBuilder.addSpread(placeholders);
            spreadBuilder.add(StandardTags.clickEvent());
            spreadBuilder.add(StandardTags.hoverEvent());
            TagResolver tagResolver = TagResolver.resolver((TagResolver[])spreadBuilder.toArray(new TagResolver[spreadBuilder.size()]));
            TagResolver resolver = tagResolver;
            sender.sendMessage(AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(lang, resolver));
         }
      }
   }

   @NotNull
   public final Component toComponent() {
      Component component = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(this.toString());
      return component;
   }

   public final void log(@NotNull TagResolver... placeholders) {
      this.send((CommandSender)Bukkit.getConsoleSender(), Arrays.copyOf(placeholders, placeholders.length));
   }


   static {
      TagResolver tagResolver = TagResolver.empty();
      PREFIX_TAG_RESOLVER = tagResolver;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\t\u001a\u00020\nR\u001e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u000b"},
      d2 = {"Lcom/nexomc/nexo/configs/Message$Companion;", "", "<init>", "()V", "value", "Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "PREFIX_TAG_RESOLVER", "getPREFIX_TAG_RESOLVER", "()Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "rebuildPrefixTagResolver", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final TagResolver getPREFIX_TAG_RESOLVER() {
         return Message.PREFIX_TAG_RESOLVER;
      }

      public final void rebuildPrefixTagResolver() {
         TagResolver.Single single = TagResolver.resolver("prefix", Tag.selfClosingInserting(AdventureUtils.STANDARD_MINI_MESSAGE.deserialize(Message.PREFIX.toString())));
         Message.PREFIX_TAG_RESOLVER = (TagResolver)single;
      }
   }
}
