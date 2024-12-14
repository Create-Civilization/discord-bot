const { Events } = require('discord.js');
const { getUserByDiscordID, deleteEntryByUserID } = require('../other_functions/whitelistDatabaseFuncs.js');
const { getMinecraftNameByUUID } = require('../other_functions/helperFunctions.js');
const { sendCommandToServer } = require('../other_functions/craftyAPIfuncs.js');


module.exports = {
    name: Events.GuildMemberRemove,
    once: false,
    async execute(client, member) {
        const databaseEntry = await getUserByDiscordID(member.id);
        if (!databaseEntry){
      
        }else{
          const mcUUID = databaseEntry.playerUUID
          const discordID = databaseEntry.discordID
          //Unwhitelist command
          const mcNameCorrect = await getMinecraftNameByUUID(mcUUID)
          await sendCommandToServer(`whitelist remove ${mcNameCorrect.name}`)
          await deleteEntryByUserID(discordID);
        }
    }
}

