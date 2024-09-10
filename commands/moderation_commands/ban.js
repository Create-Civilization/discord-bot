import { PermissionsBitField } from "discord.js";

function getTime(str) {
    let result = [];
    let firstLetterFound = false;

    for (let i = 0; i < str.length; i++) {
        const char = str[i];

        // Check if the current character is a letter
        if (/[a-zA-Z]/.test(char) && !firstLetterFound) {
            result.push(str.slice(0, i));  // Push everything before the letter
            result.push(str.slice(i));     // Push the rest of the string starting from the letter
            firstLetterFound = true;
            break;  // Exit the loop after splitting at the first letter
        }
    }
    // If no letter is found, return the whole string in the result array
    if (!firstLetterFound) {
        return "fail";
    }

    const value = parseInt(result[0]);
    console.log(result);
    // Handle different time units
    if (result[1][0].toUpperCase() === "D") {
        return value * 86400;  // Convert days to seconds
    } else if (result[1][0].toUpperCase() === "H") {
        return value * 3600;   // Convert hours to seconds
    } else if (result[1][0].toUpperCase() === "M") {
        return value * 60;     // Convert minutes to seconds
    }
    return "fail";

}
export default {
    name: 'ban',
    run: async (client,interaction) => {
        const reason = await interaction.options.get('reason_for_ban').value;
        const timeBack = await interaction.options.get('delete_message_days')?.value||null;
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
        if (!(timeBack == null)){
            timeBack = getTime(timeBack);
            if (timeBack == "fail"){
                await interaction.reply({content: "you put the time in incorrectly, please use the correct format and be within the bounds, max time back is 7 days (7D)",ephemeral:true});
                return;
            }
        }
        console.log(timeBack);
        try{
            await memberToBan.ban({
                deleteMessageSeconds: (timeBack ? timeBack : 0 ),// Use 0 if no timeBack
                reason: reason,
            });

            await interaction.reply({ 
                content: `${memberToBan.user.tag} has been banned for: ${reason}.`, 
                ephemeral: true 
            });
            return;
        
        } catch (err){
            await interaction.reply({content:"something went wrong",ephemeral:true})
            return;
        }


    }
}