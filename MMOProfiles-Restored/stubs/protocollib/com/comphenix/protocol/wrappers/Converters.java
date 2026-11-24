package com.comphenix.protocol.wrappers;
import com.comphenix.protocol.reflect.EquivalentConverter;
public class Converters {
    public static <T> EquivalentConverter<T> passthrough(Class<T> type) { return null; }
}
