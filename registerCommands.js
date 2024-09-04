import { REST, Routes, ApplicationCommandOptionType } from 'discord.js';
import configjson from './config.json' with {type: 'json'}
import { type } from 'os';

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
            require: true
        },
        {
            name: 'title',
            description: 'The title of the showcase',
            type: ApplicationCommandOptionType.String,
            require: true
        },
        {
            name: 'image',
            description: 'The image to upload',
            type: ApplicationCommandOptionType.Attachment
        }
    ]
  },
];

const rest = new REST({ version: '10' }).setToken(token);

try {
  console.log('Started refreshing application (/) commands.');

  await rest.put(Routes.applicationCommands(clientID), { body: commands });

  console.log('Successfully reloaded application (/) commands.');
} catch (error) {
  console.error(error);
}