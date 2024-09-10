import { PermissionsBitField, EmbedBuilder } from "discord.js";
import axios from 'axios';
import fs from 'fs';
import path from 'path';

const commandEmbed = new EmbedBuilder()
  .setColor(0x0099FF)
  .setTitle('Showcase Entries')
  .setDescription('Test')




export default {
    name: "edit_showcase",
    run: async (client,interaction) => {
        const requestingMember = await interaction.guild.members.fetch(interaction.user.id);
        if (!requestingMember.permissions.has(PermissionsBitField.Flags.KickMembers)) {
            await interaction.reply({ content: 'You cannot use this command', ephemeral: true });
        return;}
        interaction.reply({embeds: [commandEmbed]})

        
    }
}