const { getTicketByChannel } = require('../../other_functions/ticketDatabaseFuncs.js');
const { embedMaker } = require('../../other_functions/helperFunctions.js');
const configJson = require('../../config.json');
const { SlashCommandBuilder } = require('discord.js');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('reply')
        .setDescription('Replies to the current ticket')
        .addStringOption(option => option.setName('message')
            .setDescription('The message to send')
            .setRequired(true)),
    async execute(client, interaction) {
        const allowedRoleIds = configJson.adminRolesIDS; 
        const channel = interaction.channel;

        if (channel.isThread() && allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))) {
            try {
                const ticket = await getTicketByChannel(channel.id);
                if (ticket) {
                    const response = await interaction.options.get('message').value;
                    const threadOwner = await client.users.fetch(ticket.authorID);
                    const guild = interaction.guild;

                    const thread = await client.channels.fetch(ticket.threadChannelID);

                    // Send to user
                    let newEmbed = embedMaker({
                        colorHex: 0x32CD32,
                        title: `Response Received`,
                        description: `${response}`,
                        footer: {
                            text: `${guild.name} | ${guild.id}`,
                            iconURL: guild.iconURL({dynamic: true}) || undefined
                        },
                        author: {
                            name: interaction.user.username,
                            iconURL: interaction.user.avatarURL({dynamic: true}) || undefined
                        }
                    });

                    await threadOwner.send({embeds: [newEmbed]});

                    // Send to thread
                    newEmbed = embedMaker({
                        colorHex: 0x32CD32,
                        title: `Message Sent`,
                        description: `${response}`,
                        footer: {
                            text: `${guild.name} | ${guild.id}`,
                            iconURL: guild.iconURL({dynamic: true}) || undefined
                        },
                        author: {
                            name: interaction.user.username,
                            iconURL: interaction.user.avatarURL({dynamic: true}) || undefined
                        }
                    });

                    await thread.send({embeds: [newEmbed]});
                    return interaction.reply({
                        content: 'Message Sent',
                        ephemeral: true
                    })
                    
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
