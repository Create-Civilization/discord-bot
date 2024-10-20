const { PermissionsBitField, EmbedBuilder, SlashCommandBuilder } = require('discord.js');

const commandEmbed = new EmbedBuilder()
  .setColor(0x0099FF)
  .setTitle('Showcase Entries')
  .setDescription('Test');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('edit_showcase')
        .setDescription('Edits the showcase'),
    async execute(client, interaction) {
        const requestingMember = await interaction.guild.members.fetch(interaction.user.id);
        if (!requestingMember.permissions.has(PermissionsBitField.Flags.KickMembers)) {
            await interaction.reply({ content: 'You cannot use this command', ephemeral: true });
            return;
        }
        interaction.reply({ embeds: [commandEmbed] });
    }
};
