const { getUserByDiscordID } = require('../../other_functions/whitelistDatabaseFuncs.js');
const { ModalBuilder, ActionRowBuilder, TextInputBuilder, TextInputStyle, SlashCommandBuilder } = require('discord.js');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('whitelist')
        .setDescription('Add or remove a user from the whitelist'),
    async execute(client, interaction) {
        const isAlreadyWL = await getUserByDiscordID(interaction.user.id)

        if(!client.isServerAlive){
            return interaction.reply({
                content: `Server is offline try again when its back up`,
                ephemeral: true
            })
        }

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