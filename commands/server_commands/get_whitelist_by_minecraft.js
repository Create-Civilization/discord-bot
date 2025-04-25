const { getUserByMinecraftUsername } = require('../../other_functions/whitelistDatabaseFuncs.js');
const { embedMaker, requireAllowedId, requireWhitelistEntry } = require('../../other_functions/helperFunctions.js');
const { SlashCommandBuilder } = require('discord.js');


module.exports = {
    data: new SlashCommandBuilder()
        .setName('get_whitelist_by_minecraft')
        .setDescription('Get whitelist data of a user by using minecraft account')
        .addStringOption(option => option.setName('user_to_get')
            .setDescription('The user to get')
            .setRequired(true)),
    async execute(client, interaction) {
        requireAllowedId(interaction.member.roles.cache, async () => {
        requireWhitelistEntry(await getUserByMinecraftUsername(await interaction.options.get('user_to_get').value).discordID, async (dbObject) => {
        await interaction.deferReply({ephemeral: true})

        const guild = interaction.guild;

        let newEmbed = embedMaker({
            colorHex: 0x32CD32,
            title: `${dbObject.username}'s whitelist info`,
            description: `Minecraft username: \`${dbObject.username}\` | Minecraft UUID: \`${dbObject.playerUUID}\` | Discord Name: <@${dbObject.discordID}> | Reason For Join: \`${dbObject.reason}\` `,
            footer: {
                text: `${guild.name} | ${guild.id}`,
                iconURL: guild.iconURL({dynamic: true}) || undefined
            },
            author: {
                name: interaction.user.username,
                iconURL: interaction.user.avatarURL({dynamic: true}) || undefined
            },
        });

        await interaction.channel.send({embeds: [newEmbed]});

        return interaction.editReply({
            content: 'Here ya go',
            ephemeral: true
        })
        })
        });
    }
}