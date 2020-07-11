package net.threetag.threecore.addonpacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.threetag.threecore.addonpacks.item.ItemParser;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AddonPackManager {

    private static AddonPackManager INSTANCE;
    public static final File DIRECTORY = new File("addonpacks");
    private static final FileFilter FILE_FILTER = (file) -> {
        boolean isZip = file.isFile() && file.getName().endsWith(".zip");
        boolean hasMeta = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
        return isZip || hasMeta;
    };
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public ResourcePackList<ResourcePackInfo> addonpackFinder = new ResourcePackList<>(ResourcePackInfo::new);
    private SimpleReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);

    private AddonPackManager() {
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(this);

        // Setup resource manager
        addonpackFinder.addPackFinder(new AddonPackFinder());
        Map<ModFile, ModFileResourcePack> modResourcePacks = ModList.get().getModFiles().stream().
                filter(mf -> !Objects.equals(mf.getModLoader(), "minecraft")).
                map(mf -> new ModFileResourcePack(mf.getFile())).
                collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity()));
        addonpackFinder.reloadPacksFromFinders();
        List<IResourcePack> list = this.addonpackFinder.getAllPacks().stream().map(ResourcePackInfo::getResourcePack).collect(Collectors.toList());
        list.forEach(pack -> resourceManager.addResourcePack(pack));
        modResourcePacks.forEach((f, p) -> resourceManager.addResourcePack(p));

        // Setup default parsers
        FMLJavaModLoadingContext.get().getModEventBus().register(new ItemParser());

        // Add Pack Finder to client
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            // for the case you run runData
            if (Minecraft.getInstance() != null) {
                Minecraft.getInstance().getResourcePackList().addPackFinder(new AddonPackFinder());
            }
        });
    }

    public static void init() {
        if (INSTANCE == null)
            INSTANCE = new AddonPackManager();
    }

    public static AddonPackManager getInstance() {
        return INSTANCE;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    @SubscribeEvent
    public void serverStarting(FMLServerAboutToStartEvent e) {
        e.getServer().resourcePacks.addPackFinder(new AddonPackFinder());
    }

    private static class AddonPackFinder implements IPackFinder {

        private Supplier<IResourcePack> createResourcePack(File file) {
            return file.isDirectory() ? () -> new FolderPack(file) : () -> new FilePack(file);
        }

        @Override public <T extends ResourcePackInfo> void func_230230_a_(Consumer<T> p_230230_1_, ResourcePackInfo.IFactory<T> p_230230_2_)
        {
            if (!DIRECTORY.exists())
                DIRECTORY.mkdirs();

            File[] files = DIRECTORY.listFiles(FILE_FILTER);

            if (files != null) {
                for (File file : files) {
                    String name = "addonpack:" + file.getName();
                    //TODO name decorator
                    T container = ResourcePackInfo.createResourcePack(name, true, this.createResourcePack(file), p_230230_2_, ResourcePackInfo.Priority.TOP, IPackNameDecorator.field_232625_a_);
                    if (container != null) {
                        p_230230_1_.accept(container);
                    }
                }
            }
        }
    }

    private static class LambdaFriendlyPackFinder implements IPackFinder {
        private ResourcePackLoader.IPackInfoFinder wrapped;

        private LambdaFriendlyPackFinder(final ResourcePackLoader.IPackInfoFinder wrapped) {
            this.wrapped = wrapped;
        }

        @Override public <T extends ResourcePackInfo> void func_230230_a_(Consumer<T> p_230230_1_, ResourcePackInfo.IFactory<T> p_230230_2_)
        {
            wrapped.addPackInfos(p_230230_1_, p_230230_2_);
        }
    }

}
