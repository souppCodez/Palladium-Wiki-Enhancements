package net.threetag.palladium.condition;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.documentation.HTMLBuilder;
import net.threetag.palladium.documentation.IDefaultDocumentedConfigurable;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;
import net.threetag.palladiumcore.registry.PalladiumRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class ConditionSerializer implements IDefaultDocumentedConfigurable {

    public static final PalladiumRegistry<ConditionSerializer> REGISTRY = PalladiumRegistry.create(ConditionSerializer.class, Palladium.id("condition_serializer"));

    final PropertyManager propertyManager = new PropertyManager();
    public static ConditionContextType CURRENT_CONTEXT = ConditionContextType.ALL;

    public <T> ConditionSerializer withProperty(PalladiumProperty<T> data, T value) {
        this.propertyManager.register(data, value);
        return this;
    }

    public <T> T getProperty(JsonObject json, PalladiumProperty<T> data) {
        if (this.propertyManager.isRegistered(data)) {
            if (json.has(data.getKey())) {
                JsonElement jsonElement = json.get(data.getKey());
                if (jsonElement.isJsonPrimitive() && jsonElement.getAsString().equals("null")) {
                    return null;
                } else {
                    return data.fromJSON(json.get(data.getKey()));
                }
            } else {
                return this.propertyManager.get(data);
            }
        } else {
            throw new RuntimeException("Condition Serializer does not have " + data.getKey() + " data!");
        }
    }

    public abstract Condition make(JsonObject json);

    public Condition make(JsonObject json, ConditionContextType type) {
        return this.make(json);
    }

    public ConditionContextType getContextType() {
        return ConditionContextType.ALL;
    }

    public static List<Condition> listFromJSON(JsonElement jsonElement, ConditionContextType type) {
        List<Condition> conditions = new ArrayList<>();

        if(jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            for (JsonElement element : array) {
                conditions.add(fromJSON(element.getAsJsonObject(), type));
            }
        } else if(jsonElement.isJsonObject()) {
            conditions.add(fromJSON(jsonElement.getAsJsonObject(), type));
        } else {
            throw new JsonSyntaxException("Conditions list must either be an array of multiple conditions, or one condition json object");
        }

        return conditions;
    }

    public static Condition fromJSON(JsonObject json, ConditionContextType type) {
        ConditionSerializer conditionSerializer = ConditionSerializer.REGISTRY.get(new ResourceLocation(GsonHelper.getAsString(json, "type")));

        if (conditionSerializer == null) {
            throw new JsonParseException("Condition Serializer '" + GsonHelper.getAsString(json, "type") + "' does not exist!");
        }

        if ((type == ConditionContextType.ABILITIES && !conditionSerializer.getContextType().forAbilities()) || (type == ConditionContextType.RENDER_LAYERS && !conditionSerializer.getContextType().forRenderLayers())) {
            throw new JsonParseException("Condition Serializer '" + GsonHelper.getAsString(json, "type") + "' is not applicable for " + type.toString().toLowerCase(Locale.ROOT));
        }

        return conditionSerializer.make(json, type).setContextType(type);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("type", this.getId().toString());
        this.propertyManager.values().forEach((palladiumProperty, o) -> json.add(palladiumProperty.getKey(), ((PalladiumProperty) palladiumProperty).toJSON(o)));

        return json;
    }

    public static HTMLBuilder documentationBuilder() {
        return new HTMLBuilder(new ResourceLocation(Palladium.MOD_ID, "conditions"), "Conditions")
                .add(HTMLBuilder.heading("Conditions"))
                .addDocumentationSettings(REGISTRY.getValues().stream().sorted(Comparator.comparing(o -> o.getId().toString())).collect(Collectors.toList()));
    }

    @Override
    public PropertyManager getPropertyManager() {
        return this.propertyManager;
    }

    @Override
    public ResourceLocation getId() {
        return REGISTRY.getKey(this);
    }

    @Override
    public void generateDocumentation(JsonDocumentationBuilder builder) {
        IDefaultDocumentedConfigurable.super.generateDocumentation(builder);
        builder.setTitle(this.getId().getPath());
        builder.setDescription("Applicable for: " + this.getContextType().toString().toLowerCase(Locale.ROOT));
    }
}
