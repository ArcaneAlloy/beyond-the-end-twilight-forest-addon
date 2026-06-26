package com.arcanealloy.forgottentf.client.model;

import com.mojang.math.Vector3f;
import com.arcanealloy.forgottentf.util.Vec2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GiantCarminiteModel implements IDynamicBakedModel {

    public static final ModelProperty<GiantBlockData> DATA = new ModelProperty<>();
    private static final FaceBakery FACE_BAKERY = new FaceBakery();

    private final TextureAtlasSprite texture;
    private final TextureAtlasSprite particle;
    private final ItemOverrides overrides;
    private final ItemTransforms transforms;

    public GiantCarminiteModel(TextureAtlasSprite texture, TextureAtlasSprite particle,
                               ItemOverrides overrides, ItemTransforms transforms) {
        this.texture = texture;
        this.particle = particle;
        this.overrides = overrides;
        this.transforms = transforms;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
                                              @NotNull RandomSource rand, @NotNull ModelData extraData,
                                              @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>();
        if (extraData.has(DATA) && side != null) {
            BlockPos pos = extraData.get(DATA).pos();
            Vec2i coords = this.calculateOffset(side, pos.offset(this.magicOffsetFromDir(side)));
            quads.add(FACE_BAKERY.bakeQuad(
                new Vector3f(0.0F, 0.0F, 0.0F),
                new Vector3f(16.0F, 16.0F, 16.0F),
                new BlockElementFace(side, side.ordinal(), side.name(),
                    new BlockFaceUV(new float[]{
                        0.0F + coords.x(), 0.0F + coords.z(),
                        4.0F + coords.x(), 4.0F + coords.z()}, 0)),
                this.texture, side, BlockModelRotation.X0_Y0, null, false,
                new ResourceLocation(this.texture.getName().getNamespace(),
                    this.texture.getName().getPath() + "_" + side.name().toLowerCase(Locale.ROOT))));
        }
        return quads;
    }

    private BlockPos magicOffsetFromDir(Direction side) {
        return switch (side) {
            case DOWN -> new BlockPos(0, 0, 2);
            case NORTH, SOUTH -> new BlockPos(0, 1, 0);
            case WEST, EAST -> new BlockPos(0, 1, -1);
            default -> new BlockPos(0, 0, -1);
        };
    }

    private Vec2i calculateOffset(Direction side, BlockPos pos) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        int offsetX, offsetY;

        if (side.getAxis().isVertical()) {
            offsetX = x % 4;
            offsetY = (side.getStepY() * z + 1) % 4;
        } else if (side.getAxis() == Direction.Axis.Z) {
            offsetX = x % 4;
            offsetY = -y % 4;
        } else {
            offsetX = (z + 1) % 4;
            offsetY = -y % 4;
        }

        if (side == Direction.NORTH || side == Direction.EAST) {
            offsetX = (4 - offsetX - 1) % 4;
        }
        if (offsetX < 0) offsetX += 16;
        if (offsetY < 0) offsetY += 16;

        return new Vec2i((offsetX % 4) * 4, (offsetY % 4) * 4);
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos,
                                            @NotNull BlockState state, @NotNull ModelData modelData) {
        if (modelData == ModelData.EMPTY) {
            modelData = ModelData.builder().with(DATA, new GiantBlockData(level, pos)).build();
        }
        return modelData;
    }

    @Override public boolean useAmbientOcclusion() { return false; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean usesBlockLight() { return true; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public TextureAtlasSprite getParticleIcon() { return this.particle; }
    @Override public ItemOverrides getOverrides() { return this.overrides; }
    @Override public @NotNull ItemTransforms getTransforms() { return this.transforms; }

    public record GiantBlockData(BlockAndTintGetter getter, BlockPos pos) {}
}
