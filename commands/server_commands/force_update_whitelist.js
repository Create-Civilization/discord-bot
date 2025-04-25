const { checkForIgnChanges } = require('../../other_functions/tasks.js');
const { SlashCommandBuilder } = require('discord.js');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('force_update_whitelist')
        .setDescription('force_update_whitelist'),
    async execute(client, interaction) {
        if(interaction.user.id !== "336891653291900949") {
            return interaction.reply({
                content: 'You do not have permission to run this command',
            });
        }

        await interaction.deferReply({ ephemeral: true });
        try {
           await checkForIgnChanges(client);
        } catch (err) {
            console.log(err);
            await interaction.editReply('An error occurred while updating whitelist.');
        }
    }
};