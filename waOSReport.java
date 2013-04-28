package com.github.lyokofirelyte.waOSReport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public class WAOSReport extends JavaPlugin implements CommandExecutor
{
  
	private String url;
	private String username;
	private String password;
	
	private Connection conn;
	private PreparedStatement pst;
	
	public WAOSReport()
	{
		
	}
	
	public void onDisable()
	{
		try
		{
			//Close the connection if it exists
			if(conn != null)
			{
				conn.close();
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void onEnable()
	{
		//Register the command.
		getCommand("report").setExecutor(this);
		//Provide the config.yml
		saveDefaultConfig();
		//Obtain config values
		url = getConfig().getString("url");
		username = getConfig().getString("username");
		password = getConfig().getString("password");
		//Check if values are null (Default setting)
		if(url == null || username == null || password == null)
		{
			getLogger().log(Level.SEVERE, "You must provide a url, username, and password in the config.yml.");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		else
		{
			//Create the connection
			try
			{
				conn = DriverManager.getConnection(url, username, password);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		//Check for the correct command
		if(cmd.getName().equalsIgnoreCase("report"))
		{
			
			//Convert all args after args[0] to a single string.
			StringBuilder sb = new StringBuilder();
			for(int i = 1; i < args.length; i++)
			{
				if(i != args.length && i != 1)
				{
					sb.append(" ");
				}
				sb.append(args[i]);
			}
			String message = sb.toString();
			
			//Sends the information to the SQL table.
			try
			{
				
				pst = conn.prepareStatement("INSERT INTO Reports(SenderName, ReportedName, Message) VALUES(?, ?, ?)");
				
				pst.setString(1, sender.getName());
				pst.setString(2, args[0]);
				pst.setString(3, message);
				
				pst.executeUpdate();
				pst.close();
				
				sender.sendMessage("[ waOS ] Your report has been received! " + args[0] + " Report: " + message);
				return true;
			}
			catch(SQLException e)
			{
				e.printStackTrace();
				sender.sendMessage("[ waOS ] Something went wrong! Contact a System Administrator!");
			}
			
		}
		return false;
	}
}
