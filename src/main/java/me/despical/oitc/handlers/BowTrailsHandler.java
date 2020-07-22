package me.despical.oitc.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.despical.oitc.Main;
import me.despical.oitc.arena.ArenaRegistry;
import me.despical.oitc.utils.Debugger;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class BowTrailsHandler implements Listener {

	private Main plugin;
	private Map<String, Particle> registeredTrails = new HashMap<>();

	public BowTrailsHandler(Main plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		registerBowTrail("oitc.trails.heart", Particle.HEART);
		registerBowTrail("oitc.trails.flame", Particle.FLAME);
		registerBowTrail("oitc.trails.critical", Particle.CRIT);
		registerBowTrail("oitc.trails.cloud", Particle.CLOUD);
	}

	public void registerBowTrail(String permission, Particle particle) {
		registeredTrails.put(permission, particle);
	}

	@EventHandler
	public void onArrowShoot(EntityShootBowEvent e) {
		if (!(e.getEntity() instanceof Player && e.getProjectile() instanceof Arrow)) {
			return;
		}
		if (!ArenaRegistry.isInArena((Player) e.getEntity()) || e.getProjectile() == null || e.getProjectile().isDead() || e.getProjectile().isOnGround()) {
			return;
		}
		for (String perm : registeredTrails.keySet()) {
			if (e.getEntity().hasPermission(perm)) {
				new BukkitRunnable() {
					
					@Override
					public void run() {
						if (e.getProjectile() == null || e.getProjectile().isDead() || e.getProjectile().isOnGround()) {
							this.cancel();
						}
						Debugger.debug(Level.INFO, "Spawned particle with perm {0} for player {1}", perm, e.getEntity().getName());
						e.getProjectile().getWorld().spawnParticle(registeredTrails.get(perm), e.getProjectile().getLocation(), 3, 0, 0, 0, 0);
					}
				}.runTaskTimer(plugin, 0, 0);
				break;
			}
		}
	}
}