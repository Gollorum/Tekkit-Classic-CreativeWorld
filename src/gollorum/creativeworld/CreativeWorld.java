package gollorum.creativeworld;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CreativeWorld extends JavaPlugin {
	
	private static final String WORLD_NAME = "CreativeWorld";
	
	private World normalWorld;
	private World creativeWorld;
	private Random random = new Random();

	@Override
	public void onEnable() {
		InventoryHandler.init();
		normalWorld = Bukkit.getWorlds().get(0);
		creativeWorld = Bukkit.getWorld(WORLD_NAME);
		if(creativeWorld == null) {
			creativeWorld = Bukkit.createWorld(new WorldCreator(WORLD_NAME));
		}
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if(args.length > 0) {
			if(!sender.hasPermission("CreativeWorld.others")) {
				sender.sendMessage("You don't have the right to specify a player");
				return true;
			}
			player = Bukkit.getPlayer(args[0]);
		}
		if(player == null)
			if(sender instanceof Player) player = (Player) sender;
			else {
				sender.sendMessage("No valid player name was passed");
				return false;
			}
		if(cmd.getName().equals("creative")) {
			tpToCreative(player, sender);
		} else if(cmd.getName().equals("survival")) {
			tpToSurvival(player, sender);
		}
		return true;
	}
	
	private void tpToCreative(Player player, CommandSender sender) {
		if(player.getWorld().equals(creativeWorld)) {
			sender.sendMessage(randomColor()+"N"+randomColor()+"o"+randomColor()+"p"+randomColor()+"e");
			return;
		}
		Location target = InventoryHandler.swapInventory(player, normalWorld, creativeWorld);
		player.teleport(teleportTarget(target, creativeWorld));
		player.setGameMode(GameMode.CREATIVE);
	}
	
	private void tpToSurvival(Player player, CommandSender sender) {
		if(!player.getWorld().equals(creativeWorld)) {
			sender.sendMessage(randomColor()+"N"+randomColor()+"ö");
			return;
		}
		Location target = InventoryHandler.swapInventory(player, creativeWorld, normalWorld);
		player.teleport(teleportTarget(target, normalWorld));
		player.setGameMode(GameMode.SURVIVAL);
	}
	
	private String randomColor() {
		int c = random.nextInt(15)+1;
		return "§"+Integer.toHexString(c);
	}
	
	private Location teleportTarget(Location l, World world) {
		if(l != null) {
			if(l.getWorld() == null) {
				l.setWorld(world);
			}
			return l;
		} else return world.getSpawnLocation();
	}
}
