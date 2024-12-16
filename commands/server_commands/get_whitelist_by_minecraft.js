const { getUserByMinecraftUsername } = require('../../other_functions/whitelistDatabaseFuncs.js');
const { embedMaker } = require('../../other_functions/helperFunctions.js');
const configJson = require('../../config.json');
const { SlashCommandBuilder } = require('discord.js');


module.exports = {
    data: new SlashCommandBuilder()
        .setName('get_whitelist_by_minecraft')
        .setDescription('Get whitelist data of a user by using minecraft account')
        .addStringOption(option => option.setName('user_to_get')
            .setDescription('The user to get')
            .setRequired(true)),
    async execute(client, interaction) {
        const allowedRoleIds = configJson.adminRolesIDS;

        if(!allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))){
            return interaction.reply({
                content: 'You do not have permission to run this',
                ephemeral: true
            })
        }

        await interaction.deferReply({ephemeral: true})

        const userId = await interaction.options.get('user_to_get').value;
        const dbObject = await getUserByMinecraftUsername(userId)

        if(!dbObject){
            return interaction.editReply({
                content: "This user has no whitelist entries",
                ephemeral: true
            })
        }

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

        
    }
}