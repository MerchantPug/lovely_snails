/*
 * Copyright (c) 2021-2022 LambdAurora <email@lambdaurora.dev>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.lambdaurora.lovely_snails.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.lovely_snails.LovelySnails;
import dev.lambdaurora.lovely_snails.entity.SnailEntity;
import dev.lambdaurora.lovely_snails.screen.SnailScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the snail inventory screen.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.0.0
 */
@Environment(EnvType.CLIENT)
public class SnailInventoryScreen extends HandledScreen<SnailScreenHandler> {
	private static final Identifier TEXTURE = LovelySnails.id("textures/gui/container/snail.png");
	private final SnailEntity entity;
	private float mouseX;
	private float mouseY;
	private EnderChestButton enderChestButton;
	private PageButton[] pageButtons = new PageButton[3];

	public SnailInventoryScreen(SnailScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, handler.snail().getDisplayName());
		this.backgroundWidth += 19;
		this.entity = handler.snail();
	}

	private void clearListeners() {
		if (this.enderChestButton != null) {
			this.getScreenHandler().getInventory().removeListener(this.enderChestButton);
		}
		this.enderChestButton = null;

		for (int page = 0; page < 3; page++) {
			if (this.pageButtons[page] != null) {
				this.getScreenHandler().getInventory().removeListener(this.pageButtons[page]);
				this.getScreenHandler().removePageChangeListener(this.pageButtons[page]);
			}

			this.pageButtons[page] = null;
		}
	}

	@Override
	protected void init() {
		super.init();
		this.clearListeners();

		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;
		this.addDrawableChild(this.enderChestButton = new EnderChestButton(x + 7 + 18, y + 35 + 18));
		this.getScreenHandler().getInventory().addListener(this.enderChestButton);

		int buttonX = x + this.backgroundWidth - 3;
		int buttonY = y + 17;
		for (int page = 0; page < 3; page++) {
			this.addDrawableChild(this.pageButtons[page] = new PageButton(buttonX, buttonY, page));
			this.getScreenHandler().getInventory().addListener(this.pageButtons[page]);
			this.getScreenHandler().addPageChangeListener(this.pageButtons[page]);
		}
	}

	@Override
	public void removed() {
		super.removed();
		this.clearListeners();
	}

	@Override
	public void closeScreen() {
		super.closeScreen();
		this.clearListeners();
	}

	/* Input */

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;
		if (mouseX > x + 98 && mouseY > y + 17 && mouseX <= x + 98 + 5 * 18 && mouseY <= y + 17 + 54) {
			int oldPage = this.getScreenHandler().getCurrentStoragePage();
			int newPage = MathHelper.clamp(oldPage + (amount > 0 ? -1 : 1), 0, 2);
			if (oldPage == newPage)
				return true;

			if (!this.getScreenHandler().hasChest(newPage)) {
				int otherNewPage = MathHelper.clamp(newPage + (amount > 0 ? -1 : 1), 0, 2);
				if (newPage == otherNewPage || !this.getScreenHandler().hasChest(otherNewPage))
					return true;

				newPage = otherNewPage;
			}

			this.getScreenHandler().requestStoragePage(newPage);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	/* Rendering */

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

		if (this.entity.canBeSaddled()) {
			this.drawTexture(matrices, x + 7 + 18, y + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);
		}

		this.drawTexture(matrices, x + 7 + 18, y + 35, 36, this.backgroundHeight + 54, 18, 18);

		if (!this.entity.isBaby()) {
			for (int row = y + 17; row <= y + 35 + 18; row += 18) {
				this.drawTexture(matrices, x + 7, row, 54, this.backgroundHeight + 54, 18, 18);
			}
		}

		if (this.getScreenHandler().hasChests()) {
			this.drawTexture(matrices, x + 98, y + 17, 0, this.backgroundHeight, 5 * 18, 54);
		}

		InventoryScreen.drawEntity(x + 70, y + 60, 17,
				(x + 51) - this.mouseX,
				(y + 75 - 50) - this.mouseY,
				this.entity);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	private class EnderChestButton extends TexturedButtonWidget implements InventoryChangedListener {
		public EnderChestButton(int x, int y) {
			super(x, y, 18, 18, 0, 0, 18, LovelySnails.id("textures/gui/snail_ender_chest_button.png"),
					18, 36,
					btn -> {
						var client = MinecraftClient.getInstance();
						var screenHandler = SnailInventoryScreen.this.getScreenHandler();
						client.interactionManager.clickButton(screenHandler.syncId, 0);
					});
		}

		@Override
		public void playDownSound(SoundManager soundManager) {
			var snail = SnailInventoryScreen.this.getScreenHandler().snail();
			soundManager.play(PositionedSoundInstance.master(SoundEvents.BLOCK_ENDER_CHEST_OPEN, snail.getRandom().nextFloat() * .1f + .9f, .5f));
		}

		@Override
		public void onInventoryChanged(Inventory sender) {
			this.visible = this.active = SnailInventoryScreen.this.getScreenHandler().hasEnderChest();
		}
	}

	private class PageButton extends TexturedButtonWidget implements InventoryChangedListener, SnailScreenHandler.InventoryPageChangeListener {
		private final int page;

		public PageButton(int x, int y, int page) {
			super(x, y + page * 18 + 1, 15, 16, 211 + page * 15, 0, 16, TEXTURE,
					256, 256,
					btn -> {
						SnailInventoryScreen.this.getScreenHandler().requestStoragePage(page);
					});
			this.page = page;

			this.visible = SnailInventoryScreen.this.getScreenHandler().hasChest(this.page);
			this.onCurrentPageSet(SnailInventoryScreen.this.getScreenHandler().getCurrentStoragePage());
		}

		@Override
		public void onInventoryChanged(Inventory sender) {
			this.visible = SnailInventoryScreen.this.getScreenHandler().hasChest(page);
		}

		@Override
		public void onCurrentPageSet(int page) {
			this.active = this.page != page;
			this.setFocused(this.page == page);
		}
	}
}
