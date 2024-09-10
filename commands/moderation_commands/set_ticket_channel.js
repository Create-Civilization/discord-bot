import { EmbedBuilder } from 'discord.js';

export default {
    name: 'set_ticket_channel',
     run: async(client, interaction) => {
        const requestingMember = await interaction.guild.members.fetch(interaction.user.id);
        const ticketChannel = await interaction.options.getChannel('channel_name');
        if (requestingMember.id !== interaction.guild.ownerId){
            await interaction.reply({content:"you can't use this command",ephemeral:true});
            return;
        }

        if (!ticketChannel){
            await interaction.reply({content:"This channel was not found",ephemeral:true});
            return;
        }
        const ticketEmbed = new EmbedBuilder()
        .setColor('#6568BF')
        .setTitle("Ticket Creator")
        .setDescription("Welcome to the Ticket Creator. This will allow you to make a private channel with the mods to discuss an issue you are having related to anything Create: Civilization. We ask that before you make a ticket you read the guidelines below to make sure you have done the most you can to solve the issue yourself")
        .addFields(
            {name:'Whitelisting', value:'Before making a ticket concerning a whitelist request please wait 24 hours. Give the mods time to review the request before making an unneeded ticket. If there is an issue with your username the mods will reach out to you'},
            {name: 'Issues with the Website', value:'If your issue with the website is that it is not updated for you, please wait 4-6 hours before making a ticket. Depending on your location and browser settings it takes time for the cache to update'},
            {name: 'Issues with the MC server', value:'If the server has completely crashed DO NOT MAKE A TICKET, just ping McArctic in general chat. Do not spam him tho, I promise that he will get to it as fast as possible'},
            {name:"Issues with another user", value:"Before making a ticket concerning the behavior of another player and/or faction please read through the rules and make sure they have broken a rule and please have proof at the ready. We handle issues like this on a case by case basis but in general if it happened in another private server it is out of our control. If you feel like the situation is a outlier and is not covered in our rules but needs addressing, please make go ahead and make a ticket and we will see what we can do for you :)"}
        );
        await ticketChannel.send({ embeds: [ticketEmbed] });
        await interaction.reply({ content: "Ticket message has been sent!", ephemeral: true });
     }
}