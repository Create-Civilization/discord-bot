import {getUserByUUID, getUserByDiscordID, addUserToWhitelist, deleteEntryByUserID, initWhiteListDatabase} from '../../other_functions/whitelistDatabaseFuncs.js'
import { embedMaker } from '../../other_functions/helperFunctions.js';


export default {
    name: "get_whitelist",
    description: "Get whitelist data of a user",
    async run(client, interaction) {
        const allowedRoleIds = configJson.adminRolesIDS;

        if(!allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))){
            return interaction.reply({
                content: 'You do not have permission to run this',
                ephemeral: true
            })
        }

        await interaction.deferReply({ephemeral: true})

        const userId = await interaction.options.get('user_to_get').value;
        const dbObject = await getUserByDiscordID(userId)

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
            description: `Minecraft username: \`${dbObject.username}\` | Minecraft UUID: \`${dbObject.playerUUID}\` | Discord ID: \`${dbObject.discordID}\` `,
            footer: {
                text: `${guild.name} | ${guild.id}`,
                iconURL: guild.iconURL({dynamic: true}) || undefined
            }
        });

        await interaction.channel.send({embeds: [newEmbed]});

        return interaction.editReply({
            content: 'Here ya go',
            ephemeral: true
        })

        
    }
}