package ru.olamedia.olacraft.world.blockTypes;

import com.jogamp.opengl.util.texture.Texture;

public interface BlockType {
	public String getName();
	public int getMaxStack();
	public String getStackTextureFile();
	public String getTopTextureFile();
	public String getBottomTextureFile();
	public String getLeftTextureFile();
	public String getRightTextureFile();
	public String getFrontTextureFile();
	public String getBackTextureFile();
	public Texture getTopTexture();
	public Texture getBottomTexture();
	public Texture getLeftTexture();
	public Texture getRightTexture();
	public Texture getFrontTexture();
	public Texture getBackTexture();
}
