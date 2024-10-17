import { sendCommandToServer } from "../../other_functions/craftyAPIfuncs.js";
import configJson from '../../config.json' with { type: 'json' };

export default {
    name: 'send_command_to_server',
    description: "Send a command to the server",
    async run(client,interaction) {
        const allowedRoleIds = configJson.adminRolesIDS; 
        const channel = interaction.channel;

        await interaction.deferReply({ephemeral: true});

        if(allowedRoleIds.some(roleId => interaction.member.roles.cache.has(roleId))){
            try{
            const command = await interaction.options.get('message').value;
            sendCommandToServer(command)

            interaction.editReply({
                content: "It was sent to server if it failed well idk",
                ephemeral: true
            })
        } catch(err) {
            interaction.editReply({
                content: err,
                ephemeral: true
            })
        }

        } else{
            interaction.editReply({
                content: "You do not the the permission to run this command",
                ephemeral: true
            })
        }
    }
}