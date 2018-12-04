package tk.nukeduck.hud.element;

import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPercentage;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;

public class GlobalSettings extends HudElement {
	private SettingPercentage billboardScale;
	private SettingSlider billboardDistance;
	private SettingBoolean hideOnDebug;

	public GlobalSettings() {
		super("global");
		ELEMENTS.remove(this);
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(billboardScale = new SettingPercentage("billboardScale"));
		settings.add(billboardDistance = new SettingSlider("rayDistance", 5, 200).setUnlocalizedValue("betterHud.hud.meters"));
		settings.add(hideOnDebug = new SettingBoolean("hideOnDebug"));
	}

	public float getBillboardScale() {
		return billboardScale.get().floatValue();
	}

	public float getBillboardDistance() {
		return billboardDistance.get().floatValue();
	}

	public boolean hideOnDebug() {
		return hideOnDebug.get();
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		billboardScale.set(0.5);
		billboardDistance.set(100.0);
		hideOnDebug.set(true);
	}

	@Override public boolean shouldRender(Event event) {return false;}
	@Override public Bounds render(Event event) {return null;}
}
