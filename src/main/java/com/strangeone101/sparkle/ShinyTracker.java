package com.strangeone101.sparkle;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.sparkle.particle.StarParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ShinyTracker {

    public static final ShinyTracker INSTANCE = new ShinyTracker();

    private Set<Pokemon> shinyMap = new HashSet<>();
    private Set<PixelmonEntity> shinyTracking = new HashSet<>();

    public ClippingHelper camera = null;

    public boolean shouldTrackShiny(PixelmonEntity entity) {
        /*System.out.println("Uncatchable: " + entity.isUncatachable());
        System.out.println("Added to world: " + entity.isAddedToWorld());
        System.out.println("Alive: " + entity.isAlive());
        System.out.println("Owner: " + entity.getOwner());
        System.out.println("Shiny: " + entity.getPokemon().isShiny());*/
        //Check if the pokemon can be caught and isn't owned by a trainer, and is shiny
        if (entity.isUncatachable() || !entity.isAlive() || entity.isBossPokemon()
                || entity.getOwner() != null || !entity.getPokemon().isShiny()
                || shinyMap.contains(entity.getPokemon()) || shinyTracking.contains(entity)) {
            return false;
        }
        return true;
    }

    public void track(PixelmonEntity entity) {
        shinyTracking.add(entity);
    }

    public void tick() {

        //Check if the player is in a battle, and if so, don't sparkle shinies
        if (BattleRegistry.getBattle(Minecraft.getInstance().player) != null || camera == null) return;

        //Filter out all dead entities
        shinyTracking.removeIf(entity -> !entity.isLoaded() || !entity.isAlive());

        Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

        camera.setCameraPosition(vec.x, vec.y, vec.z);


        //Check all pokemon
        Iterator<PixelmonEntity> iterator = shinyTracking.iterator();
        while (iterator.hasNext()) {
            PixelmonEntity entity = iterator.next();
            //Check if the pokemon is in range & entity is being rendered

            boolean rendered = Minecraft.getInstance().getRenderManager().shouldRender(entity, camera, vec.x, vec.y, vec.z);
            boolean visible = rayTrace(entity);

            if (entity.getPositionVec().squareDistanceTo(Minecraft.getInstance().player.getPositionVec()) <= 20 * 20 && rendered && visible) {
                //Sparkle.LOGGER.info("Visisble2: " + visible);
                //Remove from tracking
                iterator.remove();
                //Sparkle
                ClientScheduler.schedule(10, () -> {
                    Vector3d vec2 = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
                    camera.setCameraPosition(vec2.x, vec2.y, vec2.z);
                    boolean rendered2 = Minecraft.getInstance().getRenderManager().shouldRender(entity, camera, vec.x, vec.y, vec.z);

                    if (rendered2 && rayTrace(entity)) {
                        //Add to shiny map
                        shinyMap.add(entity.getPokemon());
                        spawnSparkle(entity);
                    } else {
                        shinyTracking.add(entity); //Allow it to try again
                    }
                });
            }
        }
    }

    public void untrackAll() {
        shinyTracking.clear();
    }

    public boolean rayTrace(PixelmonEntity entity) {
        PlayerEntity player = Minecraft.getInstance().player;

        Vector3d vector3d = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        Vector3d vector3d1 = new Vector3d(entity.getPosX(), entity.getPosYEye(), entity.getPosZ());

        if (entity.world != player.world || vector3d1.squareDistanceTo(vector3d) > 128.0D * 128.0D) return false; //Forge Backport MC-209819
        BlockRayTraceResult result = entity.world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, entity));

        if (result.getType() == RayTraceResult.Type.MISS) {
            //Sparkle.LOGGER.info(result.getPos() + " " + result.hitInfo);
            return true;
        }
        return false;
    }

    public void spawnSparkle(PixelmonEntity entity) {
        ClientPlayerEntity thiz = Minecraft.getInstance().player;
        //thiz.playSound(Sparkle.SHINY_SOUND.get(), 1F, 1F);
        ClientScheduler.schedule(3, () -> {
            //thiz.playSound(ClientProxy.SHINY_SOUND.get(), 1F, 1F);
            SimpleSound sound = new SimpleSound(new ResourceLocation(Sparkle.MODID, "sparkle"), SoundCategory.NEUTRAL,
                    1F, 1F, false, 0, ISound.AttenuationType.LINEAR,
                    entity.getPosX(), entity.getPosY(), entity.getPosZ(), true);
            Minecraft.getInstance().getSoundHandler().play(sound);
        });

        final double d = entity.getWidth() / 2.5D + 0.2D;
        final double h = entity.getHeight() / 2.5D - 0.5D;

        int amount = 5;
        int div = 360 / amount;

        for (int i = 0; i < amount; i++) {
            double deg = i * (div) + (entity.getEntityWorld().rand.nextInt(div / 6) - div / 3F);
            double xx = Math.cos(Math.toRadians(deg));
            double zz = Math.sin(Math.toRadians(deg));

            double driftX = (entity.getEntityWorld().rand.nextDouble() * 0.2D - 0.1D) * d;
            double driftY = (entity.getEntityWorld().rand.nextDouble() * 0.2D - 0.1D) * h;
            double driftZ = (entity.getEntityWorld().rand.nextDouble() * 0.2D - 0.1D) * d;

            double x = xx * d + entity.getPosX() + driftX;
            double y = h + entity.getPosY() + driftY;
            double z = zz * d + entity.getPosZ() + driftZ;

            //if (i % 3 == 0) {
            //Minecraft.getInstance().particles.

            StarParticle particle = new StarParticle(Minecraft.getInstance().world, x, y, z, 0.01 * xx, 0.1 * entity.getHeight(), 0.01 * zz);
            Minecraft.getInstance().particles.addEffect(particle);
            //Minecraft.getInstance().particles.addParticle(ClientProxy.STAR_PARTICLE.get(), x, y, z, 0.01 * xx, 0.1 * entity.getHeight(), 0.01 * zz);
            //} else {
            //Minecraft.getInstance().particles.addParticle(Sparkle.TWINKLE_PARTICLE.get(), x, y, z, 0.01 * xx, 0, 0.01 * zz);
            // }
        }
    }

}
