package com.ticxo.modelengine.core21.mythic.mechanics.nametag;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.NameTag;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.placeholders.PlaceholderVector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;

@MythicMechanic(name = "setmodeltag", aliases = {})
public class SetNametagMechanic implements ITargetedEntitySkill {
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final PlaceholderString tag;
   private final boolean visible;
   private final Integer backgroundColor;
   private final boolean useDefaultBackgroundColor;
   private final TextAlignment alignment;
   private final Billboard billboard;
   private final PlaceholderVector scale;
   private final boolean seeThrough;
   private final byte textOpacity;
   private final boolean shadow;
   private final int lineWidth;

   public SetNametagMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = mlc.getPlaceholderString(new String[]{"b", "bone", "p", "pid", "part", "partid"}, null, new String[0]);
      this.tag = mlc.getPlaceholderString(new String[]{"t", "tag"}, null, new String[0]);
      this.visible = mlc.getBoolean(new String[]{"v", "visible"}, true);
      this.backgroundColor = mlc.getInteger(new String[]{"background", "backgroundcolor"}, 0);
      this.useDefaultBackgroundColor = mlc.getBoolean(new String[]{"usedefaultbackground", "defaultbackground", "db"}, true);
      this.alignment = mlc.getEnum(new String[]{"align", "alignment"}, TextAlignment.class, TextAlignment.CENTER);
      this.billboard = mlc.getEnum(new String[]{"billboard"}, Billboard.class, Billboard.CENTER);
      this.scale = mlc.getPlaceholderVector(new String[]{"scale"}, "1,1,1");
      this.seeThrough = mlc.getBoolean(new String[]{"seethrough"}, true);
      this.textOpacity = (byte)mlc.getInteger(new String[]{"opacity", "textopacity"}, -1);
      this.shadow = mlc.getBoolean(new String[]{"shadow"}, false);
      this.lineWidth = mlc.getInteger(new String[]{"linewidth"}, 200);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
      if (model == null) {
         return SkillResult.CONDITION_FAILED;
      }

      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      model.getModel(modelId)
         .ifPresent(
            activeModel -> {
               String partId = MythicUtils.getOrNullLowercase(this.partId, meta, target);
               activeModel.getBone(partId)
                  .flatMap(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG))
                  .ifPresent(
                     nameTag -> {
                        ((NameTag)nameTag)
                           .setComponentSupplier(
                              this.tag == null
                                 ? null
                                 : () -> {
                                    String raw = this.tag.get(meta, target);
                                    return (raw.indexOf(167) >= 0
                                       ? LegacyComponentSerializer.legacySection().deserialize(raw)
                                       : MiniMessage.miniMessage().deserialize(raw));
                                 }
                           );
                        ((NameTag)nameTag).setVisible(this.visible);
                        ((NameTag)nameTag).setBackgroundColor(this.backgroundColor);
                        ((NameTag)nameTag).setUseDefaultBackgroundColor(this.useDefaultBackgroundColor);
                        ((NameTag)nameTag).setAlignment(this.alignment);
                        ((NameTag)nameTag).setBillboard(this.billboard);
                        ((NameTag)nameTag).setScale(this.scale.get().toVector3f());
                        ((NameTag)nameTag).setSeeThrough(this.seeThrough);
                        ((NameTag)nameTag).setTextOpacity(this.textOpacity);
                        ((NameTag)nameTag).setShadow(this.shadow);
                        ((NameTag)nameTag).setLineWidth(this.lineWidth);
                     }
                  );
            }
         );
      return SkillResult.SUCCESS;
   }
}
