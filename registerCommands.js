import { REST, Routes, ApplicationCommandOptionType, Options } from 'discord.js';
import configjson from './config.json' with {type: 'json'}

const token = configjson.token
const clientID = configjson.clientID

const commands = [
  {
    name: 'ping',
    description: 'Replies with Pong!',
  },
  {
    name: 'add_to_showcase',
    description: 'Upload Items to the Showcase of the website',
    options: [
        {
            name: 'creator',
            description: 'User who posted the posty post.',
            type: ApplicationCommandOptionType.String,
            required: true
        },
        {
            name: 'title',
            description: 'The title of the showcase',
            type: ApplicationCommandOptionType.String,
            required: true
        },
        {
            name: 'image',
            description: 'The image to upload',
            type: ApplicationCommandOptionType.Attachment,
            required: false,

        }
    ]
  },
  {
    name: 'coinflip',
    description: 'flip a coin!',
    usage: 'coin flip'
  },
  {
    name: 'edit_showcase',
    description: 'Edit showcase entries',
  },
  {
    name: 'restart_server',
    description: 'Restarts the Minecraft server using Crafty API',
  },
  {
    name: 'close',
    description: 'Close current ticket',
    options: [
      {
        name: 'reason',
        description: 'The reason for closing the ticket',
        type: ApplicationCommandOptionType.String,
        required: true,
      }
    ]
  },
  {
    name: 'reply',
    description: 'Replay to current ticket',
    options: [
      {
        name: 'message',
        description: 'The reply to the ticket',
        type: ApplicationCommandOptionType.String,
        required: true,
      }
    ]
  },
  {
    name: 'stop_server',
    description: 'Stops The Server Using Crafty API',
  },
  {
    name: 'whitelist',
    description: 'Whitelist yourself to the server',
  },
  {
    name: 'remove_whitelist',
    description: 'Remove yourself from the whitelist'
  },
  {
    name: 'send_command_to_server',
    description: 'Send a command to the minecraft server',
    options: [
      {
        name: 'command',
        description: 'The command to run NO /. Also you wont get a response so. Sucks to suck',
        type: ApplicationCommandOptionType.String
      }
    ]
  },
  {
    name: 'get_whitelist',
    description: 'Get the whitelist data of a discord user',
    options: [
      {
        name: 'user_to_get',
        description: 'User to get data for',
        type: ApplicationCommandOptionType.User,
        required: true,
      }
    ]
  },
  {
    name: 'admin_remove_whitelist',
    description: 'Admin command to remove anyone off whitelist',
    options: [
      {
        name: 'user_to_remove',
        description: 'User to remove from whitelist',
        type: ApplicationCommandOptionType.User,
        required: true,
      }
    ]
  }
];


const rest = new REST({ version: '10' }).setToken(token);

async function registerCommands() {
  try {
    console.log('Started refreshing application (/) commands.');

    // Register the commands globally
    await rest.put(Routes.applicationCommands(clientID), { body: commands });

    console.log('Successfully reloaded application (/) commands.');
  } catch (error) {
    console.error('Error reloading application commands:', error);
  }
}

// Call the function to register commands
registerCommands();