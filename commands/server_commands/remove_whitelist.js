
import { sendCommandToServer } from '../../other_functions/craftyAPIfuncs.js';
import {getUserByUUID, getUserByDiscordID, addUserToWhitelist, initWhiteListDatabase, deleteEntryByUserID} from '../../other_functions/whitelistDatabaseFuncs.js';
import { ModalBuilder, ActionRowBuilder, TextInputBuilder, TextInputStyle, Events } from 'discord.js';
import configJson from '../../config.json' with { type: 'json' };

export default {
    name: "remove_whitelist",
    description: "Remove yourself from the whitelist",
    async run(client, interaction) {
        const isAlreadyWL = await getUserByDiscordID(interaction.user.id)
        const guild = interaction.guild;


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
