package ru.olamedia.olacraft.world.blockTypes;

import com.jogamp.opengl.util.texture.Texture;

import ru.olamedia.texture.TextureManager;

public abstract class AbstractBlockType implements BlockType {

	@Override
	abstract public String getName();

	@Override
	public int getMaxStack() {
		return 64;
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
		return TextureManager.get(this.getTopTextureFile());
	}

	@Override
	public Texture getBottomTexture() {
		return TextureManager.get(this.getBottomTextureFile());
	}

	@Override
	public Texture getLeftTexture() {
		return TextureManager.get(this.getLeftTextureFile());
	}

	@Override
	public Texture getRightTexture() {
		return TextureManager.get(this.getRightTextureFile());
	}

	@Override
	public Texture getFrontTexture() {
		return TextureManager.get(this.getFrontTextureFile());
	}

	@Override
	public Texture getBackTexture() {
		return TextureManager.get(this.getBackTextureFile());
	}
	
	public void register(){
		getBackTexture();
		getBottomTexture();
		getFrontTexture();
		getLeftTexture();
		getRightTexture();
		getTopTexture();
	}
}
