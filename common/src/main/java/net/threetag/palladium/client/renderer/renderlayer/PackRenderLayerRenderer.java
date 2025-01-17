package net.threetag.palladium.client.renderer.renderlayer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public class PackRenderLayerRenderer extends RenderLayer<LivingEntity, EntityModel<LivingEntity>> {

    public PackRenderLayerRenderer(RenderLayerParent<LivingEntity, EntityModel<LivingEntity>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        PackRenderLayerManager.forEachLayer(livingEntity, (context, layer) -> {
            layer.render(context, matrixStack, buffer, this.getParentModel(), packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        });
    }
}
