package com.strangeone101.sparkle;

import com.pixelmonmod.pixelmon.api.util.Scheduling;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.HashSet;
import java.util.Set;

public class Listener {

    public Listener() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onPokemonSpawn);
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLeaveWorld);
        MinecraftForge.EVENT_BUS.addListener(this::onRenderWorldLastEvent);
    }

    public void onPokemonSpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof PixelmonEntity && !event.isCanceled() && event.getResult() != Event.Result.DENY && event.getWorld().isRemote) {
            //Sparkle.LOGGER.info("Pixelmon spawned on client: " + event.getWorld().isRemote);
            Scheduling.schedule(1, () -> { //Wait a tick so the entity is fully loaded, so it isn't a bulbasaur
                PixelmonEntity entity = (PixelmonEntity) event.getEntity();
                if (entity.getPokemon().isShiny() && !entity.isBossPokemon()) {
                    //Sparkle.LOGGER.info("Pixelmon spawned on client2: " + event.getWorld().isRemote);
                    ShinyTracker tracker = Sparkle.SHINY_TRACKER;
                    if (tracker.shouldTrackShiny(entity)) {
                        //Sparkle.LOGGER.info("Pixelmon spawned on client3: " + event.getWorld().isRemote);
                        tracker.track(entity);
                    }
                }
            }, false);
        }
    }

    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !Minecraft.getInstance().isGamePaused()
                && (Minecraft.getInstance().currentScreen == null || Minecraft.getInstance().currentScreen instanceof ChatScreen)) {
            Sparkle.SHINY_TRACKER.tick();
        }
    }

    public void onPlayerLeaveWorld(PlayerEvent.PlayerLoggedOutEvent event) {
        Sparkle.SHINY_TRACKER.untrackAll();
    }

    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {

        Sparkle.SHINY_TRACKER.camera = new ClippingHelper(event.getMatrixStack().getLast().getMatrix(), event.getProjectionMatrix());
        //Sparkle.LOGGER.info("Matrix: " + event.getMatrixStack().getLast().getMatrix());
        //Sparkle.LOGGER.info("Projection: " + event.getProjectionMatrix());
    }

    /*public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Minecraft.getInstance().gameRenderer.getActiveRenderInfo().
    }*/
}
