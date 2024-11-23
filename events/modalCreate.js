const { Events } = require('discord.js');
const configJson = require('../config.json');
const { addUserToWhitelist, getUserByDiscordID } = require('../other_functions/whitelistDatabaseFuncs.js');
const { isMcUsernameReal } = require('../other_functions/helperFunctions.js');
const { sendCommandToServer } = require('../other_functions/craftyAPIfuncs.js');

module.exports = {
    name: Events.InteractionCreate,
    async execute(client, interaction) {
        if (!interaction.isModalSubmit()) return;

        if (interaction.customId === 'whitelistModal') {
            // Get the data entered by the user
            const inGameName = interaction.fields.getTextInputValue('inGameName');
            const reason = interaction.fields.getTextInputValue('reason');
            const mojangAPI = await isMcUsernameReal(inGameName)
            const guild = interaction.guild;
            const whitelistRole = guild.roles.cache.get(configJson.whitelistedRoleID)
            
            await interaction.deferReply({ephemeral: true})
      
            if(mojangAPI == 404) {
              return interaction.editReply({
                content: 'No account found with that usernmame. Please double check it',
                ephemeral: true,
              })
            } else if (mojangAPI && typeof mojangAPI === 'object') {
              //Valid Username
              try{
                await addUserToWhitelist(mojangAPI.id, interaction.user.id, mojangAPI.name, reason)
                await sendCommandToServer(`whitelist add ${mojangAPI.name}`)
                const member = await guild.members.fetch(interaction.user.id);
                await member.roles.add(whitelistRole);
              } catch(err){
                console.log(`There was an error running addUserToWhitelist ${err}`)
                return interaction.editReply({
                  content: `A fatal error occured if this happens multiple times make a support ticket`,
                  ephemeral: true
                })
              }

              //Check if log channel exists and if so send a log message
              console.log("MADE IT HERE TEST TEST TES")
              if(configJson.logChannelID) {
                const dbObject = await getUserByDiscordID(interaction.user.id);
                let newEmbed = embedMaker({
                  colorHex: 0x32CD32,
                  title: `${dbObject.username}'s just whitelisted`,
                  description: `Minecraft username: \`${dbObject.username}\` | Minecraft UUID: \`${dbObject.playerUUID}\` | Discord ID: \`${dbObject.discordID}\` | Reason For Join: \`${dbObject.reason}\` `,
                  footer: {
                      text: `${guild.name} | ${guild.id}`,
                      iconURL: guild.iconURL({dynamic: true}) || undefined
                  },
                  author: {
                      name: interaction.user.username,
                      iconURL: interaction.user.avatarURL({dynamic: true}) || undefined
                  },
                });
                console.log("MADE IT HERE")
                const Logchannel = await client.channels.cache.get(configJson.logChannelID);
                await Logchannel.send({embeds: [newEmbed]});
              }

      
              return interaction.editReply({
                content: `Added ${mojangAPI.name} to the whitelist. If you added the wrong username or want to be removed to /remove_whitelist`,
                ephemeral: true,
              })
           }
           else {
            console.log(JSON.stringify(mojangAPI))
            return interaction.editReply({
                content: 'Unknown Error Occured. Try again later',
                ephemeral: true,
            });
        }
        }
    }
}