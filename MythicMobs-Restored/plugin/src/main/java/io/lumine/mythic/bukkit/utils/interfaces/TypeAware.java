package io.lumine.mythic.bukkit.utils.interfaces;

import com.google.common.reflect.TypeToken;
import javax.annotation.Nonnull;

public interface TypeAware<T> {
   @Nonnull
   TypeToken<T> getType();
}
