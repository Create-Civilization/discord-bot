const { REST } = require('@discordjs/rest');
const { Routes } = require('discord-api-types/v9');
const {guildID, clientID, token}= require('./config.json');

async function unregisterCommands() {
    const rest = new REST({ version: '9' }).setToken(token);

    try {
        // Fetch all registered commands for the bot in the guild
        const commands = await rest.get(Routes.applicationGuildCommands(clientID, guildID));

        // Log the commands that will be deleted
        console.log('Unregistering the following commands:');
        commands.forEach(command => {
            console.log(`- ${command.name} (${command.id})`);
        });

        // Delete each command
        await Promise.all(commands.map(command => {
            console.log(command.name);
            return rest.delete(Routes.applicationGuildCommand(clientID, guildID, command.id));
        }));

        console.log('Successfully unregistered all commands.');
    } catch (error) {
        console.error('Error unregistering commands:', error);
    }
}

// Execute the function
unregisterCommands();
