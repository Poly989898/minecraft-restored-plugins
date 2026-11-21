package io.lumine.mythic.core.skills.placeholders.segments.types;

import io.lumine.mythic.api.config.MythicLineConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record GenericPlaceholderSegment(@NotNull String key, @Nullable MythicLineConfig config) {
}
