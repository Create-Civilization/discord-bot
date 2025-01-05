const { SlashCommandBuilder } = require('discord.js');
const { getAllTickets} = require('../../other_functions/ticketDatabaseFuncs.js');
const configJson = require('../../config.json');


module.exports = {
    data: new SlashCommandBuilder()
        .setName('open_tickets')
        .setDescription('Gets all the currently open tickets'),
    async execute(client, interaction) {
        const allowedRoleIds = configJson.adminRolesIDS; 
        if(!allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))){
            return interaction.reply({ content: 'No Permission', ephemeral: true });
        }

        const tickets = await getAllTickets();
        let message = [];
        for (let i = 0; i < tickets.length; i++) {
            const ticket = tickets[i];
            message.push(ticket.threadChannelID);
        }

        for (let i = 0; i < message.length; i++) {
            message[i] = `<#${message[i]}>`;
        }

        if (message.length === 0) {
            message.push('No tickets found.');
        }

        await interaction.reply({ content: message.join(' '), ephemeral: true });
    }
};
