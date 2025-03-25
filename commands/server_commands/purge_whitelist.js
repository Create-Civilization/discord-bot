const { sendCommandToServer } = require('../../other_functions/panelAPIFunctions.js');
const { getUserByDiscordID, deleteEntryByUserID } = require('../../other_functions/whitelistDatabaseFuncs.js');
const { SlashCommandBuilder } = require('discord.js');
const configJson = require('../../config.json');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('purge_whitelist')
        .setDescription('Remove all non discord members from the whitelist'),
    async execute(client, interaction) {

        if(interaction.user.id !== "336891653291900949") {
            return interaction.reply({
                content: 'You do not have permission to run this command',
            })
        }

        await interaction.deferReply({ ephemeral: true });
        try {
            const whitelistDiscordIdEntrys = await getUserByDiscordID();
            if (!whitelistDiscordIdEntrys || whitelistDiscordIdEntrys.length === 0) {
                return interaction.editReply({
                    content: "There are no whitelisted users",
                    ephemeral: true
                });
            }

            const guild = interaction.guild;
            if (!guild) {
                return interaction.editReply({
                    content: "This command can only be used in a server",
                    ephemeral: true
                });
            }

            const members = await guild.members.fetch({ force: true });
            const membersIDs = members.map(member => member.id);

            let removed = 0;

            for (const entry of whitelistDiscordIdEntrys) {
                const discordID = entry.discordID;
                if (!membersIDs.includes(discordID)) {
                    await deleteEntryByUserID(discordID);
                    await sendCommandToServer(`whitelist remove ${entry.username}`);
                    removed++;
                }
            }

            if (removed === 0) {
                await interaction.editReply('No non-Discord members were found in the whitelist.');
            } else {
                await interaction.editReply(`Successfully removed ${removed} non-Discord members from the whitelist.`);
            }

        } catch (err) {
            console.log(err);
            await interaction.editReply('An error occurred while purging the whitelist.');
        }
    }
};

