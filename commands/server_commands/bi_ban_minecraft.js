const configJson = require('../../config.json');
const { SlashCommandBuilder } = require('discord.js');
const { sendCommandToServer } = require('../../other_functions/panelAPIFunctions')
const { embedMaker } = require('../../other_functions/helperFunctions');
const { getUserByMinecraftUsername } = require('../../other_functions/whitelistDatabaseFuncs');
const { newPunishment, isBanned } = require('../../other_functions/moderationDatabaseFuncs')

module.exports = {
    data: new SlashCommandBuilder()
        .setName('bi_ban_by_minecraft')
        .setDescription('Bans player from server and minecraft for a set amount of time')
        .addStringOption(option => option.setName('user')
            .setDescription('The User to ban')
            .setRequired(true))
        .addIntegerOption(option => option.setName('time')
            .setDescription('Time in seconds to ban, <0=perm')
            .setRequired(true))
        .addStringOption(option => option.setName('reason')
            .setDescription("The reason for the ban")
            .setRequired(true)),
    async execute(client,interaction) {
        const allowedRoleIds = configJson.adminRolesIDS; 
        const username = await interaction.options.get('user').value;
        const user = (await getUserByMinecraftUsername(username)).discordID

        await interaction.deferReply({ephemeral: true});

        if(allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))){
            if (await isBanned(user)) {
                interaction.editReply({
                    content: "User already banned",
                    ephemeral: true
                })
            } else {
                try{
                const reason = await interaction.options.get('reason').value;
                await sendCommandToServer(`ban ${username} ${reason}`)
                const banRelease = Math.floor(Date.now() / 1000) + (await interaction.options.get('time').value);
                newPunishment((await interaction.user.id), user, username, 'TEMPBAN', reason, banRelease)
                setBan(user, now, banRelease)
                const guild = interaction.guild;

                const logEmbed = embedMaker(
                    {
                        colorHex: 0xFF3024,
                        title: `${username} Banned by ${interaction.member.nickname}`,
                        description: `Banned user: \"${username}\" | Banned Until: ${new Date(banRelease * 1000).toLocaleString('en-US', {timeZoneName: 'short'})} | Reason: ${reason}`,
                        footer: {
                            text: `${guild.name} | ${guild.id}`,
                            iconURL: guild.iconURL({dynamic: true}) || undefined
                        },
                        author: {
                            name: interaction.user.username,
                            iconURL: interaction.user.avatarURL({dynamic: true}) || undefined
                        },
                    }
                )
                const Logchannel = await client.channels.cache.get(configJson.logChannelID);

                try {
                    await Logchannel.send({embeds: [logEmbed]})
                } catch(err) {
                    console.log("There was an error sending embed to log channel in bi_ban_discord")
                    console.log(err)
                }

                interaction.editReply({
                    content: `Successfully banned ${username}`,
                    ephemeral: true
                })
                } catch(err) {
                    interaction.editReply({
                        content: err,
                        ephemeral: true
                    })
                }
            }

        } else{
            interaction.editReply({
                content: "You do not the the permission to run this command",
                ephemeral: true
            })
        }
    }
}