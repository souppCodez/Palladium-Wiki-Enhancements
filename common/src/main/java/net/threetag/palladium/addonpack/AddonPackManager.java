package net.threetag.palladium.addonpack;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.io.File;

public class AddonPackManager {

    private static AddonPackManager INSTANCE;
    private static PackType PACK_TYPE;

    public static AddonPackManager getInstance() {
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new AddonPackManager();
    }

    private final RepositorySource folderPackFinder;

    private AddonPackManager() {
        folderPackFinder = new FolderRepositorySource(getLocation(), PackSource.DEFAULT);
    }

    public File getLocation() {
        File folder = Platform.getGameFolder().resolve("addonpacks").toFile();
        if (!folder.exists() && !folder.mkdirs())
            throw new RuntimeException("Could not create addonpacks directory! Please create the directory yourself, or make sure the name is not taken by a file and you have permission to create directories.");
        return folder;
    }

    public RepositorySource getWrappedPackFinder() {
        return getWrappedPackFinder(this.folderPackFinder);
    }

    @ExpectPlatform
    public static RepositorySource getWrappedPackFinder(RepositorySource folderPackFinder) {
        throw new AssertionError();
    }

    public static PackType getPackType() {
        if (PACK_TYPE == null) {
            for (PackType type : PackType.values()) {
                if (type.getDirectory().equalsIgnoreCase("addon")) {
                    PACK_TYPE = type;
                    return PACK_TYPE;
                }
            }
        }
        return PACK_TYPE;
    }
}