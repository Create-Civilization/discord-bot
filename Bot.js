import { Client, GatewayIntentBits } from 'discord.js';
import configJson from './config.json' with {type: 'json'}
const client = new Client({ intents: [GatewayIntentBits.Guilds] });

const token = configJson.token

client.on('ready', () => {
  console.log(`Logged in as ${client.user.tag}!`);
});

client.on('interactionCreate', async interaction => {
  if (!interaction.isChatInputCommand()) return;

  if (interaction.commandName === 'ping') {
    await interaction.reply('Pong!');
  }
});

client.login(token);