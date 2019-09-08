package net.threetag.threecore.abilities.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.threetag.threecore.util.attributes.AttributeRegistry;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class AttributeThreeData extends ThreeData<IAttribute> {

    public AttributeThreeData(String key) {
        super(key);
    }

    @Override
    public IAttribute parseValue(JsonObject jsonObject, IAttribute defaultValue) {
        if (!JSONUtils.hasField(jsonObject, this.jsonKey))
            return defaultValue;
        AttributeRegistry.AttributeEntry attributeEntry = AttributeRegistry.REGISTRY.getValue(new ResourceLocation(JSONUtils.getString(jsonObject, this.jsonKey))).orElse(null);
        if (attributeEntry == null)
            throw new JsonSyntaxException("Attribute " + JSONUtils.getString(jsonObject, this.jsonKey) + " does not exist!");
        return attributeEntry.getAttribute();
    }

    @Override
    public void writeToNBT(CompoundNBT nbt, IAttribute value) {
        nbt.putString(this.key, Objects.requireNonNull(AttributeRegistry.REGISTRY.getKey(AttributeRegistry.getEntry(value))).toString());
    }

    @Override
    public IAttribute readFromNBT(CompoundNBT nbt, IAttribute defaultValue) {
        if (!nbt.contains(this.key))
            return defaultValue;
        return AttributeRegistry.REGISTRY.getOrDefault(new ResourceLocation(nbt.getString(this.key))).getAttribute();
    }

    @Override
    public String getDisplay(IAttribute value) {
        return AttributeRegistry.REGISTRY.getKey(AttributeRegistry.getEntry(value)).toString();
    }

    @Override
    public boolean displayAsString(IAttribute value) {
        return true;
    }
}