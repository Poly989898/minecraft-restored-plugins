package team.unnamed.creative.serialize.minecraft.sound;

import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.serialize.minecraft.ResourceCategoryImpl;
import team.unnamed.creative.serialize.minecraft.io.BinaryResourceDeserializer;
import team.unnamed.creative.sound.Sound;

@Internal
public class SoundSerializer {
   public static final ResourceCategoryImpl<Sound> CATEGORY = new ResourceCategoryImpl<Sound>("sounds", ".ogg", ResourceContainer::sounds, (BinaryResourceDeserializer)(data, key) -> Sound.sound(key, data), (sound, output, targetPackFormat) -> sound.data().write(output));
}
