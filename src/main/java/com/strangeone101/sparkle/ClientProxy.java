package com.strangeone101.sparkle;

import com.strangeone101.sparkle.particle.StarParticle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientProxy {

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
        //LOGGER.info("Registering particle factories");
        Minecraft.getInstance().particles.registerFactory(Sparkle.STAR_PARTICLE.get(), StarParticle.Factory::new);
        //Minecraft.getInstance().particles.registerFactory(TWINKLE_PARTICLE.get(), TwinkleParticle.Factory::new);
    }

}