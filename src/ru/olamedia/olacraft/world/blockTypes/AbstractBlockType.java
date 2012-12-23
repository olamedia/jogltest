package ru.olamedia.olacraft.world.blockTypes;

import com.jogamp.opengl.util.texture.Texture;

import ru.olamedia.asset.SpriteRectangle;
import ru.olamedia.olacraft.world.blockRenderer.AbstractBlockRenderer;
import ru.olamedia.olacraft.world.blockRenderer.BoxRenderer;
import ru.olamedia.olacraft.world.blockRenderer.RenderLocation;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;
import ru.olamedia.texture.TextureManager;

public abstract class AbstractBlockType implements BlockType {

	protected AbstractBlockRenderer renderer = new BoxRenderer();

	public void dropBlock(WorldProvider provider, BlockLocation location){
		provider.dropBlock(location, this);
	}
	
	public AbstractBlockRenderer getRenderer() {
		return this.renderer;
	}

	@Override
	abstract public String getName();

	@Override
	public int getMaxStack() {
		return 64;
	}

	public boolean isOpaque() { // solid, non-transparent block
		return true;
	}

	public boolean isLoose() {
		return false;
	}

	@Override
	public boolean hideTouchedSides() {
		return isOpaque();
	}

	public boolean canMoveThrough() {
		return false;
	}

	@Override
	abstract public String getStackTextureFile();

	@Override
	abstract public String getTopTextureFile();

	@Override
	public String getBottomTextureFile() {
		return this.getTopTextureFile();
	}

	@Override
	public String getLeftTextureFile() {
		return this.getFrontTextureFile();
	}

	@Override
	public String getRightTextureFile() {
		return this.getFrontTextureFile();
	}

	@Override
	public String getFrontTextureFile() {
		return this.getTopTextureFile();
	}

	@Override
	public String getBackTextureFile() {
		return this.getFrontTextureFile();
	}

	@Override
	public Texture getTopTexture() {
		return TextureManager.getSprite(this.getTopTextureFile());
	}

	@Override
	public SpriteRectangle getTopTextureOffset() {
		return TextureManager.getSpriteOffset(this.getTopTextureFile());
	}

	@Override
	public Texture getBottomTexture() {
		return TextureManager.getSprite(this.getBottomTextureFile());
	}

	@Override
	public SpriteRectangle getBottomTextureOffset() {
		return TextureManager.getSpriteOffset(this.getBottomTextureFile());
	}

	@Override
	public Texture getLeftTexture() {
		return TextureManager.getSprite(this.getLeftTextureFile());
	}

	@Override
	public SpriteRectangle getLeftTextureOffset() {
		return TextureManager.getSpriteOffset(this.getLeftTextureFile());
	}

	@Override
	public Texture getRightTexture() {
		return TextureManager.getSprite(this.getRightTextureFile());
	}

	@Override
	public SpriteRectangle getRightTextureOffset() {
		return TextureManager.getSpriteOffset(this.getRightTextureFile());
	}

	@Override
	public Texture getFrontTexture() {
		return TextureManager.getSprite(this.getFrontTextureFile());
	}

	@Override
	public SpriteRectangle getFrontTextureOffset() {
		return TextureManager.getSpriteOffset(this.getFrontTextureFile());
	}

	@Override
	public Texture getBackTexture() {
		return TextureManager.getSprite(this.getBackTextureFile());
	}

	@Override
	public SpriteRectangle getBackTextureOffset() {
		return TextureManager.getSpriteOffset(this.getBackTextureFile());
	}

	public void register(WorldProvider provider, boolean registerTextures) {
		if (registerTextures) {
			getBackTexture();
			getBottomTexture();
			getFrontTexture();
			getLeftTexture();
			getRightTexture();
			getTopTexture();
		} else {
			provider.getTypeRegistry().registerBlockType(this);
		}
	}

	public int getId(WorldProvider provider) {
		return provider.getTypeRegistry().getBlockIdByClassName(this.getClass().getName());
	}

	@Override
	public void render(RenderLocation location) {
		this.renderer.render(this, location, true);
	}

	@Override
	public boolean isTimeManaged() {
		return false;
	}
}
