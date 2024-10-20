const { SlashCommandBuilder } = require('discord.js');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('ping')
        .setDescription('Replies with a lil pong!'),
    async execute(client, interaction) {
        await interaction.reply('Pong!!!!');
    },
};
