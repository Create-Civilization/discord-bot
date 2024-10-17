import { PermissionsBitField } from "discord.js";
import { addTempBanToDatabase } from "../../other_functions/userPunishmentDatabaseFuncs.js";




function convertToTimestamp(){
    
}




export default {
    name: 'temp_ban',
    run: async (client,interaction) => {
        const reason = await interaction.options.get('reason_for_ban').value;
        const banLength = await interaction.options.get('ban_length').value;
        const timeToDeleteMessages = await interaction.options.get('delete_message_days')?.value || null;
        const commandAuthor = await interaction.guild.members.fetch(interaction.user.id);
        const userToBan = interaction.options.get('username')?.user; // Get user object
        //checks if user has the permission to ban other users
        if (!commandAuthor.permissions.has(PermissionsBitField.Flags.BanMembers)) {
            await interaction.reply({ content: 'You cannot use this command', ephemeral: true });
            return;}
        //checks if a user was mentioned
        if (!userToBan) {
            await interaction.reply({content: "no user mentioned",ephemeral:true});
            return;
        }
        // checks that the users are different
        if (userToBan.user.id === commandAuthor){
            await interaction.reply({content: "you can't ban yourself!",ephemeral:true});
            return;
        }
        //checks if they are trying to ban the bot
        if (userToBan.user.id === client.user.id){
            await interaction.reply({content:"You cannot ban the bot",ephemeral:true});
            return;
        }

        addTempBanToDatabase(interaction.user.id, "temp_ban", "THE REASON OF TRUTH!!!!", )


    }
}