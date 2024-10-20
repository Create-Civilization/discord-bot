const { sendCommandToServer } = require('../../other_functions/craftyAPIfuncs.js');
const { getUserByDiscordID, deleteEntryByUserID } = require('../../other_functions/whitelistDatabaseFuncs.js');
const { SlashCommandBuilder } = require('discord.js');
const configJson = require('../../config.json');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('remove_whitelist')
        .setDescription('Remove yourself from the whitelist'),
    async execute(client, interaction) {
        const isAlreadyWL = await getUserByDiscordID(interaction.user.id)
        const guild = interaction.guild;

        if(!client.isServerAlive){
            return interaction.reply({
                content: `Server is offline try again when its online`,
                ephemeral: true
            })  
        }

        if(!isAlreadyWL){
            return interaction.reply({
                content: `You are already not whitelisted`,
                ephemeral: true
            })
        } else{
            try {
                await interaction.deferReply({ephemeral: true});
                const whitelistRole = guild.roles.cache.get(configJson.whitelistedRoleID)
                await sendCommandToServer(`whitelist remove ${isAlreadyWL.username}`)
                await deleteEntryByUserID(interaction.user.id)
                const member = await guild.members.fetch(interaction.user.id);
                if (await member.roles.cache.has(whitelistRole.id)) {
                    await member.roles.remove(whitelistRole);
                }
                return interaction.editReply({
                    content: `You have removed yourself from the whitelist`,
                    ephemeral: true
                })
            } catch(err){
                console.error(err)
            }
        }
    }





};
