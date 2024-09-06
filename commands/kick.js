import { PermissionsBitField } from 'discord.js';
export default async function kickCommand(client, theInteraction) {
    const requestingMember = await theInteraction.guild.members.fetch(theInteraction.user.id);
    if (!requestingMember.permissions.has(PermissionsBitField.Flags.KickMembers)) {
        await theInteraction.reply({ content: 'You cannot use this permission', ephemeral: true });
        return;
    }

    const kicked_user = theInteraction.options.get('username');
    const reason = theInteraction.options.get('reason_for_kick') || 'no reason given :(';

    if (!kicked_user) {
        await theInteraction.deferReply();
        await theInteraction.reply({ content: 'Please mention a user to kick.', ephemeral: true });
        return;
    }

    console.log('at least this ran');
}

    


