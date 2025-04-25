const { SlashCommandBuilder, time } = require('discord.js');
const { sendCommandToServer } = require('../../other_functions/panelAPIFunctions')
const { getMinecraftNameByDiscordID, createLogEmbed, requireAllowedId, log } = require('../../other_functions/helperFunctions');
const { newPunishment, isBanned } = require('../../other_functions/moderationDatabaseFuncs')
const configJson = require('../../config.json')

module.exports = {
    data: new SlashCommandBuilder()
        .setName('bi_ban_by_discord')
        .setDescription('Bans player from server and minecraft for a set amount of time')
        .addUserOption(option => option.setName('user')
            .setDescription('The User to ban')
            .setRequired(true))
        .addIntegerOption(option => option.setName('time')
            .setDescription('Time in seconds to ban, <0=perm')
            .setRequired(true))
        .addStringOption(option => option.setName('reason')
            .setDescription("The reason for the ban")
            .setRequired(true)),
    async execute(client,interaction) {
        requireAllowedId(interaction.member.roles.cache, async () => {
        const user = await interaction.options.get('user').value;

        await interaction.deferReply({ephemeral: true});

        if(!client.isServerAlive){
            return interaction.reply({
                content: `Server is offline try again when its back up`,
                ephemeral: true
            })
        }

        if (await isBanned(user)) return interaction.editReply({content: "User already banned", ephemeral: true});
        

        try{
            const username = await getMinecraftNameByDiscordID(user);
            const reason = await interaction.options.get('reason').value;
            await sendCommandToServer(`ban ${username} ${reason}`)
            const time = await interaction.options.get('time').value;
            const isPermanent = time < 0;
            const banRelease = isPermanent ? 0 : Math.floor(Date.now() / 1000) + (time);
            newPunishment((await interaction.user.id), user, username, isPermanent ? 'BAN' : 'TEMPBAN', reason, banRelease)
            const member = await interaction.guild.members.fetch(user);
            try {
                if (isPermanent) {
                await member.ban({
                    reason: reason
                })
            } else {
                await member.roles.add(configJson.bannedID);
            }} catch (err) {
                console.error(err);
            }

            log({
                embeds: [await createLogEmbed(
                    false, 
                    `${username} Banned by ${interaction.member.nickname}`,
                    `Banned user: \"${username}\" | Banned Until: ${isPermanent ? 'Permanent' : new Date(banRelease * 1000).toLocaleString('en-US', {timeZoneName: 'short'})} | Reason: ${reason}`,
                    client
                )]
            }, client)
            
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
        })        
    }
}