const { SlashCommandBuilder } = require('discord.js');
const { deleteTicketByTicketID, getTicketByChannel } = require('../../other_functions/ticketDatabaseFuncs.js');
const { embedMaker } = require('../../other_functions/helperFunctions.js');
const config = require('../../config.json');


module.exports = {
    data: new SlashCommandBuilder()
        .setName('close')
        .setDescription('Close the current thread')
        .addStringOption(option => option.setName('reason')
            .setDescription('The reason for closing the ticket')
            .setRequired(true))
        .addBooleanOption(option => option.setName('anonymous')
            .setDescription('Set to true if you want to send the reply anonymously')),
    async execute(client, interaction) {

        const allowedRoleIds = config.adminRolesIDS; 
        const channel = interaction.channel;

        if (channel.isThread() && allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))) {
            try {
                const ticket = await getTicketByChannel(channel.id);
                if (ticket) {
                    const reason = await interaction.options.get('reason').value;
                    const threadOwner = await client.users.fetch(ticket.authorID);
                    const guild = interaction.guild;
                    const helpChannelOBJ = client.channels.cache.get(config.helpTicketChannelID);
                    const fetchedMessage = await helpChannelOBJ.messages.fetch(ticket.threadChannelID);
                    const anonymousMode = await interaction.options.get('anonymous');

                    const thread = await client.channels.fetch(ticket.threadChannelID);
                    await deleteTicketByTicketID(ticket.id);
                    let newEmbed = embedMaker({
                        colorHex: 0xD70040,
                        title: `Ticket Closed`,
                        description: `Ticket has been closed by <@${interaction.user.id}>. Reason: ${reason}`,
                        footer: {
                            text: `${interaction.user.globalName} | ${interaction.user.id}`,
                            iconURL: interaction.user.avatarURL({dynamic: true}) || undefined
                        }
                    })
                        

                    await fetchedMessage.edit({embeds: [newEmbed]});

                    newEmbed = embedMaker({
                        colorHex: 0xD70040,
                        title: `Ticket Closed`,
                        description: reason,
                        footer: {
                            text: `${guild.name} | ${guild.id}`,
                            iconURL: guild.iconURL({dynamic: true}) || undefined

                        },
                        author: {
                            name: anonymousMode ? guild.name : interaction.user.globalName,
                            iconURL: anonymousMode ? guild.iconURL({dynamic: true}) : interaction.user.avatarURL({dynamic: true}) || undefined
                        }
                    })
                                    

                    await threadOwner.send({embeds: [newEmbed]})


                    await interaction.reply({
                        content: 'Ticket Closed',
                        ephemeral: true
                    });

                    await thread.setLocked(true);
                    await thread.setArchived(true);
                    return;

                    
                } else {
                    return interaction.reply({
                        content: 'This is not a known help ticket',
                        ephemeral: true
                    });
                }
            } catch (error) {
                console.error('Error retrieving ticket:', error);
                return interaction.reply({
                    content: 'There was an error getting the ticket',
                    ephemeral: true
                });


                
            }
        } else if(!channel.isThread && allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))) {
            return interaction.reply({
                content: 'This channel is not a thread',
                ephemeral: true
            });
        } else if(!allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))){
            return interaction.reply({
                content: 'No Permission',
                ephemeral: true
            });
        }
    }
}
