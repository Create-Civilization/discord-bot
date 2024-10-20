const { getUserByDiscordID, deleteEntryByUserID } = require('../../other_functions/whitelistDatabaseFuncs.js');
const { sendCommandToServer } = require('../../other_functions/craftyAPIfuncs.js');
const configJson = require('../../config.json');
const { SlashCommandBuilder } = require('discord.js');


module.exports = {
    data: new SlashCommandBuilder()
        .setName('admin_remove_whitelist')
        .setDescription('Get whitelist data of a user'),
    async execute(client, interaction) {
        const allowedRoleIds = configJson.adminRolesIDS;

        if(!allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))){
            return interaction.reply({
                content: 'You do not have permission to run this',
                ephemeral: true
            })
        } 

        await interaction.deferReply({ephemeral: true})

        const userId = await interaction.options.get('user_to_remove').value;
        const dbObject = await getUserByDiscordID(userId)

        if(!dbObject){
            return interaction.editReply({
                content: "This user has no whitelist entries",
                ephemeral: true
            })
        }

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
        const member = await guild.members.fetch(interaction.user.id);
        if (await member.roles.cache.has(whitelistRole.id)) {
            await member.roles.remove(whitelistRole);
        }

        sendCommandToServer(`whitelist remove ${dbObject.username}`)

        return interaction.editReply({
            content: 'user hase been removed :)',
            ephemeral: true
        })

        
    }
}