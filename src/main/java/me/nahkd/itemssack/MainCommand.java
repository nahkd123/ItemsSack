package me.nahkd.itemssack;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainCommand implements CommandExecutor, TabCompleter {
	
	private void addSuggestions(List<String> suggestions, String kwd, String... kwds) {
		for (String k : kwds) if (k.startsWith(kwd)) suggestions.add(k);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> suggestions = new ArrayList<>();
		String kwd = args[args.length - 1];
		
		if (args.length == 1) {
			 addSuggestions(suggestions, kwd, "attach", "info", "setmax", "setmaterial", "delmaterial");
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("setmax")) addSuggestions(suggestions, kwd, "64", "128", "256", "512", "1024", "2048", "4096", "8192", "16384", "32765");
			if (args[0].equalsIgnoreCase("setmaterial") || args[0].equalsIgnoreCase("delmaterial")) {
				if (kwd.length() < 3) suggestions.add("<type " + (3 - kwd.length()) + " more chars to find>");
				else for (Material mat : Material.values()) if (mat.toString().startsWith(kwd.toUpperCase())) suggestions.add(mat.toString());
			}
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("setmaterial")) addSuggestions(suggestions, kwd, "64", "128", "256", "512", "1024", "2048", "4096", "8192", "16384", "32765");
		}
		return suggestions;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cThis command is for player. Consider apply itemssack.command.main permission to yourself.");
			return true;
		}
		
		Player player = (Player) sender;
		if (args.length == 0) {
			sender.sendMessage(new String[] {
					"§bItemsSack §3by nahkd123",
					"§b/" + label + " §3Show help messages",
					"§b/" + label + " attach §3Attach items sack tag to held item",
					"§b/" + label + " info §3Get info about holding items sack",
					"§b/" + label + " setmax §e<max> §3Set max materials items sack can hold",
					"§b/" + label + " setmaterial §e<material> §6[amount] §3Set material in items sack (ignore amount param to attach material)",
					"§b/" + label + " delmaterial §e<material> §3Remove material from items sack"
			});
		} else if (args[0].equalsIgnoreCase("attach")) {
			ItemStack item = player.getInventory().getItemInMainHand();
			if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) {
				sender.sendMessage("§cYou're not holding any item in your main hand.");
				return true;
			}
			
			item = ItemsSack.attach(item);
			player.getInventory().setItemInMainHand(item);
			sender.sendMessage("§eAttached item in your hand as item sack");
		} else if (args[0].equalsIgnoreCase("info")) {
			ItemStack item = player.getInventory().getItemInMainHand();
			ItemsSackData data = ItemsSack.getData(item);
			if (data == null) {sender.sendMessage("§cNot an items sack"); return true;}
			
			player.sendMessage(" §7--- ");
			player.sendMessage("§7Max materials: §e" + data.maxMaterials);
			if (data.materialMap.size() == 0) {
				player.sendMessage("§7Materials: §o(not configurated)");
			} else {
				player.sendMessage("§7Materials:");
				data.materialMap.forEach((mat, count) -> {
					if (count >= 0) player.sendMessage(" §7- " + Utils.toFriendlyName(mat) + ": §e" + count);
				});
			}
			player.sendMessage(" §7--- ");
		} else if (args[0].equalsIgnoreCase("setmax")) {
			if (args.length == 1) {player.sendMessage("§cMissing §emax §cparameter"); return true;}
			ItemStack item = player.getInventory().getItemInMainHand();
			ItemsSackData data = ItemsSack.getData(item);
			if (data == null) {sender.sendMessage("§cNot an item sack"); return true;}
			
			data.maxMaterials = Integer.parseInt(args[1]);
			item = ItemsSack.apply(item, data);
			player.getInventory().setItemInMainHand(item);
			sender.sendMessage("§eApplied max materials to holding items sack");
		} else if (args[0].equalsIgnoreCase("setmaterial") || args[0].equalsIgnoreCase("delmaterial")) {
			if (args.length == 1) {player.sendMessage("§cMissing §ematerial §cparameter"); return true;}
			ItemStack item = player.getInventory().getItemInMainHand();
			ItemsSackData data = ItemsSack.getData(item);
			if (data == null) {sender.sendMessage("§cNot an item sack"); return true;}
			Material mat = Material.valueOf(args[1].toUpperCase());
			
			if (args[0].equalsIgnoreCase("setmaterial")) {
				int amount = args.length == 2? 0 : Integer.parseInt(args[2]);
				data.materialMap.put(mat, amount);
			} else if (args[0].equalsIgnoreCase("delmaterial")) {
				data.materialMap.remove(mat);
			}
			
			item = ItemsSack.apply(item, data);
			player.getInventory().setItemInMainHand(item);
			sender.sendMessage("§eApplied changes to holding items sack");
		}
		return true;
	}

}
