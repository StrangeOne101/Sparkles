package com.strangeone101.sparkle;

import com.strangeone101.sparkle.particle.StarParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientProxy {

    public static final ShinyTracker SHINY_TRACKER = new ShinyTracker();
    //public static RegistryObject<BasicParticleType> TWINKLE_PARTICLE = PARTICLES.register("twinkle", () -> new BasicParticleType(true));
    public static RegistryObject<SoundEvent> SHINY_SOUND = Sparkle.SOUNDS.register("sparkle", () -> new SoundEvent(new ResourceLocation(Sparkle.MODID, "sparkle")));
    public static RegistryObject<BasicParticleType> STAR_PARTICLE = Sparkle.PARTICLES.register("star", () -> new BasicParticleType(true));

    public DistExecutor.SafeRunnable register() {
        return () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerParticleFactories);

            //FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, this::onPixelmonSpawn);

            Sparkle.PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
            Sparkle.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());


            new Listener();
        };
    }

    public void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        Sparkle.LOGGER.info("Registering particle factories");
        Minecraft.getInstance().particles.registerFactory(STAR_PARTICLE.get(), StarParticle.Factory::new);
        //Minecraft.getInstance().particles.registerFactory(TWINKLE_PARTICLE.get(), TwinkleParticle.Factory::new);
    }

}
