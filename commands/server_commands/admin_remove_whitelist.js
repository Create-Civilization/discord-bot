import {getUserByUUID, getUserByDiscordID, addUserToWhitelist, deleteEntryByUserID, initWhiteListDatabase} from '../../other_functions/whitelistDatabaseFuncs.js'
import { embedMaker } from '../../other_functions/helperFunctions.js';
import { sendCommandToServer } from '../../other_functions/craftyAPIfuncs.js';
import configJson from '../../config.json' with { type: 'json' };


export default {
    name: "admin_remove_whitelist",
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