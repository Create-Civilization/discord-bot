const { sendCommandToServer } = require('../../other_functions/craftyAPIfuncs.js');
const configJson = require('../../config.json');
const { SlashCommandBuilder } = require('discord.js');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('send_command_to_server')
        .setDescription('Send a command to the server')
        .addStringOption(option => option.setName('command')
            .setDescription('The command to send')
            .setRequired(true)),
    async execute(client,interaction) {
        const allowedRoleIds = configJson.adminRolesIDS; 
        const channel = interaction.channel;

        await interaction.deferReply({ephemeral: true});

        if(allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))){
            try{
            const command = await interaction.options.get('command').value;
            sendCommandToServer(command)

            interaction.editReply({
                content: "It was sent to server if it failed well idk",
                ephemeral: true
            })
        } catch(err) {
            interaction.editReply({
                content: err,
                ephemeral: true
            })
        }

        } else{
            interaction.editReply({
                content: "You do not the the permission to run this command",
                ephemeral: true
            })
        }
    }
}