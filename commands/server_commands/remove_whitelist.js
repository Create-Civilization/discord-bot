
import { sendCommandToServer } from '../../other_functions/craftyAPIfuncs.js';
import {getUserByDiscordID, deleteEntryByUserID} from '../../other_functions/whitelistDatabaseFuncs.js';
import { SlashCommandBuilder} from 'discord.js';
import configJson from '../../config.json' with { type: 'json' };

export default {
    data: new SlashCommandBuilder()
        .setName('remove_whitelist')
        .setDescription('Remove yourself from the whitelist'),
    async execute(client, interaction, isServerAlive) {
        const isAlreadyWL = await getUserByDiscordID(interaction.user.id)
        const guild = interaction.guild;

        if(!isServerAlive){
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
