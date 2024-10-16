import { PermissionsBitField } from "discord.js";
import { getTicketByAuthor, insertTicket, deleteTicketByTicketID, getTicketByChannel } from '../../database.js';
import { embedMaker } from '../../helperFunctions.js';
import configJson from '../../config.json' with { type: 'json' };

export default {
    name: "close",
    description: "Close the current thread",




    async run(client, interaction) {
        const allowedRoleIds = configJson.adminRolesIDS; 
        const channel = interaction.channel;

        if (channel.isThread() && allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))) {
            try {
                const ticket = await getTicketByChannel(channel.id);
                if (ticket) {
                    const reason = await interaction.options.get('reason').value;
                    const threadOwner = await client.users.fetch(ticket.authorID);
                    const guild = interaction.guild;
                    const helpChannelOBJ = client.channels.cache.get(configJson.helpTicketChannelID);
                    const fetchedMessage = await helpChannelOBJ.messages.fetch(ticket.threadChannelID);

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
                            name: interaction.user.globalName,
                            iconURL: interaction.user.avatarURL({dynamic: true}) || undefined
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
