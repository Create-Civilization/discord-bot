const { sendCommandToServer } = require('../../other_functions/panelAPIFunctions.js');
const { requireAllowedId } = require('../../other_functions/helperFunctions.js')
const { SlashCommandBuilder } = require('discord.js');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('send_command_to_server')
        .setDescription('Send a command to the server')
        .addStringOption(option => option.setName('command')
            .setDescription('The command to send')
            .setRequired(true)),
    async execute(client,interaction) {

        await interaction.deferReply({ephemeral: true});

        requireAllowedId(interaction.member.roles.cache, async () => {
        try{
            const command = await interaction.options.get('command').value;
            const returnText = await sendCommandToServer(command)

            interaction.editReply({
                content: returnText,
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