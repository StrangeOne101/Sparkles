package com.strangeone101.sparkle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Sparkle.MODID)
public class Sparkle {

    public static final String MODID = "sparkles";

    public static final int SHINY_COLOR = 0xe8aa00;

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static RegistryObject<BasicParticleType> STAR_PARTICLE = PARTICLES.register("star", () -> new BasicParticleType(true));
    //public static RegistryObject<BasicParticleType> TWINKLE_PARTICLE = PARTICLES.register("twinkle", () -> new BasicParticleType(true));
    public static RegistryObject<SoundEvent> SHINY_SOUND = SOUNDS.register("sparkle", () -> new SoundEvent(new ResourceLocation(MODID, "sparkle")));

    /*public static RenderType GOLD_GLINT = RenderType.makeType("shiny_glint", DefaultVertexFormats.POSITION_TEX, 7, 256,
            RenderType.State.getBuilder().texture(new RenderState.TextureState(new ResourceLocation(MODID, "misc/glint"), true, false))
                    .writeMask(new RenderState.WriteMaskState(true, false))
                    .cull( new RenderState.CullState(false))
                    .depthTest(new RenderState.DepthTestState("==", 514))
                    .transparency(new RenderState.TransparencyState("glint_transparency", () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    })).texturing(RenderState.GLINT_TEXTURING).target(ITEM_ENTITY_TARGET).build(false));*/
    public Sparkle() {

        //Make the server tell clients it is fine to join without it
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
            ClientProxy client = new ClientProxy();
            return client.register();
        });
    }
}
