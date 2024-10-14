import { Client, Collection, GatewayIntentBits} from 'discord.js';
import configJson from './config.json' with { type: 'json' };
import fs from 'fs';
import { checkCrashTask, updateStatusTask } from './tasks.js';

const client = new Client({ intents: [GatewayIntentBits.Guilds] });
client.commands = new Collection();

const token = configJson.token;

client.on('ready', () => {
  console.log(`Logged in as ${client.user.tag}!`);

  setInterval(async () => {
      await updateStatusTask(client);
  }, 5000);

  setInterval(async () => {
    await checkCrashTask(client);
}, 5 * 60 * 1000);
});



let modules = ["fun_commands", "moderation_commands", "misc_commands"];

modules.forEach(async (module) => {
  fs.readdir(`./commands/${module}`, async (err, files) => {
    if (err) {
      throw new Error("Missing Folder Of Commands! Example: Commands/<Folder>/<Command>.js");
    }

    for (const file of files) {
      if (!file.endsWith(".js")) continue;
      
      try {
        const { default: command } = await import(`./commands/${module}/${file}`);
        console.log(`${command.name} loaded`);
        if (command.name) client.commands.set(command.name, command);
      } catch (err) {
        console.error(`Failed to load command ${file}:`, err);
      }
    }
  });
});


// Command handling for slash commands (interaction)
client.on('interactionCreate', async interaction => {
  if (!interaction.isChatInputCommand()) return;

  let command = client.commands.get(interaction.commandName);
  if (!command) return;

  command.run(client, interaction);
});



// Log in to Discord using the token from the config
client.login(token);
