import fetch from 'node-fetch';
import configJson from '../../config.json' with { type: 'json' };
import https from 'https';
import { restartMinecraftServer } from '../../other_functions/craftyAPIfuncs.js'

export default {
    name: 'restart_server',
    description: 'Restarts the Minecraft server using Crafty API',
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

        try {
            const result = await restartMinecraftServer();

            // Edit the deferred reply based on the result
            if (result.status === 'ok') {
                return interaction.editReply({
                    content: 'Server restarted successfully!',
                });
            } else {
                return interaction.editReply({
                    content: 'Failed to restart the server.',
                });
            }

        } catch (error) {
            console.error('Error restarting the server:', error);
            return interaction.editReply({
                content: 'There was an error trying to restart the server.',
            });
        }
    }
};
