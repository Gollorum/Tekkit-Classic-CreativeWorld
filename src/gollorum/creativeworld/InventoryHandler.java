package gollorum.creativeworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class InventoryHandler {

	private static final String BASE_DIRECTORY = "plugins//CreativeWorld//";
	private static final String SAVE_DIRECTORY = BASE_DIRECTORY + "saves//";
	
	public static void init() {
		File dir = new File(SAVE_DIRECTORY);
		if(!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	public static Location swapInventory(Player player, World from, World to) {
		try {
			saveInventory(player, from);
			return loadInventory(player, to);
		} catch (IOException e) {
			player.sendMessage("§4Inventory swap error");
			e.printStackTrace();
			return null;
		}
	}
	
	private static void saveInventory(Player player, World world) throws IOException {
		File file = getFileFor(player, world);
		if(!file.exists()) file.createNewFile();
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		yaml.set(world.getName()+":contents", player.getInventory().getContents());
		yaml.set(world.getName()+":armorContents", player.getInventory().getArmorContents());

		yaml.set(world.getName()+":location", player.getLocation().toVector());
		yaml.set(world.getName()+":origin", player.getWorld().getName());
		yaml.save(file);
	}
	
	private static Location loadInventory(Player player, World world) {
		File file = getFileFor(player, world);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
		if(file.exists()) {
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
			player.getInventory().setContents(load(yaml, world.getName()+":contents"));
			player.getInventory().setArmorContents(load(yaml, world.getName()+":armorContents"));
			Object location = yaml.get(world.getName()+":location");
			Object origin = yaml.get(world.getName()+":origin");
			if(location instanceof Vector){
				Vector position = (Vector)location;
				if(origin instanceof String) {
					return new Location(Bukkit.getWorld((String)origin), 0, 0, 0).add(position);
				} else return new Location(null, 0, 0, 0).add(position);
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static ItemStack[] load(YamlConfiguration yaml, String key) {
		Object loaded = yaml.get(key);
		Class<? extends ArrayList> arrayListClass = new ArrayList<ItemStack>().getClass();
		if(arrayListClass.isInstance(loaded)) {
			ArrayList<ItemStack> list = (ArrayList<ItemStack>) loaded;
			return list.toArray(new ItemStack[list.size()]);
		} else return new ItemStack[0];
	}
	
	private static File getFileFor(Player player, World world) {
		return new File(SAVE_DIRECTORY + player.getName()+".yml");
	}
	
}
