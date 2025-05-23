const { Events } = require('discord.js');
const { embedMaker, log, createLogEmbed } = require('../other_functions/helperFunctions.js');
const configJson = require('../config.json'); 
const { isBanned } = require('../other_functions/moderationDatabaseFuncs.js');

module.exports = {
    name: Events.GuildMemberAdd,
    once: false,
    async execute(client, member) {

        const accountCreationDate = member.user.createdTimestamp / 1000;
        const oneMonthAgo = (Date.now() / 1000) - 2628000;

        const Logchannel = await client.channels.cache.get(configJson.logChannelID);
        const guild = member.guild;

        if (accountCreationDate > oneMonthAgo) {
            try {
                await member.send("Sorry, you cannot join the server as your discord account is too new. Your account needs to be over a month old.");
            } catch (error) {
                console.error('Could Not Send DM', error);
            }

            let newEmbed = embedMaker({
                colorHex: 0xCC0000,
                title: `${member.user.username} was kicked because account was too new`,
                description: 'Account creation date was too recent.',
                footer: {
                    text: `${guild.name} | ${guild.id}`,
                    iconURL: guild.iconURL({ dynamic: true }) || null
                }
            });

            try {
                await Logchannel.send({ embeds: [newEmbed] });
            } catch (err) {
                console.error(err);
            }

            try {
                await member.kick('Account Created Too Recently');
            } catch (error) {
                console.error('Could Not Kick Member', error);
            }

        } else {
            log(
                {
                    embeds: [
                        createLogEmbed(
                            true,
                            `${member.user.username} joined`,
                            'Welcome to the server!'
                        )
                    ]
                },
                client
            )
            if (isBanned(member.id)) {
                await member.roles.add(configJson.bannedID);
            }
        }
    }
};
