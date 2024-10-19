import fs from 'node:fs';
import path from 'node:path';
import { Client, Collection, GatewayIntentBits, Partials } from 'discord.js';
import config from './config.json';

const token = config.token;

const client = new Client({ 
    intents: [
      GatewayIntentBits.Guilds, 
      GatewayIntentBits.GuildMembers, 
      GatewayIntentBits.GuildMessages,
      GatewayIntentBits.DirectMessages,
    ],
    partials: [Partials.Channel] 
  });


  client.commands = new Collection();
  const commandsPath = path.join(__dirname, 'commands');
  const commandFolders = fs.readdirSync(commandsPath);

  for (const folder of commandFolders) {
    const commandsPath2 = path.join(commandsPath, folder);
    const commandFiles = fs.readdirSync(commandsPath2).filter(file => file.endsWith('.js'));
    for (const file of commandFiles) {
      const filePath = path.join(commandsPath2, file);
      const command = require(filePath);
      if ('data' in command && 'execute' in command) {
        client.commands.set(command.data.name, command);
      } else {
        console.log(`[WARNING] The command at ${filePath} is missing a required "data" or "execute" property.`);
      }
    }
  }



  const eventsPath = path.join(__dirname, 'events');
  const eventFiles = fs.readdirSync(eventsPath).filter(file => file.endsWith('.js'));

  for (const file of eventFiles) {
    const filePath = path.join(eventsPath, file);
    const event = require(filePath);
    if (event.once) {
      client.once(event.name, (...args) => event.execute(...args));
    } else {
      client.on(event.name, (...args) => event.execute(...args));
    }
  }


  client.login(token);