package com.strangeone101.sparkle.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TwinkleParticle extends SpriteTexturedParticle {

    public IAnimatedSprite texture;
    private float angleDirection;
    private int startAge;
    private float localScale;
    private float drag = 0.95F;

    private static final int[] fadeColors = {0x00D5ED, 0xD400E8, 0xE52A00, 0x95E500, 0xE27C00, 0x5200E0};
    private static final int color = 0xFFEF66;

    protected TwinkleParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite texture) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.texture = texture;

        this.startAge = this.rand.nextInt(7);
        this.maxAge = this.startAge + 4;
        this.localScale = 0.5F + (this.rand.nextFloat() * 0.5F);
        this.particleAngle = this.rand.nextFloat();
        this.prevParticleAngle = this.particleAngle;
        this.particleRed = 0xFF / 255F;
        this.particleGreen = 0xEF / 255F;
        this.particleBlue = 0x55F / 255F;
        this.angleDirection = this.rand.nextFloat() > 0.5F ? 1F : -1F;
        this.particleScale = 0.25F * this.getViewScale() * localScale;
        this.setSize(particleScale, particleScale);

        this.selectSpriteWithAge(texture);
    }

    @Override
    public void tick() {
        int realAge = this.age - this.startAge;
        if (realAge < 0) {
            this.age++;
            return;
        } else {
            standardTick();
        }

        this.prevParticleAngle = this.particleAngle;
        this.particleAngle += (this.angleDirection) * 0.1;

        this.setSprite(texture.get(realAge, 4));
    }

    private void standardTick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.motionY -= 0.04D * (double)this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)drag;
            this.motionY *= (double)drag;
            this.motionZ *= (double)drag;
            if (this.onGround) {
                this.motionX *= (double)0.7F;
                this.motionZ *= (double)0.7F;
            }

        }
    }

    //Get the scale to render the particle at based on how far away the player is
    private float getViewScale() {
        return 0.1F + (float) (Math.sqrt(Minecraft.getInstance().player.getPositionVec().squareDistanceTo(this.posX, this.posY, this.posZ))) / 5.0F;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new TwinkleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
