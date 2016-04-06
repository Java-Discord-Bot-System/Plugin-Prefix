package com.almightyalpaca.discord.bot.plugin.prefix;

import java.util.Iterator;

import com.almightyalpaca.discord.bot.system.command.Command;
import com.almightyalpaca.discord.bot.system.command.annotation.CommandHandler;
import com.almightyalpaca.discord.bot.system.command.arguments.special.Rest;
import com.almightyalpaca.discord.bot.system.config.Config;
import com.almightyalpaca.discord.bot.system.events.commands.CommandEvent;
import com.almightyalpaca.discord.bot.system.events.commands.CommandPrefixEvent;
import com.almightyalpaca.discord.bot.system.events.manager.EventHandler;
import com.almightyalpaca.discord.bot.system.exception.PluginLoadingException;
import com.almightyalpaca.discord.bot.system.exception.PluginUnloadingException;
import com.almightyalpaca.discord.bot.system.plugins.Plugin;
import com.almightyalpaca.discord.bot.system.plugins.PluginInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.MessageBuilder.Formatting;

public class PrefixPlugin extends Plugin {
	class PrefixComand extends Command {

		public PrefixComand() {
			super("prefixes", "Set the prefix", "[add/remove] [prefix]");
		}

		@CommandHandler(dm = false, guild = true, async = true)
		private void onCommand(final CommandEvent event, String action, Rest prefix) {
			if (action.equalsIgnoreCase("list")) {
				onCommand(event);
			} else if (action.equalsIgnoreCase("add")) {
				Iterator<JsonElement> iterator = config.getJsonArray("guilds." + event.getGuild().getId(), new JsonArray()).iterator();
				while (iterator.hasNext()) {
					String string = iterator.next().getAsString();
					if (string.equalsIgnoreCase(prefix.getString())) {
						iterator.remove();
					}
				}
				config.getJsonArray("guilds." + event.getGuild().getId(), new JsonArray()).add(prefix.getString().toLowerCase());
				config.save();
			} else if (action.equalsIgnoreCase("remove")) {
				Iterator<JsonElement> iterator = config.getJsonArray("guilds." + event.getGuild().getId(), new JsonArray()).iterator();
				while (iterator.hasNext()) {
					String string = iterator.next().getAsString();
					if (string.equalsIgnoreCase(prefix.getString())) {
						iterator.remove();
					}
				}
				config.save();
			}
		}

		@CommandHandler(dm = false, guild = true, async = true)
		private void onCommand(final CommandEvent event) {
			MessageBuilder builder = new MessageBuilder();

			builder.appendString("The following prefixes are active for this server:", Formatting.BOLD).newLine();

			CommandPrefixEvent prefixEvent = new CommandPrefixEvent(PrefixPlugin.this, event.getGuild());
			prefixEvent.fire();

			for (String prefix : prefixEvent.getPrefixes()) {
				builder.appendString(prefix).newLine();
			}

			builder.send(event.getChannel());
		}
	}

	private static final PluginInfo INFO = new PluginInfo("com.almightyalpaca.discord.bot.plugin.prefix", "1.0.0", "Almighty Alpaca", "Prefix Plugin", "Custom Prefixes");

	private Config config;

	public PrefixPlugin() {
		super(PrefixPlugin.INFO);
	}

	@Override
	public void load() throws PluginLoadingException {

		this.config = this.getPluginConfig();

		this.registerEventHandler(this);
		this.registerCommand(new PrefixComand());
	}

	@EventHandler
	private void onCommandPrefixEvent(final CommandPrefixEvent event) {
		for (JsonElement element : config.getJsonArray("guilds." + event.getGuild().getId(), new JsonArray())) {
			event.addPrefix(element.getAsString());
		}
	}

	@Override
	public void unload() throws PluginUnloadingException {}

}
