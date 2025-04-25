const { Events } = require('discord.js');
const configJson = require('../config.json'); 
const { log, createLogEmbed } = require('../other_functions/helperFunctions');

module.exports = {
    name: Events.GuildMemberRemove,
    once: false,
    async execute(client, member) {
        console.log(configJson.logChannelID)
        if(configJson.logChannelID){
            const Logchannel = await client.channels.cache.get(configJson.logChannelID);
            const guild = member.guild;
            log(
                {
                    embeds: [
                        createLogEmbed(
                            false,
                            `User Leave`,
                            `${member.user.username} has left the server`,
                            client
                        )
                    ]
                },
                client
            )
        }


    }
}


