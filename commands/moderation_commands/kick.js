const { PermissionsBitField, SlashCommandBuilder } = require('discord.js');


module.exports = {
    data: new SlashCommandBuilder()
        .setName('kick')
        .setDescription('Kicks a user'),
    async execute(client, interaction) {
        const memberToKickOption = interaction.options.get('username');
        const memberToKick = interaction.guild.members.cache.get(memberToKickOption.user.id);
        const reason = interaction.options.get('reason_for_kick')?.value || 'No reason given';
        const requestingMember = await interaction.guild.members.fetch(interaction.user.id);
        //checks that user has the perms to kick other users
        if (!requestingMember.permissions.has(PermissionsBitField.Flags.KickMembers)) {
            await interaction.reply({ content: 'You cannot use this command', ephemeral: true });
        return;}
        // checks if the users are the same person
        if (memberToKick.user.id == requestingMember){
            await interaction.reply({content: "you can't kick yourself!",ephemeral:true})
            return;
        }
        // checks if user exists in that server
        if (!guild.member.fetch(memberToKick.user.id)) {
            interaction.reply({content:'User not in server',ephemeral:true});
            return;
        }
        try {
            memberToKick.kick({reason:reason});
            interaction.reply({ content: `${memberToKick.user.tag} has been kicked for: ${reason}.`,ephemeral: true });
            return;
        } catch (error) {
            interaction.reply({content:'there was an error, please make sure this user is kickable',ephemeral:true});
            return;
        }
        


    }
}


    


