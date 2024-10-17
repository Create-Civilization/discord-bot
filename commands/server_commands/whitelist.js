
import {getUserByUUID, getUserByDiscordID, addUserToWhitelist, initWhiteListDatabase} from '../../other_functions/whitelistDatabaseFuncs.js';
import { ModalBuilder, ActionRowBuilder, TextInputBuilder, TextInputStyle, Events } from 'discord.js';

export default {
    name: "whitelist",
    description: "Add or remove a user from the whitelist",
    async run(client, interaction) {
        const isAlreadyWL = await getUserByDiscordID(interaction.user.id)

        if(isAlreadyWL){
            return interaction.reply({
                content: `This account already has a whitelisted minecraft account. Username: ${isAlreadyWL.username}`,
                ephemeral: true
            })
        }
        // Create the modal
        const modal = new ModalBuilder()
            .setCustomId('whitelistModal')
            .setTitle('Whitelist User');

        const inGameNameInput = new TextInputBuilder()
            .setCustomId('inGameName')
            .setLabel("Input your Minecraft username")
            .setStyle(TextInputStyle.Short)
            .setPlaceholder('Type your username here');

        const reasonInput = new TextInputBuilder()
            .setCustomId('reason')
            .setLabel("Why would you like to join the server")
            .setStyle(TextInputStyle.Paragraph)
            .setPlaceholder('Type your reasons here');

        const firstActionRow = new ActionRowBuilder().addComponents(inGameNameInput);
        const secondActionRow = new ActionRowBuilder().addComponents(reasonInput);

        modal.addComponents(firstActionRow, secondActionRow);

        // Show the modal to the user
        await interaction.showModal(modal);
    }





};
