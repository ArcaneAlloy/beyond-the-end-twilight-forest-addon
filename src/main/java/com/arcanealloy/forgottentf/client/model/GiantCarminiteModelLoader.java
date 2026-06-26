package com.arcanealloy.forgottentf.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class GiantCarminiteModelLoader implements IGeometryLoader<GiantCarminiteModelLoader.UnbakedGiantCarminiteModel> {

    public static final GiantCarminiteModelLoader INSTANCE = new GiantCarminiteModelLoader();
    public static final ResourceLocation ID = new ResourceLocation("forgottentf", "giant_carminite");

    @Override
    public UnbakedGiantCarminiteModel read(JsonObject object, JsonDeserializationContext ctx) throws JsonParseException {
        return new UnbakedGiantCarminiteModel();
    }

    public record UnbakedGiantCarminiteModel() implements IUnbakedGeometry<UnbakedGiantCarminiteModel> {

        @Override
        public GiantCarminiteModel bake(IGeometryBakingContext context, ModelBakery bakery,
                                        Function<Material, TextureAtlasSprite> spriteGetter,
                                        ModelState modelState, ItemOverrides overrides,
                                        ResourceLocation modelLocation) {
            TextureAtlasSprite texture = spriteGetter.apply(context.getMaterial("all"));
            TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
            return new GiantCarminiteModel(texture, particle, overrides, context.getTransforms());
        }

        @Override
        public Collection<Material> getMaterials(IGeometryBakingContext context,
                                                  Function<ResourceLocation, UnbakedModel> modelGetter,
                                                  Set<Pair<String, String>> missingTextureErrors) {
            return List.of(context.getMaterial("all"), context.getMaterial("particle"));
        }
    }
}
