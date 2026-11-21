package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.holograms.types.SpeechBubble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "speak",
   aliases = {"speech"},
   description = "Makes the caster speak using chat and speech bubbles"
)
public class SpeakMechanic extends Aura implements INoTargetSkill, ITargetedEntitySkill {
   protected PlaceholderString chatMessage;
   protected PlaceholderString bubbleMessage;
   protected SkillAudience audience;
   protected Float offset;
   protected PlaceholderInt radius;
   protected PlaceholderInt maxLineLength;
   protected boolean animateWords;
   protected boolean animateLetters;
   protected boolean sendChatMessage;
   protected PlaceholderString linePrefix;

   public SpeakMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.target_creative = true;
      this.audience = mlc.getAudience(new String[]{"audience"}, "tracked");
      this.auraName = Optional.of(PlaceholderString.of("#speaking"));
      this.charges = PlaceholderInt.of("1");
      this.maxStacks = PlaceholderInt.of("1");
      this.mergeSameCaster = false;
      this.overwriteCaster = true;
      this.refreshDuration = false;
      this.offset = mlc.getFloat(new String[]{"offset", "yoffset", "yo", "o"}, 1.0F);
      this.radius = mlc.getPlaceholderInteger(new String[]{"radius", "r"}, 12);
      this.maxLineLength = mlc.getPlaceholderInteger(new String[]{"maxlinelength", "linelength", "ll", "mll", "ml"}, 22);
      this.linePrefix = mlc.getPlaceholderString(new String[]{"lineprefix", "lp"}, null);
      this.sendChatMessage = mlc.getBoolean(new String[]{"sendchatmessage", "chatmessage", "chat"}, true);
      PlaceholderString message = mlc.getPlaceholderString(new String[]{"message", "msg", "m"}, null);
      String chatPrefix = mlc.getString(new String[]{"chatprefix", "cp"}, "<caster.name>&f<&co> ");
      if (message == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'message' attribute is required.");
         this.chatMessage = PlaceholderString.of("INCORRECTLY CONFIGURED. SEE CONSOLE ON RELOAD.");
         this.bubbleMessage = PlaceholderString.of("INCORRECTLY CONFIGURED. SEE CONSOLE ON RELOAD.");
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loaded Speak mechanic with message {0}", message.toString());
         this.chatMessage = PlaceholderString.of(chatPrefix + message);
         this.bubbleMessage = message;
      }

      this.duration = mlc.getPlaceholderDuration(
         new String[]{"ticks", "t", "duration", "d", "time", "t"}, DurationUnit.TICKS, String.valueOf(message.toString().length() * 4)
      );
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, data.getCaster().getEntity());
      String message = this.chatMessage.get(data);
      new SpeakMechanic.SpeechTracker(data, audience, message);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, target);
      String message = this.chatMessage.get(data, target);
      new SpeakMechanic.SpeechTracker(data, target, audience, message);
      return SkillResult.SUCCESS;
   }

   public void sendChatSpeech(SkillMetadata data, Collection<AbstractPlayer> audience, String message) {
      if (this.sendChatMessage) {
         int radius = this.radius.get(data);
         double radiusSq = (double)radius * radius;

         for (AbstractPlayer player : audience) {
            if (player.getLocation().distanceSquared(data.getCaster().getLocation()) <= radiusSq) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Sending speech to {0}: {1}", player.getName(), message);
               player.sendMessage(message);
            }
         }
      }
   }

   public class SpeechTracker extends Aura.AuraTracker {
      private final String speech;
      private final Collection<AbstractPlayer> audience;
      private SpeechBubble bubble;

      public SpeechTracker(SkillMetadata data, Collection<AbstractPlayer> audience, String message) {
         super(data);
         this.audience = audience;
         this.speech = SpeakMechanic.this.bubbleMessage.get(data);
         if (this.start()) {
            SpeakMechanic.this.sendChatSpeech(data, audience, message);
         }
      }

      public SpeechTracker(SkillMetadata data, AbstractEntity target, Collection<AbstractPlayer> audience, String message) {
         super(data.getCaster(), target, data);
         this.audience = audience;
         this.speech = SpeakMechanic.this.bubbleMessage.get(data, target);
         if (this.start()) {
            SpeakMechanic.this.sendChatSpeech(data, audience, message);
         }
      }

      @Override
      public void auraStart() {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Creating speech bubble...");
         float offset;
         if (SpeakMechanic.this.offset == null) {
            if (this.skillMetadata.getCaster() instanceof ActiveMob am) {
               offset = am.getType().getSpeechBubbleOffset();
            } else {
               offset = 0.4F;
            }
         } else {
            offset = SpeakMechanic.this.offset;
         }

         PacketTextDisplay.PacketTextDisplayBuilder options = PacketTextDisplay.create()
            .lineWidth(200)
            .backgroundColor(SpeakMechanic.this.getPlugin().getConfiguration().getHologramSpeechBackgroundColor())
            .billboard(SpeakMechanic.this.getPlugin().getConfiguration().getHologramSpeechBillboarding())
            .scale(new AbstractVector(1, 1, 1))
            .yOffset(offset)
            .brightness(15)
            .cullingDistance(SpeakMechanic.this.radius.get(this.skillMetadata));
         this.bubble = SpeakMechanic.this.getPlugin().getHologramManager().createSpeechBubble(this.skillMetadata.getCaster(), () -> this.audience, options);
         this.bubble.setLineLength(SpeakMechanic.this.maxLineLength.get(this.skillMetadata));
         if (SpeakMechanic.this.linePrefix != null) {
            String prefix = SpeakMechanic.this.linePrefix.get();
            if (prefix != null && !prefix.isEmpty()) {
               this.bubble.setLinePrefix(prefix);
            }
         }

         this.bubble.setText(this.speech);
         this.executeAuraSkill(SpeakMechanic.this.onStartSkill, this.skillMetadata);
      }

      @Override
      public void auraTick() {
         this.executeAuraSkill(SpeakMechanic.this.onTickSkill, this.skillMetadata);
      }

      @Override
      public void auraStop() {
         this.bubble.terminate();
         if (SpeakMechanic.this.doEndSkillOnTerminate) {
            this.executeAuraSkill(SpeakMechanic.this.onEndSkill, this.skillMetadata);
         }
      }
   }
}
