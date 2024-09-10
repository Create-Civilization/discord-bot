import { REST, Routes, ApplicationCommandOptionType } from 'discord.js';
import configjson from './config.json' with {type: 'json'}
import { type } from 'os';

const token = configjson.token
const clientID = configjson.clientID

const commands = [
  {
    name: 'set_ticket_channel',
    description: 'sets channel for a ticket maker embed. CAN ONLY BE USED BY OWNER',
    options: [
      {
        name: 'channel_name',
        description: 'channel to place ticket embed',
        type: ApplicationCommandOptionType.Channel,
        required: true
      }
    ]
  },
  {
    name: 'ping',
    description: 'Replies with Pong!',
  },
  {
  name:"kick",
  aliases: [],
  description: "Kick someone",
  usage: "kick <member here>",
  options: [
      {
          name: 'username',
          description: 'username of user you wish to kick',
          type: ApplicationCommandOptionType.User,
          required: true
        },
        {
          name: 'reason_for_kick',
          description: 'explain why this user was kicked (this WILL be sent to user)',
          type: ApplicationCommandOptionType.String,
          required: false
        }]
      },
      {
        name: "ban",
        aliases: [],
        description: "Ban a server member",
        usage: "ban a member",
        options: [
          {
            name: 'username',
            description: 'provide the name of the user you wish to ban',
            type: ApplicationCommandOptionType.User,
            required: true
          },
          {
            name: 'reason_for_ban',
            description: ' provide a reason for the banning of this user (this WILL be sent to the user)',
            type: ApplicationCommandOptionType.String,
            required: true
          },
          {
            name: 'delete_message_back',
            description: 'How many far back would you like to delete this users messages?',
            type: ApplicationCommandOptionType.String,
            required: false
          }
        ]
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