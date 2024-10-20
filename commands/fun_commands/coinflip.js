const { SlashCommandBuilder } = require('discord.js');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('coinflip')
        .setDescription('Flips a coin'),
    async execute(client, interaction) {
        const num = Math.floor(Math.random() * 11);
        if (num % 2 === 0) {
            await interaction.reply({ content: "Heads!!!", ephemeral: false });
        } else {
            await interaction.reply({ content: "Tails!!!", ephemeral: false });
        }
    },
};
