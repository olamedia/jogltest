package ru.olamedia.olacraft.world.blockTypes;

import ru.olamedia.asset.SpriteOffset;
import ru.olamedia.asset.SpriteRectangle;
import ru.olamedia.olacraft.world.blockRenderer.AbstractBlockRenderer;
import ru.olamedia.olacraft.world.blockRenderer.IBlockRenderer;
import ru.olamedia.olacraft.world.blockRenderer.RenderLocation;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

import com.jogamp.opengl.util.texture.Texture;

public interface BlockType {
	public String getName();

	public int getMaxStack();

	public int getId(WorldProvider provider);

	public String getStackTextureFile();

	public String getTopTextureFile();

	public SpriteRectangle getTopTextureOffset();

	public String getBottomTextureFile();

	public SpriteRectangle getBottomTextureOffset();

	public String getLeftTextureFile();

	public SpriteRectangle getLeftTextureOffset();

	public String getRightTextureFile();

	public SpriteRectangle getRightTextureOffset();

	public String getFrontTextureFile();

	public SpriteRectangle getFrontTextureOffset();

	public String getBackTextureFile();

	public SpriteRectangle getBackTextureOffset();

	public Texture getTopTexture();

	public Texture getBottomTexture();

	public Texture getLeftTexture();

	public Texture getRightTexture();

	public Texture getFrontTexture();

	public Texture getBackTexture();

	/**
	 * Is block solid and completely non-transparent
	 */
	public boolean isOpaque();

	/**
	 * Is block solid and we can remove sides if touched by another solid block
	 */
	public boolean hideTouchedSides();

	/**
	 * Is block loose
	 */
	public boolean isLoose();

	/**
	 * Can live entities move through this block
	 */
	public boolean canMoveThrough();

	public AbstractBlockRenderer getRenderer();

	public void render(RenderLocation location);

	public boolean isTimeManaged();
}
