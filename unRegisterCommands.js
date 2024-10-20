const { REST } = require('@discordjs/rest');
const { Routes } = require('discord-api-types/v9');
const { guildID, clientID, token } = require('./config.json');

async function unregisterCommands() {
    const rest = new REST({ version: '9' }).setToken(token);

    try {
        // Unregister global commands by passing an empty array
        console.log('Unregistering all global commands...');
        await rest.put(Routes.applicationCommands(clientID), { body: [] });
        console.log('Successfully unregistered all global commands.');

        // Unregister guild commands
        console.log('Unregistering all guild commands...');
        const guildCommands = await rest.get(Routes.applicationGuildCommands(clientID, guildID));

        // Log the guild commands that will be deleted
        console.log('Unregistering the following guild commands:');
        guildCommands.forEach(command => {
            console.log(`- ${command.name} (${command.id})`);
        });

        // Delete each guild command
        await Promise.all(guildCommands.map(command => 
            rest.delete(Routes.applicationGuildCommand(clientID, guildID, command.id))
        ));

        console.log('Successfully unregistered all guild commands.');

    } catch (error) {
        console.error('Error unregistering commands:', error);
    }
}

// Execute the function
unregisterCommands();
