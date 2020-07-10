package me.simplicitee.project.addons;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.util.ParticleEffect;

import me.simplicitee.project.addons.util.HexColor;

public class CustomMethods {

	private ProjectAddons plugin;
	private String[] lightning = {"e6efef", "03d2d2", "33e6ff", "03d2d2", "03d2d2", "33e6ff", "03d2d2", "33e6ff", "33e6ff"};
	
	public CustomMethods(ProjectAddons plugin) {
		this.plugin = plugin;
	}
	
	public ProjectAddons getPlugin() {
		return plugin;
	}
	
	public void playDynamicFireParticles(Player player, Location loc, int amount, double xOff, double yOff, double zOff) {
        Random r = new Random();
		if (BendingPlayer.getBendingPlayer(player).hasSubElement(Element.BLUE_FIRE)) 
        	ParticleEffect.SOUL_FIRE_FLAME.display(loc, amount, xOff, yOff, zOff, 0.02);
		else 
        	ParticleEffect.FLAME.display(loc, amount, xOff, yOff, zOff, 0.02);
		
		if (r.nextInt(100) < 20) {
			ParticleEffect.SMOKE_NORMAL.display(loc, (int) 1, xOff, yOff, zOff);
		}
	}
	
	public void playLightningParticles(Location loc, int amount, double xOff, double yOff, double zOff) {
		int i = (int) Math.floor(Math.random() * (lightning.length - 1));
		GeneralMethods.displayColoredParticle(lightning[i], loc, amount, xOff, yOff, zOff);
	}
}
