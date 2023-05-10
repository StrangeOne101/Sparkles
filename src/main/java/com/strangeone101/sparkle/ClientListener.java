package com.strangeone101.sparkle;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.sparkle.particle.FakeParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientListener {

    public ClientListener() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onPokemonSpawn);
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLeaveWorld);
        MinecraftForge.EVENT_BUS.addListener(this::onRenderWorldLastEvent);
        //MinecraftForge.EVENT_BUS.addListener(this::onTextureStitch);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onTextureStitch);
    }

    public void onPokemonSpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof PixelmonEntity && !event.isCanceled() && event.getResult() != Event.Result.DENY && event.getWorld().isRemote) {
            //Sparkle.LOGGER.info("Pixelmon spawned on client: " + event.getWorld().isRemote);
            ClientScheduler.schedule(1, () -> { //Wait a tick so the entity is fully loaded, so it isn't a bulbasaur
                PixelmonEntity entity = (PixelmonEntity) event.getEntity();
                if (entity.getPokemon().isShiny() && !entity.isBossPokemon()) {
                    //Sparkle.LOGGER.info("Pixelmon spawned on client2: " + event.getWorld().isRemote);
                    ShinyTracker tracker = ShinyTracker.INSTANCE;
                    if (tracker.shouldTrackShiny(entity)) {
                        //Sparkle.LOGGER.info("Pixelmon spawned on client3: " + event.getWorld().isRemote);
                        tracker.track(entity);
                    }
                }
            });
        }
    }

    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !Minecraft.getInstance().isGamePaused()
                && (Minecraft.getInstance().currentScreen == null || Minecraft.getInstance().currentScreen instanceof ChatScreen)) {
            ShinyTracker.INSTANCE.tick();
            ClientScheduler.tick();
        }
    }

    public void onPlayerLeaveWorld(PlayerEvent.PlayerLoggedOutEvent event) {
        ShinyTracker.INSTANCE.untrackAll();
    }

    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {

        ShinyTracker.INSTANCE.camera = new ClippingHelper(event.getMatrixStack().getLast().getMatrix(), event.getProjectionMatrix());
        //Sparkle.LOGGER.info("Matrix: " + event.getMatrixStack().getLast().getMatrix());
        //Sparkle.LOGGER.info("Projection: " + event.getProjectionMatrix());
    }

    /*public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Minecraft.getInstance().gameRenderer.getActiveRenderInfo().
    }*/

    public void clientSetup(FMLClientSetupEvent event) {

    }

    public void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_PARTICLES_TEXTURE)) {
            event.addSprite(new ResourceLocation(Sparkle.MODID, "particle/stars_0"));
            event.addSprite(new ResourceLocation(Sparkle.MODID, "particle/stars_1"));
            FakeParticle.atlasTexture = event.getMap();
        }
    }
}
