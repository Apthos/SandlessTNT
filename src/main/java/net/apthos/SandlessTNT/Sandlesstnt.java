package net.apthos.SandlessTNT;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;

public final class Sandlesstnt extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {

        File SettingsFile = new File(this.getDataFolder() + "/Settings.yml");
        if (!SettingsFile.exists()) {
            saveResource("Settings.yml", false);
        }

        loadLanguage();
    }

    private int Booster;

    private void loadLanguage() {

        File SettingsFile = new File(this.getDataFolder() + "/Settings.yml");
        YamlConfiguration Settings = new YamlConfiguration().loadConfiguration(SettingsFile);

        Booster = Settings.getInt("Boost_Multiplier");

        broadcast = Settings.getString("booster_broadcast");

        Command = Settings.getString( "sandlesstnt_command" );


    }

    private String Command;

    private String broadcast;

    private boolean Booster_Enabled = false;

    private java.util.List<String> Queue = new ArrayList<>();

    @SuppressWarnings({"all"})
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("tntsandless")) {

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only Players can use this command");
                return true;
            }

            Player player = (Player) sender;

            craftTNT(player);

        }

        //tntbooster [Player] [Amplifier]
        if (command.getName().equalsIgnoreCase("tntbooster")) {
            if (!sender.hasPermission("tntbooster.use")) return true;

            if (args.length == 1) {

                int hours = Integer.parseInt(args[0]);

                if (Boosting) {

                    // SENDER:HOURS

                    String Queue = sender.getName() + ":" + args[0];

                    this.Queue.add(Queue);

                    sender.sendMessage(ChatColor.GREEN + "Your boost has been put on queue!");

                } else {
                    playBooster(sender.getName(), args[0]);
                    Boosting = true;
                }

            } else if( args.length == 2){

                if (Boosting) {

                    // SENDER:HOURS

                    String Queue = args[1] + ":" + args[0];

                    this.Queue.add(Queue);

                    sender.sendMessage(ChatColor.GREEN + "Your boost has been put on queue!");

                } else {
                    playBooster(args[1], args[0]);
                    Boosting = true;
                }

            } else {
                sender.sendMessage(ChatColor.RED + "/tntbooster [Time] [Player]");
                return true;
            }

        }


        return true;
    }

    private boolean Boosting = false;

    @SuppressWarnings({"all"})
    public void playBooster(String sender, String hours) {

        String Send = broadcast.replaceAll("%PLAYER%", sender);
        Send = Send.replaceAll("%BOOSTER%", Integer.toString(Booster));
        Send = Send.replaceAll("%HOURS%", Integer.toString(Integer.parseInt(hours)));
        Send = ChatColor.translateAlternateColorCodes('&', Send);

        Bukkit.broadcastMessage(Send);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable() {
            @Override
            public void run() {

                Boosting = false;

                if (!Queue.isEmpty()) {
                    Queue.get(0);
                }

            }
        }, (20 * 60 * 60) * Integer.parseInt(hours));

    }

    @SuppressWarnings({"all"})
    public void craftTNT(Player p) {

        int TG = 0;

        for ( int x = 0; x < p.getInventory().getSize(); x++ ) {
            if( p.getInventory().getItem( x ) != null ) {
                if (p.getInventory().getItem(x).getType().equals(Material.SULPHUR)) {
                    TG = TG + p.getInventory().getItem(x).getAmount();
                    p.getInventory().setItem(x, null);
                }
            }
        }

        int TT = TG / 5;
        TG = TG % 5;

        if( TG > 0 ) {
            giveToPlayer(new ItemStack(Material.SULPHUR, TG), p);
        }

        if (Boosting) {
            TT = TT * Booster;
        }

        String M = Command;
        M = M.replaceAll( "%CRAFTED-TNT%", Integer.toString( TT ) );
        M = ChatColor.translateAlternateColorCodes('&', M );

        p.sendMessage( M );

        while (TT > 0) {
            if (TT >= 64) {
                giveToPlayer(new ItemStack(Material.TNT, 64), p);
                TT -= 64;
            } else if( TT > 0 ) {
                giveToPlayer(new ItemStack(Material.TNT, TT), p);
                TT = TT - TT;
            }
        }

    }


    public void giveToPlayer(ItemStack I, Player p) {
        if (p.getInventory().firstEmpty() == -1) {
            p.getLocation().getWorld().dropItem(p.getLocation().add(0.0, 1.0, 0.0), I);
        } else {
            p.getInventory().addItem(I);
        }
    }

}

