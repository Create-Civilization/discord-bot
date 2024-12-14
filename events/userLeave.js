const { Events } = require('discord.js');
const { getUserByDiscordID, deleteEntryByUserID } = require('../other_functions/whitelistDatabaseFuncs.js');
const { getMinecraftNameByUUID, embedMaker } = require('../other_functions/helperFunctions.js');
const { sendCommandToServer } = require('../other_functions/craftyAPIfuncs.js');
const configJson = require('../config.json'); 

module.exports = {
    name: Events.GuildMemberRemove,
    once: false,
    async execute(client, member) {
        console.log(configJson.logChannelID)
        if(configJson.logChannelID){
            const Logchannel = await client.channels.cache.get(configJson.logChannelID);
            const guild = member.guild;
            let newEmbed = embedMaker({
                colorHex: 0xCC0000,
                title: `User Leave`,
                description: `${member.user.username} has left the server`,
                footer: {
                    text: `${guild.name} | ${guild.id}`,
                    iconURL: guild.iconURL({dynamic: true}) || undefined
                }
              });
            try{
                await Logchannel.send({embeds: [newEmbed]});
            } catch(err){
                console.error(err)
            }
        }


    }
}


