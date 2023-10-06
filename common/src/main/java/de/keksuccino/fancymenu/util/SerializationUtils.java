package de.keksuccino.fancymenu.util;

import de.keksuccino.fancymenu.util.file.ResourceFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public class SerializationUtils {

    @Nullable
    public static ResourceFile deserializeAssetResourceFile(@Nullable String gameDirectoryFilePath) {
        if (gameDirectoryFilePath == null) return null;
        else return ResourceFile.asset(gameDirectoryFilePath);
    }

    @Nullable
    public static ResourceFile deserializeResourceFile(@Nullable String gameDirectoryFilePath) {
        if (gameDirectoryFilePath == null) return null;
        else return ResourceFile.of(gameDirectoryFilePath);
    }

    @NotNull
    public static <T extends Number> T deserializeNumber(@NotNull Class<T> type, @NotNull T fallbackValue, @Nullable String serialized) {
        try {
            if (serialized != null) {
                serialized = serialized.replace(" ", "");
                if (type == Float.class) {
                    return (T) Float.valueOf(serialized);
                }
                if (type == Double.class) {
                    return (T) Double.valueOf(serialized);
                }
                if (type == Integer.class) {
                    return (T) Integer.valueOf(serialized);
                }
                if (type == Long.class) {
                    return (T) Long.valueOf(serialized);
                }
            }
        } catch (Exception ignore) {}
        return fallbackValue;
    }

    public static boolean deserializeBoolean(boolean fallbackValue, @Nullable String serialized) {
        if (serialized != null) {
            if (serialized.replace(" ", "").equalsIgnoreCase("true")) {
                return true;
            }
            if (serialized.replace(" ", "").equalsIgnoreCase("false")) {
                return false;
            }
        }
        return fallbackValue;
    }

}
