package net.threetag.threecore.accessoires;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.threetag.threecore.ThreeCore;
import net.threetag.threecore.client.renderer.entity.model.WoodenLegModel;

import javax.annotation.Nullable;
import java.util.Objects;

public class WoodenLegAccessoire extends Accessoire {

    public static final WoodenLegModel MODEL = new WoodenLegModel(RenderType::getEntityCutoutNoCull);
    public static final ResourceLocation TEXTURE = new ResourceLocation(ThreeCore.MODID, "textures/models/accessories/wooden_leg.png");

    @Override
    public boolean isAvailable(PlayerEntity entity) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStackIn.push();
        renderer.getEntityModel().bipedLeftLeg.translateRotate(matrixStackIn);
        matrixStackIn.translate(0, -12.2F * 0.06125F, 0);
        IVertexBuilder builder = bufferIn.getBuffer(MODEL.getRenderType(new ResourceLocation(ThreeCore.MODID, "textures/models/accessories/wooden_leg.png")));
        MODEL.render(matrixStackIn, builder, packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        builder = bufferIn.getBuffer(Objects.requireNonNull(getRenderType(player, player.getLocationSkin(), MODEL)));
        MODEL.renderPlayerLeg(matrixStackIn, builder, packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        matrixStackIn.pop();
    }

    @Nullable
    @Override
    public PlayerPart getPlayerPart() {
        return PlayerPart.LEFT_LEG;
    }
}
