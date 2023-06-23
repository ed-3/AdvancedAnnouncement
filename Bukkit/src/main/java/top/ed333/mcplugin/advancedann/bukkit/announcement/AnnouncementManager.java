package top.ed333.mcplugin.advancedann.bukkit.announcement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnnouncementManager {
    public static final List<Announcement> loadedAnnouncements = new ArrayList<>();
    private static List<String> annNames = null;

    public @Nullable static Announcement forName(String name) {
        for (Announcement announcement : loadedAnnouncements) {
            if (announcement.getName().equals(name)) {
                return announcement;
            }
        }
        return null;
    }

    public @NotNull static Announcement forIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0) throw new IndexOutOfBoundsException("Index out of bounds, while expects 0-" + (loadedAnnouncements.size() - 1) + " but found a negative integer.");
        if (index >= loadedAnnouncements.size()) throw new IndexOutOfBoundsException("Index out of bounds, while expects 0-" + (loadedAnnouncements.size() - 1) + " but found a bigger integer.");
        return loadedAnnouncements.get(index);
    }

    public static @NotNull List<String> getAnnNames() {
        if (annNames == null) {
            annNames = new ArrayList<>();
            loadedAnnouncements.forEach(announcement -> annNames.add(announcement.getName()));
        }
        return annNames;
    }

    /**
     * generates next announcement
     */
    private static final Random random = new Random();
    public static @NotNull Announcement randomNext() {
        return forIndex(random.nextInt(loadedAnnouncements.size()));
    }
}
