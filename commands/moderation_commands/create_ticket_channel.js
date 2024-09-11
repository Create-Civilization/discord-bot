import { ActionRowBuilder, ButtonBuilder, ButtonStyle, PermissionsBitField, PermissionOverwriteManager} from 'discord.js';
import { EmbedBuilder } from 'discord.js';
async function getRolesWithBanPerms(){
    const canBan = []
    const cannotBan = []
    guild.roles.cache.forEach(role => {
        if (role.id.permissions.has(PermissionsBitField.Flags.BanMembers)){
            canBan.push(role.id);
        }
        else {
            cannotBan.push(role.id)
        }
        const banObj = {
            'canBan': canBan,
            'cannotBan': cannotBan
        }
        return banObj;
    });
}

export default {
    name: 'create_ticket_channel',
     run: async(client, interaction) => {
        // checks if the user is the server owner
        const requestingMember = await interaction.guild.members.fetch(interaction.user.id);
        if (requestingMember.id !== interaction.guild.ownerId){
            await interaction.reply({content:"you can't use this command",ephemeral:true});
            return;
        }
        const roleInfo = getRolesWithBanPerms()
        //creates a category for tickets and a ticket channel
        const ticketCategory = await interaction.guild.channels.create({
            name:'ticket_category',
            type:4,
            reason:"need a category for tickets"
        })
        const mainTicketChannel = await interaction.guild.channels.create({ name:'make_a_ticket',
            type: 0, 
            reason: 'Creating a ticket channel',}) .catch((error) => {
                interaction.reply({ content: "An error occurred while creating the channel.", ephemeral: true });
                return;
            });
        /*roleInfo.get('canBan').forEach(roleID =>{
            mainTicketChannel.permissionsOverwrites.create(channel.guild.roles.roleID, {

            })
        })*/
        //creates button for ticket message
        const ticket_Button = new ButtonBuilder()
        .setCustomId("create_ticket")
        .setLabel("Create Ticket")
        .setStyle(ButtonStyle.Primary);
        const theRow = new ActionRowBuilder().addComponents(ticket_Button)
        //creates embed for ticket message
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
        //sends ticket message into the channel
        await mainTicketChannel.send({ embeds: [ticketEmbed], components: [theRow], });
        await interaction.reply({ content: "Ticket Channel created", ephemeral: true });
        return;
     }
}