const { getMinecraftNameByUUID } = require('../../other_functions/helperFunctions.js');
const { getAllWhitelistData } = require('../../other_functions/whitelistDatabaseFuncs.js');
const { SlashCommandBuilder } = require('discord.js');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('update_nicknames')
        .setDescription('update nicknames'),
    async execute(client, interaction) {
        if(interaction.user.id !== "336891653291900949") {
            return interaction.reply({
                content: 'You do not have permission to run this command',
            });
        }

        await interaction.deferReply({ ephemeral: true });
        try {
            const whitelist = await getAllWhitelistData();
            const guild = interaction.guild;
            
            for (const element of whitelist) {
                try {
                    const member = await guild.members.fetch(element.discordID).catch(() => null);
                    await interaction.editReply(`Updating nickname for ${member.nickname}...`);
                    if (member && member.nickname !== element.username) {
                        await member.setNickname(element.username).catch(err => {
                            console.error(`Could not set nickname for ${element.discordID}: ${err.message}`);
                        });
                    }
                } catch (error) {
                    console.error(`Error updating nickname for ${element.discordID}:`, error.message);
                }
            }

            await interaction.editReply('Nicknames updated.');
            
        } catch (err) {
            console.log(err);
            await interaction.editReply('An error occurred while updating nicknames.');
        }
    }
};