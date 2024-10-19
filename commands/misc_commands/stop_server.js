import fetch from 'node-fetch';
import configJson from '../../config.json' with { type: 'json' };
import https from 'https';
import { SlashCommandBuilder} from "discord.js";

export default {
    data: new SlashCommandBuilder()
        .setName('stop_server')
        .setDescription('Stops The Server Using Crafty API'),
    async run(client, interaction) {
        const allowedRoleIds = configJson.adminRolesIDS;  // Log the array  
        const member = interaction.guild.members.cache.get(interaction.user.id);

        // Check if the member has permission to restart the server
        if (!member || !allowedRoleIds.some(roleId => member.roles.cache.has(roleId)) || interaction.guild.id == !'1268369952348442775') {
            return interaction.reply({
                content: 'You do not have permission to restart the server!',
                ephemeral: true
            });
        }

        // Defer the reply to acknowledge the interaction
        await interaction.deferReply({ ephemeral: true });

        const serverId = configJson.serverID; 
        const apiToken = configJson.craftyToken; 
        const apiUrl = `https://${configJson.serverIP}:${configJson.serverPort}/api/v2/servers/${serverId}/action/stop_server`;

        const agent = new https.Agent({
            rejectUnauthorized: false 
        });

        // Send the API request to Crafty to restart the server
        try {
            const response = await fetch(apiUrl, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${apiToken}`,
                    'Content-Type': 'application/json'
                },
                agent 
            });

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            const result = await response.json();

            // Edit the deferred reply based on the result
            if (result.status === 'ok') {
                return interaction.editReply({
                    content: 'Server stopped successfully!',
                });
            } else {
                return interaction.editReply({
                    content: 'Failed to stop the server.',
                });
            }

        } catch (error) {
            console.error('Error stopping the server:', error);
            return interaction.editReply({
                content: 'There was an error trying to stop the server.',
            });
        }
    }
};
