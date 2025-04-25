const { getUserByDiscordID, deleteEntryByUserID } = require('../../other_functions/whitelistDatabaseFuncs.js');
const { sendCommandToServer } = require('../../other_functions/panelAPIFunctions.js');
const { requireAllowedId, requireWhitelistEntry } = require('../../other_functions/helperFunctions.js')
const configJson = require('../../config.json');
const { SlashCommandBuilder } = require('discord.js');


module.exports = {
    data: new SlashCommandBuilder()
        .setName('admin_remove_whitelist')
        .setDescription('remove whitelist data of a user')
        .addUserOption(option => option.setName('user_to_remove')
            .setDescription('The user to remove')
            .setRequired(true)),
    async execute(client, interaction) {
        requireAllowedId(interaction.member.roles.cache, async () => {
        const userId = await interaction.options.get('user_to_remove').value;
        requireWhitelistEntry(userId, async (dbObject) => {
        await interaction.deferReply({ephemeral: true})
        try{
            deleteEntryByUserID(dbObject.discordID)
        } catch(err){
            return interaction.editReply({
                content: `Error ARCTIC POOKIE FIX ME :) ERROR: ${err}`,
                ephemeral: true
            })
        }
        const guild = interaction.guild;
        const whitelistRole = await guild.roles.cache.get(configJson.whitelistedRoleID)
        const member = await guild.members.fetch(userId);
        const roles = member.roles;
        if (await roles.cache.has(whitelistRole.id)) {
            await roles.remove(whitelistRole);
        }

        sendCommandToServer(`whitelist remove ${dbObject.username}`)

        return interaction.editReply({
            content: 'User has been removed.',
            ephemeral: true
        })
        })
        })
    }
}