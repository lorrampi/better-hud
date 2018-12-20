package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import java.util.List;

import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.geom.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Vignette extends OverrideElement {
	private static final ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation("textures/misc/vignette.png");

	private SettingBoolean warnings;
	private float brightness = 1;

	public Vignette() {
		super("vignette");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(warnings = new SettingBoolean("warnings").setValuePrefix(SettingBoolean.VISIBLE));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		warnings.set(true);
	}

	@Override
	protected ElementType getType() {
		return ElementType.VIGNETTE;
	}

	@Override
	public boolean shouldRender(Event event) {
		return Minecraft.isFancyGraphicsEnabled() && super.shouldRender(event);
	}

	@Override
	protected Rect render(Event event) {
		WorldBorder border = MC.world.getWorldBorder();

		float distance = (float)border.getClosestDistance(MC.player);
		float warningDistance = (float)getWarningDistance(border);

		float f;
		if(distance < warningDistance) {
			f = 1 - distance / warningDistance;
		} else {
			f = 0;
		}

		// Animate brightness
		brightness = brightness + (MathHelper.clamp(1 - MC.player.getBrightness(), 0, 1) - brightness) / 100;

		GlUtil.blendFuncSafe(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);

		if(f > 0) {
			GlStateManager.color(0, f, f, 1);
		} else {
			GlStateManager.color(brightness, brightness, brightness, 1);
		}

		MC.getTextureManager().bindTexture(VIGNETTE_TEX_PATH);
		GlUtil.drawRect(MANAGER.getScreen(), new Rect(256, 256));

		GlUtil.blendFuncSafe(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		MC.getTextureManager().bindTexture(Gui.ICONS);
		return null;
	}

	private double getWarningDistance(WorldBorder border) {
		// Players closer to the border than this distance receive a warning
		double warningDistance = border.getWarningDistance();

		double warningShrink = border.getResizeSpeed() * border.getWarningTime() * 1000;
		if(warningShrink > warningDistance) {
			warningDistance = warningShrink;
		}

		double diameterDelta = Math.abs(border.getTargetSize() - border.getDiameter());
		if(diameterDelta < warningDistance) {
			warningDistance = diameterDelta;
		}

		return warningDistance;
	}
}