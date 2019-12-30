package me.simplicitee.project.addons.ability.earth;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;

import me.simplicitee.project.addons.ProjectAddons;

public class MagmaSlap extends LavaAbility implements AddonAbility {
	
	private double offset, damage;
	private int length, maxLength, width;
	private long next, last, revertTime;
	private Location start, curr;
	private List<TempBlock> tempBlocks;

	public MagmaSlap(Player player) {
		super(player);
		if (!bPlayer.canBend(this)) {
			return;
		}
		
		if (hasAbility(player, MagmaSlap.class)) {
			return;
		}
		
		setFields();
		start();
	}
	
	private void setFields() {
		this.offset = ProjectAddons.instance.getConfig().getDouble("Abilities.MagmaSlap.Offset");
		this.damage = ProjectAddons.instance.getConfig().getDouble("Abilities.MagmaSlap.Damage");
		this.length = 0;
		this.maxLength = ProjectAddons.instance.getConfig().getInt("Abilities.MagmaSlap.Length");
		this.width = ProjectAddons.instance.getConfig().getInt("Abilities.MagmaSlap.Width");
		this.next = 50;
		this.last = 0;
		this.revertTime = ProjectAddons.instance.getConfig().getLong("Abilities.MagmaSlap.RevertTime");
		this.start = player.getLocation().clone().subtract(0, 1, 0);
		this.start.setPitch(0);
		this.start.add(start.getDirection().clone().multiply(offset));
		this.curr = start.clone();
		this.tempBlocks = new ArrayList<>();
	}

	@Override
	public long getCooldown() {
		return ProjectAddons.instance.getConfig().getLong("Abilities.MagmaSlap.Cooldown");
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "MagmaSlap";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public void progress() {
		if (!player.isOnline() || player.isDead()) {
			remove();
			return;
		}
		
		if (length > maxLength) {
			bPlayer.addCooldown(this);
			remove();
			return;
		}
		
		if (System.currentTimeMillis() < last + next) {
			return;
		}
		
		last = System.currentTimeMillis();
		length++;
		
		if (length % 4 == 0) {
			width++;
		}
		
		for (double i = -width; i <= width; i+=0.5) {
			Location check = curr.clone();
			if (i != 0) {
				Vector dir = GeneralMethods.getOrthogonalVector(check.getDirection(), 90, i);
				check.add(dir);
			}
			checkBlock(check.getBlock());
		}
		
		curr.add(start.getDirection().normalize());
	}
	
	private void checkBlock(Block b) {
		if (TempBlock.isTempBlock(b)) {
			return;
		}
		
		b = GeneralMethods.getTopBlock(b.getLocation(), 2);
		if (b.isPassable() && !b.isLiquid()) {
			b.breakNaturally();
			b = b.getRelative(BlockFace.DOWN);
		}
		
		if (!isEarthbendable(b.getType(), true, true, true)) {
			return;
		}
		
		tempBlocks.add(new TempBlock(b, Material.AIR));
		
		FallingBlock fb = GeneralMethods.spawnFallingBlock(b.getLocation().add(0.5, 0.7, 0.5), Material.MAGMA_BLOCK);
		fb.setVelocity(new Vector(0, Math.random()*0.3, 0));
		fb.setDropItem(false);
		fb.setMetadata("lavaflux", new FixedMetadataValue(ProjectKorra.plugin, this));
		
		for (Entity entity : GeneralMethods.getEntitiesAroundPoint(fb.getLocation(), 2)) {
			if (entity instanceof LivingEntity && entity.getEntityId() != player.getEntityId()) {
				DamageHandler.damageEntity(entity, damage, this);
				entity.setVelocity(fb.getVelocity().multiply(2.5));
			}
		}
		
		player.getWorld().playSound(fb.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 0.4f, 1f);
	}
	
	public void turnToTempBlock(Block b) {
		if (TempBlock.isTempBlock(b)) {
			TempBlock tb = TempBlock.get(b);
			if (tempBlocks.contains(tb)) {
				tb.setType(Material.MAGMA_BLOCK);
				tb.setRevertTime(revertTime);
			}
		}
	}
	
	public static boolean isBlock(FallingBlock fb) {
		return fb.hasMetadata("lavaflux");
	}

	@Override
	public String getAuthor() {
		return "Simplicitee";
	}

	@Override
	public String getVersion() {
		return ProjectAddons.instance.version();
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}
	
	@Override
	public String getDescription() {
		return "A simple ability in a lavabender's arsenal, this allows them to create a small wave style attack of lava that throws enemies up into the air, leaving magma in it's trail.";
	}
	
	@Override
	public String getInstructions() {
		return "Click on your magmaslap bind";
	}
	
	@Override
	public boolean isEnabled() {
		return ProjectAddons.instance.getConfig().getBoolean("Abilities.MagmaSlap.Enabled");
	}
}
