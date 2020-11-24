package net.threetag.threecore.accessoires;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.threetag.threecore.client.renderer.entity.modellayer.CapeModelLayer;
import net.threetag.threecore.client.renderer.entity.modellayer.IModelLayerContext;
import net.threetag.threecore.client.renderer.entity.modellayer.texture.ModelLayerTexture;
import net.threetag.threecore.client.renderer.entity.modellayer.texture.transformer.ITextureTransformer;
import net.threetag.threecore.util.SupporterHandler;

import java.util.Collection;
import java.util.Collections;

public class SupporterCloakAccessoire extends Accessoire {

    @Override
    public boolean isAvailable(PlayerEntity entity) {
        return SupporterHandler.getPlayerData(entity.getUniqueID()).hasCloak();
    }

    @Override
    public Collection<AccessoireSlot> getPossibleSlots() {
        return Collections.singletonList(AccessoireSlot.BACK);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Client {

        public static final CapeModelLayer MODEL_LAYER = new CapeModelLayer(new ModelLayerTexture() {
            @Override
            public ResourceLocation getTexture(IModelLayerContext context) {
                return SupporterHandler.getPlayerData(context.getAsEntity().getUniqueID()).getCloakTexture();
            }

            @Override
            public ModelLayerTexture transform(ITextureTransformer textureTransformer) {
                return null;
            }
        });

    }
}