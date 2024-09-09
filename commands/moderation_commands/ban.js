import { PermissionsBitField } from "discord.js";
export default {
    name: 'ban',
    run: async (client,interaction) => {
        const reason = await interaction.options.get('reason_for_ban')?.value || 'No reason given';
        const daysBack = await interaction.options.get('delete_message_days')?.value||null;
        const requestingMember = await interaction.guild.members.fetch(interaction.user.id);
        const userToBan = interaction.options.get('username')?.user; // Get user object
        const memberToBan = await interaction.guild.members.fetch(userToBan.id); // Fetch the GuildMember
        //checks if user has the permission to ban other users
        if (!requestingMember.permissions.has(PermissionsBitField.Flags.BanMembers)) {
            await interaction.reply({ content: 'You cannot use this command', ephemeral: true });
            return;}
        //checks if a user was mentioned
        if (!memberToBan) {
            await interaction.reply({content: "no user mentioned",ephemeral:true});
            return;
        }
        // checks that the users are different
        if (memberToBan.user.id === requestingMember){
            await interaction.reply({content: "you can't ban yourself!",ephemeral:true});
            return;
        }
        //checks if they are trying to ban the bot
        if (memberToBan.user.id === client.user.id){
            await interaction.reply({content:"pls dont ban me :O",ephemeral:true});
            return;
        }
        try{
            await memberToBan.ban({
                deleteMessageDays: daysBack ? parseInt(daysBack) : 0, // Use 0 if no daysBack
                reason: reason,
            });

            await interaction.reply({ 
                content: `${memberToBan.user.tag} has been banned for: ${reason}.`, 
                ephemeral: true 
            });
            
        } catch (err){
            await interaction.reply({content:"something went wrong",ephemeral:true})
            return;
        }


    }
}